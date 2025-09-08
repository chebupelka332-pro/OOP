#!/bin/bash

rm -rf build docs app.jar
mkdir -p build docs

# Компиляция исходных файлов
javac -d build src/main/java/ru/nsu/tokarev/HeapSort.java src/main/java/ru/nsu/tokarev/Main.java

# Генерация Javadoc
javadoc -d docs src/main/java/ru/nsu/tokarev/HeapSort.java src/main/java/ru/nsu/tokarev/Main.java

# Создание JAR-файла
jar cfe app.jar ru.nsu.tokarev.Main -C build .

# Запуск приложения из JAR-файла
java -jar app.jar
