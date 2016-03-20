package ru.ncedu.dynamic;

import java.io.*;
import java.net.*;
import javax.tools.*;

public class ByteArrayJavaFileObject extends SimpleJavaFileObject {

    private final ByteArrayOutputStream bos = new ByteArrayOutputStream();

    public ByteArrayJavaFileObject(String name, Kind kind) {
        super(
                URI.create("string:///" + name.replace('.', '/')
                + kind.extension), kind);
    }

    public byte[] getClassBytes() {
        return bos.toByteArray();
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return bos;
    }
}