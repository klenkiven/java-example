package zzj.klenkiven.proxy.jdk;

import net.sf.cglib.proxy.Enhancer;
import zzj.klenkiven.proxy.ITarget;
import zzj.klenkiven.proxy.NoInterfaceTarget;
import zzj.klenkiven.proxy.Target;
import zzj.klenkiven.proxy.cglib.MyMethodInvocation;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class JdkProxyTest {
    public static void main(String[] args) {
        Target target = new Target();

        /* -------------------- WITHOUT INTERFACES ------------------- */
        // 使用 JDK 动态代理的时候，如果没有接口，就只会生成一个普通的代理类（动态类），没办法进行强制类型转换
        NoInterfaceTarget noInterfaceTarget = new NoInterfaceTarget();
        Object obj = Proxy.newProxyInstance(
                noInterfaceTarget.getClass().getClassLoader(),
                noInterfaceTarget.getClass().getInterfaces(),
                new MyInvocationHandler(target)
        );
        System.out.println("Target without Interface Class: " + noInterfaceTarget.getClass());
        System.out.println("Target without Interface Proxy Class: " + obj.getClass());
        System.out.println("Target without Interface Proxy Interfaces: " + Arrays.toString(obj.getClass().getInterfaces()));
        printDeclaredMethods("Target without Interface Proxy Declared Methods: ", obj);
        System.out.println("事实证明，虽然没有接口，但是 JdkProxy还是会正常代理这个对象，但是没办法强制类型转换");
        System.out.println(obj.toString());

        /* -------------------- WITH INTERFACES ------------------- */
        ITarget proxy = (ITarget) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                new MyInvocationHandler(target)
        );
        printClassBasicInfo("Target with Interface", target);
        printClassBasicInfo("Target with Interface Proxy", proxy);

        System.out.println("\nDo Proxy Method ========>");
        proxy.doBusiness();

        /* -------------------- PROXY PROXIED INSTANCE -------------------- */
        // 代理一个已经被代理过的对象是合法的
        ITarget proxyProxiedInstance = (ITarget) Proxy.newProxyInstance(
                proxy.getClass().getClassLoader(),
                proxy.getClass().getInterfaces(),
                new MyInvocationHandler(proxy)
        );
        System.out.println("\n>>>>>>>>> PROXY A PROXIED INSTANCE");
        proxyProxiedInstance.doBusiness();
        /* -------------------- PROXY PROXIED INSTANCE -------------------- */

        /* -------------------- CGLIB PROXY A PROXIED PROXIED INSTANCE -------------------- */
        // CGLIB 没办法代理JDK代理的对象，JDK 可以代理 JDK 代理过的对象
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(proxyProxiedInstance.getClass());
        enhancer.setCallback(new MyMethodInvocation());
        ITarget cglibProxy = (ITarget) enhancer.create();
        System.out.println("\n>>>>>>>>> CGLIB PROXY A PROXIED PROXIED INSTANCE");
        cglibProxy.doBusiness();
        /* -------------------- CGLIB PROXY A PROXIED PROXIED INSTANCE -------------------- */
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
