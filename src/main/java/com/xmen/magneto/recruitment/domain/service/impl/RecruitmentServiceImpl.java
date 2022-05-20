package com.xmen.magneto.recruitment.domain.service.impl;

import com.xmen.magneto.recruitment.application.RecruitmentRequest;
import com.xmen.magneto.recruitment.application.RecruitmentResponse;
import com.xmen.magneto.recruitment.domain.service.RecruitmentService;
import com.xmen.magneto.recruitment.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

@Slf4j
@Service
public class RecruitmentServiceImpl implements RecruitmentService {

    private static int MINIMUM_DNA_SEGMENT = 2;
    private static int MINIMUM_DNA_NITROGENOUS_BASE = 4;
    private static String PERMITTED_DNA_NITROGENOUS_BASE = "^[ATCG]+$";

    @Override
    public Mono<RecruitmentResponse> validateIfHumanIsMutant(RecruitmentRequest recruitmentRequest) {
        log.debug("Validating if Human is a mutant -> DNA Sequence: {}", recruitmentRequest);
        return Mono.just(recruitmentRequest)
                .flatMap(x -> validateDnaSequenceIntegrity(x.getDnaSequence()))
                .flatMap(strings -> Mono.just(new RecruitmentResponse(false)));
    }

    private Mono<String[]> validateDnaSequenceIntegrity(String[] dnaSequence) {
        if (dnaSequence == null || dnaSequence.length < MINIMUM_DNA_SEGMENT) {
            throw new BusinessException("Se requieren más de un segmento de ADN para realizar la validación del humano");
        }

        Stream.of(dnaSequence).forEach(this::validateDnaNitrogenousBase);
        int sumOfVectorLength = Stream.of(dnaSequence).mapToInt(eachVector -> eachVector.length()).sum();

        if (sumOfVectorLength % dnaSequence.length != 0) {
            throw new BusinessException("El tamaño de los segmentos de ADN deben coincidir");
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
