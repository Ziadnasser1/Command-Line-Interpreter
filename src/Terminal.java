import java.nio.file.*;//Contain Paths, Files which is helpful for filesystem manipulation.
import java.util.*;//contain DataStructures


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
    public void echoCommand(String[] args) {}
    public void pwdCommand() {}
    public void changeDirectoryCommand(String[] args) {}
    public void listDirectoryCommand(String[] args) {}
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
