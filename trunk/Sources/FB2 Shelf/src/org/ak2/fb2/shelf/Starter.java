package org.ak2.fb2.shelf;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;

import org.ak2.fb2.library.Main;
import org.ak2.fb2.library.commands.CommandArgs;
import org.ak2.fb2.shelf.gui.MainFrame;
import org.ak2.utils.LengthUtils;
import org.ak2.utils.collections.SafeMap;
import org.ak2.utils.collections.factories.IMapValueFactory;
import org.ak2.utils.enums.EnumUtils;
import org.ak2.utils.jlog.JLog;
import org.ak2.utils.jlog.JLogLevel;
import org.ak2.utils.jlog.JLogMessage;
import org.ak2.utils.jlog.JLog.LogFormatter;

public class Starter {

    private static final JLogMessage MSG_BAD_CONSOLE_ENC = new JLogMessage(JLogLevel.ERROR, "Unable to setup console codepage: {0}");

    static StyledDocument logDocument = new DefaultStyledDocument();

    static final Map<String, CommandArgs> cmdArgs = new SafeMap<String, CommandArgs>(new IMapValueFactory<String, CommandArgs>() {
        @Override
        public CommandArgs create(final String key) {
            return new CommandArgs();
        }

    });

    public static void main(final String[] args) {
        initConsole();
        initXalan();
        initLog();

        final MainFrame frame = new MainFrame();

        frame.setLocationRelativeTo(null);
        frame.showFrame();
    }


    static void initLog() {
        final String logFilePattern = MessageFormat.format("fb2-shelf.{0,date,yyyyMMdd.HHmmss}.log", new Date());
        final JLogLevel consoleLogLevel = EnumUtils.valueOf(JLogLevel.class, System.getProperty("jlog.console.level"), JLogLevel.INFO);
        final JLogLevel fileLogLevel = EnumUtils.valueOf(JLogLevel.class, System.getProperty("jlog.file.level"), JLogLevel.INFO);
        JLog.setConsoleLevel(consoleLogLevel);
        JLog.addLogFile(logFilePattern, fileLogLevel);
        JLog.addLogHander(new LogAreaHandler());
    }

    static void initXalan() {
        System.setProperty("com.sun.org.apache.xalan.internal.serialize.encodings", Main.class.getResource("Encodings.properties").toString());
    }

    static void initConsole() {
        final String consoleEnc = System.getProperty("console.encoding");
        if (LengthUtils.isNotEmpty(consoleEnc)) {
            try {
                System.setOut(new PrintStream(System.out, true, consoleEnc));
                System.setErr(new PrintStream(System.err, true, consoleEnc));
            } catch (final UnsupportedEncodingException e) {
                MSG_BAD_CONSOLE_ENC.log("" + e);
            }
        }
    }

    private static class LogAreaHandler extends Handler implements Runnable {

        private final ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();

        private final AtomicBoolean running = new AtomicBoolean();

        public LogAreaHandler() {
            LogFormatter formatter = new LogFormatter();
            formatter.setAddDate(false);
            formatter.setAddError(false);
            formatter.setAddLevel(false);
            formatter.setAddThread(false);

            this.setFormatter(formatter);
        }

        @Override
        public void publish(final LogRecord record) {
            if (!isLoggable(record)) {
                return;
            }
            try {
                String msg = getFormatter().format(record);
                queue.add(msg);
                if (running.compareAndSet(false, true)) {
                    if (SwingUtilities.isEventDispatchThread()) {
                        run();
                    } else {
                        SwingUtilities.invokeLater(this);
                    }
                }
            } catch (final Exception ex) {
                reportError(null, ex, ErrorManager.FORMAT_FAILURE);
                return;
            }
        }

        @Override
        public void run() {
            while (!queue.isEmpty()) {
                final String msg = queue.poll();
                try {
                    logDocument.insertString(logDocument.getLength(), msg, null);
                } catch (final BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
            running.set(false);
        }

        @Override
        public void close() throws SecurityException {
            // TODO Auto-generated method stub
        }

        @Override
        public void flush() {
            // TODO Auto-generated method stub
        }

    }
}
