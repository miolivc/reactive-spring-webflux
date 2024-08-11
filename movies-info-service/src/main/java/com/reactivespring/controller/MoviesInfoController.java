package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Mono<MovieInfo> getMovieInfoById(@RequestParam("id") String movieId) {

        return movieInfoService.getMovieInfoById(movieId);
    }

    @PostMapping("/movies-info")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody final MovieInfo movieInfo) {

        return movieInfoService.addMovieInfo(movieInfo);
    }

}
