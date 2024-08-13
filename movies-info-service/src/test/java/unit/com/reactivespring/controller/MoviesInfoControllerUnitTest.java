package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest(MoviesInfoController.class)
@AutoConfigureWebTestClient
public class MoviesInfoControllerUnitTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MovieInfoService movieInfoServiceMock;

    private static final String MOVIES_INFO_URL = "/v1/movies-info";

    @Test
    public void getAllMoviesInfo() {

        var movieinfos = List.of(
                new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"))
        );

        when(movieInfoServiceMock.getAllMovieInfo()).thenReturn(Flux.fromIterable(movieinfos));

        webTestClient.get()
                .uri(MOVIES_INFO_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void getMovieInfoById() {

        var movieId = "abc";

        when(movieInfoServiceMock.getMovieInfoById(isA(String.class)))
                .thenReturn(Mono.just(new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"))));

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
    void addMovieInfo() {

        final MovieInfo movieInfo = MovieInfo.builder()
                .title("Batman Begins1")
                .year(2005)
                .cast(List.of("Christian Bale", "Michael Cane"))
                .releasedAt(LocalDate.parse("2005-06-15"))
                .build();

        when(movieInfoServiceMock.addMovieInfo(isA(MovieInfo.class))).thenReturn(
                Mono.just(
                        MovieInfo.builder()
                                .id("mockId")
                                .title("Batman Begins1")
                                .year(2005)
                                .cast(List.of("Christian Bale", "Michael Cane"))
                                .releasedAt(LocalDate.parse("2005-06-15"))
                                .build()
                )
        );

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
                    assertEquals("mockId", responseBody.getId());
                });
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

        when(movieInfoServiceMock.updateMovieInfo(isA(String.class), isA(MovieInfo.class))).thenReturn(
                Mono.just(
                        MovieInfo.builder()
                                .id(movieId)
                                .title("Dark Knight Rises1")
                                .year(2005)
                                .cast(List.of("Christian Bale", "Michael Cane"))
                                .releasedAt(LocalDate.parse("2005-06-15"))
                                .build()
                )
        );

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
                    assertEquals("abc", responseBody.getId());
                });
    }

    @Test
    void deleteMovieInfo() {
        var movieId = "abc";

        when(movieInfoServiceMock.deleteMovieInfo(isA(String.class))).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri(MOVIES_INFO_URL + "/{id}", movieId)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody(Void.class);
    }

}
