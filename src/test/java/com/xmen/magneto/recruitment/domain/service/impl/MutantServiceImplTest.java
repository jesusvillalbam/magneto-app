package com.xmen.magneto.recruitment.domain.service.impl;

import com.xmen.magneto.recruitment.domain.service.MutantService;
import com.xmen.magneto.recruitment.exception.HumanNotMutantException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MutantServiceImplTest {

    @Autowired
    MutantService mutantService;

    @Test
    void humanIsMutant_True() {
        //given
        String[] dnaSequence = new String[]{
                "ATGCGA",
                "CAGTGC",
                "TCATGT",
                "AGCAGG",
                "CCCCTA",
                "TCACTG"};

        //when
        Mono<Boolean> result = mutantService.humanIsMutant(dnaSequence);

        //then
        StepVerifier.create(result)
                .expectNext(Boolean.TRUE)
                .verifyComplete();

    }

    @Test
    void humanIsMutant_False() {
        //given
        String[] dnaSequence = new String[]{
                "ATGCGA",
                "CAGTGC",
                "TTCTGT",
                "AGAAGG",
                "CCCATA",
                "TCACTG"
        };

        //when
        Mono<Boolean> result = mutantService.humanIsMutant(dnaSequence);

        //then
        StepVerifier.create(result)
                .expectError(HumanNotMutantException.class)
                .verify();

    }
}