package ru.javafiddle.exectest;

import org.junit.Test;
import ru.javafiddle.execution.JFiddleCompiler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nk on 25.11.2015.
 */
public class Main {

    public static void main(String[] args) {
        // Some example
        JFiddleCompiler jfc = new JFiddleCompiler();
        String pack = "ru.ncedu.java.tasks.kudashov.";
        String path = "C:\\Users\\nk\\Desktop\\ru\\ncedu\\java\\tasks\\kudashov\\";
        File f = new File("C:\\Users\\nk\\Desktop\\ru\\ncedu\\java\\tasks\\kudashov\\");
        Map<String[], byte[]> files = new HashMap<String[], byte[]>();
        Map<String, byte[]> lib = new HashMap<String, byte[]>();

        File[] fres = f.listFiles();
        String pathLib = "C:\\Users\\nk\\Desktop\\ru\\ncedu\\commons-math3-3.5.jar";
        byte[] libr = null;
        try {
            libr = Files.readAllBytes(Paths.get("C:\\Users\\nk\\Desktop\\ru\\ncedu\\commons-math3-3.5.jar"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        lib.put(pathLib, libr);
        for (int i = 0; i < fres.length; i++) {
            byte[] encoded = null;
            try {
                encoded = Files.readAllBytes(Paths.get(path + fres[i].getName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            String[] packName = {pack, fres[i].getName()};

            files.put(packName, encoded);
        }

        jfc.init(null, lib);
        String mes = "";
        try {
            mes = jfc.compile(files);
            System.out.println(mes);
            jfc.run(files);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
