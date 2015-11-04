package ru.ncedu.test.java;

import java.io.FilePermission;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.ReflectPermission;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestFailure;
import junit.framework.TestResult;

import junit.textui.TestRunner;
import org.apache.log4j.Logger;
import ru.ncedu.javabench.Constants;
import ru.ncedu.javabench.BenchTest;
import ru.ncedu.javabench.BenchTestSuite;
import ru.ncedu.dynamic.DynamicCompiler;
import ru.ncedu.test.model.JavaTask;
//import ru.ncedu.entity.task.EntityJavaTask;

public class DefaultJavaTest {
    
    private static final Logger LOG = Logger.getLogger(DefaultJavaTest.class);


    private ClassLoader loader;
    private Class task = null;
    private BenchTestSuite<?> tester = null;
    private TestRunner tr;
    private JUnit4TestAdapter adapter;
    private JavaTask javaTask;

    public DefaultJavaTest(
            JavaTask javaTask, Class task, ClassLoader loader) {
        this.loader = loader;
        this.task = task;
        this.javaTask = javaTask;
        if(LOG.isTraceEnabled()){
            LOG.trace("task=" + task);
            LOG.trace("javaTask=" + javaTask);
        }
    }

    public TestResult executeTesting() throws Exception{
        try {
            ensureImplExistance(Constants.IMPL) ;
            return executeTest();

        } catch (Throwable e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            System.out.println(sw.toString());
            throw new RuntimeException(e.toString(), e);
        }
//        return null;
    }
    
    public TestResult executeTestingEtalon() {
        try {
            ensureImplExistance("Etalon");
            return executeTest();

        } catch(IllegalStateException e){
            throw e;
        } 
        catch (Throwable e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            System.out.println(sw.toString());
        }
        return null;
    }

    private TestResult executeTest() throws ClassNotFoundException, InstantiationException, IllegalAccessException, PrivilegedActionException {
        tester = loader.loadClass(task.getName() + Constants.TEST).asSubclass(BenchTestSuite.class).newInstance();
        return execute();
    }

    public TestResult execute() throws PrivilegedActionException {
        Permissions ps = javaTask.getAllPermissions();
        
        ps.add(new FilePermission(DynamicCompiler.DEFAULT_SERVER_LOG, "read,write"));
        ps.add(new ReflectPermission("suppressAccessChecks", null));
        ps.add(new RuntimePermission("modifyThreadGroup", null));
        ps.add(new RuntimePermission("stopThread", null));
        ps.add(new RuntimePermission("getStackTrace", null));
        System.out.print("All Permissions: " + ps);
        
//        ps.add(new RuntimePermission("accessDeclaredMembers"));
        
        
        adapter = new JUnit4TestAdapter(tester.getClass());
        CodeSource cs = null;
        cs = tester.getClass().getProtectionDomain().getCodeSource();
        ProtectionDomain pd = new ProtectionDomain(cs, ps);
        AccessControlContext acc = new AccessControlContext(new ProtectionDomain[]{pd});
        StringWriter sw = new StringWriter();
        WriterOutputStream wst = new WriterOutputStream(sw);
        PrintStream pst = new PrintStream(wst);
        tr = new TestRunner(pst);
        PrintStream out = System.out;
        System.setOut(pst);
         TestResult result;
        try{
        result = (TestResult) AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
            public Object run() throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchMethodException, ClassNotFoundException {
                return tr.doRun(adapter);
            }
        }, acc);
        }
        finally{
            System.setOut(out);
        System.out.println(sw.toString());
        }
        
        
        return result;

    }

    private String ensureImplExistance(String suffix) {

        boolean success = true;
        String message = "";
        Object impl = null;
        if (true) {
            try {
                impl = this.loader.loadClass(task.getName() + suffix)/*.asSubclass(StudentActivity.class)*/.newInstance();
            } catch (ClassNotFoundException e) {
                success = false;
                message += "Class " + task.getName() + suffix + " not found.";
            } catch (InstantiationException e) {
                success = false;
                message += "Class " + task.getName() + suffix
                        + " cannot be instantiated because it is an interface or is an abstract class or the class has no nullary constructor.";
            } catch (IllegalAccessException e) {
                success = false;
                message += "Class " + task.getName() + suffix + " should be public and have default (public) constructor.";
            } catch (Exception e) {
                success = false;
                message += "Exception while instatiating task " + task.getName() + "; " + e.getMessage();
//                e.printStackTrace();
                throw new TestInstantiationException(message, e);
            }
        }

        if (success && (!task.isInstance(impl))) {
            success = false;
            message += message == null ? "" : "\n";
            message += "Class " + task.getName() + suffix + " should implement " + task.getName() + ".";
        }
        if(!success)
            throw new TestInstantiationException(message);
        return message;
    }


    public Map<String, BenchTest> getIpcTests2() {
        return tester.getIpcTests2();
    }
    
    
}
class WriterOutputStream extends OutputStream {  
   
    private final Writer writer;  
   
    public WriterOutputStream(Writer writer) {  
        this.writer = writer;  
    }  
   
    public void write(int b) throws IOException {  
        // It's tempting to use writer.write((char) b), but that may get the encoding wrong  
        // This is inefficient, but it works  
        write(new byte[] {(byte) b}, 0, 1);  
    }  
   
    public void write(byte b[], int off, int len) throws IOException {  
        writer.write(new String(b, off, len));  
    }  
   
    public void flush() throws IOException {  
        writer.flush();  
    }  
   
    public void close() throws IOException {  
        writer.close();  
    }  
}  