package ru.nsu.tokarev.core

import java.util.logging.Logger

class GradleRunner {
    private static final Logger log = Logger.getLogger(GradleRunner.class.name)
    static ProcessResult runTask(File taskDir, int timeoutSec, String... tasks) {
        if (!taskDir.exists()) {
            return new ProcessResult(exitCode: 1, output: "Директория задачи не найдена: ${taskDir.absolutePath}")
        }

        def gradlew = new File(taskDir, "gradlew")
        if (gradlew.exists()) gradlew.setExecutable(true)

        def cmd = gradlew.exists()
            ? [gradlew.absolutePath, "--no-daemon"] + tasks.toList()
            : ["gradle", "--no-daemon"] + tasks.toList()

        def pb = new ProcessBuilder(cmd)
            .directory(taskDir)
            .redirectErrorStream(true)

        def proc = pb.start()

        def output = new StringBuilder()
        def reader = new Thread({
            proc.inputStream.eachLine { output.append(it).append('\n') }
        })
        reader.start()

        def finished = proc.waitFor(timeoutSec, java.util.concurrent.TimeUnit.SECONDS)
        if (!finished) {
            proc.destroyForcibly()
            return new ProcessResult(exitCode: 1, output: "Превышено время ожидания (${timeoutSec}с):\n${output}")
        }
        reader.join(2000)
        return new ProcessResult(exitCode: proc.exitValue(), output: output.toString())
    }

    static ProcessResult build(File taskDir, int timeout) {
        runTask(taskDir, timeout, "build", "-x", "test")
    }

    static ProcessResult generateDocs(File taskDir, int timeout) {
        runTask(taskDir, timeout, "javadoc")
    }

    static ProcessResult checkStyle(File taskDir, int timeout) {
        runTask(taskDir, timeout, "checkstyleMain")
    }

    static ProcessResult runTests(File taskDir, int timeout) {
        runTask(taskDir, timeout, "test")
    }
}
