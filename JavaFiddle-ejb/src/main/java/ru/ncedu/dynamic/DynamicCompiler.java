package ru.ncedu.dynamic;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import javax.tools.*;
import javax.tools.JavaCompiler.CompilationTask;
import java.util.ArrayList;
import java.util.List;

public class DynamicCompiler {

    private static final Logger LOG = Logger.getLogger(DynamicCompiler.class);

    private JavaCompiler compiler;
    private DiagnosticCollector<JavaFileObject> collector;
    private JavaFileManager manager;

    private ClassLoader clazzLoader = null;
    private String message;

//    public static final String JUNIT_LOCATION;
//    public static final String FRWK_LOCATION;
//    public static final String DEFAULT_SERVER_LOG;

//    static {
//        Properties compilerProps = new Properties();
//        try {
//            compilerProps.load(DynamicCompiler.class.getClassLoader().getResourceAsStream("ru/ncedu/test/JavaBenchProperties.properties"));
//        } catch (IOException ex) {
//            LOG.error("", ex);
//        }
//
//        JUNIT_LOCATION = compilerProps.getProperty("JUNIT_LOCATION");
//        FRWK_LOCATION = compilerProps.getProperty("FRWK_LOCATION");
//        DEFAULT_SERVER_LOG = compilerProps.getProperty("DEFAULT_SERVER_LOG");
//        LOG.trace("JUNIT_LOCATION=" + JUNIT_LOCATION);
//        LOG.trace("FRWK_LOCATION=" + FRWK_LOCATION);
//        LOG.trace("DEFAULT_SERVER_LOG=" + DEFAULT_SERVER_LOG);  // We have to change server log in JavaBenchProperties
//    }

    public void init(ClassLoader parentLoader) throws Exception {

        compiler = ToolProvider.getSystemJavaCompiler();
        BasicConfigurator.configure();
        collector = new DiagnosticCollector<>();
        StandardJavaFileManager fman = compiler.getStandardFileManager(null, null, null);
//        fman.setLocation(StandardLocation.CLASS_PATH, Arrays.asList(new File(
//                JUNIT_LOCATION),
//                new File(FRWK_LOCATION)
//        ));
        manager = new DynamicClassFileManager(fman, parentLoader);

    }

    protected String compileToClass(List<SimpleJavaFileObject> sources)
            throws Exception {

        long nanos = System.nanoTime();

        ArrayList<SimpleJavaFileObject> sourceFiles = new ArrayList<>();
        ArrayList<SimpleJavaFileObject> otherFiles = new ArrayList<>();
        for (SimpleJavaFileObject source : sources) {

            if (source instanceof StringJavaFileObject) {
                sourceFiles.add(source);
            } else if (source instanceof ByteArrayResource) {
                otherFiles.add(source);
            }

        }
        ArrayList<String> javaOptions = new ArrayList<>();
//         javaOptions.add("-g");
//         javaOptions.add("-verbose");

//        javaOptions.add("-bootclasspath");
//        javaOptions.add("C:\\Program Files\\Java\\jdk1.7.0_79\\jre\\lib\\rt.jar");

        Iterable<? extends JavaFileObject> units = sourceFiles;
        CompilationTask task = compiler.getTask(null, manager, collector, javaOptions,
                null, units);
        boolean status = task.call();
        message = "Compilation: " + ((System.nanoTime() - nanos) / 1000 / 1000.) + " millisec\n";
        LOG.debug("CompilerDiagnostics:(" + status + "):");
        if (status) {
            clazzLoader = manager.getClassLoader(null);

            if(clazzLoader instanceof ByteArrayClassLoader)
                for(SimpleJavaFileObject obj: otherFiles)
                    ((ByteArrayClassLoader) clazzLoader).put(obj.getName(), (ByteArrayResource)obj);

            for (Diagnostic<?> d : collector.getDiagnostics()) {
                System.out.printf("%s\n", d.getMessage(null) + "; " + d.getCode());
                message += d.getMessage(null) + "\n";
            }
        } else {
            for (Diagnostic<?> d : collector.getDiagnostics()) {
                System.out.printf("%s\n", d.getMessage(null));
                message += d.getMessage(null) + "\n";
            }
        }
        return message;
    }

    public ClassLoader getClassLoader() {
        return clazzLoader;
    }

    public String getMessage() {
        return message;
    }

}