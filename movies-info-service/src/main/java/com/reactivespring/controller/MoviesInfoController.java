package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
public class MoviesInfoController {

    private final MovieInfoService movieInfoService;

    @Autowired
    public MoviesInfoController(final MovieInfoService movieInfoService) {
        this.movieInfoService = movieInfoService;
    }

    @GetMapping("/movies-info")
    public Flux<MovieInfo> getAllMovieInfo() {

        return movieInfoService.getAllMovieInfo();
    }

    @GetMapping("/movies-info/{id}")
    public Mono<MovieInfo> getMovieInfoById(@PathVariable("id") String movieId) {

        return movieInfoService.getMovieInfoById(movieId);
    }

    @PostMapping("/movies-info")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid final MovieInfo movieInfo) {

        return movieInfoService.addMovieInfo(movieInfo);
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
