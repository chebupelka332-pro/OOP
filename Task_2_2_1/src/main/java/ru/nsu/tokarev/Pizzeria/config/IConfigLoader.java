package ru.nsu.tokarev.Pizzeria.config;

import ru.nsu.tokarev.Pizzeria.PizzeriaConfig;

import java.io.IOException;


public interface IConfigLoader {
    PizzeriaConfig load(String resourcePath) throws IOException;

    static IConfigLoader createDefault() {
        return new ConfigLoader();
    }
}
