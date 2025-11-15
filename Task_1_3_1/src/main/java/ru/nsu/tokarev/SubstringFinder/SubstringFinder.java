package ru.nsu.tokarev.SubstringFinder;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;


public class SubstringFinder {
    private static final int BUFFER_SIZE = 1024 * 1024; // 1MB

    private SubstringFinder() {
    }

    public static List<Integer> find(String fileName, String pattern, Charset encoding) throws IOException {
        List<Integer> res = new ArrayList<>();

        if (pattern == null || pattern.isEmpty()) {
            return res;
        }

        byte[] patternBytes = pattern.getBytes(encoding);
        int patternLength = patternBytes.length;
        int overlapSize = patternLength - 1;
        long globalOffset = 0;
        boolean firstChunk = true;

        int[] lps = calculateLPSArray(patternBytes);
        byte[] buffer = new byte[BUFFER_SIZE + overlapSize];

        try (InputStream inputStream = new FileInputStream(fileName)) {
            int bytesRead = inputStream.read(buffer, overlapSize, BUFFER_SIZE);

            while (bytesRead != -1) {
                int searchLength = overlapSize + bytesRead;

                List<Integer> matches = KMPSearch(buffer, searchLength, patternBytes, lps);

                for (Integer matchPos : matches) {
                    long absolutePos = globalOffset + matchPos - overlapSize;

                    if (firstChunk || matchPos >= overlapSize) {
                        res.add((int) absolutePos);
                    }
                }

                globalOffset += bytesRead;

                if (overlapSize > 0 && bytesRead > 0) {
                    System.arraycopy(buffer, searchLength - overlapSize, buffer, 0, overlapSize);
                }

                firstChunk = false;
                bytesRead = inputStream.read(buffer, overlapSize, BUFFER_SIZE);
            }
        }

        return res;
    }

    public static List<Integer> find(String fileName, String pattern) throws IOException {
        return find(fileName, pattern, StandardCharsets.UTF_8);
    }

    private static List<Integer> KMPSearch(byte[] text, int textLength, byte[] pattern, int[] lps) {
        int textIndex = 0;
        int patternIndex = 0;
        List<Integer> res = new ArrayList<>();

        while (textIndex < textLength) {
            if (text[textIndex] == pattern[patternIndex]) {
                textIndex++;
                patternIndex++;
                if (patternIndex == pattern.length) {
                    res.add(textIndex - patternIndex);
                    patternIndex = lps[patternIndex - 1];
                }
            } else {
                if (patternIndex != 0) {
                    patternIndex = lps[patternIndex - 1];
                } else {
                    textIndex++;
                }
            }
        }

        return res;
    }

    private static int[] calculateLPSArray(byte[] pattern) {
        int[] res = new int[pattern.length];
        res[0] = 0;
        for (int i = 1; i < pattern.length; i++) {
            int len = res[i - 1];
            while (len > 0 && pattern[i] != pattern[len]) {
                len = res[len - 1];
            }
            if (pattern[i] == pattern[len]) {
                len++;
            }
            res[i] = len;
        }
        return res;
    }
}
