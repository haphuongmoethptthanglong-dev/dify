package com.dify.iam.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.HexFormat;
import java.util.regex.Pattern;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Password hashing service compatible with Python's {@code libs/password.py}.
 *
 * Python hash chain:
 * <ol>
 *   <li>{@code hashlib.pbkdf2_hmac("sha256", password.encode(), salt, 10000)} → raw bytes</li>
 *   <li>{@code binascii.hexlify(dk)} → hex string as bytes</li>
 *   <li>{@code base64.b64encode(hex_bytes).decode()} → stored password</li>
 * </ol>
 *
 * Salt is {@code base64.b64encode(secrets.token_bytes(16)).decode()}.
 */
public final class PasswordService {

    private static final int PBKDF2_ITERATIONS = 10000;
    private static final int KEY_LENGTH_BITS = 256;
    private static final int SALT_LENGTH_BYTES = 16;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d).{8,}$");

    private PasswordService() {
    }

    /**
     * Validate that a password meets the required pattern.
     * Matches Python's {@code valid_password()} in libs/password.py.
     *
     * @throws IllegalArgumentException if the password is invalid
     */
    public static String validPassword(String password) {
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException(
                    "Password must contain letters and numbers, and the length must be greater than 8.");
        }
        return password;
    }

    /**
     * Generate a random salt and return it as a base64-encoded string.
     */
    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH_BYTES];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hash a password with the given salt bytes.
     * Returns base64(hexlify(pbkdf2_hash)) matching Python's storage format.
     */
    public static String hashPassword(String password, byte[] saltBytes) {
        try {
            var keySpec = new PBEKeySpec(
                    password.toCharArray(), saltBytes, PBKDF2_ITERATIONS, KEY_LENGTH_BITS);
            var factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] dk = factory.generateSecret(keySpec).getEncoded();
            // Python: binascii.hexlify(dk) → hex string bytes → base64.b64encode()
            String hexString = HexFormat.of().formatHex(dk);
            return Base64.getEncoder().encodeToString(hexString.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    /**
     * Hash a password with a base64-encoded salt string.
     */
    public static String hashPassword(String password, String base64Salt) {
        byte[] saltBytes = Base64.getDecoder().decode(base64Salt);
        return hashPassword(password, saltBytes);
    }

    /**
     * Compare a plaintext password against stored hash and salt.
     * Matches Python's {@code compare_password()} in libs/password.py.
     */
    public static boolean comparePassword(String password, String storedPasswordBase64, String storedSaltBase64) {
        String computed = hashPassword(password, storedSaltBase64);
        return computed.equals(storedPasswordBase64);
    }
}
