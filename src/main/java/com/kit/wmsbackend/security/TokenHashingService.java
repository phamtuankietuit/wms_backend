package com.kit.wmsbackend.security;

import com.kit.wmsbackend.exception.TokenHashingException;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
public class TokenHashingService {
    private static final String ALGORITHM = "SHA-256";

    public String hashToken(@NonNull String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);

            byte[] encodedHash = digest.digest(
                rawToken.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(encodedHash);

        } catch (NoSuchAlgorithmException e) {
            throw new TokenHashingException("Error initializing hashing algorithm", e);
        }
    }

    public boolean verifyToken(@NonNull String rawToken, @NonNull String storedHash) {
        String generatedHash = hashToken(rawToken);

        return MessageDigest.isEqual(
            generatedHash.getBytes(StandardCharsets.UTF_8),
            storedHash.getBytes(StandardCharsets.UTF_8)
        );
    }
}
