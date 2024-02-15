package main.config.security;

import io.jsonwebtoken.*;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import main.utils.SecurityConstants;

import java.time.Instant;
import java.util.Date;

@Component
public class JWTGenerator {
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(SecurityConstants.JWT_EXPIRATION / 1000); // Assuming JWT_EXPIRATION is in milliseconds

        Date currentDate = Date.from(now);
        Date expiryDate = Date.from(expiry);

        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_SECRET)
                .compact();

        return token;
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SecurityConstants.JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SecurityConstants.JWT_SECRET).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            throw new AuthenticationCredentialsNotFoundException("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            throw new AuthenticationCredentialsNotFoundException("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            throw new AuthenticationCredentialsNotFoundException("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            throw new AuthenticationCredentialsNotFoundException("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            throw new AuthenticationCredentialsNotFoundException("JWT claims string is empty");
        } catch (Exception ex) {
            throw new AuthenticationCredentialsNotFoundException("JWT was expired, or incorrect");
        }
    }
}
