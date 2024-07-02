package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService =
            new FluxAndMonoGeneratorService();

    @Test
    void namesFlux() {

        final Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux();

        StepVerifier.create(namesFlux)
                .expectNextCount(3)
                .expectNext("alex", "ben", "chloe")
                .verifyComplete();

        StepVerifier.create(namesFlux)
                .expectNext("alex")
                .expectNextCount(2)
                .verifyComplete();

    }

    @Test
    void namesFlux_map() {

        int stringLength = 3;

        final Flux<String> namesFlux_map = fluxAndMonoGeneratorService.namesFlux_map(stringLength);

        StepVerifier.create(namesFlux_map)
                .expectNext("4-ALEX", "5-CHLOE")
                .verifyComplete();

    }

    /**
     * Este teste falha, ele mostra que a natureza de um flux é imutável
     * como a função map dentro de {@link FluxAndMonoGeneratorService#namesFlux_immutability()}
     * não é reatribuida, nennhum dado dentro do flux é modificado, retornando o
     * estado inicial do mesmo (nomes com lowercase)
     *
     * Para que este teste passe o método expectNext deve esperar os valores iniciais
     * aplicados, neste caso .expectNext("alex", "ben", "chloe")
     */
    @Test
    void namesFlux_immutability() {

        final Flux<String> namesFlux_map = fluxAndMonoGeneratorService.namesFlux_immutability();

        StepVerifier.create(namesFlux_map)
                .expectNext("ALEX", "BEN", "CHLOE")
                .verifyComplete();
    }
}
