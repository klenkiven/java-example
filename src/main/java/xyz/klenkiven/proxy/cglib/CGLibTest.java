package xyz.klenkiven.proxy.cglib;

import net.sf.cglib.proxy.Enhancer;
import xyz.klenkiven.proxy.Target;

public class CGLibTest {

    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Target.class);
        enhancer.setCallback(new MyMethodInvocation());

        Target target = (Target) enhancer.create();
        System.out.println("Target Class: " + Target.class);
        System.out.println("Proxy Class: " + target.getClass());
        System.out.println("Do Proxy Method ========>");
        target.doBusiness();
    }

}
