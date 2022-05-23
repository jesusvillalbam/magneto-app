package com.xmen.magneto.recruitment.domain.service.impl;

import com.xmen.magneto.recruitment.application.RecruitmentRequest;
import com.xmen.magneto.recruitment.application.RecruitmentResponse;
import com.xmen.magneto.recruitment.application.StatsResponse;
import com.xmen.magneto.recruitment.domain.service.MutantService;
import com.xmen.magneto.recruitment.domain.service.RecruitmentService;
import com.xmen.magneto.recruitment.exception.HumanNotMutantException;
import com.xmen.magneto.recruitment.infraestructure.DnaValidationRepository;
import com.xmen.magneto.recruitment.infraestructure.entity.DnaValidation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class RecruitmentServiceImplTest {

    @Autowired
    RecruitmentService recruitmentService;

    @MockBean
    MutantService mutantService;

    @MockBean
    DnaValidationRepository dnaValidationRepository;

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

    @Test
    void getRecruitmentProcessStats_NoRecords() {
        //given
        //when
        when(dnaValidationRepository.findAll()).thenReturn(Flux.empty());
        Mono<StatsResponse> result = recruitmentService.getRecruitmentProcessStats();

        //then
        StepVerifier.create(result)
                .expectErrorMessage("No existen datos disponibles para generar las estadisticas")
                .verify();
    }

    @Test
    void getRecruitmentProcessStats_Success() {
        //given
        Flux<DnaValidation> listFlux  = Flux.just(DnaValidation.builder().dnaSequence("sequence").isMutant(Boolean.TRUE).build(),
                DnaValidation.builder().dnaSequence("sequence").isMutant(Boolean.FALSE).build());
        //when
        when(dnaValidationRepository.findAll()).thenReturn(listFlux);
        Mono<StatsResponse> result = recruitmentService.getRecruitmentProcessStats();

        //then
        StepVerifier.create(result)
                .expectNext(new StatsResponse(1L, 2L,0.5))
                .verifyComplete();
    }

    @Test
    void validateIfHumanIsMutant_IsMutant() {
        //given
        var recruitmentRequest = new RecruitmentRequest(new String[]{
                "ATGCGA",
                "CAGTGC",
                "TCATGT",
                "AGCAGG",
                "CCCCTA",
                "TCACTG"});

        //when
        when(dnaValidationRepository.save(any())).thenReturn(Mono.just(new DnaValidation(1L,"", Boolean.TRUE)));
        when(mutantService.humanIsMutant(any())).thenReturn(Mono.just(Boolean.TRUE));
        Mono<RecruitmentResponse> result = recruitmentService.validateIfHumanIsMutant(recruitmentRequest);

        //then
        StepVerifier.create(result)
                .expectNext(new RecruitmentResponse(Boolean.TRUE))
                .verifyComplete();
    }

    @Test
    void validateIfHumanIsMutant_IsNotMutant() {
        //given
        var recruitmentRequest = new RecruitmentRequest(new String[]{
                "ATGCGA",
                "CAGTGC",
                "TCATGT",
                "AGCCCG",
                "CCCATA",
                "TCACTG"});

        //when
        when(dnaValidationRepository.save(any())).thenReturn(Mono.empty());
        when(mutantService.humanIsMutant(any())).thenThrow(new HumanNotMutantException("Human is not a Mutant! Next... please"));
        Mono<RecruitmentResponse> result = recruitmentService.validateIfHumanIsMutant(recruitmentRequest);

        //then
        StepVerifier.create(result)
                .expectErrorMessage("Human is not a Mutant! Next... please")
                .verify();
    }
}