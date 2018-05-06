package com.biffbangpow.faq.filter;


import com.biffbangpow.faq.auth.AuthService;
import com.biffbangpow.faq.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.security.Principal;


/**
 * An Bearer based authentication filter.
 * nicely explained here:
 * https://swagger.io/docs/specification/authentication/bearer-authentication/
 */
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);
    private static final String BEARER = "Bearer";

    @Inject
    private AuthService authService;


    @Override
    public void filter(ContainerRequestContext request) throws IOException {

        BearerAuthenticator authenticator = newAuthenticator(request);
        request.setSecurityContext(authenticator.authenticate());
    }

    private BearerAuthenticator newAuthenticator(ContainerRequestContext request) {

        LOGGER.debug("Received request: {} {}", request.getMethod(), request.getUriInfo().getAbsolutePath());
        String authorization = request.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith(BEARER)) {

            LOGGER.error("No Authorization header, Bearer is expected");
            throw new NotAuthorizedException(of401(BEARER));
        } else {
            return new BearerAuthenticator(request);
        }
    }

    private class BearerAuthenticator {

        private final ContainerRequestContext request;

        BearerAuthenticator(ContainerRequestContext request) {
            this.request = request;
        }

        public SecurityContext authenticate() {

            String token = request.getHeaderString(HttpHeaders.AUTHORIZATION);
            token = token.substring((BEARER + " ").length());
            final User user = authService.validateToken(token);
            return new SecurityContext() {
                @Override
                public Principal getUserPrincipal() {
                    return () -> user.getUsername();
                }

                @Override
                public boolean isUserInRole(String s) {
                    return s.equalsIgnoreCase("admin") && user.isAdmin();
                }

                @Override
                public boolean isSecure() {
                    return "https".equals(request.getUriInfo().getRequestUri().getScheme());
                }

                @Override
                public String getAuthenticationScheme() {
                    return BEARER;
                }
            };
        }
    }


    // TV behavior for jetty for pre defined exceptions: HTML media type is always returned
    private static Response of401(String challenge) {
        return Response.status(Response.Status.UNAUTHORIZED).entity("Missing challenge").header(HttpHeaders
                .WWW_AUTHENTICATE, challenge).build();
    }
}
