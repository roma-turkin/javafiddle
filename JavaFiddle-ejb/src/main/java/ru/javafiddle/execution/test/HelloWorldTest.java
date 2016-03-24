package ru.javafiddle.execution.test;


import org.junit.Assert;
import org.junit.Test;
import ru.javafiddle.core.ejb.CompileAndRunBean;

/**
 * Created by nk on 17.03.2016.
 */
public class HelloWorldTest extends Assert {

    @Test
    public void CompileAndRunTest() {
        CompileAndRunBean compileAndRunBean = null;
        try {
            compileAndRunBean = new CompileAndRunBean();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {
            System.out.println(compileAndRunBean.compile("Hello"));
            System.out.println(compileAndRunBean.run("Hello"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestNullPointerException1() { //caused by missed compilation
        try {
            CompileAndRunBean compileAndRunBean = new CompileAndRunBean();
            compileAndRunBean.run("Hello");
            fail("NullPointerException was expected");
        } catch (NullPointerException ex) {
        }
    }

    @Test
    public void TestNullPointerException2() { //caused by wrong hash
        try {
            CompileAndRunBean compileAndRunBean = new CompileAndRunBean();
            System.out.println(compileAndRunBean.compile("Hello2"));
            System.out.println(compileAndRunBean.run("Hello2"));
            fail("NullPointerException was expected");
        } catch (NullPointerException ex) {
        }
    }
}