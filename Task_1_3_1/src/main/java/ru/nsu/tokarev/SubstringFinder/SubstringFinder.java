package ru.nsu.tokarev.SubstringFinder;

import java.io.BufferedReader;
import java.io.FileReader;
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

        int patternLength = pattern.length();
        int overlapSize = patternLength - 1;
        long globalOffset = 0;
        boolean firstChunk = true;

        int[] lps = calculateLPSArray(pattern);
        char[] buffer = new char[BUFFER_SIZE + overlapSize];

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName, encoding))) {
            int charsRead = reader.read(buffer, overlapSize, BUFFER_SIZE);

            while (charsRead != -1) {
                int searchLength = overlapSize + charsRead;

                List<Integer> matches = KMPSearch(buffer, searchLength, pattern, lps);

                for (Integer matchPos : matches) {
                    long absolutePos = globalOffset + matchPos - overlapSize;

                    if (firstChunk || matchPos >= overlapSize) {
                        res.add((int) absolutePos);
                    }
                }

                globalOffset += charsRead;

                if (overlapSize > 0 && charsRead > 0) {
                    System.arraycopy(buffer, searchLength - overlapSize, buffer, 0, overlapSize);
                }

                firstChunk = false;
                charsRead = reader.read(buffer, overlapSize, BUFFER_SIZE);
            }
        }

        return res;
    }

    public static List<Integer> find(String fileName, String pattern) throws IOException {
        return find(fileName, pattern, StandardCharsets.UTF_8);
    }

    private static List<Integer> KMPSearch(char[] text, int textLength, String pattern, int[] lps) {
        int textIndex = 0;
        int patternIndex = 0;
        List<Integer> res = new ArrayList<>();

        while (textIndex < textLength) {
            if (text[textIndex] == pattern.charAt(patternIndex)) {
                textIndex++;
                patternIndex++;
                if (patternIndex == pattern.length()) {
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

    private static int[] calculateLPSArray(String pattern) {
        int[] res = new int[pattern.length()];
        res[0] = 0;
        for (int i = 1; i < pattern.length(); i++) {
            int len = res[i - 1];
            while (len > 0 && pattern.charAt(i) != pattern.charAt(len)) {
                len = res[len - 1];
            }
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
            }
            res[i] = len;
        }
        return res;
    }
}
