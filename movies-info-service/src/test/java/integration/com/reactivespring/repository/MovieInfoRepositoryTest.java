package com.reactivespring.repository;

import com.reactivespring.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryTest {

    @Autowired
    private MovieInfoRepository movieInfoRepository;

    /**
     * Test data link: https://github.com/dilipsundarraj1/reactive-spring-webflux/blob/47bc655d2ecbd2132bcba6d8c4c09146d9f1e9a8/movies-info-service/src/test/java/intg/com/reactivespring/repository/MoviesInfoRepositoryIntgTest.java#L29
     */
    @BeforeEach
    void setUp() {
        var movieinfos = List.of(
                new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"))
        );

        movieInfoRepository.saveAll(movieinfos)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void findAll() {

        var moviesInfoFLux = movieInfoRepository.findAll().log();

        StepVerifier.create(moviesInfoFLux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void findAllByYear() {

        var year = 2005;

        var moviesInfoFLux = movieInfoRepository.findByYear(year).log();

        StepVerifier.create(moviesInfoFLux)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findById() {

        var movieInfo = movieInfoRepository.findById("abc");

        StepVerifier.create(movieInfo)
                .assertNext(movieInfo1 -> {
                    assertEquals("Dark Knight Rises", movieInfo1.getTitle());
                })
                .verifyComplete();
    }

    @Test
    void findByTitle() {

        var title = "Dark Knight Rises";

        var movieInfo = movieInfoRepository.findById(title).log();

        StepVerifier.create(movieInfo)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void saveMovieInfo() {

        var movieInfo = new MovieInfo(null, "Batman Begins1",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        var savedMovieInfo = movieInfoRepository.save(movieInfo);

        StepVerifier.create(savedMovieInfo)
                .assertNext(movieInfo1 -> {
                    assertNotNull(movieInfo1.getId());
                    assertEquals("Batman Begins1", movieInfo1.getTitle());
                })
                .verifyComplete();

    }

    @Test
    void updateMovieInfo() {

        var movieInfo = movieInfoRepository.findById("abc").block();
        movieInfo.setYear(2021);

        var savedMovieInfo = movieInfoRepository.save(movieInfo);

        StepVerifier.create(savedMovieInfo)
                .assertNext(movieInfo1 -> {
                    assertNotNull(movieInfo1.getId());
                    assertEquals(2021, movieInfo1.getYear());
                })
                .verifyComplete();

    }

    @Test
    void deleteMovieInfo() {

        movieInfoRepository.deleteById("abc").block();

        var movieInfos = movieInfoRepository.findAll();

        StepVerifier.create(movieInfos)
                .expectNextCount(2)
                .verifyComplete();

    }

}
