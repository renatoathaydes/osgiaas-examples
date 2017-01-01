package com.athaydes.osgiaas.examples.groovy

import com.athaydes.osgiaas.cli.CommandHelper
import groovy.transform.CompileStatic
import org.apache.felix.shell.Command
import org.osgi.service.component.annotations.Component

@Component( immediate = true, name = "hello-groovy" )
@CompileStatic
class HelloGroovyCommand implements Command {

    final String name = "hello-groovy"

    final String usage = "hello-groovy [<message>]"

    final String shortDescription = "Prints a Hello World message or a custom message given by the user"

    /**
     * This method implements the command logic.
     *
     * @param line full command provided by the user. Notice that this may be more than one line!
     * @param out stream for the command output (prefer this to System.out)
     * @param err stream for the command errors (prefer this to System.err)
     */
    @Override
    void execute( String line, PrintStream out, PrintStream err ) {
        // break up the command line into separate tokens.
        // notice that the first part is always the name of the command itself.
        def arguments = CommandHelper.breakupArguments( line )

        switch ( arguments.size() ) {
            case 1: // no arguments provided by the user
                out.println( "Hello Groovy!" )
                break
            case 2: arguments.size() == 2
                // The user gave an argument, print the argument instead
                out.println( "Hello " + arguments.get( 1 ) )
                break
            default: // too many arguments provided by the user
                CommandHelper.printError( err, usage, "Too many arguments" )
        }
    }

}
