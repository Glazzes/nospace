package com.nospace.security;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

@Slf4j
public class SecurityCipher {
    private static final String STRING_KEY = "StephenCrossfire";
    private static SecretKeySpec secretKeySpec;
    private static byte[] byteKey;
    private final static IvParameterSpec ivParameterSpec =
        new IvParameterSpec("aX2vhI2reKDOhZXP".getBytes(StandardCharsets.US_ASCII));

    private static void assignKey(){
        try{
            MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
            byteKey = sha512.digest(STRING_KEY.getBytes(StandardCharsets.US_ASCII));
            byteKey = Arrays.copyOf(byteKey, 16);
            secretKeySpec = new SecretKeySpec(byteKey, "AES");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String encryptCookieJwtToken(String token){
        assignKey();
        try{
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encryptedResult = cipher.doFinal(token.getBytes(StandardCharsets.US_ASCII));
            return Base64.getEncoder().encodeToString(encryptedResult);

        }catch (Exception e){
            log.info("Security cipher was not able to encrypt cookie data");
            e.printStackTrace();
            throw new IllegalStateException("Incorrect algorithm or padding to perform encryption");
        }
    }

    public static String decryptCookieJWtToken(String encryptedCookieToken){
        assignKey();
        try{
            byte[] decryptedCookie = Base64.getDecoder().decode(encryptedCookieToken);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            return new String(cipher.doFinal(decryptedCookie), StandardCharsets.US_ASCII);

        }catch (Exception e){
            log.info("Security cipher was not able to decrypt cookie data");
            e.printStackTrace();
            throw new IllegalStateException("Incorrect algorithm or padding to perform encryption");
        }
    }

}