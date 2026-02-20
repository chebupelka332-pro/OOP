package ru.nsu.tokarev.Pizzeria;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;


class ConfigLoader implements IConfigLoader {

    private final Gson gson = new Gson();

    @Override
    public PizzeriaConfig load(String resourcePath) throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (is == null) {
            throw new IOException("Config resource not found: " + resourcePath);
        }
        try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, PizzeriaConfig.class);
        }
    }
}
