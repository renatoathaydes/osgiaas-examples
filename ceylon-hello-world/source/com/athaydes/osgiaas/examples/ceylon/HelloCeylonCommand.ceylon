import com.athaydes.osgiaas.cli { CommandHelper }
import org.apache.felix.shell { Command }
import org.osgi.service.component.annotations { component }
import java.io { PrintStream }

component { immediate = true; name = "hello-ceylon"; }
shared class HelloCeylonCommand() satisfies Command {

    name = "hello-ceylon";

    usage = "hello-ceylon [<message>]";

    shortDescription = "Prints a Hello World message or a custom message given by the user";

    "This method implements the command logic."
    shared actual void execute(
        "full command provided by the user. Notice that this may be more than one line!"
        String line,
        "stream for the command output (prefer this to System.out)"
        PrintStream \iout,
        "stream for the command errors (prefer this to System.err)"
        PrintStream err) {

        // break up the command line into separate tokens.
        // notice that the first part is always the name of the command itself.
        value arguments = CommandHelper.breakupArguments(line);

        if (arguments.size() == 1 || arguments.size() == 2) {
            // no arguments provided by the user or
            // the user gave an argument, in which case print the argument instead
            \iout.println("Hello ``arguments[1] else "Ceylon"``");
        } else {
            // too many arguments provided by the user
            CommandHelper.printError( err, usage, "Too many arguments" );
        }

    }

}
