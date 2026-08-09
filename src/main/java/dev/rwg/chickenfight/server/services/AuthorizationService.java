package dev.rwg.chickenfight.server.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthorizationService {

    @Value("${jwt.secret.key}")
    private String secretKey;

    public String generateToken(UUID playerId) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            return JWT.create()
                    .withIssuer("chickenfightserver")
                    .withClaim("ID", playerId.toString())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public UUID verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("chickenfightserver")
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return UUID.fromString(jwt.getClaim("ID").asString());
        } catch (JWTVerificationException exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
