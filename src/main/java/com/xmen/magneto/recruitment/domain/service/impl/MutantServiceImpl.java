package com.xmen.magneto.recruitment.domain.service.impl;

import com.xmen.magneto.recruitment.domain.service.MutantService;
import com.xmen.magneto.recruitment.exception.HumanNotMutantException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class MutantServiceImpl implements MutantService {

    private static final String SEGMENT_SEQUENCE_A = "AAAA";
    private static final String SEGMENT_SEQUENCE_T = "TTTT";
    private static final String SEGMENT_SEQUENCE_C = "CCCC";
    private static final String SEGMENT_SEQUENCE_G = "GGGG";

    @Override
    public Mono<Boolean> humanIsMutant(String[] dnaSequence) {

        var matrix = convertStringArrayToCharMatrix(dnaSequence);

        Mono<Long> horizontalSegmentResult = validateHorizontalSegment(dnaSequence)
                .switchIfEmpty(Mono.just(0L))
                .subscribeOn(Schedulers.parallel());

        Mono<Long> verticalSegmentResult = validateVerticalSegment(matrix)
                .switchIfEmpty(Mono.just(0L))
                .subscribeOn(Schedulers.parallel());

        return Mono.zip(horizontalSegmentResult, verticalSegmentResult)
                .flatMap(t -> {
                    log.info("count horizontal -> {}", t.getT1());
                    log.info("count vertical -> {}", t.getT2());
                    if (t.getT1() > 1L || t.getT2() > 1L || (t.getT1() + t.getT2()) > 1) {
                        return Mono.just(Boolean.TRUE);
                    }

                    return Mono.error(new HumanNotMutantException("Human is not a Mutant! Next... please"));
                });
    }

    private Mono<Long> validateHorizontalSegment(String[] dnaSequence) {
        return Mono.just(Arrays.stream(dnaSequence)
                .filter(x -> x.contains(SEGMENT_SEQUENCE_A)
                        || x.contains(SEGMENT_SEQUENCE_T)
                        || x.contains(SEGMENT_SEQUENCE_C)
                        || x.contains(SEGMENT_SEQUENCE_G))
                .count());
    }

    private char[][] convertStringArrayToCharMatrix(String[] dnaSequence) {
        char[][] charArray = new char[dnaSequence.length][];
        for (int i = 0; i < dnaSequence.length; i++) {
            charArray[i] = dnaSequence[i].toCharArray();
        }
        return charArray;
    }

    private Mono<Long> validateVerticalSegment(char[][] matrix) {
        List<String> verticalSegment = new ArrayList<>();
        for (int i = 0; i < matrix.length; i++) {
            StringBuilder vertical = new StringBuilder();
            for (int j = 0; j < matrix.length; j++) {
                vertical.append(matrix[j][i]);
            }
            verticalSegment.add(vertical.toString());
        }
        return Mono.just(verticalSegment.stream()
                .filter(x -> x.contains(SEGMENT_SEQUENCE_A)
                        || x.contains(SEGMENT_SEQUENCE_T)
                        || x.contains(SEGMENT_SEQUENCE_C)
                        || x.contains(SEGMENT_SEQUENCE_G))
                .count());
    }
}
