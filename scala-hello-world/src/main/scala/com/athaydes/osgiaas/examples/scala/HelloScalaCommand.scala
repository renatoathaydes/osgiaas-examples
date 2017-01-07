package com.athaydes.osgiaas.examples.scala

import java.io.PrintStream

import com.athaydes.osgiaas.cli.CommandHelper
import org.apache.felix.shell.Command
import org.osgi.service.component.annotations.Component

@Component(immediate = true, name = "hello-scala")
class HelloScalaCommand extends Command {

  @Override def getName = "hello-scala"

  @Override def getUsage = "hello-scala [<message>]"

  @Override def getShortDescription = "Prints a Hello World message or a custom message given by the user"

  /**
    * This method implements the command logic.
    *
    * @param line full command provided by the user. Notice that this may be more than one line!
    * @param out  stream for the command output (prefer this to System.out)
    * @param err  stream for the command errors (prefer this to System.err)
    */
  @Override def execute(line: String, out: PrintStream, err: PrintStream) {
    // break up the command line into separate tokens.
    // notice that the first part is always the name of the command itself.
    val arguments = CommandHelper.breakupArguments(line)

    arguments.size match {
      case 1 => out.println("Hello Scala!")
      case 2 => out.println("Hello " + arguments.get(1))
      // too many arguments provided by the user
      case _ => CommandHelper.printError(err, getUsage, "Too many arguments")
    }
  }
}
