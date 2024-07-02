package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

public class FluxAndMonoGeneratorService {

    public Flux<String> namesFlux() {
        /**
         * ao chamar o .log as informações mais detalhadas sobre a comunicação
         * entre publisher e subscriber são mostradas
         * ex.: request, onNext, onComplete, onError
         */

        return Flux.fromIterable(Arrays.asList("alex", "ben", "chloe"))
                .log(); // db or remote call
    }

    public Mono<String> nameMono() {
        return Mono.just("alex");
    }

    public static void main(String[] args) {

        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

        fluxAndMonoGeneratorService.namesFlux()
                .subscribe((name) -> System.out.println("Name is: " + name));


        fluxAndMonoGeneratorService.nameMono()
                .subscribe((name) -> System.out.println("Mono Name is: " + name));
    }

}
