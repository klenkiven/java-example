package zzj.klenkiven.proxy.cglib;

import net.sf.cglib.proxy.Enhancer;
import zzj.klenkiven.proxy.ITarget;
import zzj.klenkiven.proxy.Target;
import zzj.klenkiven.proxy.jdk.MyInvocationHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class CGLibTest {

    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Target.class);
        enhancer.setCallback(new MyMethodInvocation());

        Target target = (Target) enhancer.create();
        printClassBasicInfo("Proxy", target);
        System.out.println("Do Proxy Method ========>");
        target.doBusiness();

        /* -------------------- JDK PROXY PROXIED INSTANCE -------------------- */
        ITarget jdkProxyTarget = (ITarget) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                Target.class.getInterfaces(),
                new MyInvocationHandler(target)
        );
        System.out.println("Do JDK Proxy Method ========>");
        jdkProxyTarget.doBusiness();
        /* -------------------- JDK PROXY PROXIED INSTANCE -------------------- */

        /* -------------------- CGLIB PROXY PROXIED INSTANCE -------------------- */
        enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(new MyMethodInvocation());
        Target proxyProxiedTarget = (Target) enhancer.create();
        printDeclaredMethods("Proxy Proxied Target", proxyProxiedTarget);
        /* -------------------- CGLIB PROXY PROXIED INSTANCE -------------------- */
    }

    private static void printClassBasicInfo(String message, Object obj) {
        System.out.println(">>>>>>>>>>>>>>>>>>> " + message + " <<<<<<<<<<<<<<<<<<<");
        System.out.println("Class: " + obj.getClass());
        System.out.println("Interfaces: " + Arrays.toString(obj.getClass().getInterfaces()));
        printDeclaredMethods("Methods: ", obj);
        System.out.println(">>>>>>>>>>>>>>>>>>> " + message + " <<<<<<<<<<<<<<<<<<<");
        System.out.println();
    }

    private static void printDeclaredMethods(String s, Object obj) {
        System.out.print(s);
        for (Method declaredMethod : obj.getClass().getDeclaredMethods()) {
            System.out.print(declaredMethod.getName() + ", ");
        }
        System.out.println();
    }

}
