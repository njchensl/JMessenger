/*
 * The MIT License
 *
 * Copyright 2019 frche1699.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jmessenger.shared;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;

public class AESUtils {

    /**
     * generates a secret AES key with AES-256
     *
     * @return the 256-bit key
     */
    public static SecretKey generate() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256, SecureRandom.getInstance("SHA1PRNG", "SUN"));
        return keyGen.generateKey();
    }

    /**
     * encrypts data using AES-256-CBC
     *
     * @param data the data to encrypt
     * @param key  the key to use
     * @return the encrypted data with the IV concatenated to the front
     */
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

    /**
     * decrypts data encrypted with AES-256-CBC
     *
     * @param data the encrypted data with the IV concatenated to the front
     * @param key  the key to use
     * @return the decrypted data
     */
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
