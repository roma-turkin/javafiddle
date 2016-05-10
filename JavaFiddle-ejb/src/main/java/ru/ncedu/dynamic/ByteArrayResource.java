/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ncedu.dynamic;

import java.net.URI;
import javax.tools.SimpleJavaFileObject;
import javax.tools.JavaFileObject.Kind;

/**
 *
 * @author kharichkin
 */
public class ByteArrayResource  extends SimpleJavaFileObject{
    
    byte[] contents;
    String name;
    
    public ByteArrayResource(String name, byte[] contents){
        super(
                URI.create("string:///" + name.replace('.', '/')), Kind.OTHER);
        this.name = name;
        this.contents = contents;
    }
    
}
