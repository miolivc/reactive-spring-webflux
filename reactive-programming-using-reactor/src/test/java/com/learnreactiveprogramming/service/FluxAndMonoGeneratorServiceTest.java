package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService =
            new FluxAndMonoGeneratorService();

    @Test
    void namesFlux() {

        final Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux();

        StepVerifier.create(namesFlux)
                .expectNext("alex", "ben", "chloe")
                .verifyComplete();

        StepVerifier.create(namesFlux)
                .expectNextCount(3)
                .expectComplete();

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

    @Test
    void namesFlux_flatmap() {

        int stringLength = 3;

        final Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_flatmap(stringLength);

        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();

    }

    @Test
    void namesFlux_flatmap_async() {

        int stringLength = 3;

        final Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_flatmap_async(stringLength);

        /**
         * A partir do momento em que esta verificação torna-se sequencial numa chamada com delay async
         * o teste deixa de passar devido aos elementos não serem mais tratados de maneira ordenada
         * para resolver irá ser modificado para esperar um count desses elementos
         */
//        StepVerifier.create(namesFlux)
//                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
//                .verifyComplete();
//
        StepVerifier.create(namesFlux)
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesFlux_concatmap() {

        int stringLength = 3;

        final Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_concatmap(stringLength);

        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();

        StepVerifier.create(namesFlux)
                .expectNextCount(9)
                .verifyComplete();

    }

    @Test
    void nameMono_flatMap() {

        final Mono<List<String>> value = fluxAndMonoGeneratorService.nameMono_flatMap();

        /**
         * Neste caso apesar do uso do flatMap, por tratar-se de um mono este resultado retorna de
         * forma ordenada pois o método {@link FluxAndMonoGeneratorService#splitStringMono(String)}
         * trabalha de forma síncrona
         */
        StepVerifier.create(value)
                .expectNext(Arrays.asList("A", "L", "E", "X"))
                .verifyComplete();
    }

    @Test
    void nameMono_flatMapMany() {

        final Flux<String> value = fluxAndMonoGeneratorService.nameMono_flatMapMany();

        /**
         * Neste caso apesar do uso do flatMapManu, por tratar-se de um mono este resultado retorna de
         * forma ordenada pois o método {@link FluxAndMonoGeneratorService#splitString(String)}
         * trabalha de forma síncrona
         */
        StepVerifier.create(value)
                .expectNext("A", "L", "E", "X")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform() {

        int stringLength = 3;

        final Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_transform(stringLength);

        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform_defaultIfEmpty() {

        int stringLength = 6;

        final Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_transform(stringLength);

        StepVerifier.create(namesFlux)
//                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform_switchIfEmpty() {

        int stringLength = 6;

        final Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_transform_switchIfEmpty(stringLength);

        StepVerifier.create(namesFlux)
                .expectNext("D", "E", "F", "A", "U", "L", "T") // D, E, F, A, U, L, T
                .verifyComplete();
    }
}
