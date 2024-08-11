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

    public Mono<MovieInfo> updateMovieInfo(final String movieId,
                                           final MovieInfo updatedMovieInfo) {

        return movieInfoRepository.findById(movieId)
                .flatMap(movieInfo -> {
                    movieInfo.setTitle(updatedMovieInfo.getTitle());
                    movieInfo.setYear(updatedMovieInfo.getYear());
                    movieInfo.setCast(updatedMovieInfo.getCast());
                    movieInfo.setReleasedAt(updatedMovieInfo.getReleasedAt());

                    return movieInfoRepository.save(movieInfo);
                });
    }

    public Mono<Void> deleteMovieInfo(final String movieId) {
        return movieInfoRepository.deleteById(movieId);
    }
}
