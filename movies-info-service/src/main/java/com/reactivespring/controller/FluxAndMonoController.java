package com.reactivespring.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class FluxAndMonoController {

    @GetMapping("/flux")
    public Flux<Integer> flux() {

        return Flux.just(1, 2, 3);
    }

    @GetMapping("/mono")
    public Mono<String> mono() {

        return Mono.just("hello-world");
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Long> stream() {

        /**
         * Valores inteiros são retornados por esse fluxo pois {@link Flux#interval(Duration)}
         * retorna um inteiro que representa o tempo incrementado entre uma duração de tempo e
         * outra. No caso como está sendo incrementado 1 segundo, serão inteiros não negativos,
         * incrementados do valor 1.
         */
        return Flux.interval(Duration.ofSeconds(1))
                .log();
    }

}
