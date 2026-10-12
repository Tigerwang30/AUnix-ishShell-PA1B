import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * {@code grep <pattern> [<file>...]}: prints every line, from the given files or from standard
 * input, that matches the {@link java.util.regex.Pattern regular expression} {@code pattern}.
 *
 * <p>With more than one file, each matching line is prefixed with {@code "<filename>:"}.
 */
public class Grep extends ShellCommand {

    public Grep(String[] args) {
        super(args);
    }

    public static void main(String[] args) {
        ShellCommand.start(Grep.class, args);
    }

    @Override
    protected void runCommand() throws IOException{
        if (cmdArgs.length == 0) {
            System.err.println("Usage: grep <pattern> [<file>...]");
            return;
        }

        String pattern = cmdArgs[0];
        String[] filenames = Arrays.copyOfRange(cmdArgs, 1, cmdArgs.length);

        if (filenames.length == 0) {
            String line;
            while ((line = readLineRaw(System.in)) != null) {
                if (matches(line, pattern)) {
                    System.out.print(line);
                }
            }
            return;
        }

        boolean showPrefix = filenames.length > 1;

        for (String filename : filenames) {
            Path path = Paths.get(filename);
            try (FileInputStream in = getFileInput(path)) {
                String line;
                while ((line = readLineRaw(in)) != null) {
                    if (matches(line, pattern)) {
                        if (showPrefix) {
                            System.out.print(filename + ":" + line);
                        } else {
                            System.out.print(line);
                        }
                    }
                }
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private boolean matches(String line, String pattern) {
        String stripped = line.replace("\r", "").replace("\n", "");
        return Pattern.compile(pattern).matcher(stripped).find();
    }
}
