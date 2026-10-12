import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {@code sort [-r] [<file>...]}: reads every line from all given files (or standard input, if
 * none given), concatenated in argument order, and prints them sorted lexicographically —
 * ascending by default, descending with {@code -r}.
 *
 * <p>Unlike {@code head}/{@code tail}, files are merged into one sorted stream rather than
 * processed (and headed) independently — that's how real {@code sort} behaves too.
 */
public class Sort extends ShellCommand {

    public Sort(String[] args) {
        super(args);
    }

    public static void main(String[] args) {
        ShellCommand.start(Sort.class, args);
    }

    @Override
    protected void runCommand() throws IOException {
        boolean reverse = cmdArgs.length > 0 && cmdArgs[0].equals("-r");
        String[] fileNames = Arrays.copyOfRange(cmdArgs, reverse ? 1 : 0, cmdArgs.length);

        List<String> lines = new ArrayList<>();
        if (fileNames.length == 0) {
            readAllLines(System.in, lines);
        }
        for (String fileName : fileNames) {
            try (InputStream input = getFileInput(Paths.get(fileName))) {
                readAllLines(input, lines);
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
            }
        }

        Collections.sort(lines);
        if (reverse) {
            Collections.reverse(lines);
        }
        for (String line : lines) {
            System.out.print(line);
        }
    }

    private void readAllLines(InputStream input, List<String> lines) throws IOException {
        String line;
        while ((line = readLineRaw(input)) != null) {
            lines.add(line);
        }
    }
}
