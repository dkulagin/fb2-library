/*
 * Created on 24.01.2007
 */
package org.ak2.fb2.library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.ak2.fb2.library.commands.CommandArgs;
import org.ak2.fb2.library.commands.ICommand;
import org.ak2.fb2.library.commands.ca.CompareAuthors;
import org.ak2.fb2.library.commands.cfn.RenameFiles;
import org.ak2.fb2.library.commands.del.DeleteFolder;
import org.ak2.fb2.library.commands.enc.FixEncoding;
import org.ak2.fb2.library.commands.ma.MergeAuthors;
import org.ak2.fb2.library.commands.xml.ExportXml;
import org.ak2.fb2.library.exceptions.BadCmdArguments;
import org.ak2.fb2.library.exceptions.LibraryException;
import org.ak2.utils.LengthUtils;
import org.ak2.utils.enums.EnumUtils;
import org.ak2.utils.jlog.JLog;
import org.ak2.utils.jlog.JLogLevel;
import org.ak2.utils.jlog.JLogMessage;

/**
 * @author Andrei Komarovskikh / Reksoft
 */
public class Main {

    private static final JLogMessage MSG_UNKNOWN_CMD = new JLogMessage(JLogLevel.FATAL, "No appropriate command found");

    private static final JLogMessage MSG_BAD_CONSOLE_ENC = new JLogMessage(JLogLevel.ERROR, "Unable to setup console codepage: {0}");

    private static final JLogMessage MSG_BAD_ARG = new JLogMessage(JLogLevel.ERROR, "Bad cmd argument: {0}");

    private static final JLogMessage MSG_ERROR = new JLogMessage(JLogLevel.ERROR, "Unexpected exception: ");

    private static final ICommand[] COMMANDS = { new RenameFiles(), new FixEncoding(), new CompareAuthors(), new MergeAuthors(), new DeleteFolder(), new ExportXml() };

    public static void main(final String[] args) {
        initLog();
        initXalan();
        initConsole();

        if (args.length < 1) {
            showReadme();
            return;
        }

        final Map<String, ICommand> commands = new HashMap<String, ICommand>();
        for (final ICommand cmd : COMMANDS) {
            commands.put(cmd.getName(), cmd);
        }

        final CommandArgs cmdArgs = new CommandArgs(args);

        for (int i = 0; i < args.length; i++) {
            final String cmdArg = args[i];

            final ICommand cmd = commands.get(cmdArg);
            if (cmd != null) {
                executeCommand(cmdArgs, cmd);
                return;
            }
        }

        MSG_UNKNOWN_CMD.log();
        showReadme();
    }

    private static void initLog() {
        String logFilePattern = MessageFormat.format("fb2-library.{0,date,yyyyMMdd.HHmmss}.log", new Date());
        JLogLevel consoleLogLevel = EnumUtils.valueOf(JLogLevel.class, System.getProperty("jlog.console.level"), JLogLevel.INFO);
        JLogLevel fileLogLevel = EnumUtils.valueOf(JLogLevel.class, System.getProperty("jlog.file.level"), JLogLevel.INFO);
        JLog.setConsoleLevel(consoleLogLevel);
        JLog.addLogFile(logFilePattern, fileLogLevel);

    }

    private static void initXalan() {
        System.setProperty("com.sun.org.apache.xalan.internal.serialize.encodings", Main.class.getResource("Encodings.properties").toString());
    }

    private static void initConsole() {
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

    private static void executeCommand(final CommandArgs cmdArgs, final ICommand cmd) {
        try {
            cmd.execute(cmdArgs);
        } catch (final BadCmdArguments ex) {
            MSG_BAD_ARG.log(ex.getMessage());
            if (ex.isShowReadme()) {
                showReadme(cmd);
            }
        } catch (final Throwable th) {
            MSG_ERROR.log(th);
        }
    }

    /**
     * Shows help text
     */
    private static void showReadme() {
        try {
            final BufferedReader readme = new BufferedReader(new InputStreamReader(Main.class.getClassLoader().getResourceAsStream("readme.txt")));
            for (String s = readme.readLine(); s != null; s = readme.readLine()) {
                System.out.println(s);
            }
            for (final ICommand cmd : COMMANDS) {
                System.out.println("");
                System.out.println(cmd.getName() + " - " + cmd.getDescription());
            }
        } catch (final IOException ex) {
        }
    }

    private static void showReadme(final ICommand cmd) {
        try {
            final BufferedReader readme = new BufferedReader(new InputStreamReader(Main.class.getClassLoader().getResourceAsStream("readme.txt")));
            for (String s = readme.readLine(); s != null; s = readme.readLine()) {
                System.out.println(s);
            }
            System.out.println(cmd.getName() + " - " + cmd.getDescription());
        } catch (final IOException ex) {
        }
    }

}
