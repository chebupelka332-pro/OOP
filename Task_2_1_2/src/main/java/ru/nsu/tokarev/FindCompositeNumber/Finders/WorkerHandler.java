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
    private static final long WATCHDOG_TICK_MS = 20;

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

            processLoop(sock, out, in);

        } catch (IOException e) {
            // Не удалось установить связь с воркером
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            activeSockets.remove(socket);
        }
    }

    private void processLoop(Socket sock, DataOutputStream out, DataInputStream in)
            throws InterruptedException {
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
            } catch (IOException e) {
                taskQueue.offer(chunk);
                return;
            }

            AtomicBoolean awaitingResult = new AtomicBoolean(true);
            AtomicBoolean cancelTriggered = new AtomicBoolean(false);
            Thread watchdog = new Thread(
                    () -> runWatchdog(sock, out, awaitingResult, cancelTriggered),
                    "WorkerHandler-CancelWatchdog");
            watchdog.setDaemon(true);
            watchdog.start();

            byte tag = 0;
            boolean ioError = false;
            try {
                tag = in.readByte();
            } catch (IOException e) {
                ioError = true;
            } finally {
                awaitingResult.set(false);
                watchdog.interrupt();
                watchdog.join();
            }

            if (cancelTriggered.get()) {
                return;
            }

            if (ioError) {
                taskQueue.offer(chunk);
                return;
            }

            if (tag == Protocol.MSG_RESULT) {
                boolean result;
                try {
                    result = Protocol.readResult(in);
                } catch (IOException e) {
                    taskQueue.offer(chunk);
                    return;
                }
                remaining.decrementAndGet();
                if (result) {
                    foundComposite.set(true);
                }
            }
        }

        safeWriteCancel(out);
    }

    private void runWatchdog(Socket sock,
                             DataOutputStream out,
                             AtomicBoolean awaitingResult,
                             AtomicBoolean cancelTriggered) {
        while (awaitingResult.get()) {
            if (foundComposite.get() || externalCancelled.get()) {
                cancelTriggered.set(true);
                safeWriteCancel(out);
                try { sock.close(); } catch (IOException ignored) {}
                return;
            }
            try {
                Thread.sleep(WATCHDOG_TICK_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private static void safeWriteCancel(DataOutputStream out) {
        try { Protocol.writeCancel(out); } catch (IOException ignored) {}
    }

    private static void safeWriteNoMoreTasks(DataOutputStream out) {
        try { Protocol.writeNoMoreTasks(out); } catch (IOException ignored) {}
    }
}
