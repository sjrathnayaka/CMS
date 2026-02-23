package edu.epic.cms.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Generates an RSA-2048 key pair once at application startup and
 * exposes the Public / Private keys as Spring beans.
 *
 * The public key is served to the frontend so it can encrypt card
 * data before transmission. The private key decrypts it on arrival.
 *
 * Note: Key pair is in-memory only (regenerated on each restart).
 * For production, persist keys via a KeyStore or secrets manager.
 */
@Slf4j
@Configuration
public class RsaKeyConfig {

    private final KeyPair keyPair;

    public RsaKeyConfig() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        this.keyPair = generator.generateKeyPair();
        log.info("RSA-2048 key pair generated for card data transport encryption.");
    }

    @Bean
    public PublicKey rsaPublicKey() {
        return keyPair.getPublic();
    }

    @Bean
    public PrivateKey rsaPrivateKey() {
        return keyPair.getPrivate();
    }
}
