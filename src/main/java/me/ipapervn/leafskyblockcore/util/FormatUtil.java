package me.ipapervn.leafskyblockcore.util;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public final class FormatUtil {

    private FormatUtil() {}

    @NotNull
    public static String formatNumber(long value) {
        if (value >= 1_000_000_000L) return String.format(Locale.ROOT, "%.1fB", value / 1_000_000_000.0);
        if (value >= 1_000_000L)     return String.format(Locale.ROOT, "%.1fM", value / 1_000_000.0);
        if (value >= 1_000L)         return String.format(Locale.ROOT, "%.1fK", value / 1_000.0);
        return String.valueOf(value);
    }
}
