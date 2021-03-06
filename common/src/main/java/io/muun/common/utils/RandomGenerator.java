package io.muun.common.utils;

import io.muun.common.utils.internal.LinuxSecureRandom;

import java.security.SecureRandom;
import java.security.Security;
import java.util.UUID;

public final class RandomGenerator {

    private static final SecureRandom random;

    static {
        if ("Android Runtime".equals(System.getProperty("java.runtime.name"))) {
            // Use Google's LinuxSecureRandom to provide SecureRandom:
            Security.insertProviderAt(LinuxSecureRandom.getProvider(), 1);
        }

        random = new SecureRandom();
    }

    /**
     * Generate the requested amount of cryptographically secure random bytes.
     *
     * @param amount The length of the byte array to be generated.
     */
    public static byte[] getBytes(int amount) {
        final byte[] bytes = new byte[amount];
        random.nextBytes(bytes);
        return bytes;
    }

    /**
     * Returns a pseudo-random, uniformly distributed UUID.
     */
    public static String getRandomUuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * Returns a pseudo-random, uniformly distributed output of sha-256, hex-encoded.
     */
    public static String getSha256() {
        return Encodings.bytesToHex(Hashes.sha256(getBytes(10)));
    }

    /**
     * Returns a pseudo-random, uniformly distributed int value.
     */
    public static int getInt() {
        return random.nextInt();
    }

    /**
     * Returns a pseudo-random, uniformly distributed int value between 0 (inclusive) and the
     * specified value (exclusive).
     */
    public static int getInt(int upperBound) {
        return random.nextInt(upperBound);
    }

    /**
     * Returns a pseudo-random, uniformly distributed int value between lowerBound (inclusive) and
     * upperBound (exclusive).
     */
    public static int getInt(int lowerBound, int upperBound) {
        return random.nextInt(upperBound - lowerBound) + lowerBound;
    }

    /**
     * Returns a pseudo-random, uniformly distributed, positive int value.
     */
    public static int getPositiveInt() {
        return random.nextInt(Integer.MAX_VALUE) + 1;
    }

    /**
     * Returns a pseudo-random, uniformly distributed long value.
     */
    public static long getLong() {
        return random.nextLong();
    }

    /**
     * Returns a pseudo-random, uniformly distributed, positive long value.
     */
    public static long getPositiveLong() {
        final long nonNegativeLong = (random.nextLong() << 1) >>> 1;
        return (nonNegativeLong % Long.MAX_VALUE) + 1;
    }

    /**
     * Return a cryptographically-secure random string, with letters taken from an alphabet.
     */
    public static String getRandomString(int length, Character[] alphabet) {

        final char[] result = new char[length];

        for (int i = 0; i < result.length; i++) {
            result[i] = alphabet[getInt(alphabet.length)];
        }

        return new String(result);
    }

    public static SecureRandom getSecureRandom() {
        return random;
    }
}
