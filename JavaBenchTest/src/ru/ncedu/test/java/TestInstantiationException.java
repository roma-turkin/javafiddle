/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ncedu.test.java;

/**
 *
 * @author kharichkin
 */
public class TestInstantiationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TestInstantiationException(Throwable cause) {
        super(cause);
    }

    public TestInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public TestInstantiationException(String message) {
        super(message);
    }
    
}
