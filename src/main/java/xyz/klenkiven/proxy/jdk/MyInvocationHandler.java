package xyz.klenkiven.proxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MyInvocationHandler implements InvocationHandler {

    private final Object target;

    public MyInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Before Advice
        System.out.println("========> Before Advice <========");

        // Invoke Origin Target
        Object retForTarget = method.invoke(target, args);

        // After Advice
        System.out.println("========> After Advice <========");

        return retForTarget;
    }
}
