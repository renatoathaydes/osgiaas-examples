package com.athaydes.osgiaas.examples.java

import com.athaydes.osgiaas.cli.CommandHelper
import org.apache.felix.shell.Command
import org.osgi.service.component.annotations.Component
import java.io.PrintStream

@Component(immediate = true, name = "hello-kotlin")
class HelloKotlinCommand : Command {

    override fun getName() = "hello-kotlin"

    override fun getUsage() = "hello-kotlin [<message>]"

    override fun getShortDescription() = "Prints a Hello World message or a custom message given by the user"

    /**
     * This method implements the command logic.
     *
     * @param line full command provided by the user. Notice that this may be more than one line!
     * @param out  stream for the command output (prefer this to System.out)
     * @param err  stream for the command errors (prefer this to System.err)
     */
    override fun execute(line: String, out: PrintStream, err: PrintStream) {
        // break up the command line into separate tokens.
        // notice that the first part is always the name of the command itself.
        val arguments = CommandHelper.breakupArguments(line)

        when (arguments.size) {
            1 -> // no arguments provided by the user
                out.println("Hello Kotlin!")
            2 -> // The user gave an argument, print the argument instead
                out.println("Hello " + arguments[1])
            else -> // too many arguments provided by the user
                CommandHelper.printError(err, usage, "Too many arguments")
        }
    }

}
