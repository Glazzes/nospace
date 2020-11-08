package com.nospace.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class SecurityCipher {
    private static final String SIGNING_KEY = "SomeSuperSecretKey";
    private static SecretKeySpec secretKey;
    private static byte[] key;

    public static void setKey(){
        MessageDigest sha;
        try{
            key = SIGNING_KEY.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-512");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
    }

    public static String encrypt(String textToEncrypt){
        if(textToEncrypt == null) return null;

        try{
            setKey();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(textToEncrypt.getBytes(StandardCharsets.UTF_8));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String textToDecrypt){
        if(textToDecrypt == null) return null;
        try{
            setKey();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(textToDecrypt.getBytes())));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
