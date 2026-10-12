import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/**
 * {@code tail [-n <count>] [<file>...]}: prints the last {@code count} lines (default {@code 10})
 * of each file.
 *
 * <p>With more than one file, each file's output is preceded by an {@code "==> <file> <=="}
 * header line, with a blank line separating consecutive files' output (no header for a single
 * file or standard input).
 */
public class Tail extends ShellCommand {
    private static final int DEFAULT_COUNT = 10;

    public Tail(String[] args) {
        super(args);
    }

    public static void main(String[] args) {
        ShellCommand.start(Tail.class, args);
    }

    @Override
    protected void runCommand() throws IOException {
        int count = DEFAULT_COUNT;
        int firstFileIndex = 0;
        if (cmdArgs.length > 0 && cmdArgs[0].equals("-n")) {
            try {
                count = Integer.parseInt(cmdArgs[1]);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                System.err.println("Usage: tail [-n <count>] [<file>...]");
                return;
            }
            firstFileIndex = 2;
        }
        String[] fileNames = Arrays.copyOfRange(cmdArgs, firstFileIndex, cmdArgs.length);

        if (fileNames.length == 0) {
            printLastLines(System.in, count);
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
                printLastLines(input, count);
                printedAFile = true;
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private void printLastLines(InputStream input, int count) throws IOException {
        Deque<String> lastLines = new ArrayDeque<>();
        String line;
        while ((line = readLineRaw(input)) != null) {
            lastLines.addLast(line);
            if (lastLines.size() > count) {
                lastLines.removeFirst();
            }
        }
        for (String lastLine : lastLines) {
            System.out.print(lastLine);
        }
    }
}
