package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ReviewHandler {

    private final ReviewReactiveRepository repository;

    public ReviewHandler(final ReviewReactiveRepository repository) {
        this.repository = repository;
    }

    public Mono<ServerResponse> addReview(final ServerRequest request) {

        /**
         * para utilizar o salvamento de forma reativa e
         * retornar o valor usa-se o flatMap
         */
        return request.bodyToMono(Review.class)
                .flatMap(repository::save)
                .flatMap(savedReview -> ServerResponse.status(HttpStatus.CREATED).bodyValue(savedReview));
    }

    public Mono<ServerResponse> getReviews(final ServerRequest request) {

        final Flux<Review> reviewsFlux = repository.findAll();

        return ServerResponse.ok().body(reviewsFlux, Review.class);
    }
    
    public Mono<ServerResponse> updateReview(final ServerRequest request) {

        final String reviewId = request.pathVariable("id");

        final Mono<Review> existingReview = repository.findById(reviewId);

        return existingReview
                .flatMap(review -> request.bodyToMono(Review.class)
                        .map(requestReview -> {
                            review.setComment(requestReview.getComment());
                            review.setRating(requestReview.getRating());

                            return review;
                        })
                        .flatMap(repository::save)
                        .flatMap(savedReview -> ServerResponse.ok().bodyValue(savedReview))
                );
    }
    
}
