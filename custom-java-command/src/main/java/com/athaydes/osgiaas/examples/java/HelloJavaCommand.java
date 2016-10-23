package com.athaydes.osgiaas.examples.java;

import com.athaydes.osgiaas.cli.CommandHelper;
import org.apache.felix.shell.Command;

import java.io.PrintStream;
import java.util.List;

public class HelloJavaCommand implements Command {

    @Override
    public String getName() {
        return "hello-java-command";
    }

    @Override
    public String getUsage() {
        return "hello-java-command <message>?";
    }

    @Override
    public String getShortDescription() {
        return "Prints a Hello World message or a custom message given by the user";
    }

    /**
     * This method implements the command logic.
     *
     * @param line full command provided by the user. Notice that this may be more than one line!
     * @param out  stream for the command output (prefer this to System.out)
     * @param err  stream for the command errors (prefer this to System.err)
     */
    @Override
    public void execute( String line, PrintStream out, PrintStream err ) {
        // break up the command line into separate tokens.
        // notice that the first part is always the name of the command itself.
        List<String> arguments = CommandHelper.breakupArguments( line );

        if ( arguments.size() == 1 ) {
            // no arguments provided by the user
            out.println( "Hello Java!" );
        } else if ( arguments.size() == 2 ) {
            // The user gave an argument, print the argument instead
            out.println( "Hello " + arguments.get( 1 ) );
        } else {
            // too many arguments provided by the user
            CommandHelper.printError( err, getUsage(), "Too many arguments" );
        }
    }

}
