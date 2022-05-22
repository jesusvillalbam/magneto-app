package com.xmen.magneto.recruitment.domain.service.impl;

import com.xmen.magneto.recruitment.application.RecruitmentRequest;
import com.xmen.magneto.recruitment.application.RecruitmentResponse;
import com.xmen.magneto.recruitment.domain.service.RecruitmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class RecruitmentServiceImplTest {

    @Autowired
    RecruitmentService recruitmentService;

    @Test
    void validateIfHumanIsMutant_DnaSequenceNull() {
        //given
        var recruitmentRequest = new RecruitmentRequest(null);

        //when
        Mono<RecruitmentResponse> result = recruitmentService.validateIfHumanIsMutant(recruitmentRequest);

        //then
        StepVerifier.create(result)
                .expectErrorMessage("Se requieren más de un segmento de ADN para realizar la validación del humano")
                .verify();
    }

    @Test
    void validateIfHumanIsMutant_DnaSequenceOnlyOneSegment() {
        //given
        var recruitmentRequest = new RecruitmentRequest(new String[]{"a"});

        //when
        Mono<RecruitmentResponse> result = recruitmentService.validateIfHumanIsMutant(recruitmentRequest);

        //then
        StepVerifier.create(result)
                .expectErrorMessage("Se requieren más de un segmento de ADN para realizar la validación del humano")
                .verify();
    }

    @Test
    void validateIfHumanIsMutant_DnaSequenceSegmentLengthCannotBeLessThanFour() {
        //given
        var recruitmentRequest = new RecruitmentRequest(new String[]{"ATGCGA", "CAG"});

        //when
        Mono<RecruitmentResponse> result = recruitmentService.validateIfHumanIsMutant(recruitmentRequest);

        //then
        StepVerifier.create(result)
                .expectErrorMessage("El tamaño del segmento de ADN CAG no puede tener un tamaño menor a cuatro")
                .verify();
    }

    @Test
    void validateIfHumanIsMutant_DnaSequenceNitrogenousBaseNotValid() {
        //given
        var recruitmentRequest = new RecruitmentRequest(new String[]{"ATGCGA", "ABCDEF"});

        //when
        Mono<RecruitmentResponse> result = recruitmentService.validateIfHumanIsMutant(recruitmentRequest);

        //then
        StepVerifier.create(result)
                .expectErrorMessage("Las bases nitrogenadas de alguno de los segmentos no es válida")
                .verify();
    }

    @Test
    void validateIfHumanIsMutant_DnaSequenceSegmentsMustHaveSameLength() {
        //given
        var recruitmentRequest = new RecruitmentRequest(new String[]{"ATGCGA", "CAGTG", "TTATGT", "AGAAGG", "CCCCT", "TCACTG"});

        //when
        Mono<RecruitmentResponse> result = recruitmentService.validateIfHumanIsMutant(recruitmentRequest);

        //then
        StepVerifier.create(result)
                .expectErrorMessage("El tamaño de los segmentos de ADN deben coincidir")
                .verify();
    }

    @Test
    void validateIfHumanIsMutant_DnaSequenceSegmentsMustHaveSameLengthThanDNA() {
        //given
        var recruitmentRequest = new RecruitmentRequest(new String[]{
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCACTA",
                "TCACTG",
                "CCACTA",
                "TCACTG"});

        //when
        Mono<RecruitmentResponse> result = recruitmentService.validateIfHumanIsMutant(recruitmentRequest);

        //then
        StepVerifier.create(result)
                .expectErrorMessage("El tamaño de los segmentos de ADN deben coincidir con el tamaño total del ADN")
                .verify();
    }
}