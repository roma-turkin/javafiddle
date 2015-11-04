/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ncedu.test.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.tools.SimpleJavaFileObject;
import junit.framework.TestFailure;
import junit.framework.TestResult;
import org.apache.log4j.Logger;
import ru.ncedu.dynamic.ByteArrayClassLoader;
import ru.ncedu.javabench.Constants;
import ru.ncedu.dynamic.CheckedByteArrayClassLoader;
import ru.ncedu.dynamic.DynamicCompiler;
import ru.ncedu.dynamic.StringJavaFileObject;
import ru.ncedu.javabench.BenchTest;
import ru.ncedu.test.model.JavaResult;
import ru.ncedu.test.model.JavaSubResult;
import ru.ncedu.test.model.JavaTask;

/**
 *
 * @author kharichkin
 */
public class JavaTester {

    private static final Logger LOG = Logger.getLogger(JavaTester.class);

    public static JavaResult checkTask(String userInput, 
            JavaTask javaTask) throws Exception {

        JavaResult result = new JavaResult();
        
        tryCompileAndRun(result, userInput, javaTask);

//        selectedResults = Arrays.asList(damBean.getQueryResults("getResults", new Object[]{getUser(), selectedTask}).toArray(new EntityResult[]{}));
//        LOG.debug("Check Java Task (" + damBean.getCurrentUser().getName() + "):" + userInput);
        /*
        try {
            tryCompileAndRun(result, userInput, taskReferenceHandler, parent);
        } catch (GuestAccessException ex) {
            LOG.debug("Guest access", ex);
        } catch (Exception ex) {
            LOG.error("Check Task failed", ex);
            if (ex.getCause() instanceof TestInstantiationException) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error during you class instantiation. Please make sure that your code meets the common requirements.", ex.getMessage()));
            } else {
                LOG.error("Error occured during the activity. ", ex);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error occured during the activity. Please contact administrator...", ex.getMessage()));
            }
            return result;
        }
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(result.getSeverity(), result.getResultMessage(), result.getFullResults()));*/
        return result;
    }

    public static void tryCompileAndRun(JavaResult result, String userInput, JavaTask javaTask) throws Exception {
        String javaResults = "";
        DynamicCompiler dc = tryCompileAll(javaTask);

        if (dc.getClassLoader() == null) {
            javaResults = javaResults + "COMPILATION FAILED!\n\n" + dc.getMessage();
            result.setResultMessage("Error");
            result.setSeverity(JavaResult.Severity.ERROR);
            result.setFullResults(javaResults);
            return;
        }

        dc = tryCompileSources(userInput, javaTask, dc.getClassLoader());

        if (dc.getClassLoader() == null) {
            javaResults = javaResults + "COMPILATION with Test resources FAILED!\n\n" + dc.getMessage();
            result.setResultMessage("Error");
            result.setSeverity(JavaResult.Severity.ERROR);
            result.setFullResults(javaResults);
            return;
        }

        javaResults = javaResults + dc.getMessage();

        ((CheckedByteArrayClassLoader) dc.getClassLoader()).cleanUp();

        DefaultJavaTest javaTest;
        TestResult tr = null;

        try {
            Class task = dc.getClassLoader().loadClass(javaTask.getInterface().getSimpleName());
            
            if(LOG.isTraceEnabled()){
                LOG.trace("InterfaceName="+javaTask.getInterface().getSimpleName());
                LOG.trace("ClassLoader="+dc.getClassLoader());
                LOG.trace("Classes="+((ByteArrayClassLoader)dc.getClassLoader()).getLoadedClasses());
                LOG.trace("Parent="+dc.getClassLoader().getParent());
                LOG.trace("Classes="+((ByteArrayClassLoader)dc.getClassLoader().getParent()).getLoadedClasses());
            }
            
            javaTest = new DefaultJavaTest(javaTask, task, dc.getClassLoader());
            tr = javaTest.executeTesting();
        } catch (Exception ex) {
            javaResults = javaResults + "\n" + ex.getMessage();
            throw ex;
        }

        List errors = Collections.list(tr.errors());
        List<String> errorMeths = new ArrayList<>();
        Map<String, String> errorDescriptions = new HashMap<>();
        Map<String, String> errorDetails = new HashMap<>();
        for (Object o : errors) {
            //System.out.println("Error:" + ((TestFailure) o).failedTest() + "; " + ((TestFailure) o).toString());
            String str = ((TestFailure) o).failedTest().toString();
            if (str.indexOf("(") > 0) {
                str = str.substring(0, str.indexOf("("));
            }
            errorMeths.add(str);
            errorDetails.put(str, ((TestFailure) o).thrownException().toString());
            errorDescriptions.put(str, ((TestFailure) o).trace());

        }
//        EntityJavaResult results = new EntityJavaResult();

//        EntityUser currentUser = damBean.getCurrentUser();
//        results.setParent(parent);
//        results.setName("[" + currentUser.getName() + "][" + taskReferenceHandler.getName() + "] Java Result");
//        try {
//            damBean.persistTx(results);
//        } catch (Exception ex) {
//            if (!(ex.getCause() instanceof GuestAccessException)) {
//                throw ex;
//            }
//        }
//        EntityUser user = currentUser;
        int maxMark = 0;
        int realMark = 0;

        for (Map.Entry<String, BenchTest> ev : javaTest.getIpcTests2().entrySet()) {
            JavaSubResult newResult = new JavaSubResult();
            result.addSubResult(newResult);
            newResult.setTestName(ev.getValue().testName());
            newResult.setMark(ev.getValue().mark() * (errorMeths.contains(ev.getKey()) ? 0 : 1));
            newResult.setPassed(!errorMeths.contains(ev.getKey()));
            newResult.setName("[" + ev.getValue().testName() + "]");
            if (!newResult.isPassed()) {
                newResult.setErrorMessage(ev.getValue().failedMessage());
                newResult.setErrorDescription(errorDescriptions.get(ev.getKey()));
                newResult.setErrorDetails(errorDetails.get(ev.getKey()));
            }
//            try {
//                damBean.persistTx(newResult);
//            } catch (Exception ex) {
//                if (!(ex.getCause() instanceof GuestAccessException)) {
//                    throw ex;
//                }
//            }
//            damBean.refresh(newResult);

            realMark += ev.getValue().mark() * (errorMeths.contains(ev.getKey()) ? 0 : 1);
            maxMark += ev.getValue().mark();
            javaResults = javaResults + "Test " + ev.getValue().testName() + "; Mark=" + ev.getValue().mark() * (errorMeths.contains(ev.getKey()) ? 0 : 1) + "; Status: "
                    + (newResult.isPassed() ? "Passed" : "Failed") + "\n";
        }
//        damBean.refresh(defaultContainer);
        int mark = Math.round(javaTask.getMark() * realMark * 1.0f / maxMark);
//        if (results.getId() != null) {
//            results = (EntityJavaResult) damBean.refreshNonTx(results);
//        }
        result.setMark(mark);
//        results.setTaskReference(taskReferenceHandler);
//        results.setUser(user);
        result.setUserInput(userInput);
//        try {
//            damBean.mergeTx(results);
//        } catch (Exception ex) {
//            if (!(ex.getCause() instanceof GuestAccessException)) {
//                throw ex;
//            }
//        }
        javaResults = javaResults + "\n";
        LOG.debug("JavaResults: " + javaResults);

        result.setFullResults(javaResults);

        if (result.getMark() < javaTask.getMark()) {
            result.setResultMessage("Checked, but some tests failed. Please see Results tab for details.");
            result.setSeverity(JavaResult.Severity.WARN);
        } else {
            result.setResultMessage("Passed");
            result.setSeverity(JavaResult.Severity.INFO);
        }
    }

    private static DynamicCompiler tryCompileSources(String userInput, JavaTask task, ClassLoader parentLoader) throws Exception {
        DynamicCompiler dc = new DynamicCompiler();
        dc.init(parentLoader);

        List<SimpleJavaFileObject> sources = new ArrayList<>();
        sources.add(task.getInterface());
        sources.add(task.counstructResource(task.getImplName(), userInput));
        sources.addAll(task.getUserResources());

        dc.compileToClass(sources);

        return dc;
    }

    private static DynamicCompiler tryCompileAll(JavaTask task) throws Exception {
        DynamicCompiler dc = new DynamicCompiler();
        dc.init(null);

        List<SimpleJavaFileObject> sources = new ArrayList<>();
        sources.add(task.getInterface());
//        sources.put(task.getInterfaceName() + Constants.IMPL, userInput);
        sources.add(task.getTaskChecker());
        sources.addAll(task.getUserResources());
        sources.addAll(task.getTestResources());

        dc.compileToClass(sources);

        return dc;
    }

}
