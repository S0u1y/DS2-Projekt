package org.kub0679.Utility;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encoding {
    public static String Hash(String heslo){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            BigInteger number = new BigInteger(md.digest(heslo.getBytes(StandardCharsets.UTF_8)));
            StringBuilder hexString = new StringBuilder(number.toString());
            return hexString.toString();
        }catch (NoSuchAlgorithmException e){

        }
        return null;
    }
}
