package ch.idsia;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class util {
    public static double parseDouble(String s) {
        if (s.contains("/")) {
            String[] frac = s.split("/");
            return Double.parseDouble(frac[0]) / Double.parseDouble(frac[1]);
            //  return Fraction.getFraction(s).doubleValue();
        }
        return Double.parseDouble(s);
    }

    public static void disableWarning() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe u = (Unsafe) theUnsafe.get(null);

            Class<?> cls = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field logger = cls.getDeclaredField("logger");
            u.putObjectVolatile(cls, u.staticFieldOffset(logger), null);
        } catch (Exception e) {
            // ignore
        }
    }
}
