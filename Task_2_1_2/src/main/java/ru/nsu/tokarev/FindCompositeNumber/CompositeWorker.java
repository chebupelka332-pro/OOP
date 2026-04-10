package ru.nsu.tokarev.FindCompositeNumber;

import ru.nsu.tokarev.FindCompositeNumber.Finders.CompositeFinder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class CompositeWorker implements Runnable {
    private final String masterHost;
    private final int masterPort;
    private final CompositeFinder finder;

    public CompositeWorker(String masterHost, int masterPort, CompositeFinder finder) {
        this.masterHost = masterHost;
        this.masterPort = masterPort;
        this.finder = finder;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(masterHost, masterPort)) {
            DataInputStream in  = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            byte tag = in.readByte();
            if (tag != Protocol.MSG_TASK) {
                return;
            }

            int[] chunk = Protocol.readTask(in);

            AtomicBoolean cancelled = new AtomicBoolean(false);

            // Daemon thread listening for CANCEL from master
            Thread cancelListener = new Thread(() -> {
                try {
                    byte msg = in.readByte();
                    if (msg == Protocol.MSG_CANCEL) {
                        cancelled.set(true);
                    }
                } catch (IOException ignored) {
                    // Socket closed or master disconnected - treat as cancel
                    cancelled.set(true);
                }
            });
            cancelListener.setDaemon(true);
            cancelListener.start();

            boolean found = finder.containsComposite(chunk, cancelled);

            Protocol.writeResult(out, found);
        } catch (IOException e) {}
    }
}
