package com.xmen.magneto.recruitment.domain.service.impl;

import com.xmen.magneto.recruitment.application.RecruitmentRequest;
import com.xmen.magneto.recruitment.application.RecruitmentResponse;
import com.xmen.magneto.recruitment.application.StatsResponse;
import com.xmen.magneto.recruitment.domain.service.MutantService;
import com.xmen.magneto.recruitment.domain.service.RecruitmentService;
import com.xmen.magneto.recruitment.exception.BusinessException;
import com.xmen.magneto.recruitment.exception.HumanNotMutantException;
import com.xmen.magneto.recruitment.exception.NotFoundException;
import com.xmen.magneto.recruitment.infraestructure.DnaValidationRepository;
import com.xmen.magneto.recruitment.infraestructure.entity.DnaValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

@Slf4j
@Service
public class RecruitmentServiceImpl implements RecruitmentService {

    private final MutantService mutantService;
    private final DnaValidationRepository dnaValidationRepository;

    private static final int MINIMUM_DNA_SEGMENT = 2;
    private static final int MINIMUM_DNA_NITROGENOUS_BASE = 4;
    private static final String PERMITTED_DNA_NITROGENOUS_BASE = "^[ATCG]+$";

    public RecruitmentServiceImpl(MutantService mutantService, DnaValidationRepository dnaValidationRepository) {
        this.mutantService = mutantService;
        this.dnaValidationRepository = dnaValidationRepository;
    }

    @Override
    public Mono<RecruitmentResponse> validateIfHumanIsMutant(RecruitmentRequest recruitmentRequest) {
        log.debug("Validating if Human is a mutant -> DNA Sequence: {}", recruitmentRequest);
        return Mono.just(recruitmentRequest)
                .flatMap(x -> validateDnaSequenceIntegrity(x.getDna()))
                .flatMap(mutantService::humanIsMutant)
                .flatMap(result -> dnaValidationRepository.save(DnaValidation.builder()
                        .dnaSequence(String.join(",", recruitmentRequest.getDna()))
                        .isMutant(result)
                        .build()))
                .flatMap(dnaValidationResult -> Mono.just(new RecruitmentResponse(dnaValidationResult.getIsMutant())))
                .doOnError(ex -> {
                    if(ex instanceof HumanNotMutantException){
                        dnaValidationRepository.save(DnaValidation.builder()
                                .dnaSequence(String.join(",", recruitmentRequest.getDna()))
                                .isMutant(Boolean.FALSE)
                                .build()).subscribe();
                    }
                });
    }

    @Override
    public Mono<StatsResponse> getRecruitmentProcessStats() {
        Flux<DnaValidation> dnaValidations = dnaValidationRepository.findAll()
                .switchIfEmpty(Mono.error(new NotFoundException("No existen datos disponibles para generar las estadisticas")));

        Mono<Long> totalValidations = dnaValidations.count();
        Mono<Long> totalMutants = dnaValidations.filter(x -> x.getIsMutant().equals(Boolean.TRUE)).count();

        return Mono.zip(totalValidations, totalMutants)
                .flatMap(tuples -> Mono.just(
                        new StatsResponse(tuples.getT2(),
                                tuples.getT1(),
                                (double) tuples.getT2() / tuples.getT1())));
    }

    private Mono<String[]> validateDnaSequenceIntegrity(String[] dnaSequence) {
        if (dnaSequence == null || dnaSequence.length < MINIMUM_DNA_SEGMENT) {
            throw new BusinessException("Se requieren más de un segmento de ADN para realizar la validación del humano");
        }

        Stream.of(dnaSequence).forEach(this::validateDnaNitrogenousBase);
        int sumOfVectorLength = Stream.of(dnaSequence).mapToInt(String::length).sum();

        if (sumOfVectorLength % dnaSequence.length != 0) {
            throw new BusinessException("El tamaño de los segmentos de ADN deben coincidir");
        }

        if (dnaSequence.length != (sumOfVectorLength / dnaSequence.length)) {
            throw new BusinessException("El tamaño de los segmentos de ADN deben coincidir con el tamaño total del ADN");
        }

        return Mono.just(dnaSequence);
    }

    private void validateDnaNitrogenousBase(String dnaNitrogenousBase) {
        if (dnaNitrogenousBase.length() < MINIMUM_DNA_NITROGENOUS_BASE) {
            throw new BusinessException(String.format("El tamaño del segmento de ADN %s no puede tener un tamaño menor a cuatro", dnaNitrogenousBase));
        }

        if (!dnaNitrogenousBase.matches(PERMITTED_DNA_NITROGENOUS_BASE)) {
            throw new BusinessException("Las bases nitrogenadas de alguno de los segmentos no es válida");
        }
    }
}
