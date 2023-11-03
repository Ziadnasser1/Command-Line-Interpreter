import java.io.File;
import java.util.*;//contain DataStructures
import java.io.IOException;
import java.util.stream.Stream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.*;

//testBranch

class Parser {
    String commandName;
    String[] args;

    public boolean parse(String input) {
        String[] words = input.split("\\s+");//split with white spaces or tabs.
        if (words.length > 0) {
            commandName = words[0];
            args = Arrays.copyOfRange(words, 1, words.length);
            return true;
        }
        return false;
    }

    public String getCommandName() {
        return commandName;
    }

    public String[] getArgs() {
        return args;
    }
}

public class Terminal {
    Parser parser;
    Path currentDirectory = Paths.get(System.getProperty("user.dir"));//gets the current working directory as a Path object.

    public void chooseCommandAction(String command, String[] args) {
        switch (command) {
            case "echo":
                echoCommand(args);
                break;
            case "pwd":
                pwdCommand();
                break;
            case "cd":
                changeDirectoryCommand(args);
                break;
            case "ls":
                listDirectoryCommand(args);
                break;
            case "ls -r":
                listDirectoryReverse();
                break;
            case "mkdir":
                makeDirectory(args);
                break;
            case "rmdir":
                removeDirectory(args);
                break;
            case "touch":
                createFile(args);
                break;
            case "cp":
                copyFile(args);
                break;
            case "cp-r":
                cpDirectory(args);
                break;
            case "rm":
                removeFile(args);
                break;
            case "cat":
                concatenateFiles(args);
                break;
            case "wc":
                wordCount(args);
                break;
            case "exit":
                exitCommand();
                break;
            default:
                System.out.println("Command not recognized.");
        }
    }

    public void echoCommand(String[] args) {
        for (String arg : args) {
            System.out.print(arg + " ");
        }
        System.out.println();
    }

    public void pwdCommand() {
        System.out.println(currentDirectory.toAbsolutePath());
    }

    public void changeDirectoryCommand(String[] args) {
        if (args.length == 0) {
            // change to the home directory
            currentDirectory = Paths.get(System.getProperty("user.home"));
        } else if (args.length == 1) {
            String newDirectoryPath = args[0];
            if (newDirectoryPath.equals("..")) {
                //Change to the previous directory
                if (currentDirectory.getParent() != null) {
                    currentDirectory = currentDirectory.getParent();
                } else {
                    System.out.println("Already in the root directory.");
                }
            } else {
                //Change to the specified path
                Path newDirectory = currentDirectory.resolve(newDirectoryPath);

                if (Files.exists(newDirectory) && Files.isDirectory(newDirectory)) {
                    currentDirectory = newDirectory.toAbsolutePath();
                } else {
                    System.out.println("Directory does not exist: " + newDirectory);
                }
            }
        } else {
            System.out.println("Usage: cd [<directory>|..]");
        }
    }

    public void listDirectoryCommand(String[] args) {
        try {
            // Use Files.list to obtain a stream of entries (files and subdirectories) in the current directory.
            try (Stream<Path> entries = Files.list(currentDirectory)) {
                entries.forEach(entry -> System.out.println(entry.getFileName()));
            }
        } catch (IOException e) {
            System.err.println("Error listing directory: " + e.getMessage());
        }
    }

    public void listDirectoryReverse() {
        try {
            // Use Files.list to obtain a stream of entries (files and subdirectories) in the current directory.
            try (Stream<Path> entries = Files.list(currentDirectory)) {
                ArrayList<Path> ls_r = new ArrayList<>();
                entries.forEach(ls_r::add);
                Collections.reverse(ls_r);
                ls_r.forEach(entry -> System.out.println(entry.getFileName()));
            }
        } catch (IOException e) {
            System.err.println("Error listing directory: " + e.getMessage());
        }
    }

    public void makeDirectory(String[] args) {
        if (args.length == 0) {
            System.err.println("No arguments provided");
        } else {
            for (String arg : args) {
                Path newPath;
                if (Paths.get(arg).isAbsolute()) {
                    newPath = Paths.get(arg);
                } else {
                    newPath = currentDirectory.resolve(arg);
                }

                try {
                    Files.createDirectories(newPath);
                } catch (IOException e) {
                    System.err.println("Error making directory: " + e.getMessage());
                }
            }
        }
    }

    public void removeDirectory(String[] args) {
        String path = currentDirectory.toAbsolutePath().toString();
        if (args[0].equals("*")) {
            File CD = new File(path);
            removeEmptyDirectories(CD);
        }
        else {
            for (int i = 0; i < args.length; i++) {
                String directoryPath = args[i];
                File directory = new File(directoryPath);
                if (!directory.exists()) {
                    System.err.println("Directory does not exist: " + directoryPath);// Return an error code
                }

                if (!directory.isDirectory()) {
                    System.err.println("Not a directory: " + directoryPath);// Return an error code
                }

                if (isDirectoryEmpty(directory)) {
                    if (!directory.delete()) {
                        System.err.println("Failed to remove directory: " + directoryPath);

                    }
                } else {
                    System.err.println("Failed to remove: Directory not empty");

                }
            }

        }
    }
    public static void removeEmptyDirectories(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    removeEmptyDirectories(file);
                }
            }
            if (directory.list().length == 0) {
                directory.delete();
            }
        }
    }

    private static boolean isDirectoryEmpty(File directory) {
        if (directory.isDirectory()) {
            String[] files = directory.list();
            return files == null || files.length == 0;
        }
        return false;
    }

    public void createFile(String[] args) {
        if (args.length == 0) {
            System.err.println("No arguments provided");
        } else {
            for (String arg : args) {
                Path newPath;
                if (Paths.get(arg).isAbsolute()) {
                    newPath = Paths.get(arg);
                } else {
                    newPath = currentDirectory.resolve(arg);
                }

                try {
                    Files.createFile(newPath);
                } catch (IOException e) {
                    System.err.println("Error making file: " + e.getMessage());
                }
            }
        }
    }
    public void copyFile(String[] args) {
        if (args.length == 0) {
            System.err.println("No arguments provided");
        } else if(args.length == 2) {
                Path firstPath;
                Path secondPath;
                if (Paths.get(args[1]).isAbsolute()) {
                    secondPath = Paths.get(args[1]);
                } else {
                    secondPath = currentDirectory.resolve(args[1]);
                }
                if (Paths.get(args[0]).isAbsolute()) {
                    firstPath = Paths.get(args[0]);
                } else {
                    firstPath = currentDirectory.resolve(args[0]);
                }

                try {
                    Files.copy(firstPath, secondPath);
                } catch (IOException e) {
                    System.err.println("Error copying file: " + e.getMessage());
                }
            }
        }

    public void cpDirectory(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: cp -r <source_directory> <destination_directory>");
            System.exit(1);
        }else{
            Path firstPath = Paths.get(args[0]);
            Path secondPath = Paths.get(args[1]);

            try {
                copyDirectory(firstPath, secondPath);
                System.out.println("Directory copied from " + firstPath + " to " + secondPath);
            } catch (IOException e) {
                System.err.println("Error copying directory: " + e.getMessage());
            }
        }

    }
    public void copyDirectory(Path source, Path destination) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path targetDir = destination.resolve(source.relativize(dir));
                Files.createDirectories(targetDir);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, destination.resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }
    public void removeFile(String[] args) {}
    public void concatenateFiles(String[] args) {}
    public void wordCount(String[] args){}

    public void exitCommand() {
        System.exit(0);
    }


    public static void main(String[] args) {
        Terminal terminal = new Terminal();
        Scanner scanner = new Scanner(System.in);
        terminal.parser = new Parser();

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();

            terminal.parser.parse(input);
            String command = terminal.parser.getCommandName();
            String[] commandArgs = terminal.parser.getArgs();

            terminal.chooseCommandAction(command, commandArgs);
        }
    }
}