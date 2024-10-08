package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class MoviesInfoControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private MovieInfoRepository movieInfoRepository;

    private static final String MOVIES_INFO_URL = "/v1/movies-info";

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
    void addMovieInfo() {

        final MovieInfo movieInfo = MovieInfo.builder()
                .title("Batman Begins1")
                .year(2005)
                .cast(List.of("Christian Bale", "Michael Cane"))
                .releasedAt(LocalDate.parse("2005-06-15"))
                .build();

        webTestClient.post()
                .uri(MOVIES_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    final MovieInfo responseBody = movieInfoEntityExchangeResult.getResponseBody();
                    assert responseBody != null;
                    assertNotNull(responseBody.getId());
                });
    }

    @Test
    void getAllMovieInfo() {

        webTestClient.get()
                .uri(MOVIES_INFO_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void getAllMovieInfo_stream() {

        final MovieInfo movieInfo = MovieInfo.builder()
                .title("Batman Begins1")
                .year(2005)
                .cast(List.of("Christian Bale", "Michael Cane"))
                .releasedAt(LocalDate.parse("2005-06-15"))
                .build();

        webTestClient.post()
                .uri(MOVIES_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    final MovieInfo responseBody = movieInfoEntityExchangeResult.getResponseBody();
                    assert responseBody != null;
                    assertNotNull(responseBody.getId());
                });

        var movieInfoStreamFlux = webTestClient.get()
                .uri(MOVIES_INFO_URL + "/stream")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(MovieInfo.class)
                .getResponseBody();

        StepVerifier.create(movieInfoStreamFlux)
                .assertNext(movieInfo1 -> {
                    assert movieInfo1 != null;
                    assert movieInfo1.getId() != null;
                })
                .thenCancel()
                .verify();
    }


    @Test
    void getAllMovieInfoByYear() {

        final URI moviesInfoUrlParamYear = UriComponentsBuilder.fromUriString(MOVIES_INFO_URL)
                .queryParam("year", 2005)
                .buildAndExpand()
                .toUri();

        webTestClient.get()
                .uri(moviesInfoUrlParamYear)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(1);
    }

    @Test
    void getMovieInfoById() {

        var movieId = "abc";

        webTestClient.get()
                .uri(MOVIES_INFO_URL + "/{id}", movieId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    final MovieInfo responseBody = movieInfoEntityExchangeResult.getResponseBody();
                    assert responseBody != null;
                });
    }

    @Test
    void getMovieInfoById_notfound() {

        var movieId = "def";

        webTestClient.get()
                .uri(MOVIES_INFO_URL + "/{id}", movieId)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void getMovieInfoByIdJsonPath() {

        var movieId = "abc";

        webTestClient.get()
                .uri(MOVIES_INFO_URL + "/{id}", movieId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Dark Knight Rises");
    }

    @Test
    void updateMovieInfo() {

        var movieId = "abc";

        final MovieInfo movieInfo = MovieInfo.builder()
                .title("Dark Knight Rises1")
                .year(2005)
                .cast(List.of("Christian Bale", "Michael Cane"))
                .releasedAt(LocalDate.parse("2005-06-15"))
                .build();

        webTestClient.put()
                .uri(MOVIES_INFO_URL + "/{id}", movieId)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    final MovieInfo responseBody = movieInfoEntityExchangeResult.getResponseBody();
                    assert responseBody != null;
                    assertNotNull(responseBody.getId());
                    assertEquals("Dark Knight Rises1", responseBody.getTitle());
                });
    }

    @Test
    void updateMovieInfo_notfound() {

        var movieId = "def";

        final MovieInfo movieInfo = MovieInfo.builder()
                .title("Dark Knight Rises1")
                .year(2005)
                .cast(List.of("Christian Bale", "Michael Cane"))
                .releasedAt(LocalDate.parse("2005-06-15"))
                .build();

        webTestClient.put()
                .uri(MOVIES_INFO_URL + "/{id}", movieId)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void deleteMovieInfo() {
        var movieId = "abc";

        webTestClient.delete()
                .uri(MOVIES_INFO_URL + "/{id}", movieId)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody(Void.class);
    }

}
