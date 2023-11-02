import java.nio.file.*;//Contain Paths, Files which is helpful for filesystem manipulation.
import java.util.*;//contain DataStructures
import java.io.IOException;
import java.util.stream.Stream;


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
            case"mkdir":
                makeDirectory(args);
                break;
            case"rmkdir":
                removeDirectory(args);
                break;
            case"touch":
                createFile(args);
                break;
            case"cp":
                copyFile(args);
                break;
            case"cp -r":
                break;
            case"rm":
                removeFile(args);
                break;
            case"cat":
                concatenateFiles(args);
                break;
            case"wc":
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
    public void listDirectoryReverse() {}
    public void makeDirectory(String[] args) {}
    public void removeDirectory(String[] args){}
    public void createFile(String[] args) {}
    public void copyFile(String[] args) {}
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
