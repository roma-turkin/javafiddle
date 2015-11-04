/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ncedu.test.model;

import java.security.Permissions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.tools.SimpleJavaFileObject;
import ru.ncedu.dynamic.ByteArrayResource;
import ru.ncedu.dynamic.StringJavaFileObject;

/**
 *
 * @author kharichkin
 */
public class JavaTask {
    
    private int mark;
    private List<SimpleJavaFileObject> userResources = new ArrayList<>();
    private List<SimpleJavaFileObject> testResources = new ArrayList<>();
    private Permissions permissions;
    private StringJavaFileObject interFace;
    private StringJavaFileObject taskChecker;
    private String implName;
    private String defaultPackagePreffix;
    
    
    public static SimpleJavaFileObject counstructResource(String className, String source, String packagePreffix){
        if(className.lastIndexOf(".java") < 0){
            return new ByteArrayResource(className, source.getBytes());
        } else {
            return new StringJavaFileObject(packagePreffix + className.substring(0, className.lastIndexOf(".java")), source);
        }
    }
    
    public SimpleJavaFileObject counstructResource(String className, String source){
            return counstructResource(className, source, defaultPackagePreffix);
    }
    
    public JavaTask(String defaultPackagePreffix){
        this.defaultPackagePreffix = defaultPackagePreffix;
    }
    
    public void addTestResource(SimpleJavaFileObject source){
        testResources.add(source);
    }
    
    public void addTestResource(String className, String source){
        addTestResource(counstructResource(className, source));
    }
    
    public void addUserResource(String className, String source){
        addUserResource(counstructResource(className, source));
    }
    
    public void addUserResource(SimpleJavaFileObject source){
        userResources.add(source);
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public void setPermissions(Permissions permissions) {
        this.permissions = permissions;
    }

    public void setInterface(String interfaceName, String interFace) {
        this.interFace = (StringJavaFileObject)counstructResource(interfaceName, interFace);
    }

    public void setTaskChecker(String name, String taskChecker) {
        this.taskChecker = (StringJavaFileObject)counstructResource(name, taskChecker);
    }    

    public String getDefaultPackagePreffix() {
        return defaultPackagePreffix;
    }

    public void setDefaultPackagePreffix(String defaultPackagePreffix) {
        this.defaultPackagePreffix = defaultPackagePreffix;
    }

    

    public String getImplName() {
        return implName;
    }

    public void setImplName(String implName) {
        this.implName = implName;
    }

    public int getMark() {
        return mark;
    }

    public  List<SimpleJavaFileObject> getUserResources() {
        return userResources;
    }

    public  List<SimpleJavaFileObject> getTestResources() {
        return testResources;
    }

    public Permissions getAllPermissions() {
        return permissions;
    }

    public StringJavaFileObject getInterface() {
        return interFace;
    }

    public StringJavaFileObject getTaskChecker() {
        return taskChecker;
    }
    
    

    
}
