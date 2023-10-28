package com.nixiedroid.unsafe;

import java.lang.instrument.Instrumentation;
@SuppressWarnings("unused")
public class Premain {
    private static volatile Instrumentation globalInstrumentation;
    private static volatile boolean isPremainAvailable = false;

    public static void premain(final String agentArgs, final Instrumentation inst) {
        globalInstrumentation = inst;
        isPremainAvailable = true;
    }

    public static boolean isPremainAvailable() {
        return isPremainAvailable;
    }

    public static long sizeOf(final Object object) {
        if (isPremainAvailable) {
            return globalInstrumentation.getObjectSize(object);
        }
        System.out.println("Agent not initialized. Use -javaagent:{THIS.JAR} ");
        return 0;
    }

}
