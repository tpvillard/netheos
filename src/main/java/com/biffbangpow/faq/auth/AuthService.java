package com.biffbangpow.faq.auth;

import com.biffbangpow.faq.config.Configuration;
import com.biffbangpow.faq.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Date;

/**
 * The authentication service.
 */
public class AuthService {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(AuthService.class);

    // FIXME this should be kept secret....
    private static final byte[] SECRET = "mydirtylittlesecret".getBytes();
    private static final String ISSUER = "www.netheos.com";

    private final Configuration config;

    public AuthService(Configuration config) {
        this.config = config;
    }

    /**
     * Validates the jwt.
     *
     * @param compact the jwt in compact form
     * @return the authenticated user.
     * @throws WebApplicationException when validation fails
     */
    public User validateToken(String compact) {

        String subject;
        String issuer;
        boolean isAdmin;
        try {
            Jws<Claims> claims = TokenBuilder.parse(compact, SECRET);
            subject = claims.getBody().getSubject();
            issuer = claims.getBody().getIssuer();
            isAdmin = (Boolean) claims.getBody().get("isAdmin");
        } catch (Exception e) {
            LOGGER.error("Invalid token: {}", e.getMessage());
            throw new WebApplicationException(e.getMessage(), newUnauthorized(e.getMessage()));
        }

        if (subject == null) {
            String message = "Subject is null";
            throw new WebApplicationException(message, newUnauthorized(message));
        }

        if (issuer == null) {
            String message = "Issuer is null";
            throw new WebApplicationException(message, newUnauthorized(message));
        }

        if (!issuer.equalsIgnoreCase(ISSUER)) {
            String message = "Issuer claim in error: " + issuer + " instead of " + ISSUER;
            throw new WebApplicationException(message, newUnauthorized(message));
        }

        User user = new User();
        user.setUsername(subject);
        user.setAdmin(isAdmin);
        return user;
    }

    private static Response newUnauthorized(String msg) {
        return Response.status(Response.Status.UNAUTHORIZED).entity(msg).build();
    }


    /**
     * generates the jwt.
     *
     * @param subject the jwt subject
     * @param isAdmin indicates whether the subject is admin or not
     * @return the jwt
     */
    public String generateToken(String subject, boolean isAdmin) {
        long nowMillis = System.currentTimeMillis();
        Date exp = new Date(nowMillis + (1000L * 60 * config.getTokenDuration()));
        TokenBuilder builder = TokenBuilder.newBuilder(subject, ISSUER).expirationDate(exp).isAdmin(isAdmin);
        String jwt = builder.build(SECRET);
        LOGGER.info("jwt for {}: {}", subject, jwt);
        return jwt;
    }
}
