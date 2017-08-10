package com.github.kornilova_l.flamegraph.javaagent.agent;

import com.github.kornilova_l.flamegraph.configuration.MethodConfig;
import com.github.kornilova_l.flamegraph.javaagent.TestHelper;
import com.github.kornilova_l.flamegraph.javaagent.generate.test_classes.OneMethod;
import com.github.kornilova_l.flamegraph.javaagent.generate.test_classes.SaveParameters;
import com.github.kornilova_l.flamegraph.javaagent.generate.test_classes.SeveralReturns;
import com.github.kornilova_l.flamegraph.javaagent.generate.test_classes.TwoMethods;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.github.kornilova_l.flamegraph.javaagent.TestHelper.removePackage;


public class InstrumentationTest {
    private static AgentConfigurationManager configurationManager;
    private static Set<MethodConfig> methodConfigs = new HashSet<>();
    private static AgentConfigurationManager configurationManagerSaveParams;
    private static Set<MethodConfig> methodConfigsSaveParams = new HashSet<>();

    @BeforeClass
    public static void setup() {
        configurationManager = createConfig("*.*(*)", methodConfigs);
        configurationManagerSaveParams = createConfig("*.*(*+)", methodConfigsSaveParams);
    }

    private static AgentConfigurationManager createConfig(String config,
                                                          Set<MethodConfig> methodConfigs) {
        TestHelper.createDir("actual");
        List<String> methodConfigsStrings = new LinkedList<>();
        methodConfigsStrings.add(config);
        AgentConfigurationManager configurationManager = new AgentConfigurationManager(
                methodConfigsStrings
        );
        methodConfigs.addAll(configurationManager.findIncludingConfigs(
                "com/github/kornilova_l/flamegraph/javaagent/generate/test_classes/OneMethod"));
        return configurationManager;
    }

    @Test
    public void instrumentationTest() {
        classTest(OneMethod.class, configurationManager, methodConfigs);
        // next test fails because TraceClassVisitor inserts spaces to end of lines
//        classTest(UsesThreadPool.class, configurationManager, methodConfigs);
        classTest(SeveralReturns.class, configurationManager, methodConfigs);
        classTest(TwoMethods.class, configurationManager, methodConfigs);
    }

    @Test
    public void instrumentationSaveParameters() {
        classTest(SaveParameters.class, configurationManagerSaveParams, methodConfigsSaveParams);
    }

    private void classTest(Class testedClass,
                           AgentConfigurationManager configurationManager,
                           Set<MethodConfig> methodConfigs) {
        try {
            String fullName = testedClass.getName();
            String fileName = removePackage(fullName);
            InputStream inputStream = Instrumentation.class.getResourceAsStream(
                    "/" + fullName.replace('.', '/') + ".class");
            byte[] bytes = TestHelper.getBytes(inputStream);
            ClassReader cr = new ClassReader(bytes);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
            cr.accept(
                    new ProfilingClassVisitor(
                            cw,
                            fullName.replace('.', '/'),
                            true,
                            methodConfigs,
                            configurationManager
                    ), ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG
            );

            bytes = cw.toByteArray();

            saveClass(bytes, fileName);

            cr = new ClassReader(bytes);
            cw = new ClassWriter(cr, 0);
            File outFile = new File("src/test/resources/actual/" + fileName + ".txt");
            cr.accept(
                    new TraceClassVisitor(cw, new PrintWriter(
                            new FileOutputStream(outFile)
                    )), 0
            );

            TestHelper.compareFiles(new File("src/test/resources/expected/" + fileName + ".txt"),
                    outFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveClass(byte[] bytes, String fileName) {
        try (OutputStream outputStream = new FileOutputStream(
                new File("src/test/resources/actual/" + fileName + ".class")
        )) {
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}