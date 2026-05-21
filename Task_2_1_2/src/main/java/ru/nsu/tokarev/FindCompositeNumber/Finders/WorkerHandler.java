package ru.nsu.tokarev.FindCompositeNumber.Finders;

import ru.nsu.tokarev.FindCompositeNumber.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

class WorkerHandler implements Runnable {
    private static final long POLL_TIMEOUT_MS = 100;

    private final Socket socket;
    private final BlockingQueue<int[]> taskQueue;
    private final AtomicInteger remaining;
    private final AtomicBoolean foundComposite;
    private final AtomicBoolean externalCancelled;
    private final Set<Socket> activeSockets;

    WorkerHandler(Socket socket,
                  BlockingQueue<int[]> taskQueue,
                  AtomicInteger remaining,
                  AtomicBoolean foundComposite,
                  AtomicBoolean externalCancelled,
                  Set<Socket> activeSockets) {
        this.socket = socket;
        this.taskQueue = taskQueue;
        this.remaining = remaining;
        this.foundComposite = foundComposite;
        this.externalCancelled = externalCancelled;
        this.activeSockets = activeSockets;
    }

    @Override
    public void run() {
        try (Socket sock = socket;
             DataOutputStream out = new DataOutputStream(sock.getOutputStream());
             DataInputStream in = new DataInputStream(sock.getInputStream())) {

            processLoop(out, in);

        } catch (IOException e) {
            // Не удалось установить связь с воркером
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            activeSockets.remove(socket);
        }
    }

    private void processLoop(DataOutputStream out, DataInputStream in) throws InterruptedException {
        while (!foundComposite.get() && !externalCancelled.get()) {
            int[] chunk = taskQueue.poll(POLL_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            if (chunk == null) {
                if (remaining.get() == 0) {
                    safeWriteNoMoreTasks(out);
                    return;
                }
                continue;
            }

            try {
                Protocol.writeTask(out, chunk);
                byte tag = in.readByte();
                if (tag == Protocol.MSG_RESULT) {
                    boolean result = Protocol.readResult(in);
                    remaining.decrementAndGet();
                    if (result) {
                        foundComposite.set(true);
                    }
                }
            } catch (IOException e) {
                // Воркер упал или мастер закрыл сокет - чанк обратно в очередь
                taskQueue.offer(chunk);
                return;
            }
        }

        safeWriteCancel(out);
    }

    private static void safeWriteCancel(DataOutputStream out) {
        try { Protocol.writeCancel(out); } catch (IOException ignored) {}
    }

    private static void safeWriteNoMoreTasks(DataOutputStream out) {
        try { Protocol.writeNoMoreTasks(out); } catch (IOException ignored) {}
    }
}
