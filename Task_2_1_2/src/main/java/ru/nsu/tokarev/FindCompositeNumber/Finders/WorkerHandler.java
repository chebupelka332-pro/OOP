package ru.nsu.tokarev.FindCompositeNumber.Finders;

import ru.nsu.tokarev.FindCompositeNumber.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

class WorkerHandler implements Runnable {
    private final Socket socket;
    private final BlockingQueue<int[]> taskQueue;
    private final AtomicInteger remaining;
    private final AtomicBoolean foundComposite;
    private final AtomicBoolean externalCancelled;

    WorkerHandler(Socket socket,
                  BlockingQueue<int[]> taskQueue,
                  AtomicInteger remaining,
                  AtomicBoolean foundComposite,
                  AtomicBoolean externalCancelled) {
        this.socket = socket;
        this.taskQueue = taskQueue;
        this.remaining = remaining;
        this.foundComposite = foundComposite;
        this.externalCancelled = externalCancelled;
    }

    @Override
    public void run() {
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            while (!foundComposite.get() && !externalCancelled.get()) {
                int[] chunk = taskQueue.poll();
                if (chunk == null) {
                    if (remaining.get() == 0) {
                        // Все чанки обработаны
                        Protocol.writeNoMoreTasks(out);
                        return;
                    }
                    try { Thread.sleep(10); } catch (InterruptedException e) { return; }
                    continue;
                }

                try {
                    Protocol.writeTask(out, chunk);
                    byte tag = in.readByte();
                    if (tag == Protocol.MSG_RESULT) {
                        boolean result = Protocol.readResult(in);
                        remaining.decrementAndGet(); // результат получен - чанк закрыт
                        if (result) {
                            foundComposite.set(true);
                        }
                    }
                } catch (IOException e) {
                    // Воркер упал - чанк обратно в очередь, remaining не меняем
                    taskQueue.offer(chunk);
                    return;
                }
            }

            try {
                Protocol.writeCancel(out);
            } catch (IOException ignored) {}

        } catch (IOException e) {
            // Не удалось установить связь с воркером
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
}
