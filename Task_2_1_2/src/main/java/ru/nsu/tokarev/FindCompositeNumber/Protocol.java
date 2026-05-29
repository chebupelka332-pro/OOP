package ru.nsu.tokarev.FindCompositeNumber;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class Protocol {
    public static final byte MSG_TASK          = 0x01;
    public static final byte MSG_RESULT        = 0x02;
    public static final byte MSG_CANCEL        = 0x03;
    public static final byte MSG_NO_MORE_TASKS = 0x04;

    public static final int CHUNK_SIZE = 128;

    private Protocol() {}

    public static void writeTask(DataOutputStream out, int[] chunk) throws IOException {
        out.writeByte(MSG_TASK);
        out.writeInt(chunk.length);
        for (int n : chunk) {
            out.writeInt(n);
        }
        out.flush();
    }

    public static int[] readTask(DataInputStream in) throws IOException {
        int length = in.readInt();
        int[] chunk = new int[length];
        for (int i = 0; i < length; i++) {
            chunk[i] = in.readInt();
        }
        return chunk;
    }

    public static void writeResult(DataOutputStream out, boolean found) throws IOException {
        out.writeByte(MSG_RESULT);
        out.writeBoolean(found);
        out.flush();
    }

    public static boolean readResult(DataInputStream in) throws IOException {
        return in.readBoolean();
    }

    public static void writeCancel(DataOutputStream out) throws IOException {
        out.writeByte(MSG_CANCEL);
        out.flush();
    }

    public static void writeNoMoreTasks(DataOutputStream out) throws IOException {
        out.writeByte(MSG_NO_MORE_TASKS);
        out.flush();
    }
}
