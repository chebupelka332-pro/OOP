package ru.nsu.tokarev.FindCompositeNumber;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class CompositeWorker implements Runnable {
    private static final int RECONNECT_DELAY_MS = 500;

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

                if (!handleSession(in, out)) {
                    return;
                }
            } catch (IOException e) {
                // Соединение прервано - попробуем переподключиться
            }
        }
    }

    /**
     * @return false если получен сигнал остановки (MSG_CANCEL/MSG_NO_MORE_TASKS),
     *         true если соединение оборвалось и стоит попробовать переподключиться.
     */
    private boolean handleSession(DataInputStream in, DataOutputStream out) throws IOException {
        while (true) {
            byte tag = in.readByte();
            if (tag == Protocol.MSG_TASK) {
                int[] chunk = Protocol.readTask(in);
                boolean found = computeWithCache(chunk);
                Protocol.writeResult(out, found);
            } else if (tag == Protocol.MSG_CANCEL || tag == Protocol.MSG_NO_MORE_TASKS) {
                return false;
            }
        }
    }

    private boolean computeWithCache(int[] chunk) {
        for (int n : chunk) {
            if (!cache.isPrime(n)) return true;
        }
        return false;
    }
}
