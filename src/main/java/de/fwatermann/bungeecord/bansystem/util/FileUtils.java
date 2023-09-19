package de.fwatermann.bungeecord.bansystem.util;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileUtils {

    /**
     * Write contents of an {@link InputStream} to a {@link File}.
     *
     * @param input InputStream
     * @param output File
     * @throws IOException if an I/O error occurs
     */
    public static void inputStreamToFile(InputStream input, File output) throws IOException {
        FileOutputStream fos = new FileOutputStream(output);
        int read;
        byte[] buffer = new byte[1024];
        while ((read = input.read(buffer)) != -1) {
            fos.write(buffer, 0, read);
        }
        fos.close();
        input.close();
    }

    /**
     * Create a {@link InputStreamReader} for a {@link File}.
     *
     * @param file File
     * @return InputStreamReader for the file
     * @throws IOException if an I/O error occurs
     */
    public static InputStreamReader createReader(File file) throws IOException {
        if (!file.exists()) file.createNewFile();
        return new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
    }

    public static String[] readLines(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(createReader(file))) {
            return reader.lines().toArray(String[]::new);
        }
    }
}
