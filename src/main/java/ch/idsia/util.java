package ch.idsia;

import org.apache.commons.lang3.math.Fraction;

public class util {
    public static double parseDouble(String s){
        if (s.contains("/")){
            String[] frac = s.split("/");
            return Double.valueOf(frac[0]) / Double.valueOf(frac[1]);
            //  return Fraction.getFraction(s).doubleValue();
        }
        return Double.valueOf(s);
    }
}
