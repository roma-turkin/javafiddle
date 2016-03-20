package ru.javafiddle.execution.test;

import ru.javafiddle.core.ejb.CompileAndRunBean;

/**
 * Created by nk on 17.03.2016.
 */
public class BuilderTest {

    public static void main(String[] args) {
        CompileAndRunBean compileAndRunBean = new CompileAndRunBean();
        String message1 = compileAndRunBean.compile("Builder");
        System.out.println(compileAndRunBean.run("Builder"));


//        String message12 = compileAndRunBean.compile("123");
//        System.out.println(compileAndRunBean.run("123"));
    }
}
