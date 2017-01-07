package com.athaydes.osgiaas.examples.java;

import com.athaydes.osgiaas.cli.CommandHelper;
import org.apache.felix.shell.Command;
import org.osgi.service.component.annotations.Component;

import java.io.PrintStream;
import java.util.List;

@Component( immediate = true, name = "hello-java" )
public class HelloJavaCommand implements Command {

    @Override
    public String getName() {
        return "hello-java";
    }

    @Override
    public String getUsage() {
        return "hello-java [<message>]";
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

        switch ( arguments.size() ) {
            case 1:// no arguments provided by the user
                out.println( "Hello Java!" );
                break;
            case 2:// The user gave an argument, print the argument instead
                out.println( "Hello " + arguments.get( 1 ) );
                break;
            default: // too many arguments provided by the user
                CommandHelper.printError( err, getUsage(), "Too many arguments" );
        }
    }

}
