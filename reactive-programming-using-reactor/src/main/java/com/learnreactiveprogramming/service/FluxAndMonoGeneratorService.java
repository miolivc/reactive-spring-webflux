package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.Random;

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

    public Flux<String> namesFlux_map(int stringLength) {

        return Flux.fromIterable(Arrays.asList("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .map(s-> s.length() + "-" + s)
                .log(); // db or remote call
    }

    public Flux<String> namesFlux_immutability() {

        final Flux<String> namesFlux = Flux.fromIterable(Arrays.asList("alex", "ben", "chloe"));

        // nenhuma mudança no fluxo foi realizado devido a reactive Flux ser imutável
        namesFlux.map(String::toUpperCase);
        return namesFlux;
    }

    public Flux<String> namesFlux_flatmap(int stringLength) {

        return Flux.fromIterable(Arrays.asList("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                // ALEX, CHLOE -> A, L, E, X, C, H, L, O, E
                .flatMap(s -> splitString(s)) // return A, L, E, X, C, H, L, O, E
                .log(); // db or remote call
    }

    public Flux<String> splitString(String name) {

        final String[] split = name.split("");

        return Flux.fromArray(split);
    }

    public Flux<String> namesFlux_flatmap_async(int stringLength) {

        return Flux.fromIterable(Arrays.asList("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                // ALEX, CHLOE -> A, L, E, X, C, H, L, O, E
                .flatMap(s -> splitString_withDelay(s)) // return A, L, E, X, C, H, L, O, E
                .log(); // db or remote call
    }

    public Flux<String> splitString_withDelay(String name) {

        final String[] split = name.split("");

        final long delay = new Random().nextInt(1000);

        return Flux.fromArray(split)
                .delayElements(Duration.ofMillis(delay));
    }

    public static void main(String[] args) {

        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

        fluxAndMonoGeneratorService.namesFlux()
                .subscribe((name) -> System.out.println("Name is: " + name));


        fluxAndMonoGeneratorService.nameMono()
                .subscribe((name) -> System.out.println("Mono Name is: " + name));
    }

}
