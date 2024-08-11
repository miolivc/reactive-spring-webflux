package com.reactivespring.service;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MovieInfoService {

    private final MovieInfoRepository movieInfoRepository;

    @Autowired
    public MovieInfoService(final MovieInfoRepository movieInfoRepository) {
        this.movieInfoRepository = movieInfoRepository;
    }

    public Flux<MovieInfo> getAllMovieInfo() {

        return movieInfoRepository.findAll();
    }

    public Mono<MovieInfo> getMovieInfoById(String movieId) {

        return movieInfoRepository.findById(movieId);
    }

    public Mono<MovieInfo> addMovieInfo(final MovieInfo movieInfo) {

        return movieInfoRepository.save(movieInfo);
    }
}
