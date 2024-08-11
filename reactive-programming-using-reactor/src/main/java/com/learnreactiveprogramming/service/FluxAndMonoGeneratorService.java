package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

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
                // ALEX, CHLOE -> A, L, E, X, C, H, L, O, E de forma assincrona (não necessariamente nesta ordem)
                .flatMap(s -> splitString_withDelay(s)) // return A, L, E, X, C, H, L, O, E de forma assincrona
                .log(); // db or remote call
    }

    /**
     * flatMap() e concatMap() trabalham de forma semelhante a diferença encontra-se
     * no fato de que o concatMap() preserva a ordenação inicial do ReactiveStreams
     */
    public Flux<String> namesFlux_concatmap(int stringLength) {

        return Flux.fromIterable(Arrays.asList("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                // ALEX, CHLOE -> A, L, E, X, C, H, L, O, E
                .concatMap(s -> splitString_withDelay(s)) // return A, L, E, X, C, H, L, O, E
                .log(); // db or remote call
    }

    public Flux<String> namesFlux_transform(int stringLength) {

        /**
         * transform()
         * Extrair parte da lógica e poder reutiliza-las em diversos locais
         */
        final Function<Flux<String>, Flux<String>> filtermap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringLength);

        /**
         * Ao exeuctar com um numero de caracteres que não existe na lista é esperado um
         * {@link Flux#empty()}
         * Para atribuir um valor padrão podemos utilizar o método defaultIfEmpty
         */
        return Flux.fromIterable(Arrays.asList("alex", "ben", "chloe"))
                .transform(filtermap)
                // ALEX, CHLOE -> A, L, E, X, C, H, L, O, E
                .flatMap(s -> splitString(s)) // return A, L, E, X, C, H, L, O, E
                .defaultIfEmpty("default")
                .log(); // db or remote call
    }


    /**
     * Ao exeuctar com um numero de caracteres que não existe na lista é esperado um
     * {@link Flux#empty()}
     * Ao invés de atribuir um valor pode ser chaamdo um novo flux no lugar, utilizando
     * a função switchIfEmpty
     */
    public Flux<String> namesFlux_transform_switchIfEmpty(int stringLength) {

        /**
         * transform()
         * Extrair parte da lógica e poder reutiliza-las em diversos locais
         */
        final Function<Flux<String>, Flux<String>> filtermap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(s -> splitString(s)); // return A, L, E, X, C, H, L, O, E

        /**
         * Criação de um novo flux para demonstrar o funcionamento do método switchIfEmpty
         */
        final Flux<String> alternativeFlux = Flux.just("default")
                .transform(filtermap); // D, E, F, A, U, L, T


        return Flux.fromIterable(Arrays.asList("alex", "ben", "chloe"))
                .transform(filtermap)
                // ALEX, CHLOE -> A, L, E, X, C, H, L, O, E
                .switchIfEmpty(alternativeFlux)
                .log(); // db or remote call
    }

    public Mono<String> nameMono() {
        return Mono.just("alex");
    }

    public Mono<List<String>> nameMono_flatMap() {
        return Mono.just("alex")
                .map (String::toUpperCase)
                .flatMap(this::splitStringMono)
                .log();
    }

    public Flux<String> explore_concat() {

        final Flux<String> abcFlux = Flux.just("A", "B", "C");

        final Flux<String> defFlux = Flux.just("D", "E", "F");

        return Flux.concat(abcFlux, defFlux).log();
    }

    public Flux<String> explore_concatWith() {

        final Flux<String> abcFlux = Flux.just("A", "B", "C");

        final Flux<String> defFlux = Flux.just("D", "E", "F");

        return abcFlux.concatWith(defFlux).log();
    }

    /**
     * A versão de método merge() no objeto flux é mergeWith()
     */
    public Flux<String> explore_merge() {

        final Flux<String> abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));

        final Flux<String> defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(125));

        return Flux.merge(abcFlux, defFlux).log();
    }

    public Flux<String> explore_concatWithMono() {

        final Mono<String> aFlux = Mono.just("A");

        final Mono<String> bFlux = Mono.just("B");

        return aFlux.concatWith(bFlux).log();
    }

    /**
     * O método zip retorna um tipo tupla que poderá ser utilizada para combinar os elementos do Flux
     */
    public Flux<String> explore_zip() {

        final Flux<String> abcFlux = Flux.just("A", "B", "C");
        final Flux<String> defFlux = Flux.just("D", "E", "F");
        final Flux<String> start123Flux = Flux.just("1", "2", "3");
        final Flux<String> start456Flux = Flux.just("4", "5", "6");

        return Flux.zip(abcFlux, defFlux, start123Flux, start456Flux)
                .map(t4 -> t4.getT1() + t4.getT2() + t4.getT3() + t4.getT4());
    }

    public Flux<String> explore_zip_2() {
        final Flux<String> abcFlux = Flux.just("A", "B", "C");
        final Flux<String> defFlux = Flux.just("D", "E", "F");

        return Flux.zip(abcFlux, defFlux, (first, second) -> first + second);
    }

    /**
     * O uso de flatMapMany() permite que um Mono seja transformado em um Flux
     */
    public Flux<String> nameMono_flatMapMany() {
        return Mono.just("alex")
                .map (String::toUpperCase)
                .flatMapMany(this::splitString)
                .log();
    }

    public Mono<List<String>> splitStringMono(String name) {

        final String[] charArray = name.split("");

        final List<String> nameSplit = Arrays.asList(charArray);

        return Mono.just(nameSplit);
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
