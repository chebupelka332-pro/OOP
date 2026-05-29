package ru.nsu.tokarev.FindCompositeNumber.Finders;

import ru.nsu.tokarev.FindCompositeNumber.Protocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DistributedFinder implements CompositeFinder {
    private final ServerSocket serverSocket;

    public DistributedFinder(int port, int acceptTimeoutMs) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.serverSocket.setSoTimeout(acceptTimeoutMs);
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    @Override
    public boolean containsComposite(int[] numbers, AtomicBoolean cancelled) {
        if (numbers.length == 0) {
            closeServerSocket();
            return false;
        }

        List<int[]> allChunks = buildChunks(numbers);
        BlockingQueue<int[]> taskQueue = new LinkedBlockingQueue<>(allChunks);
        AtomicInteger remaining = new AtomicInteger(allChunks.size());
        AtomicBoolean foundComposite = new AtomicBoolean(false);
        AtomicBoolean done = new AtomicBoolean(false);
        Set<Socket> activeSockets = ConcurrentHashMap.newKeySet();

        ExecutorService pool = Executors.newCachedThreadPool();

        Thread acceptor = new Thread(() -> {
            try {
                while (!done.get() && !cancelled.get() && !foundComposite.get()) {
                    Socket s;
                    try {
                        s = serverSocket.accept();
                    } catch (SocketTimeoutException e) {
                        if (remaining.get() == 0) {
                            done.set(true);
                        }
                        continue;
                    }

                    activeSockets.add(s);
                    try {
                        pool.submit(new WorkerHandler(s, taskQueue, remaining,
                                foundComposite, cancelled, activeSockets));
                    } catch (RejectedExecutionException ex) {
                        activeSockets.remove(s);
                        try { s.close(); } catch (IOException ignored) {}
                    }
                }
            } catch (IOException e) {
                done.set(true);
            } finally {
                closeServerSocket();
            }
        });
        acceptor.setDaemon(true);
        acceptor.start();

        while (!done.get() && !foundComposite.get() && !cancelled.get()) {
            if (remaining.get() == 0) {
                done.set(true);
            } else {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        done.set(true);
        closeServerSocket();
        pool.shutdown();
        try {
            if (!pool.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                closeAllSockets(activeSockets);
                pool.awaitTermination(2, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return foundComposite.get();
    }

    private List<int[]> buildChunks(int[] numbers) {
        List<int[]> chunks = new ArrayList<>();
        int len = numbers.length;
        for (int start = 0; start < len; start += Protocol.CHUNK_SIZE) {
            int end = Math.min(start + Protocol.CHUNK_SIZE, len);
            int[] chunk = new int[end - start];
            System.arraycopy(numbers, start, chunk, 0, end - start);
            chunks.add(chunk);
        }
        return chunks;
    }

    private void closeServerSocket() {
        try {
            if (!serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException ignored) {}
    }

    private static void closeAllSockets(Set<Socket> sockets) {
        for (Socket s : sockets) {
            try { s.close(); } catch (IOException ignored) {}
        }
    }
}
