package ru.nsu.tokarev;

import java.util.Scanner;

/**
 * Главный класс приложения, который служит точкой входа.
 */
public class Main {
    /**
     * Точка входа в программу.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Game game = new Game(scanner);
        game.start();
    }
}