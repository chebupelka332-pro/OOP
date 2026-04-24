package ru.nsu.tokarev.core

class ProcessResult {
    int exitCode
    String output

    boolean isSuccess() { exitCode == 0 }
}
