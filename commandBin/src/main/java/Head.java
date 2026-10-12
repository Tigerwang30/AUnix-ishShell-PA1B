import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * {@code head [-n <count>] [<file>...]}: prints the first {@code count} lines (default {@code 10})
 * of each file.
 *
 * <p>With more than one file, each file's output is preceded by an {@code "==> <file> <=="}
 * header line, with a blank line separating consecutive files' output (no header for a single
 * file or standard input).
 */
public class Head extends ShellCommand {
    private static final int DEFAULT_COUNT = 10;

    public Head(String[] args) {
        super(args);
    }

    public static void main(String[] args) {
        ShellCommand.start(Head.class, args);
    }

    @Override
    protected void runCommand() throws IOException {
        int count = DEFAULT_COUNT;
        int firstFileIndex = 0;
        if (cmdArgs.length > 0 && cmdArgs[0].equals("-n")) {
            try {
                count = Integer.parseInt(cmdArgs[1]);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                System.err.println("Usage: head [-n <count>] [<file>...]");
                return;
            }
            firstFileIndex = 2;
        }
        String[] fileNames = Arrays.copyOfRange(cmdArgs, firstFileIndex, cmdArgs.length);

        if (fileNames.length == 0) {
            printFirstLines(System.in, count);
            return;
        }

        boolean printedAFile = false;
        for (String fileName : fileNames) {
            Path file = Paths.get(fileName);
            try (InputStream input = getFileInput(file)) {
                if (fileNames.length > 1) {
                    if (printedAFile) {
                        System.out.println();
                    }
                    System.out.printf("==> %s <==%n", fileName);
                }
                printFirstLines(input, count);
                printedAFile = true;
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private void printFirstLines(InputStream input, int count) throws IOException {
        String line;
        int linesPrinted = 0;
        while (linesPrinted < count && (line = readLineRaw(input)) != null) {
            System.out.print(line);
            linesPrinted++;
        }
    }
}
