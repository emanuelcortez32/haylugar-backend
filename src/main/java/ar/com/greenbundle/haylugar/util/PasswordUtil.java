package ar.com.greenbundle.haylugar.util;

import ar.com.greenbundle.haylugar.dto.SecurePassword;
import ar.com.greenbundle.haylugar.exceptions.LoginPasswordException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

public class PasswordUtil {
    private static final String KEY_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH = 128;
    public static SecurePassword createSecurePassword(String rawPassword) {
        try {
            SecureRandom secureRandom = new SecureRandom();
            byte[] salt = new byte[16];
            secureRandom.nextBytes(salt);

            KeySpec keySpec = new PBEKeySpec(rawPassword.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);

            byte[] hash = keyFactory.generateSecret(keySpec).getEncoded();

            return new SecurePassword(salt, hash);
        }  catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static void verifySecurePassword(SecurePassword securePassword, String rawPassword) {
        try {
            byte[] originSalt = securePassword.getSalt();
            byte[] originHash = securePassword.getHash();

            KeySpec keySpec = new PBEKeySpec(rawPassword.toCharArray(), originSalt, ITERATION_COUNT, KEY_LENGTH);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);

            byte[] hash = keyFactory.generateSecret(keySpec).getEncoded();

            if(!Arrays.equals(hash, originHash))
                throw new LoginPasswordException("Invalid password");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
}
