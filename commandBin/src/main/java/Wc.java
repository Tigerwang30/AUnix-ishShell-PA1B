import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * {@code wc [<file>...]}: prints newline, word, and byte counts for each file, and a total line
 * if more than one file is specified. A word is a non-zero-length sequence of characters
 * delimited by white space.
 */
public class Wc extends ShellCommand {
    /**
     * Format specifier for printing output counts. Do not change.
     */
    public static final String formatSpecifier = "%8d %8d %8d %s%n";

    public Wc(String[] args) {
        super(args);
    }

    public static void main(String[] args) {
        ShellCommand.start(Wc.class, args);
    }

    @Override
    protected void runCommand() throws IOException {
        if (cmdArgs.length == 0) {
            long[] counts = countStream(System.in);
            System.out.printf(formatSpecifier, counts[0], counts[1], counts[2], "");
            return;
        }

        int totalLines = 0;
        int totalWords = 0;
        int totalBytes = 0;

        for (String filename : cmdArgs) {
            Path filePath = Paths.get(filename);
            try {
                checkExists(filePath);
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
                continue;
            }

            if (Files.isDirectory(filePath)) {
                try {
                    checkIsNotDir(filePath);
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getMessage());
                }
                System.out.printf(formatSpecifier, 0, 0, 0, filename);
                continue;
            }
            try (FileInputStream in = getFileInput(filePath)){
                long[] counts = countStream(in);
                System.out.printf(formatSpecifier, counts[0], counts[1], counts[2], filename);
                totalLines += counts[0];
                totalWords += counts[1];
                totalBytes += counts[2];
            }
        }

        if (cmdArgs.length > 1) {
            System.out.printf(formatSpecifier, totalLines, totalWords, totalBytes, "total");
        }
    }
    private long[] countStream(InputStream in) throws IOException {
        long lines = 0;
        long words = 0;
        long bytes = 0;
        String line; 
        while ((line = readLineRaw(in)) != null) {
            lines++;
            bytes += line.getBytes().length;
            String[] wordArray = line.trim().split("\\s+");
            if (wordArray.length == 1 && wordArray[0].isEmpty()) {
                continue;
            }
            words += wordArray.length;
        }
        return new long[]{lines, words, bytes};
    }
}
