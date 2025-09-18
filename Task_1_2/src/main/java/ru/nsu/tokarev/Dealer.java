package ru.nsu.tokarev;

/**
 * Представляет дилера (компьютерного оппонента).
 * Наследует всю основную логику от класса {@link Participant}.
 */
public class Dealer extends Participant {
    /**
     * Возвращает строковое представление руки дилера в начале раунда.
     * Показывает только первую карту, скрывая вторую.
     *
     * @return строка вида "[Первая карта, закрытая карта]".
     */
    public String getDealerFirstCard() {
        if (hand.isEmpty()) {
            return "[]";
        }
        return "[" + hand.get(0).toString() + ", <закрытая карта>]";
    }
}
