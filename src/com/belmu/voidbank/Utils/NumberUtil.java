package com.belmu.voidbank.Utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class NumberUtil {

    public static String format(Double count) {
        return NumberFormat.getNumberInstance(Locale.ITALY).format(count);
    }

    public static Double formatDouble(Double count) {

        String formatted = String.format(Locale.ROOT, "%.2f", count);

        try {

            return Double.parseDouble(formatted);
        } catch (NumberFormatException nfe) {

            nfe.printStackTrace();
        }

        return 0.0D;

    }

    public static String withSuffix(Double count) {

        DecimalFormat df = new DecimalFormat("#.00");
        String formatted = df.format(count);

        if (count < 1000) return formatted;
        int exp = (int) (Math.log(count) / Math.log(1000));

        return String.format("%.1f%c",
                count / Math.pow(1000, exp),
                "kM".charAt(exp - 1));

    }

    public static String toRoman(int number) {

        return String.valueOf(new char[number]).replace('\0', 'I')
                .replace("IIIII", "V")
                .replace("IIII", "IV")
                .replace("VV", "X")
                .replace("VIV", "IX")
                .replace("XXXXX", "L")
                .replace("XXXX", "XL")
                .replace("LL", "C")
                .replace("LXL", "XC")
                .replace("CCCCC", "D")
                .replace("CCCC", "CD")
                .replace("DD", "M")
                .replace("DCD", "CM");
    }

}
