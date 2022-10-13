package zzj.klenkiven.proxy.cglib;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class MyMethodInvocation implements MethodInterceptor {

    /**
     * CGLib Method Interceptor
     * All generated proxied methods call this method instead of the original method.
     * The original method may either be invoked by normal reflection using the Method
     * object, or by using the MethodProxy (faster).
     *
     * @param obj "this", the ** ENHANCED ** object
     * @param method intercepted method
     * @param args arguments array
     * @param proxy used to invoke super (non-intercepted method);
     *              may be called as many times as needed
     * @return
     * any value compatible with the signature of the proxied method.
     * Method returning void will ignore this value.
     *
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        // Before Advice
        System.out.println("========> Before Advice <========");

        // Invoke Original Method which instance is not proxied
        Object retValForSuper = proxy.invokeSuper(obj, args);

        // After Advice
        System.out.println("========> After Advice <========");

        return retValForSuper;
    }
}
