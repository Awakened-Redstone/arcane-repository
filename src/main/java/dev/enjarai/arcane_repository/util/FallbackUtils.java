package dev.enjarai.arcane_repository.util;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;

public class FallbackUtils {
    @NotNull
    public static String selfOrFallbackIfBlank(@Nullable String self, @NotNull String fallback) {
        return StringUtils.isNotBlank(self) ? self : fallback;
    }

    public static String matcherGroupOrFallback(Matcher matcher, String group, String fallback) {
        return matcher.namedGroups().get(group) == null ? fallback : matcher.group(group);
    }
}
