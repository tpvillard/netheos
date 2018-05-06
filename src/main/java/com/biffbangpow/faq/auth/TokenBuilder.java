package com.biffbangpow.faq.auth;

import io.jsonwebtoken.*;

import java.util.Date;

/**
 * A token builder.
 */
public final class TokenBuilder {

    private final String subject;
    private final String issuer;
    private Date expirationDate;
    private boolean isAdmin;

    private TokenBuilder(String subject, String issuer) {

        this.subject = subject;
        this.issuer = issuer;
    }

    public static TokenBuilder newBuilder(String subject, String issuer) {

        return new TokenBuilder(subject, issuer);
    }


    public TokenBuilder expirationDate(Date expirationDate) {
        this.expirationDate = new Date(expirationDate.getTime());
        return this;
    }

    public TokenBuilder isAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
        return this;
    }

    public String build(byte[] secret) {

        Claims claims = Jwts.claims().setSubject(subject);
        claims.setIssuer(issuer);
        if (expirationDate != null) {
            claims.setExpiration(expirationDate);
        }
        claims.put("isAdmin", isAdmin);
        return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS256, secret).compact();
    }

    /**
     * Parses a jwt.
     *
     * @param jwt the json web token
     * @return the jwt claims the jwt claims
     */
    public static Jws<Claims> parse(String jwt, byte[] secret) {

        return Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt);
    }
}
