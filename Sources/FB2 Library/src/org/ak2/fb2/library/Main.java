/*
 * Created on 24.01.2007
 */
package org.ak2.fb2.library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.ak2.fb2.library.commands.CommandArgs;
import org.ak2.fb2.library.commands.ICommand;
import org.ak2.fb2.library.commands.cfn.RenameFiles;
import org.ak2.fb2.library.commands.enc.FixEncoding;
import org.ak2.fb2.library.exceptions.BadCmdArguments;
import org.ak2.fb2.library.exceptions.LibraryException;

/**
 * @author Andrei Komarovskikh / Reksoft
 */
public class Main {

    private static final ICommand[] COMMANDS = { new RenameFiles(), new FixEncoding() };

    public static void main(final String[] args) {
        System.setProperty("com.sun.org.apache.xalan.internal.serialize.encodings", Main.class.getResource("Encodings.properties").toString());
        try {
            final String consoleEnc = System.getProperty("console.encoding", "cp866");
            System.setOut(new PrintStream(System.out, true, consoleEnc));
            System.setErr(new PrintStream(System.err, true, consoleEnc));
        } catch (final UnsupportedEncodingException e) {
            System.out.println("Unable to setup console codepage: " + e);
        }

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

        System.err.println("No appropriate command found");
        showReadme();
    }

    private static void executeCommand(final CommandArgs cmdArgs, final ICommand cmd) {
        try {
            cmd.execute(cmdArgs);
        } catch (final BadCmdArguments ex) {
            System.err.println(ex.getMessage());
            if (ex.isShowReadme()) {
                showReadme();
            }
        } catch (final LibraryException ex) {
            ex.printStackTrace();
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
        } catch (final IOException ex) {
        }
    }
}
