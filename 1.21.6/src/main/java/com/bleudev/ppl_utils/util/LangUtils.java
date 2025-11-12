package com.bleudev.ppl_utils.util;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LangUtils {
    private static double precision(@NotNull BigDecimal d, int p) {
        return d.setScale(p, RoundingMode.DOWN).doubleValue();
    }

    public static double round(double x, int p) {
        return precision(new BigDecimal(x), p);
    }

    public static <T> @NotNull List<T> union(List<T> list1, List<T> list2, boolean unmodifiable) {
        var a = new ArrayList<>(list1);
        a.addAll(list2);
        if (unmodifiable) return Collections.unmodifiableList(a);
        return a.stream().toList();
    }
    public static <T> @NotNull List<T> unmodifiableUnion(List<T> list1, List<T> list2) {
        return union(list1, list2, true);
    }
}
