import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * {@code uniq [-c] [<file>]}: collapses runs of <b>adjacent</b> duplicate lines into a single
 * line — non-adjacent duplicates are left alone, same as real {@code uniq} (pair with
 * {@code sort} first to dedupe an entire file). With {@code -c}, each output line is prefixed
 * with the number of times it occurred in its run.
 *
 * <p>Unlike the other commands, {@code uniq} takes at most <b>one</b> file argument — that's a
 * real constraint of the actual Unix command, not a simplification for this project.
 */
public class Uniq extends ShellCommand {

    public Uniq(String[] args) {
        super(args);
    }

    public static void main(String[] args) {
        ShellCommand.start(Uniq.class, args);
    }

    @Override
    protected void runCommand() throws IOException {
        boolean showCounts = cmdArgs.length > 0 && cmdArgs[0].equals("-c");
        int firstFileIndex = showCounts ? 1 : 0;
        int fileCount = cmdArgs.length - firstFileIndex;

        if (fileCount > 1) {
            System.err.println("Usage: uniq [-c] [<file>]");
            return;
        }
        if (fileCount == 0) {
            collapseDuplicates(System.in, showCounts);
            return;
        }

        Path file = Paths.get(cmdArgs[firstFileIndex]);
        try (InputStream input = getFileInput(file)) {
            collapseDuplicates(input, showCounts);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }

    private void collapseDuplicates(InputStream input, boolean showCounts) throws IOException {
        String previousLine = null;
        int runLength = 0;
        String rawLine;
        while ((rawLine = readLineRaw(input)) != null) {
            String line = rawLine.replaceFirst("\\R$", "");
            if (line.equals(previousLine)) {
                runLength++;
            } else {
                printRun(previousLine, runLength, showCounts);
                previousLine = line;
                runLength = 1;
            }
        }
        printRun(previousLine, runLength, showCounts);
    }

    private void printRun(String line, int runLength, boolean showCounts) {
        if (line == null) {
            return;
        }
        if (showCounts) {
            System.out.print(runLength + " ");
        }
        System.out.println(line);
    }
}
