package com.androstock.smsapp;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncDecr {
    String AES = "AES";

    public EncDecr(){

    }

    public String[] decrypt(String outputString, String password) throws Exception {
        String[] uhu = new String[2];
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE, key);
        Long tsLong = System.nanoTime();
        byte[] decodedValue = Base64.decode(outputString, Base64.DEFAULT);
        byte[] decValue = c.doFinal(decodedValue);
        String decryptedValue = new String(decValue);
        Long tsLong1 = System.nanoTime();
        Long time = tsLong1-tsLong;
        uhu[0] = decryptedValue;
        uhu[1] = time.toString();
        return uhu;
    }

    public String[] encrypt(String data, String password) throws Exception{
        String[] uhu = new String[2];
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE,key);
        Long tsLong = System.nanoTime();
        byte[] enVal = c.doFinal(data.getBytes());
        String encryptedValue = Base64.encodeToString(enVal, Base64.DEFAULT);
        Long tsLong1 = System.nanoTime();
        Long time = tsLong1-tsLong;
        uhu[0] = encryptedValue;
        uhu[1] = time.toString();
        return uhu;
    }

    private SecretKeySpec generateKey(String password) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }


    public static String randomKey( int maxlength){
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz+=";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(maxlength);
        for( int i = 0; i < maxlength; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }


}
