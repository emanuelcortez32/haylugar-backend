package ar.com.greenbundle.haylugar.util;

import java.util.Arrays;
import java.util.Base64;

public class EncodeUtil {
    private static final Base64.Encoder ENCODER = Base64.getEncoder();
    private static final Base64.Decoder DECODER = Base64.getDecoder();

    public static String encodeBase64AsString(byte[] src) {
        return ENCODER.encodeToString(src);
    }

    public static String decodeBase64AsString(byte[] src) {
        return Arrays.toString(DECODER.decode(src));
    }
    public static byte[] decodeBase64(byte[] src) { return DECODER.decode(src); }
}
