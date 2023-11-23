package es.in2.wallet.crypto;

import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import es.in2.wallet.crypto.config.KeyVaultAutoconfiguredClient;
import es.in2.wallet.crypto.config.KeyVaultClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = {"es.in2.wallet.crypto"})
public class WalletCryptoApplication implements CommandLineRunner {

    private final KeyVaultClient keyVaultClient;

    public WalletCryptoApplication(@Qualifier(value = "KeyVaultAutoconfiguredClient") KeyVaultAutoconfiguredClient keyVaultAutoconfiguredClient) {
        this.keyVaultClient = keyVaultAutoconfiguredClient;
    }

    private static final ObjectMapper OBJECT_MAPPER =
            // sort alphabetically, to ensure same order when hashing.
            JsonMapper.builder().configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true).build();

    public static void main(String[] args) {
        SpringApplication.run(WalletCryptoApplication.class, args);
    }

    @Override
    public void run(String... args) {
        KeyVaultSecret keyVaultSecret = keyVaultClient.getSecret("kizuna-test");
        log.info("Hey, our secret is here -> " + keyVaultSecret.getValue());
    }

    @Bean
    public ObjectMapper objectMapper() {
        // the ObjectMapper should only be created once per JVM for resource reasons. To ensure thread-safety, get
        // the thread-safe reader and writer instances from it
        return OBJECT_MAPPER;
    }

}
