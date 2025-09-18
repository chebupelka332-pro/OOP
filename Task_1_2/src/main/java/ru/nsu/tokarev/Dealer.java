package ru.nsu.tokarev;

public class Dealer extends Participant {
    public String getDealerFirstCard() {
        if (hand.isEmpty()) return "[]";
        return "[" + hand.get(0).toString() + ", <закрытая карта>]";
    }
}
