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

import org.jetbrains.annotations.NotNull;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.ByteBuffer;
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
     * encrypts data using AES-256-GCM
     *
     * @param data the data to encrypt
     * @param key  the key to use
     * @return the encrypted data with the IV concatenated to the front
     */
    public static byte[] encrypt(byte[] data, @NotNull SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        /*
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
        byte[] IV = new byte[16];
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(IV);
        IvParameterSpec ivSpec = new IvParameterSpec(IV);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encryptedData = cipher.doFinal(data);
        // add the IV to the start of the data
        return ArrayUtils.addAll(IV, encryptedData);
         */
        SecureRandom secureRandom = new SecureRandom();
        byte[] IV = new byte[12];
        secureRandom.nextBytes(IV);
        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, IV); //128 bit auth tag length
        cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);
        byte[] cipherText = cipher.doFinal(data);
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + IV.length + cipherText.length);
        byteBuffer.putInt(IV.length);
        byteBuffer.put(IV);
        byteBuffer.put(cipherText);
        return byteBuffer.array();
    }


    /**
     * decrypts data encrypted with AES-256-GCM
     *
     * @param data the encrypted data with the IV concatenated to the front
     * @param key  the key to use
     * @return the decrypted data
     */
    public static byte[] decrypt(byte[] data, @NotNull SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        /*
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

         */
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        int ivLength = byteBuffer.getInt();
        if (ivLength < 12 || ivLength >= 16) { // check input parameter
            throw new IllegalArgumentException("invalid iv length");
        }
        byte[] iv = new byte[ivLength];
        byteBuffer.get(iv);
        byte[] cipherText = new byte[byteBuffer.remaining()];
        byteBuffer.get(cipherText);
        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));
        return cipher.doFinal(cipherText);
    }
}
