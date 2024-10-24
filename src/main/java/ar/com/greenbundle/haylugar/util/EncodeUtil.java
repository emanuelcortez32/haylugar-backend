package ar.com.greenbundle.haylugar.util;

import java.util.Arrays;
import java.util.Base64;

public class EncodeUtil {
    private static final Base64.Encoder BASE64_ENCODER = Base64.getEncoder();
    private static final Base64.Decoder BASE64_DECODER = Base64.getDecoder();

    public static String encodeBase64AsString(byte[] src) {
        return BASE64_ENCODER.encodeToString(src);
    }
    public static byte[] encodeBase64(byte[] src) {return BASE64_ENCODER.encode(src);}
    public static byte[] encodeBase64FromString(String src) { return BASE64_ENCODER.encode(src.getBytes()); }

    public static String decodeBase64AsString(byte[] src) {
        return Arrays.toString(BASE64_DECODER.decode(src));
    }
    public static byte[] decodeBase64(byte[] src) { return BASE64_DECODER.decode(src); }
}
