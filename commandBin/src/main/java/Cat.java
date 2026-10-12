import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * {@code cat}: prints the contents of one or more files to standard out, in order.
 *
 * <p>On a missing file or directory, print the error and keep going with the rest —
 * {@link #getFileInput} already throws a ready-to-print {@link IllegalArgumentException}.
 */
public class Cat extends ShellCommand {

    public Cat(String[] args) {
        super(args);
    }

    public static void main(String[] args) throws Exception {
        ShellCommand.start(Cat.class, args);
    }

    @Override
    protected void runCommand() throws IOException {
        if (cmdArgs.length == 0) {
            String line;
            while ((line = readLineRaw(System.in)) != null) {
                System.out.print(line);
            }
        } else {
            for (String filename : cmdArgs) {
                Path filePath = Paths.get(filename);
                try (FileInputStream in = getFileInput(filePath)) {
                    System.out.write(in.readAllBytes());
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }
}
