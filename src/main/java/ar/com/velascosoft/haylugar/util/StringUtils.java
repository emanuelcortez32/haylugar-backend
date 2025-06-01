package ar.com.velascosoft.haylugar.util;

public class StringUtils {
    public static boolean isNullOrEmpty(String value) {
        return value == null ||
                io.micrometer.common.util.StringUtils.isBlank(value) ||
                io.micrometer.common.util.StringUtils.isEmpty(value);
    }
}
