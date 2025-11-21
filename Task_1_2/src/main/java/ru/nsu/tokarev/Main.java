package ru.nsu.tokarev;

import ru.nsu.tokarev.game.Game;
import ru.nsu.tokarev.localization.EnglishGameMessages;
import ru.nsu.tokarev.localization.GameMessages;
import ru.nsu.tokarev.ui.ConsoleGameUI;
import ru.nsu.tokarev.ui.GameUI;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        GameMessages messages = new EnglishGameMessages();
        GameUI ui = new ConsoleGameUI(scanner, messages);
        Game game = new Game(ui, messages);
        game.start();

        scanner.close();
    }
}