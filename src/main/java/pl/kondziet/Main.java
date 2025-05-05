package pl.kondziet;

import java.util.Scanner;

public class Main {

    private static final int PORT = 6379;

    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        System.out.println("Are you a server? y/n");
        var command = scanner.nextLine();

        if (command.equalsIgnoreCase("y")) {
            new Server().start(PORT);
        } else if (command.equalsIgnoreCase("n")) {
            new Client().start(PORT, scanner);
        }
    }
}