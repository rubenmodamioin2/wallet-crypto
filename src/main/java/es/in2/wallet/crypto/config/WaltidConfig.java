package es.in2.wallet.crypto.config;

import id.walt.servicematrix.ServiceMatrix;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static es.in2.wallet.crypto.util.Utils.SERVICE_MATRIX;

@Configuration
public class WaltidConfig {

    @Tag(name = "WaltidConfig", description = "Injects Walt.id services at runtime")
    @Bean
    public void instanceServiceMatrix() {
        new ServiceMatrix(SERVICE_MATRIX);
    }

}
