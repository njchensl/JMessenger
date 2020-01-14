/*
 * Copied and modified from https://www.devglan.com/java8/rsa-encryption-decryption-java
 */

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

import java.security.*;

/**
 *
 * @author frche1699
 */
public class RSAKeyPairGenerator {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    /**
     * constructs a key pair generator with 3500 bit keys
     *
     * @throws NoSuchAlgorithmException never thrown
     * @throws NoSuchProviderException never thrown
     */
    public RSAKeyPairGenerator() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(3500, SecureRandom.getInstance("SHA1PRNG", "SUN"));
        KeyPair pair = keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }

    @NotNull
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    @NotNull
    public PublicKey getPublicKey() {
        return publicKey;
    }
}
