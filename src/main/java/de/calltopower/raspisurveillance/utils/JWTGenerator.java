package de.calltopower.raspisurveillance.utils;

import javax.crypto.SecretKey;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;

public class JWTGenerator {

    public static String generateSecurityTokenSecret() {
        SecretKey key = io.jsonwebtoken.security.Keys.secretKeyFor(SignatureAlgorithm.HS512);
        return Encoders.BASE64.encode(key.getEncoded());
    }

    public static void main(String[] args) {
        System.out.println(generateSecurityTokenSecret());
    }

}
