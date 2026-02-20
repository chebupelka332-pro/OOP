package ru.nsu.tokarev.Pizzeria;
import java.io.IOException;


interface IConfigLoader {
    PizzeriaConfig load(String resourcePath) throws IOException;
}
