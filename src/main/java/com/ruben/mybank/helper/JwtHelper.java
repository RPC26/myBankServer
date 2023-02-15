package com.ruben.mybank.helper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import com.ruben.mybank.exception.JWTException;

public class JwtHelper {

    private static final String SECRET = "HOLAHOLAasdfghjklHOLAHOLA";
    private static final String ISSUER = "MyBank";

    private static SecretKey secretKey() {
        return Keys.hmacShaKeyFor((SECRET + ISSUER + SECRET).getBytes());
    }

    public static String generateJWT(String id, String name, String role) {

        Date currentTime = Date.from(Instant.now());
        Date expiryTime = Date.from(Instant.now().plus(Duration.ofSeconds(5000000)));

        return Jwts.builder()
               .setId(UUID.randomUUID().toString())
               .setIssuer(ISSUER)
               .setIssuedAt(currentTime)
               .setExpiration(expiryTime)
               .claim("id", id)
               .claim("name", name)
               .claim("role", role)
               .signWith(secretKey())
               .compact();
    }

    public static String validateJWT(String strJWT) {
        Jws<Claims> headerClaimsJwt = Jwts.parserBuilder()
               .setSigningKey(secretKey())
               .build()
               .parseClaimsJws(strJWT);

        Claims claims = headerClaimsJwt.getBody();
        if (!claims.getIssuer().equals(ISSUER)) {
            throw new JWTException("Error validating JWT");
        }
        return claims.get("name", String.class);
    }
}