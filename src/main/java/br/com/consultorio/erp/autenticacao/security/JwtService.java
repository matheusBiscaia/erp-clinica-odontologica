package br.com.consultorio.erp.autenticacao.security;

import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey chave;
    private final long tempoExpiracao;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long tempoExpiracao) {

        this.chave = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secret)
        );

        this.tempoExpiracao = tempoExpiracao;
    }

    public String gerarToken(String email) {
        Instant agora = Instant.now();
        Instant expiracao = agora.plusMillis(tempoExpiracao);

        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(agora))
                .expiration(Date.from(expiracao))
                .signWith(chave)
                .compact();
    }

    public String extrairEmail(String token) {
        return Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean tokenValido(String token, String email) {
        try {
            String emailToken = extrairEmail(token);

            Date expiracao = Jwts.parser()
                    .verifyWith(chave)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();

            return emailToken.equalsIgnoreCase(email)
                    && expiracao.after(new Date());

        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }
}