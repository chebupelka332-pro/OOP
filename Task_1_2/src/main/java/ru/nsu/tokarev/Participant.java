package ru.nsu.tokarev;

/**
 * Абстрактный базовый класс для всех участников игры (Игрока и Дилера).
 * Содержит общую логику, такую как наличие руки и возможность брать карты.
 */
public class Participant {
    protected Hand hand;

    /**
     * Рука (набор карт) участника.
     */
    public Participant() {
        this.hand = new Hand();
    }

    /**
     * Создает участника с новой пустой рукой.
     */
    public void addCard(Card card) {
        hand.addCard(card);
    }

    /**
     * Гетер hand.
     *
     * @return возвращает hand.
     */
    public Hand getHand() {
        return hand;
    }

    /**
     * Подсчет суммы достоинств карт.
     *
     * @return возвращает сумму карт.
     */
    public int getHandValue() {
        return hand.getValue();
    }

    /**
     * Производит новую выдачу карт.
     */
    public void resetHand() {
        this.hand = new Hand();
    }
}