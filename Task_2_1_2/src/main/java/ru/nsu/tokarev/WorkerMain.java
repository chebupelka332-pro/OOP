package ru.nsu.tokarev;

import ru.nsu.tokarev.FindCompositeNumber.CompositeWorker;


/**
 * Запуск: java -jar worker.jar [host] [port]
*/
public class WorkerMain {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port    = args.length > 1 ? Integer.parseInt(args[1]) : 12345;
        new CompositeWorker(host, port).run();
    }
}
