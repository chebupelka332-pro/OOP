package ru.nsu.tokarev.FindCompositeNumber;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class CompositeWorker implements Runnable {
    private static final int RECONNECT_DELAY_MS = 500;
    private static final long LISTENER_JOIN_TIMEOUT_MS = 1000;

    private final String masterHost;
    private final int masterPort;
    private final PrimeCache cache = new PrimeCache();

    public CompositeWorker(String masterHost, int masterPort) {
        this.masterHost = masterHost;
        this.masterPort = masterPort;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Socket socket;
            try {
                socket = new Socket(masterHost, masterPort);
            } catch (IOException e) {
                try {
                    Thread.sleep(RECONNECT_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
                continue;
            }

            try (Socket sock = socket;
                 DataInputStream in = new DataInputStream(sock.getInputStream());
                 DataOutputStream out = new DataOutputStream(sock.getOutputStream())) {

                if (!handleSession(sock, in, out)) {
                    return;
                }
            } catch (IOException e) {
                // Соединение прервано - попробуем переподключиться
            }
        }
    }
    
    private boolean handleSession(Socket socket, DataInputStream in, DataOutputStream out)
            throws IOException {
        BlockingQueue<int[]> taskQueue = new LinkedBlockingQueue<>();
        AtomicBoolean stopSignal = new AtomicBoolean(false);
        AtomicReference<IOException> readerError = new AtomicReference<>(null);
        Thread mainThread = Thread.currentThread();

        Thread listener = new Thread(
                () -> runListener(in, taskQueue, stopSignal, readerError, mainThread),
                "CompositeWorker-Listener");
        listener.setDaemon(true);
        listener.start();

        try {
            while (!stopSignal.get()) {
                int[] chunk;
                try {
                    chunk = taskQueue.take();
                } catch (InterruptedException e) {
                    break;
                }

                boolean found = computeWithCache(chunk, stopSignal);
                if (stopSignal.get()) break;

                Protocol.writeResult(out, found);
            }
        } finally {
            stopSignal.set(true);
            try { socket.close(); } catch (IOException ignored) {}
            try {
                listener.join(LISTENER_JOIN_TIMEOUT_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        IOException err = readerError.get();
        if (err != null) throw err;
        return false;
    }

    private void runListener(DataInputStream in,
                             BlockingQueue<int[]> taskQueue,
                             AtomicBoolean stopSignal,
                             AtomicReference<IOException> readerError,
                             Thread mainThread) {
        try {
            while (!stopSignal.get()) {
                byte tag = in.readByte();
                if (tag == Protocol.MSG_TASK) {
                    int[] chunk = Protocol.readTask(in);
                    taskQueue.offer(chunk);
                } else if (tag == Protocol.MSG_CANCEL || tag == Protocol.MSG_NO_MORE_TASKS) {
                    stopSignal.set(true);
                    mainThread.interrupt();
                    return;
                }
            }
        } catch (IOException e) {
            if (!stopSignal.get()) {
                readerError.set(e);
            }
            stopSignal.set(true);
            mainThread.interrupt();
        }
    }

    private boolean computeWithCache(int[] chunk, AtomicBoolean stopSignal) {
        for (int n : chunk) {
            if (stopSignal.get()) return false;
            if (!cache.isPrime(n)) return true;
        }
        return false;
    }
}
