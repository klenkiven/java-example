package xyz.klenkiven.proxy.jdk;

import xyz.klenkiven.proxy.ITarget;
import xyz.klenkiven.proxy.NoInterfaceTarget;
import xyz.klenkiven.proxy.Target;

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
        System.out.print("Target without Interface Proxy Methods: ");
        Method proxyClassLookup = null;
        for (Method declaredMethod : obj.getClass().getDeclaredMethods()) {
            System.out.print(declaredMethod.getName() + ", ");
        }
        System.out.println();
        System.out.println("事实证明，虽然没有接口，但是 JdkProxy还是会正常代理这个对象，但是没办法强制类型转换");
        System.out.println(obj.toString());

        /* -------------------- WITH INTERFACES ------------------- */
        ITarget proxy = (ITarget) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                new MyInvocationHandler(target)
        );
        System.out.println("Target with Interface Class: " + target.getClass());
        System.out.println("Target with Interface Proxy Class: " + proxy.getClass());
        System.out.println("Target with Interface Proxy Interfaces: " + Arrays.toString(proxy.getClass().getInterfaces()));
        System.out.print("Target with Interface Proxy Methods: ");
        for (Method declaredMethod : proxy.getClass().getDeclaredMethods()) {
            System.out.print(declaredMethod.getName() + ", ");
        }
        System.out.println();

        System.out.println("\nDo Proxy Method ========>");
        proxy.doBusiness();
    }
}
