package us.elron.sassist.tester;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import us.elron.sassist.Advice;
import us.elron.sassist.IAdviceBuilder;
import us.elron.sassist.IAdviceListener;
import us.elron.sassist.IAssistService;

public class Activator implements BundleActivator {

    public void start(BundleContext bundleContext) throws Exception {
        ServiceReference ref = bundleContext.getServiceReference(IAssistService.class.getName());
        IAssistService assistService = (IAssistService) bundleContext.getService(ref);
        //
        ITestService testService = new TestService();
        IAdviceBuilder adviceBuilder = assistService.createProxyFor(testService, false);
        testService = this.addLogging(adviceBuilder);

        for (int i = 0; i < 100; i++) {
            testService.testMethod(i, "hey [" + i + "]!", new TestObject(i * 100, "how u doin ?"));
        }
    }

    public void stop(BundleContext bundleContext) throws Exception {
    }

    public ITestService addLogging(final IAdviceBuilder builder) throws Exception {
        final StringBuilder startAdvice = new StringBuilder();
        startAdvice.append("long start = System.currentTimeMillis();").append(IAdviceBuilder.NLT);
        startAdvice.append("try {").append(IAdviceBuilder.NLT);
        //
        final StringBuilder catchAdvice = new StringBuilder();
        catchAdvice.append("} catch (Throwable e) {").append(IAdviceBuilder.NLT);
        catchAdvice.append("System.err.println(\"Error ! :: \" + e.getMessage());").append(IAdviceBuilder.NLT);
        catchAdvice.append("throw e;");
        //
        final StringBuilder finallyAdvice = new StringBuilder();
        finallyAdvice.append("} finally {").append(IAdviceBuilder.NLT);
        log(finallyAdvice, "\"[Delta:\" + (System.currentTimeMillis() - start) + \"ms][Params:\"+Arrays.toString($args)+\"]\"");
        finallyAdvice.append("}").append(IAdviceBuilder.NLT);
        //
        return builder.addImport(Arrays.class)
                      .addInterface(ITestService.class)
                      .register(Advice.BEFORE, startAdvice.toString())
                      .register(Advice.AFTER, catchAdvice.toString(), new IAdviceListener() {
                          @Override
                          public boolean applyAdviceFor(final Advice advice, final Method m, final String code) {
                              return !m.toString().contains("throws");
                          }
                      })
                      .register(Advice.AFTER, finallyAdvice.toString())
                      .generate(ITestService.class);
    }

    private static void log(final StringBuilder body, final String code) {
        body.append("System.err.println(").append(code).append(");").append(IAdviceBuilder.NLT);
    }

}
