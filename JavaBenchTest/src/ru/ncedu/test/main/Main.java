/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ncedu.test.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Permissions;
import ru.ncedu.test.java.JavaTester;
import ru.ncedu.test.model.JavaResult;
import ru.ncedu.test.model.JavaTask;

/**
 *
 * @author kharichkin
 */
public class Main {

    public static final String DIRECTORY_PREFIX = "D:\\IPCCenter\\Bench_old\\JavaEngine\\";
    public static final String USER_RESOURCES = DIRECTORY_PREFIX + "user";
    public static final String TASK_RESOURCES = DIRECTORY_PREFIX + "task";
    public static final String TEST_RESOURCES = DIRECTORY_PREFIX + "test";
    public static final String DEFAULT_PACKAGE_PREFFIX = "ru.ncedu.java.tasks.";

    public static void main(String... args) throws IOException, Exception {

        JavaTask javaTask = new JavaTask(DEFAULT_PACKAGE_PREFFIX);

        String userInput = "";

        File user = new File(USER_RESOURCES);
        for (File userFile : user.listFiles()) {
              String className = userFile.getName();
            
            if (className.contains("Impl")) {
                
                userInput = readFile(userFile, Charset.defaultCharset().displayName());
                javaTask.setImplName(className);                
                System.out.println("Adding Impl file: " + className);
            }  else
            javaTask.addUserResource(userFile.getName(), readFile(userFile, Charset.defaultCharset().displayName()));
        }
        
        File test = new File(TEST_RESOURCES);
        for (File testFile : test.listFiles()) {
            String className = testFile.getName();
            System.out.println("Adding test file: " + className);
            javaTask.addTestResource(className, readFile(testFile, Charset.defaultCharset().displayName()));
        }

        File task = new File(TASK_RESOURCES);
        for (File taskFile : task.listFiles()) {
            String className = taskFile.getName();
            System.out.println("Adding task file: " + className);
            if (className.contains("Test")) {
                javaTask.setTaskChecker(className, readFile(taskFile, Charset.defaultCharset().displayName()));
            } else if (className.contains("Etalon")) {

            } else  {
                javaTask.setInterface(className, readFile(taskFile, Charset.defaultCharset().displayName()));
            }

            javaTask.setMark(100);
            javaTask.setPermissions(new Permissions());
        }

       JavaResult result = JavaTester.checkTask(userInput, javaTask);
       
       System.out.println(result.getFullResults());

    }

    static String readFile(File file, String charset)
            throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] buffer = new byte[fileInputStream.available()];
        int length = fileInputStream.read(buffer);
        fileInputStream.close();
        return new String(buffer, 0, length, charset);
    }

}
