package org.ak2.fb2.library;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;

import org.ak2.fb2.library.commands.CommandArgs;
import org.ak2.fb2.library.commands.ICommand;
import org.ak2.fb2.library.commands.ICommandParameter;
import org.ak2.fb2.library.commands.parameters.BoolParameter;
import org.ak2.fb2.library.commands.parameters.EnumParameter;
import org.ak2.fb2.library.commands.parameters.FileSystemParameter;
import org.ak2.utils.FileUtils;
import org.ak2.utils.LengthUtils;
import org.ak2.utils.collections.SafeMap;
import org.ak2.utils.collections.factories.IMapValueFactory;
import org.ak2.utils.csv.CsvBuilder;
import org.ak2.utils.enums.EnumUtils;
import org.ak2.utils.html.HtmlBuilder;
import org.ak2.utils.html.HtmlBuilder.StyleSheet;
import org.ak2.utils.jlog.JLog;
import org.ak2.utils.jlog.JLogLevel;
import org.ak2.utils.jlog.JLog.LogFormatter;

public class Starter {

    private static ICommand selected = null;

    private static StyledDocument logDocument = new DefaultStyledDocument();

    private static final Map<String, CommandArgs> cmdArgs = new SafeMap<String, CommandArgs>(new IMapValueFactory<String, CommandArgs>() {
        @Override
        public CommandArgs create(final String key) {
            return new CommandArgs();
        }

    });

    public static void main(final String[] args) {
        Main.initConsole();
        Main.initXalan();
        initLog();

        final CmdFrame frame = new CmdFrame();

        frame.setLocationRelativeTo(null);
        frame.showCmdFrame();
    }

    static void initLog() {
        final String logFilePattern = MessageFormat.format("fb2-library.{0,date,yyyyMMdd.HHmmss}.log", new Date());
        final JLogLevel consoleLogLevel = EnumUtils.valueOf(JLogLevel.class, System.getProperty("jlog.console.level"), JLogLevel.INFO);
        final JLogLevel fileLogLevel = EnumUtils.valueOf(JLogLevel.class, System.getProperty("jlog.file.level"), JLogLevel.INFO);
        JLog.setConsoleLevel(consoleLogLevel);
        JLog.addLogFile(logFilePattern, fileLogLevel);
        JLog.addLogHander(new LogAreaHandler());
    }

    private static class CmdFrame extends JFrame {

        private static final long serialVersionUID = 3595272628118286814L;

        private final Object lock = new Object();

        private JPanel mainPanel;

        private JComboBox commandBox;

        private JButton execBtn;

        private JTextField cmdLineField;

        private JPanel paramsPanel;

        private JPanel logPanel;

        private JTextArea logArea;

        private JScrollPane logScrollPane;

        public CmdFrame() {
            super("FB2 Library Utils");
            setName("MainFrame");
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            setPreferredSize(new Dimension(800, 600));

            final Container contentPane = getContentPane();
            contentPane.setLayout(new BorderLayout());
            contentPane.add(getMainPanel());

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(final WindowEvent e) {
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                }
            });

            pack();
        }

        public void showCmdFrame() {
            this.setVisible(true);
            try {
                synchronized (lock) {
                    lock.wait();
                }
            } catch (final InterruptedException ex) {
                Thread.interrupted();
            }
        }

        protected void setParameterValue(final ICommand cmd, final ICommandParameter param, final Object value) {
            String str = LengthUtils.toString(value);
            str = LengthUtils.unsafeString(str);
            cmdArgs.get(cmd.getName()).setValue(param.getName(), str);

            updateCommandLine();
        }

        protected void updateCommandLine() {
            final ICommand cmd = (ICommand) getCommandBox().getSelectedItem();
            final String cmdLine = getCmdLine(cmd);
            getCmdLineField().setText(cmdLine);
        }

        protected String getCmdLine(final ICommand selected) {
            if (selected == null) {
                return "";
            }
            final CommandArgs args = cmdArgs.get(selected.getName());
            final CsvBuilder builder = new CsvBuilder(" ");
            builder.add(selected.getName());
            for (final String name : args.getArgNames()) {
                final String value = args.getValue(name);
                if (LengthUtils.isNotEmpty(value)) {
                    builder.add("-" + name);
                    builder.add(value);
                }
            }
            return builder.toString();

        }

        protected String getHtmlTooltip(final ICommand cmd, final ICommandParameter param) {
            if (cmd == null) {
                return null;
            }

            final HtmlBuilder buf = new HtmlBuilder();
            final StyleSheet styleSheet = new StyleSheet();

            styleSheet.selector("html").attr("color", "black").attr("background", "FFFFC4");

            buf.start("html");
            buf.style(styleSheet);

            Stack<Integer> indents = new Stack<Integer>();
            Pattern p = Pattern.compile("^\\s*");

            if (param == null) {
                buf.start("div");
                buf.start("b").text(cmd.getName()).end();
                buf.end("div");

                final String[] lines = cmd.getDescription().split("\n+");
                for (final String string : lines) {
                    String text = string;
                    Matcher matcher = p.matcher(text);
                    matcher.find();
                    int start = matcher.start();
                    int end = matcher.end();

                    int indent = end - start;
                    if (indent > 0) {
                        text = text.substring(indent);
                        Integer peek = indents.isEmpty() ? null : indents.peek();
                        if (peek == null) {
                            indents.push(indent);
                            buf.start("ul");
                            buf.start("li").text(text).end();
                        } else if (indent == peek.intValue()) {
                            buf.start("li").text(text).end();
                        } else if (indent > peek.intValue()) {
                            indents.push(indent);
                            buf.start("ul");
                            buf.start("li").text(text).end();
                        } else {
                            buf.end("ul");
                            indents.pop();
                            while (!indents.isEmpty()) {
                                peek = indents.peek();
                                if (indent == peek.intValue()) {
                                    buf.start("li").text(text).end();
                                    break;
                                } else if (indent > peek.intValue()) {
                                    indents.push(indent);
                                    buf.start("ul");
                                    buf.start("li").text(text).end();
                                    break;
                                } else {
                                    buf.end("ul");
                                    indents.pop();
                                }
                            }
                        }
                    } else {
                        while (!indents.isEmpty()) {
                            buf.end("ul");
                            indents.pop();
                        }
                        buf.start("div").text(text).end();
                    }
                }
            } else {
                buf.start("div");
                buf.start("b").text(cmd.getName()).text(":").text(param.getName()).end();
                buf.end("div");

                final String[] lines = param.getDescription().split("\n+");
                for (final String string : lines) {
                    if (LengthUtils.isNotEmpty(string.trim())) {
                        buf.start("div").text(string).end();
                    }
                }
            }
            return buf.finish();
        }

        private JPanel getMainPanel() {
            if (mainPanel == null) {
                mainPanel = new JPanel();
                mainPanel.setName("MainPanel");
                mainPanel.setLayout(new GridBagLayout());

                final GridBagConstraints c00 = new GridBagConstraints();
                c00.gridy = 0;
                c00.gridx = 0;
                c00.gridwidth = 1;
                c00.gridheight = 1;
                c00.fill = GridBagConstraints.NONE;
                c00.anchor = GridBagConstraints.EAST;
                c00.insets = new Insets(8, 8, 0, 0);

                final JLabel cmdLabel = new JLabel();
                cmdLabel.setName("CommandLabel");
                cmdLabel.setText("Command:");
                mainPanel.add(cmdLabel, c00);

                final GridBagConstraints c01 = new GridBagConstraints();
                c01.gridy = 0;
                c01.gridx = 1;
                c01.gridwidth = 1;
                c01.gridheight = 1;
                c01.fill = GridBagConstraints.NONE;
                c01.anchor = GridBagConstraints.WEST;
                c01.insets = new Insets(8, 8, 0, 0);
                mainPanel.add(getCommandBox(), c01);

                final GridBagConstraints c02 = new GridBagConstraints();
                c02.gridy = 0;
                c02.gridx = 2;
                c02.gridwidth = 1;
                c02.gridheight = 1;
                c02.fill = GridBagConstraints.NONE;
                c02.anchor = GridBagConstraints.WEST;
                c02.insets = new Insets(8, 8, 0, 8);
                mainPanel.add(getExecButton(), c02);

                final GridBagConstraints c10 = new GridBagConstraints();
                c10.gridy = 1;
                c10.gridx = 0;
                c10.gridwidth = 3;
                c10.gridheight = 1;
                c10.fill = GridBagConstraints.HORIZONTAL;
                c10.weightx = 1.0;
                c10.anchor = GridBagConstraints.CENTER;
                c10.insets = new Insets(8, 8, 8, 8);
                mainPanel.add(getCmdLineField(), c10);

                final GridBagConstraints c20 = new GridBagConstraints();
                c20.gridy = 2;
                c20.gridx = 0;
                c20.gridwidth = 3;
                c20.gridheight = 1;
                c20.fill = GridBagConstraints.HORIZONTAL;
                c20.weightx = 1.0;
                c20.anchor = GridBagConstraints.CENTER;
                c20.insets = new Insets(8, 8, 0, 8);
                mainPanel.add(getParamsPanel(), c20);

                final GridBagConstraints c30 = new GridBagConstraints();
                c30.gridy = 3;
                c30.gridx = 0;
                c30.gridwidth = 3;
                c30.gridheight = 1;
                c30.fill = GridBagConstraints.BOTH;
                c30.weightx = 1.0;
                c30.weighty = 1.0;
                c30.anchor = GridBagConstraints.CENTER;
                c30.insets = new Insets(8, 8, 0, 8);
                mainPanel.add(getLogPanel(), c30);

            }
            return mainPanel;
        }

        private JComboBox getCommandBox() {
            if (commandBox == null) {
                commandBox = new JComboBox(new DefaultComboBoxModel(Main.COMMANDS));
                commandBox.setName("CommandBox");
                commandBox.setEditable(false);
                commandBox.setEnabled(true);
                commandBox.setMinimumSize(new Dimension(40, commandBox.getPreferredSize().height));

                commandBox.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(final ItemEvent e) {
                        final JComponent source = (JComponent) e.getSource();
                        final ICommand cmd = (ICommand) e.getItem();
                        source.setToolTipText(getHtmlTooltip(cmd, null));
                        final CardLayout paramsLayout = (CardLayout) getParamsPanel().getLayout();
                        paramsLayout.show(paramsPanel, cmd != null ? cmd.getName() : "");
                        updateCommandLine();
                    }
                });

                commandBox.setSelectedItem(null);
                commandBox.setSelectedIndex(0);
            }
            return commandBox;
        }

        private JButton getExecButton() {
            if (execBtn == null) {
                execBtn = new JButton("Execute");
                execBtn.setName("ExecuteButton");
                execBtn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        selected = (ICommand) getCommandBox().getSelectedItem();
                        if (selected != null) {
                            getExecButton().setEnabled(false);
                            getCommandBox().setEnabled(false);
                            final CommandArgs x = cmdArgs.get(selected.getName());
                            final SwingWorker<String, String> task = new SwingWorker<String, String>() {
                                @Override
                                protected String doInBackground() throws Exception {
                                    Main.executeCommand(x, selected);
                                    return null;
                                }
                                @Override
                                protected void done() {
                                    getExecButton().setEnabled(true);
                                    getCommandBox().setEnabled(true);
                                }
                            };

                            task.execute();
                        }
                    }
                });
            }
            return execBtn;
        }

        private JTextField getCmdLineField() {
            if (cmdLineField == null) {
                cmdLineField = new JTextField();
                cmdLineField.setName("CommandLineField");
                cmdLineField.setText("");
                cmdLineField.setEditable(false);
            }
            return cmdLineField;
        }

        private JPanel getParamsPanel() {
            if (paramsPanel == null) {
                final CardLayout paramsLayout = new CardLayout();
                paramsPanel = new JPanel();
                paramsPanel.setName("ParametersPanel");
                paramsPanel.setLayout(paramsLayout);
                paramsPanel.setBorder(BorderFactory.createEtchedBorder());
                for (final ICommand cmd : Main.COMMANDS) {
                    final JPanel cmdPanel = createCmdPanel(cmd);
                    paramsPanel.add(cmdPanel, cmd.getName());
                }
            }
            return paramsPanel;
        }

        private JPanel createCmdPanel(final ICommand cmd) {
            final JPanel cmdPanel = new JPanel();
            cmdPanel.setName("CmdPanel/" + cmd.getName());
            cmdPanel.setLayout(new GridBagLayout());
            cmdPanel.setToolTipText(getHtmlTooltip(cmd, null));

            int gridy = -1;
            for (final ICommandParameter param : cmd.getParameters()) {
                final Object defValue = param.getDefaultValue();
                cmdArgs.get(cmd.getName()).setValue(param.getName(), defValue != null ? defValue.toString() : null);

                final GridBagConstraints lc = new GridBagConstraints();
                lc.gridy = ++gridy;
                lc.gridx = 0;
                lc.gridwidth = 1;
                lc.gridheight = 1;
                lc.fill = GridBagConstraints.NONE;
                lc.anchor = GridBagConstraints.EAST;
                lc.insets = new Insets(4, 4, 0, 0);

                final JLabel label = new JLabel();
                label.setName("CmdParamLabel/" + cmd.getName() + "/" + param.getName());
                label.setText(param.getName() + ":");
                label.setToolTipText(getHtmlTooltip(cmd, param));
                cmdPanel.add(label, lc);

                final GridBagConstraints fc = new GridBagConstraints();
                fc.gridy = gridy;
                fc.gridx = 1;
                fc.gridwidth = 1;
                fc.gridheight = 1;
                fc.fill = GridBagConstraints.HORIZONTAL;
                fc.weightx = 1.0;
                fc.anchor = GridBagConstraints.WEST;
                fc.insets = new Insets(4, 4, 0, 4);

                final JComponent paramComp = createCmdParamField(cmd, param, fc);
                cmdPanel.add(paramComp, fc);
            }

            final Component glue = Box.createGlue();
            glue.setName("TempGlue/" + cmd.getName());
            final GridBagConstraints gc = new GridBagConstraints();
            gc.gridy = ++gridy;
            gc.gridx = 0;
            gc.gridwidth = 2;
            gc.gridheight = 1;
            gc.fill = GridBagConstraints.BOTH;
            gc.weightx = 1.0;
            gc.weighty = 1.0;
            gc.anchor = GridBagConstraints.CENTER;
            gc.insets = new Insets(4, 4, 0, 4);
            cmdPanel.add(glue, gc);
            return cmdPanel;
        }

        private JComponent createCmdParamField(final ICommand cmd, final ICommandParameter param, final GridBagConstraints fc) {
            JComponent paramComp = null;
            if (param instanceof FileSystemParameter) {
                paramComp = createCmdFileSystemParam(cmd, (FileSystemParameter) param);
            } else if (param instanceof BoolParameter) {
                fc.insets = new Insets(4, 0, 0, 4);
                paramComp = createCmdBoolParam(cmd, param);
            } else if (param instanceof EnumParameter) {
                fc.insets = new Insets(4, 4, 0, 4);
                paramComp = createCmdEnumParam(cmd, (EnumParameter) param);
            } else {
                paramComp = createCmdTextParam(cmd, param);
            }
            paramComp.setToolTipText(getHtmlTooltip(cmd, param));
            return paramComp;
        }

        private JPanel createCmdFileSystemParam(final ICommand cmd, final FileSystemParameter param) {
            final JPanel fsPanel = new JPanel();
            fsPanel.setName("FileSystemParamPanel/" + cmd.getName() + "/" + param.getName());
            fsPanel.setLayout(new BorderLayout());

            final JTextField field = new JTextField();
            field.setName("CmdParamField/" + cmd.getName() + "/" + param.getName());
            field.setText(LengthUtils.toString(param.getDefaultValue()));

            field.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void removeUpdate(final DocumentEvent e) {
                    setParameterValue(cmd, param, field.getText());
                }

                @Override
                public void insertUpdate(final DocumentEvent e) {
                    setParameterValue(cmd, param, field.getText());
                }

                @Override
                public void changedUpdate(final DocumentEvent e) {
                    setParameterValue(cmd, param, field.getText());
                }
            });

            fsPanel.add(field, BorderLayout.CENTER);

            final JButton btn = new JButton();
            btn.setName("FileSystemBtn/" + cmd.getName() + "/" + param.getName());
            btn.setText("...");
            btn.setPreferredSize(new Dimension(20, 20));
            btn.setMaximumSize(new Dimension(20, 20));
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    final JFileChooser fs = new JFileChooser();
                    fs.setDialogTitle(param.getDescription());
                    fs.setCurrentDirectory(new File("."));
                    fs.setAcceptAllFileFilterUsed(true);

                    if (param.isFileAccepted() && param.isFolderAccepted()) {
                        fs.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    } else if (param.isFileAccepted()) {
                        fs.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    } else if (param.isFolderAccepted()) {
                        fs.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    }
                    final String text = field.getText();
                    final File oldFile = LengthUtils.isNotEmpty(text) ? new File(text) : null;
                    fs.setSelectedFile(oldFile);

                    if (JFileChooser.APPROVE_OPTION == fs.showDialog(mainPanel, "Select")) {
                        final File f = fs.getSelectedFile();
                        field.setText(FileUtils.getRelativeFileName(f));
                    }
                }
            });

            fsPanel.add(btn, BorderLayout.EAST);
            return fsPanel;
        }

        private JCheckBox createCmdBoolParam(final ICommand cmd, final ICommandParameter param) {
            final JCheckBox field = new JCheckBox();
            field.setName("CmdParamField/" + cmd.getName() + "/" + param.getName());
            field.setText("");
            field.setSelected(Boolean.TRUE.equals(param.getDefaultValue()));

            field.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(final ItemEvent e) {
                    setParameterValue(cmd, param, field.isSelected());
                }
            });
            return field;
        }

        private JComboBox createCmdEnumParam(final ICommand cmd, final EnumParameter enumParam) {
            final JComboBox field = new JComboBox(enumParam.getValues());
            field.setName("CmdParamField/" + cmd.getName() + "/" + enumParam.getName());
            field.setSelectedItem(null);
            field.setSelectedItem(enumParam.getDefaultValue());

            field.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(final ItemEvent e) {
                    final Object selected = field.getSelectedItem();
                    setParameterValue(cmd, enumParam, selected);
                }
            });
            return field;
        }

        private JTextField createCmdTextParam(final ICommand cmd, final ICommandParameter param) {
            final JTextField field = new JTextField();
            field.setName("CmdParamField/" + cmd.getName() + "/" + param.getName());
            field.setText(LengthUtils.toString(param.getDefaultValue()));
            field.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void removeUpdate(final DocumentEvent e) {
                    setParameterValue(cmd, param, field.getText());
                }

                @Override
                public void insertUpdate(final DocumentEvent e) {
                    setParameterValue(cmd, param, field.getText());
                }

                @Override
                public void changedUpdate(final DocumentEvent e) {
                    setParameterValue(cmd, param, field.getText());
                }
            });
            return field;
        }

        private JPanel getLogPanel() {
            if (logPanel == null) {
                logPanel = new JPanel();
                logPanel.setName("LogPanel");
                logPanel.setBorder(BorderFactory.createEtchedBorder());
                logPanel.setLayout(new BorderLayout());
                logPanel.add(getLogScrollPane(), BorderLayout.CENTER);
            }
            return logPanel;
        }

        private JScrollPane getLogScrollPane() {
            if (logScrollPane == null) {
                logScrollPane = new JScrollPane();
                logScrollPane.setName("LogScrollPane");
                logScrollPane.setViewportView(getLogArea());
            }
            return logScrollPane;
        }

        private JTextArea getLogArea() {
            if (logArea == null) {
                logArea = new JTextArea();
                logArea.setName("LogArea");
                logArea.setDocument(logDocument);
                logArea.setEditable(false);
                logArea.setLineWrap(false);
                logArea.setTabSize(4);
            }
            return logArea;
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
