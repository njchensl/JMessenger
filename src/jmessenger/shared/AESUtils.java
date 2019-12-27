package jmessenger.shared;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;

public class AESUtils {

    public static SecretKey generate() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256, SecureRandom.getInstance("SHA1PRNG", "SUN"));
        return keyGen.generateKey();
    }

    public static byte[] encrypt(byte[] data, @NotNull SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
        byte[] IV = new byte[16];
        SecureRandom sr = new SecureRandom(SecureRandom.getSeed(16));
        sr.nextBytes(IV);
        IvParameterSpec ivSpec = new IvParameterSpec(IV);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encryptedData = cipher.doFinal(data);
        // add the IV to the start of the data
        return ArrayUtils.addAll(IV, encryptedData);
    }

    public static byte[] decrypt(byte[] data, @NotNull SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
        // get the IV from the beginning of the data
        byte[] IV = new byte[16];
        System.arraycopy(data, 0, IV, 0, 16);
        // remove the IV from the data
        byte[] originalData = new byte[data.length - 16];
        System.arraycopy(data, 16, originalData, 0, originalData.length);
        IvParameterSpec ivSpec = new IvParameterSpec(IV);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        return cipher.doFinal(originalData);
    }
}
