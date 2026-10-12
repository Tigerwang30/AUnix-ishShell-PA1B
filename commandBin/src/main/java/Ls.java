import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@code ls [<path>...]}: lists files and directories. With no arguments, lists the current
 * directory.
 *
 * <p>Files print first (sorted by name), then directories (sorted by name), blank-line separated.
 * A directory's contents get a {@code "<dir>:"} header only when more than one argument was given.
 */
public class Ls extends ShellCommand {

    public Ls(String[] args) {
        super(args);
    }

    public static void main(String[] args) {
        ShellCommand.start(Ls.class, args);
    }

    @Override
    protected void runCommand() {
        String[] paths = cmdArgs; 
        if (paths.length == 0) {
            paths = new String[]{"."};
        }
        boolean showHead = paths.length > 1;
        List<String> files = new ArrayList<>();
        List<String> dirs = new ArrayList<>();
        for (String p : paths) {
            Path path = Paths.get(p);
            try {
                checkExists(path);

            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
                continue;
            }
            if (Files.isDirectory(path)) {
                dirs.add(p);
            } else {
                files.add(p);
            }
        }
        Collections.sort(files);
        Collections.sort(dirs);
        for (String file : files) {
            System.out.println(file);
        }
        if (!files.isEmpty() && !dirs.isEmpty()) {
            System.out.println();
        }
        for (int i = 0; i < dirs.size(); i++) {
            String dir = dirs.get(i);
            if (showHead) {
                System.out.println(dir + ":");
            } 
            File[] entries = new File(dir).listFiles();
            List<String> names = new ArrayList<>();
            for (File entry : entries) {
                if (!entry.getName().startsWith(".")) {
                    names.add(entry.getName());
                }
            }
            Collections.sort(names);
            for (String name : names) {
                System.out.println(name);
            }
            boolean isLastDir = (i == dirs.size() - 1);
            if (!isLastDir) {
                System.out.println();
            }
        }
    }
}
