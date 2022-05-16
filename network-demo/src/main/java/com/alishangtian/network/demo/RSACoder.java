package com.alishangtian.network.demo;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author maoxiaobing@baidu.com
 * @Date 2022/5/16
 */
public class RSACoder {
    //非对称密钥算法
    public static final String KEY_ALGORITHM = "RSA";
    private static final int KEY_SIZE = 512;
    private static final String PUBLIC_KEY = "RSAPublicKey";
    private static final String PRIVATE_KEY = "RSAPrivateKey";
    //公钥
    private static final String PUBLIC_KEY_CONTENT =
            "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJRvbko5BAN0S7CPEGNDk2tbNfXYzMKvEFPW+3U4Cnwtme5PVWS8l+ExYB5Iwk+tsaB3a5qb2mTZ+9KD+jXW5FsCAwEAAQ==";
    //私钥
    private static final String PRIVATE_KEY_CONTENT =
            "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAlG9uSjkEA3RLsI8QY0OTa1s19djMwq8QU9b7dTgKfC2Z7k9VZLyX4TFgHkjCT62xoHdrmpvaZNn70oP6NdbkWwIDAQABAkAmBfVmpfxVNjE9ZSh5hRH6aj8EXyj0pgu0rllzyYtGC7Nzv2R6vyMLhVg+707c04pewwZ9QtC7z5k8fNnvj0lhAiEA4cUdkqJv7/nPdT+ohGp1j3vPpP95Zw0bAVQUUDxjQgUCIQCoT3TaVPCGUMcNY5qaTV0b8o5xgyStT1LRE4oZaGJ63wIhALddzUMHQzr4/hIQfeHuRUUgHem4xPV5o3FUxJrWMRexAiBnqhCxzPEb+TKwh3GYqh+37+xV880qRZofliZPweAuBwIgJd80UxYW0gDFgVqMGWXlEHd58DLNkiIuaKQR5B65YcA=\n" +
                    "\n";

    /**
     * 初始化密钥对
     *
     * @return Map 密钥对
     */
    public static Map<String, Object> initKey() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }


    /**
     * 私钥加密
     *
     * @param data
     * @param key  密钥
     * @return byte[] 加密数据
     */
    public static byte[] encryptByPrivateKey(byte[] data, byte[] key) throws Exception {
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     * 公钥加密
     *
     * @param data
     * @param key  密钥
     * @return byte[] 加密数据
     */
    public static byte[] encryptByPublicKey(byte[] data, byte[] key) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return cipher.doFinal(data);
    }

    /**
     * 私钥解密
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return byte[] 解密数据
     */
    public static byte[] decryptByPrivateKey(byte[] data, byte[] key) throws Exception {
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     * 公钥解密
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return byte[] 解密数据
     */
    public static byte[] decryptByPublicKey(byte[] data, byte[] key) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, pubKey);
        return cipher.doFinal(data);
    }

    /**
     * 取得私钥
     *
     * @return byte[] 私钥
     */
    public static byte[] getPrivateKey() {
        return Base64.decodeBase64(PRIVATE_KEY_CONTENT);
    }

    /**
     * 取得公钥
     *
     * @return byte[] 公钥
     */
    public static byte[] getPublicKey() {
        return Base64.decodeBase64(PUBLIC_KEY_CONTENT);
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String content = "您好";
        byte[] enContent = RSACoder.encryptByPrivateKey(content.getBytes(StandardCharsets.UTF_8), getPrivateKey());
        byte[] deContent = RSACoder.decryptByPublicKey(enContent, getPublicKey());
        String deString = new String(deContent, StandardCharsets.UTF_8);
        System.out.println(String.format("deString %s", deString));
    }

}
