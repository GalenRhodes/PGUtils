package com.projectgalen.lib.utils;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.Charset;

@SuppressWarnings({ "UnusedReturnValue", "unused" })
public class IO {

    private static final PGProperties props = PGProperties.getSharedInstanceForNamedResource("pg_properties.properties", IO.class);

    private IO() {
    }

    public static void closeQuietly(@NotNull Closeable closeable) {
        try { closeable.close(); } catch(Exception ignore) { }
    }

    public static long copy(@NotNull File inputFile, @NotNull OutputStream outputStream, boolean closeOutputOnSuccess) throws IOException {
        return copy(new FileInputStream(inputFile), outputStream, closeOutputOnSuccess);
    }

    public static long copy(@NotNull File inputFile, @NotNull File outputFile) throws IOException {
        return copy(inputFile, new FileOutputStream(outputFile), true);
    }

    public static long copy(@NotNull InputStream inputStream, @NotNull OutputStream outputStream) throws IOException {
        return copy(inputStream, outputStream, true);
    }

    public static long copy(@NotNull InputStream inputStream, @NotNull File outputFile) throws IOException {
        return copy(inputStream, new FileOutputStream(outputFile), true);
    }

    public static long copy(@NotNull InputStream inputStream, @NotNull OutputStream outputStream, boolean closeOutputOnSuccess) throws IOException {
        try(inputStream) {
            long total = 0;

            try {
                byte[] buff = new byte[props.getIntProperty("default.read_buffer_size")];
                int    cc   = inputStream.read(buff);

                while(cc >= 0) {
                    outputStream.write(buff, 0, cc);
                    total += cc;
                    cc = inputStream.read(buff);
                }
            }
            finally {
                outputStream.flush();
            }

            if(closeOutputOnSuccess) closeQuietly(outputStream);
            return total;
        }
    }

    public static long copy(@NotNull Reader reader, @NotNull Writer writer, boolean closeOutputOnSuccess) throws IOException {
        try(reader) {
            long total = 0;

            try {
                char[] buff = new char[props.getIntProperty("default.read_buffer_size")];
                int    cc   = reader.read(buff);

                while(cc >= 0) {
                    writer.write(buff, 0, cc);
                    cc = reader.read(buff);
                }
            }
            finally {
                writer.flush();
            }

            if(closeOutputOnSuccess) closeQuietly(writer);
            return total;
        }
    }

    public static long copy(@NotNull Reader reader, @NotNull Writer writer) throws IOException {
        return copy(reader, writer, true);
    }

    public static byte[] readFile(@NotNull File file) throws IOException {
        return readFile(new FileInputStream(file));
    }

    public static @NotNull String readFile(@NotNull File file, @NotNull Charset cs) throws IOException {
        return readFile(new FileInputStream(file), cs);
    }

    public static @NotNull String readFile(@NotNull Reader reader) throws IOException {
        StringWriter sw = new StringWriter();
        copy(reader, sw, true);
        return sw.toString();
    }

    public static byte[] readFile(@NotNull InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        copy(inputStream, outputStream, true);
        return outputStream.toByteArray();
    }

    public static @NotNull String readFile(@NotNull InputStream inputStream, @NotNull Charset cs) throws IOException {
        return readFile(new InputStreamReader(inputStream, cs));
    }

    public static long translate(@NotNull InputStream inputStream,
                                 @NotNull Charset csInput,
                                 @NotNull OutputStream outputStream,
                                 @NotNull Charset csOutput,
                                 boolean closeOutputOnSuccess) throws IOException {
        return copy(new InputStreamReader(inputStream, csInput), new OutputStreamWriter(outputStream, csOutput), closeOutputOnSuccess);
    }

    public static long translate(@NotNull InputStream inputStream, @NotNull Charset csInput, @NotNull OutputStream outputStream, @NotNull Charset csOutput) throws IOException {
        return translate(inputStream, csInput, outputStream, csOutput, true);
    }

    public static long translate(@NotNull Reader reader, @NotNull OutputStream outputStream, @NotNull Charset csOutput, boolean closeOutputOnSuccess) throws IOException {
        return copy(reader, new OutputStreamWriter(outputStream, csOutput), closeOutputOnSuccess);
    }

    public static long translate(@NotNull Reader reader, @NotNull OutputStream outputStream, @NotNull Charset csOutput) throws IOException {
        return translate(reader, outputStream, csOutput, true);
    }
}
