package ru.nsu.tokarev.FindCompositeNumber.Finders;

import ru.nsu.tokarev.FindCompositeNumber.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class DistributedFinder implements CompositeFinder {
    private final int workerCount;
    private final int timeoutMs;

    private ServerSocket serverSocket;

    public DistributedFinder(int port, int workerCount, int timeoutMs) throws IOException {
        this.workerCount = workerCount;
        this.timeoutMs = timeoutMs;
        this.serverSocket = new ServerSocket(port);
        this.serverSocket.setSoTimeout(timeoutMs);
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

        int actualWorkers = Math.min(workerCount, numbers.length);
        int[][] chunks = splitIntoChunks(numbers, actualWorkers);

        List<Socket> connections = new ArrayList<>();
        List<int[]> unprocessedChunks = new ArrayList<>();

        // Accept worker connections
        for (int i = 0; i < actualWorkers; i++) {
            try {
                Socket socket = serverSocket.accept();
                socket.setSoTimeout(timeoutMs);
                connections.add(socket);
            } catch (SocketTimeoutException e) {
                // Worker didn't connect in time - handle remaining chunks locally
                for (int j = connections.size(); j < actualWorkers; j++) {
                    unprocessedChunks.add(chunks[j]);
                }
                break;
            } catch (IOException e) {
                for (int j = connections.size(); j < actualWorkers; j++) {
                    unprocessedChunks.add(chunks[j]);
                }
                break;
            }
        }

        closeServerSocket();

        // Send tasks to connected workers
        List<Socket> activeConnections = new ArrayList<>();
        for (int i = 0; i < connections.size(); i++) {
            Socket socket = connections.get(i);
            try {
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                Protocol.writeTask(out, chunks[i]);
                activeConnections.add(socket);
            } catch (IOException e) {
                unprocessedChunks.add(chunks[i]);
                closeSocket(socket);
            }
        }

        AtomicBoolean found = new AtomicBoolean(false);

        // Collect results from workers
        List<Thread> resultThreads = new ArrayList<>();
        List<Socket> socketsToCancel = new ArrayList<>(activeConnections);

        for (Socket socket : activeConnections) {
            Thread t = new Thread(() -> {
                try {
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    byte tag = in.readByte();
                    if (tag == Protocol.MSG_RESULT) {
                        boolean workerFound = Protocol.readResult(in);
                        if (workerFound) {
                            found.set(true);
                            sendCancelToAll(socketsToCancel, socket);
                        }
                    }
                } catch (IOException e) {
                    // Worker failed — ignore, result not counted
                } finally {
                    closeSocket(socket);
                }
            });
            t.start();
            resultThreads.add(t);
        }

        // If externally cancelled - notify all workers and stop
        if (cancelled.get()) {
            sendCancelToAll(socketsToCancel, null);
            for (Thread t : resultThreads) {
                try { t.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
            return false;
        }

        // Process failed/unconnected chunks locally
        SingleThreadFinder local = new SingleThreadFinder();
        for (int[] chunk : unprocessedChunks) {
            if (found.get() || cancelled.get()) break;
            if (local.containsComposite(chunk, cancelled)) {
                found.set(true);
                sendCancelToAll(socketsToCancel, null);
            }
        }

        // Wait for all worker result threads
        for (Thread t : resultThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return found.get();
    }

    private int[][] splitIntoChunks(int[] numbers, int count) {
        int len = numbers.length;
        int chunkSize = (len + count - 1) / count;
        int[][] chunks = new int[count][];
        for (int i = 0; i < count; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, len);
            int[] chunk = new int[end - start];
            System.arraycopy(numbers, start, chunk, 0, end - start);
            chunks[i] = chunk;
        }
        return chunks;
    }

    private void sendCancelToAll(List<Socket> sockets, Socket except) {
        for (Socket s : sockets) {
            if (s == except) continue;
            try {
                DataOutputStream out = new DataOutputStream(s.getOutputStream());
                Protocol.writeCancel(out);
            } catch (IOException ignored) {}
        }
    }

    private void closeSocket(Socket socket) {
        try {
            socket.close();
        } catch (IOException ignored) {}
    }

    private void closeServerSocket() {
        try {
            if (!serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException ignored) {}
    }
}
