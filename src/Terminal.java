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
    private List<String> commandHistory = new ArrayList<>();

    public void chooseCommandAction(String command, String[] args) {
        if(command.equals("history")){
            printCommandHistory();
        }else{
            switch (command) {
                case "echo": {
                    if (args.length == 0) {
                        System.out.println("You should use an argument to print with echo command!");
                    } else {
                        echoCommand(args);
                    }
                    break;
                }
                case "pwd": {
                    if(args.length!=0) {
                        System.out.println("You should not provide any arguments with pwd command!");
                    }else {
                        pwdCommand(args);
                    }
                    break;
                }
                case "cd":
                    changeDirectoryCommand(args);
                    break;
                case "ls":{
                    if(args.length == 0){
                        listDirectoryCommand();
                    }else if(args.length == 1 && Objects.equals(args[0], "-r")){
                        listDirectoryReverse();
                    }else{
                        System.out.println("invalid argument!");
                    }
                    break;
                }
                case "mkdir": {
                    if (args.length == 0) {
                        System.out.println("You should provide an argument or more for 'mkdir' command!");
                    } else {
                        makeDirectory(args);
                    }
                    break;
                }
                case "rmdir": {
                    if (args.length == 1) {
                        removeDirectory(args);
                    }else{
                        System.out.println("One argument is provided with the 'rmdir' command!");
                    }
                    break;
                }
                case "touch":{
                    if (args.length == 1){
                        createFile(args);
                    }else{
                        System.out.println("One argument is provided with the 'touch' command!");
                    }
                }
                    break;
                case "ncp":{
                    if(args.length == 2 && !Objects.equals(args[0], "-r")){
                        copyFile(args);
                    }else if(args.length == 3 && Objects.equals(args[0], "-r")){
                        cpDirectory(args);
                    }else{
                        System.out.println("invalid argument!");
                    }
                    break;
                }
                case "rm":
                    if(args.length == 1){
                        removeFile(args);
                    }else{
                        System.out.println("One argument is provided with the 'rm' command");
                    }
                    break;
                case "wc":
                    if(args.length == 1){
                        wordCount(args);
                    }else{
                        System.out.println("One argument is provided with the 'wc' command");
                    }
                    break;
                case "exit":
                    exitCommand();
                    break;
                default:
                    System.out.println("Command not recognized.");
            }
        }

    }
    public void printCommandHistory() {
        for (int i = 0; i < commandHistory.size(); i++) {
            System.out.println((i + 1) + " " + commandHistory.get(i));
        }
    }

    public void echoCommand(String[] args) {
       for (String arg : args) {
                System.out.print(arg + " ");
       }
       System.out.println();
       commandHistory.add("echo");
    }

    public void pwdCommand(String[] args) {
        System.out.println(currentDirectory.toAbsolutePath());
        commandHistory.add("pwd");

    }

    public void changeDirectoryCommand(String[] args) {
        if (args.length == 0) {
            // change to the home directory
            currentDirectory = Paths.get(System.getProperty("user.home"));
            commandHistory.add("cd");
        } else if (args.length == 1) {
            String newDirectoryPath = args[0];
            if (newDirectoryPath.equals("..")) {
                //Change to the previous directory
                if (currentDirectory.getParent() != null) {
                    currentDirectory = currentDirectory.getParent();
                    commandHistory.add("cd ..");
                } else {
                    System.out.println("Already in the root directory.");
                }
            } else {
                //Change to the specified path
                Path newDirectory = currentDirectory.resolve(newDirectoryPath);

                if (Files.exists(newDirectory) && Files.isDirectory(newDirectory)) {
                    currentDirectory = newDirectory.toAbsolutePath();
                    commandHistory.add("cd "+ newDirectoryPath);
                } else {
                    System.out.println("Directory does not exist: " + newDirectory);
                }
            }
        } else {
            System.out.println("Usage: cd [<directory>|..]");
        }
    }

    public void listDirectoryCommand() {
        try {
            // Use Files.list to obtain a stream of entries (files and subdirectories) in the current directory.
            try (Stream<Path> entries = Files.list(currentDirectory)) {
                entries.forEach(entry -> System.out.println(entry.getFileName()));
                commandHistory.add("ls");
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
                commandHistory.add("ls -r");
            }
        } catch (IOException e) {
            System.err.println("Error listing directory: " + e.getMessage());
        }
    }

    public void makeDirectory(String[] args) {
        for (String arg : args) {
                Path newPath;
                if (Paths.get(arg).isAbsolute()) {
                    newPath = Paths.get(arg);
                } else {
                    newPath = currentDirectory.resolve(arg);
                }

                try {
                    Files.createDirectories(newPath);
                    commandHistory.add("mkdir");
                } catch (IOException e) {
                    System.err.println("Error making directory: " + e.getMessage());
                }
            }
    }

    public void removeDirectory(String[] args) {
        String path = currentDirectory.toAbsolutePath().toString();
        if (args[0].equals("*")) {
            File CD = new File(path);
            removeEmptyDirectories(CD);
            commandHistory.add("rmdir *");
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
        for (String arg : args) {
                Path newPath;
                if (Paths.get(arg).isAbsolute()) {
                    newPath = Paths.get(arg);
                } else {
                    newPath = currentDirectory.resolve(arg);
                }

                try {
                    Files.createFile(newPath);
                    commandHistory.add("touch");
                } catch (IOException e) {
                    System.err.println("Error making file: " + e.getMessage());
                }
        }
    }
    public void copyFile(String[] args) {
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
                commandHistory.add("cp");
            } catch (IOException e) {
                System.err.println("Error copying file: " + e.getMessage());
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
                commandHistory.add("cp -r");
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
    public void removeFile(String[] args) {
        if (args.length == 1) {
            try {
                Path fileToDelete = Paths.get(currentDirectory.toString(), args[0]);
                if (Files.exists(fileToDelete) && Files.isRegularFile(fileToDelete)) {
                    Files.delete(fileToDelete);
                    System.out.println("File deleted: " + fileToDelete);
                    commandHistory.add("rm");
                } else {
                    System.out.println("File does not exist or is not a regular file.");
                }
            } catch (IOException e) {
                System.out.println("Error deleting file: " + e.getMessage());
            }
        } else {
            System.out.println("Invalid arguments for rm.");
        }
    }
    public void wordCount(String[] args) {
        if (args.length == 1) {
            try {
                Path file = Paths.get(currentDirectory.toString(), args[0]);
                if (Files.exists(file) && Files.isRegularFile(file)) {
                    long LC = Files.lines(file).count();
                    long WC = Files.lines(file)
                            .flatMap(line -> Arrays.stream(line.split("\\s+")))
                            .filter(word -> !word.isEmpty())
                            .count();
                    long CC = Files.size(file);

                    System.out.println(LC + " " + WC + " " + CC + " " + file.getFileName());
                    commandHistory.add("wc");
                } else {
                    System.out.println("File does not exist or is not a regular file.");
                }
            } catch (IOException e) {
                System.out.println("Error counting : " + e.getMessage());
            }
        } else {
            System.out.println("Invalid arguments for wc.");
        }
    }


    public void exitCommand() {
        System.exit(0);
    }


    public static void main(String[] args) {
        Terminal terminal = new
                Terminal();
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