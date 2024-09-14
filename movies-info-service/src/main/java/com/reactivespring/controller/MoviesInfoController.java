package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/v1")
public class MoviesInfoController {

    private final MovieInfoService movieInfoService;

    /**
     * Ao usar replay all todos os subscribers receberão todos os dados
     * que foram publicados no sink, mesmo antes de sua subscrição.
     * Caso no lugar do all o latest for usado, novos subscriptions
     * receberão apenas a ultima informação recebida em diante.
     */
    Sinks.Many<MovieInfo> movieInfoSink = Sinks.many().replay().all();

    @Autowired
    public MoviesInfoController(final MovieInfoService movieInfoService) {
        this.movieInfoService = movieInfoService;
    }

    @GetMapping("/movies-info")
    public Flux<MovieInfo> getAllMovieInfo(@RequestParam(value = "year", required = false) Integer year) {

        log.info("Year is: {}", year);

        if (year != null) {
            return movieInfoService.getAllMovieInfoByYear(year);
        }

        return movieInfoService.getAllMovieInfo();
    }

    @GetMapping("/movies-info/{id}")
    public Mono<ResponseEntity<MovieInfo>> getMovieInfoById(@PathVariable("id") String movieId) {

        return movieInfoService.getMovieInfoById(movieId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping(value = "/movies-info/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<MovieInfo> getMovieInfoById() {

        // Sink subscriber
        return movieInfoSink.asFlux();
    }

    @PostMapping("/movies-info")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid final MovieInfo movieInfo) {

        // quando uma nova MovieInfo é adicionada, é publicada na sink
        return movieInfoService.addMovieInfo(movieInfo)
                .doOnNext(savedMovieInfo -> movieInfoSink.tryEmitNext(savedMovieInfo));
    }

    @PutMapping("/movies-info/{id}")
    public Mono<ResponseEntity<MovieInfo>> updateMovieInfo(@PathVariable("id") String movieId,
                                                @RequestBody final MovieInfo updatedMovieInfo) {

        return movieInfoService.updateMovieInfo(movieId, updatedMovieInfo)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/movies-info/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> updateMovieInfo(@PathVariable("id") String movieId) {

        return movieInfoService.deleteMovieInfo(movieId);
    }

}
