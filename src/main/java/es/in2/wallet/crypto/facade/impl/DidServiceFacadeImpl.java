package es.in2.wallet.crypto.facade.impl;

import es.in2.wallet.crypto.config.properties.AppProperties;
import es.in2.wallet.crypto.facade.DidServiceFacade;
import es.in2.wallet.crypto.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static es.in2.wallet.crypto.util.Utils.PROCESS_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DidServiceFacadeImpl implements DidServiceFacade {

    private final AppProperties appProperties;
    private final KeyGenerationService keyGenerationService;
    private final DidGenerationService didGenerationService;
    private final HashiCorpVaultStorageService hashiCorpVaultStorageService;
    private final AzureKeyVaultStorageService azureKeyVaultStorageService;
    private final DataStorageService dataStorageService;

    @Override
    public Mono<String> createDidKeyAndPersistIntoWalletDataAndVault(String token) {
        String processId = MDC.get(PROCESS_ID);
        // Key Generation
        return keyGenerationService.generateKey()
                // Key Export
                .flatMap(keyGenerationService::exportKey)
                // DID Generation
                .flatMap(keyDetails -> didGenerationService.createDidKey(keyDetails.keyId())
                        .flatMap(did -> {
                            if (appProperties.secretProvider().name().equals("hashicorp")) {
                                return hashiCorpVaultStorageService.saveSecret(did, keyDetails.privateKey())
                                        // DID Persistence in Wallet Data
                                        // todo - uncomment this line when the DID persistence in Wallet Data is implemented
//                                        .then(dataStorageService.saveDidKey(token, did))
                                        .thenReturn(did);
                            } else if (appProperties.secretProvider().name().equals("azure")) {
                                return azureKeyVaultStorageService.saveSecret(did, keyDetails.privateKey())
                                        // DID Persistence in Wallet Data
                                        // todo - uncomment this line when the DID persistence in Wallet Data is implemented
//                                        .then(dataStorageService.saveDidKey(token, did))
                                        .thenReturn(did);
                            } else {
                                return Mono.error(new Exception("Secret provider not supported"));
                            }
                        })
                        .doOnSuccess(did -> log.info("ProcessID: {} - DID created and saved successfully: {}", processId, did))
                        .doOnError(throwable -> log.error("ProcessID: {} - Failed to create or save DID: {}", processId, throwable.getMessage()))
                );
    }

}
