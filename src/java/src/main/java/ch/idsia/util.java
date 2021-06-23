package ch.idsia;

import ch.idsia.crema.core.Strides;
import ch.idsia.crema.factor.bayesian.BayesianDefaultFactor;
import ch.idsia.crema.factor.bayesian.BayesianFactor;
import ch.idsia.crema.factor.bayesian.BayesianFactorUtilities;
import ch.idsia.crema.factor.convert.BayesianToVertex;
import ch.idsia.crema.factor.credal.vertex.separate.VertexDefaultFactor;
import ch.idsia.crema.factor.credal.vertex.separate.VertexFactor;
import ch.idsia.crema.factor.credal.vertex.separate.VertexFactorUtilities;
import ch.idsia.crema.utility.RandomUtil;
import ch.idsia.crema.utility.hull.ConvexHull;
import com.google.common.primitives.Doubles;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
