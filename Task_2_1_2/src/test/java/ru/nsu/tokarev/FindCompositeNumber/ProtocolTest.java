package ru.nsu.tokarev.FindCompositeNumber;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ProtocolTest {

    @Test
    void testWriteReadTask() throws IOException {
        int[] chunk = {2, 3, 5, 7, 11};
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Protocol.writeTask(new DataOutputStream(baos), chunk);

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(Protocol.MSG_TASK, in.readByte());
        int[] result = Protocol.readTask(in);
        assertArrayEquals(chunk, result);
    }

    @Test
    void testWriteReadTaskEmpty() throws IOException {
        int[] chunk = {};
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Protocol.writeTask(new DataOutputStream(baos), chunk);

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(Protocol.MSG_TASK, in.readByte());
        int[] result = Protocol.readTask(in);
        assertArrayEquals(chunk, result);
    }

    @Test
    void testWriteReadTaskSingleElement() throws IOException {
        int[] chunk = {42};
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Protocol.writeTask(new DataOutputStream(baos), chunk);

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(Protocol.MSG_TASK, in.readByte());
        int[] result = Protocol.readTask(in);
        assertArrayEquals(chunk, result);
    }

    @Test
    void testWriteReadResultTrue() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Protocol.writeResult(new DataOutputStream(baos), true);

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(Protocol.MSG_RESULT, in.readByte());
        assertTrue(Protocol.readResult(in));
    }

    @Test
    void testWriteReadResultFalse() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Protocol.writeResult(new DataOutputStream(baos), false);

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(Protocol.MSG_RESULT, in.readByte());
        assertFalse(Protocol.readResult(in));
    }

    @Test
    void testWriteCancel() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Protocol.writeCancel(new DataOutputStream(baos));

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(Protocol.MSG_CANCEL, in.readByte());
    }
}
