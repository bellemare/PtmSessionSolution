import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * JCommander annotations.
 * Lombok is used to take care of the boilerplate code.
 */
@Getter
@ToString
@Slf4j
public class SessionAppCLIArgs {

    /**
     * Parses the arguments from the CLI.
     * Throws an IllegalArgumentException with the usage message in case of parse errors.
     *
     * @param args The array passed to main.
     *
     */
    public static SessionAppCLIArgs parse(final String[] args) {

        final SessionAppCLIArgs retval = new SessionAppCLIArgs();

        final JCommander argsParser = new JCommander(retval);
        try {
            argsParser.parse(args);

            //Print help - kinda hacky but it works. Could be improved if necessary.
            if (retval.help) {
                throw new com.beust.jcommander.ParameterException("Print help and exit");
            }
        } catch (com.beust.jcommander.ParameterException e) {
            if (!retval.help) {
                System.out.println(String.format("Exception while parsing CLI Args: %s ", e.getMessage()));
            }

            final StringBuilder sb = new StringBuilder();
            argsParser.usage(sb);

            throw new IllegalArgumentException(sb.toString());
        }

        return retval;
    }

    @Parameter(names = "--help", help = true)
    private boolean help;

    @Parameter(
            names = {"-i", "--inputFile"},
            description = "The URI of the input file containing the plain-text AWS EBS log events.",
            required = true
    )
    private String inputFile;

    @Parameter(
            names = {"-t", "--timeoutInSeconds"},
            description = "The session timeout in seconds - Period of time that must elapse before a new session is created.",
            required = false
    )
    private int timeoutInSeconds = 30*60;

}
