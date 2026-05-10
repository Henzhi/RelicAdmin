package com.relic;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Encoders;
import javax.crypto.SecretKey;
import io.jsonwebtoken.SignatureAlgorithm;
public class Key {

    public static void main(String[] args) {


        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String base64Secret = Encoders.BASE64.encode(key.getEncoded());
        System.out.println(base64Secret);


        SecretKey adminKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        System.out.println("Admin Base64: " + Encoders.BASE64.encode(adminKey.getEncoded()));

        SecretKey userKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        System.out.println("User Base64: " + Encoders.BASE64.encode(userKey.getEncoded()));
    }
}
