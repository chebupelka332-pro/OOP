package ru.nsu.tokarev;

import ru.nsu.tokarev.Pizzeria.Pizzeria;

public class Main {
    public static void main(String[] args) throws Exception {
        Pizzeria pizzeria = new Pizzeria("pizzeria_config.json");
        pizzeria.start();
    }
}
