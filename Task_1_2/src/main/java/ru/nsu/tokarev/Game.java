package ru.nsu.tokarev;

import java.util.Scanner;

/**
 * Основной класс, управляющий всей логикой игры "Блэкджек".
 * Он отвечает за инициализацию, проведение раундов, взаимодействие
 * с пользователем и определение результатов игры.
 */
public class Game {
    private final Deck deck;
    private final Player player;
    private final Dealer dealer;
    private final Scanner scanner;
    private int playerScore = 0;
    private int dealerScore = 0;
    private int round = 1;

    /**
     * Создает новый экземпляр игры.
     * Инициализирует колоду, игрока, дилера и сканер для ввода из консоли.
     */
    public Game(Scanner scanner) {
        deck = new Deck();
        player = new Player();
        dealer = new Dealer();
        this.scanner = scanner;
    }

    /**
     * Запускает основной игровой цикл.
     * Метод приветствует игрока и продолжает запускать раунды до тех пор,
     * пока игрок не решит завершить игру.
     */
    public void start() {
        System.out.println("Добро пожаловать в Блэкджек!");

        while (true) {
            playRound();
            System.out.println("\nХотите сыграть еще один раунд? (1 - да, 0 - нет)");
            if (scanner.nextInt() != 1) {
                break;
            }
        }
        System.out.println("Спасибо за игру!");
    }

    /**
     * Проводит один полный раунд игры.
     * Включает в себя сброс рук, перемешивание колоды, раздачу карт,
     * ходы игрока и дилера, а также определение победителя раунда.
     */
    private void playRound() {
        System.out.println("\nРаунд " + round++);

        player.resetHand();
        dealer.resetHand();
        deck.shuffle();

        player.addCard(deck.deal());
        dealer.addCard(deck.deal());
        player.addCard(deck.deal());
        dealer.addCard(deck.deal());

        System.out.println("Дилер раздал карты");
        showHands(false);

        if (player.getHand().isBlackjack()) {
            System.out.println("Блэкджек! Вы выиграли раунд!");
            playerScore++;
            showScore();
            return;
        }
        if (dealer.getHand().isBlackjack()) {
            System.out.println("У дилера блэкджек! Вы проиграли раунд.");
            dealerScore++;
            showScore();
            return;
        }

        playerTurn();

        if (!player.getHand().isBusted()) {
            dealerTurn();
        }

        determineWinner();
        showScore();
    }

    /**
     * Обрабатывает ход игрока.
     * В цикле запрашивает у пользователя действие (взять карту или остановиться),
     * пока игрок не остановится или у него не будет перебор.
     */
    private void playerTurn() {
        System.out.println("\nВаш ход");
        while (true) {
            System.out.println("Введите '1', чтобы взять карту, и '0', чтобы остановиться");
            int choice = scanner.nextInt();
            if (choice == 1) {
                Card newCard = deck.deal();
                player.addCard(newCard);
                System.out.println("Вы открыли карту " + newCard);
                showHands(false);
                if (player.getHand().isBusted()) {
                    System.out.println("Перебор! Вы проиграли раунд.");
                    dealerScore++;
                    break;
                }
            } else if (choice == 0) {
                break;
            }
        }
    }

    /**
     * Обрабатывает ход дилера.
     * Дилер открывает свою вторую карту и добирает карты до тех пор,
     * пока сумма очков в его руке не достигнет 17 или больше.
     */
    private void dealerTurn() {
        System.out.println("\nХод дилера");
        System.out.println("Дилер открывает закрытую карту.");
        showHands(true);

        // Дилер берет карты, пока сумма не станет 17 или больше
        while (dealer.getHandValue() < 17) {
            Card newCard = deck.deal();
            dealer.addCard(newCard);
            System.out.println("Дилер открывает карту " + newCard);
            showHands(true);
        }
    }

    /**
     * Определяет победителя раунда на основе финальных очков.
     * Сравнивает очки игрока и дилера, учитывая возможные переборы.
     */
    private void determineWinner() {
        if (player.getHand().isBusted()) {
            return;
        }
        if (dealer.getHand().isBusted()) {
            System.out.println("У дилера перебор! Вы выиграли раунд!");
            playerScore++;
        } else if (player.getHandValue() > dealer.getHandValue()) {
            System.out.println("Вы выиграли раунд!");
            playerScore++;
        } else if (player.getHandValue() < dealer.getHandValue()) {
            System.out.println("Вы проиграли раунд.");
            dealerScore++;
        } else {
            System.out.println("Ничья!");
        }
    }

    /**
     * Отображает текущее состояние карт на столе.
     *
     * @param revealDealerCard если {@code true}, показывает все карты дилера;
     * если {@code false}, показывает только первую карту дилера.
     */
    private void showHands(boolean revealDealerCard) {
        System.out.println("Ваши карты: " + player.getHand() + " => " + player.getHandValue());
        if (revealDealerCard) {
            System.out.println("Карты дилера: " + dealer.getHand() + " => " + dealer.getHandValue());
        } else {
            System.out.println("Карты дилера: " + dealer.getDealerFirstCard());
        }
    }

    /**
     * Выводит в консоль текущий счет игры.
     */
    private void showScore() {
        if (playerScore > dealerScore) {
            System.out.printf("Счет %d:%d в вашу пользу.%n", playerScore, dealerScore);
        } else if (playerScore < dealerScore) {
            System.out.printf("Счет %d:%d в пользу диллера.%n", playerScore, dealerScore);
        } else {
            System.out.printf("Счет %d:%d - ничья.%n", playerScore, dealerScore);
        }

    }
}