package dev.rwg.chickenfight.server.config;

import dev.rwg.chickenfight.server.services.AuthorizationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthorizationService authService;

    public AuthInterceptor(AuthorizationService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String bearerToken = request.getHeader("Authorization");

        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Missing or invalid authorization token.");
            return false;
        }

        String token = bearerToken.substring(7);
        UUID playerId = authService.verifyToken(token);

        if (playerId == null) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "The token has expired or is invalid.");
            return false;
        }
        request.setAttribute("authenticatedPlayerId", playerId);
        return true;
    }
}