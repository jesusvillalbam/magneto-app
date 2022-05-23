package com.xmen.magneto.recruitment.domain.service.impl;

import com.xmen.magneto.recruitment.domain.service.MutantService;
import com.xmen.magneto.recruitment.exception.HumanNotMutantException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.regex.Pattern;

@Slf4j
@Service
public class MutantServiceImpl implements MutantService {
    private final Pattern p = Pattern.compile("(AAAA|CCCC|TTTT|GGGG)");

    @Override
    public Mono<Boolean> humanIsMutant(String[] dnaSequence) {

        var matrix = convertStringArrayToCharMatrix(dnaSequence);

        Mono<Long> horizontalSegmentResult = validateHorizontalSegment(dnaSequence)
                .switchIfEmpty(Mono.just(0L))
                .subscribeOn(Schedulers.parallel());

        Mono<Long> verticalSegmentResult = validateVerticalSegment(matrix)
                .switchIfEmpty(Mono.just(0L))
                .subscribeOn(Schedulers.parallel());

        Mono<Long> principalDiagonalRightToLeftSegmentResult = validatePrincipalDiagonalRightToLeftSegment(dnaSequence)
                .switchIfEmpty(Mono.just(0L))
                .subscribeOn(Schedulers.parallel());

        Mono<Long> principalDiagonalLeftToRightSegmentResult = validatePrincipalDiagonalLeftToRightSegment(dnaSequence)
                .switchIfEmpty(Mono.just(0L))
                .subscribeOn(Schedulers.parallel());

        Mono<Long> secondaryDiagonalLeftToRightFirstSegmentResult = validateSecondaryDiagonalLeftToRightFirstSegment(dnaSequence)
                .switchIfEmpty(Mono.just(0L))
                .subscribeOn(Schedulers.parallel());

        Mono<Long> secondaryDiagonalLeftToRightSecondSegmentResult = validateSecondaryDiagonalLeftToRightSecondSegment(dnaSequence)
                .switchIfEmpty(Mono.just(0L))
                .subscribeOn(Schedulers.parallel());

        return Mono.zip(horizontalSegmentResult,
                        verticalSegmentResult,
                        principalDiagonalRightToLeftSegmentResult,
                        principalDiagonalLeftToRightSegmentResult,
                        secondaryDiagonalLeftToRightFirstSegmentResult,
                        secondaryDiagonalLeftToRightSecondSegmentResult)
                .flatMap(t -> {
                    log.info("count horizontal -> {}", t.getT1());
                    log.info("count vertical -> {}", t.getT2());
                    log.info("count diagonal 1 -> {}", t.getT3());
                    log.info("count diagonal 2 -> {}", t.getT4());
                    log.info("count diagonal 3 -> {}", t.getT5());
                    log.info("count diagonal 4 -> {}", t.getT6());
                    if (t.getT1() > 1L
                            || t.getT2() > 1L
                            || t.getT3() > 1L
                            || t.getT4() > 1L
                            || t.getT5() > 1L
                            || t.getT6() > 1L
                            || (t.getT1() + t.getT2() + t.getT3() + t.getT4() + t.getT5() + t.getT6()) > 1) {
                        return Mono.just(Boolean.TRUE);
                    }

                    return Mono.error(new HumanNotMutantException("Human is not a Mutant! Next... please"));
                });
    }

    private Mono<Long> validateHorizontalSegment(String[] dnaSequence) {
        log.info("validateHorizontalSegment Inicia: {}", System.currentTimeMillis());
        Long count = 0L;
        for (int i = 0; i < dnaSequence.length; i++) {
            if (p.matcher(dnaSequence[i]).find()) {
                count += 1;
            }
            if (count > 1) {
                break;
            }
        }

        return Mono.just(count);
    }

    private char[][] convertStringArrayToCharMatrix(String[] dnaSequence) {
        char[][] charArray = new char[dnaSequence.length][];
        for (int i = 0; i < dnaSequence.length; i++) {
            charArray[i] = dnaSequence[i].toCharArray();
        }
        return charArray;
    }

    private Mono<Long> validateVerticalSegment(char[][] matrix) {
        log.info("validateVerticalSegment Inicia: {}", System.currentTimeMillis());
        Long count = 0L;
        for (int i = 0; i < matrix.length; i++) {
            StringBuilder vertical = new StringBuilder();
            for (int j = 0; j < matrix.length; j++) {
                vertical.append(matrix[j][i]);
            }
            if (p.matcher(vertical.toString()).find()) {
                count += 1;
            }
            if (count > 1) {
                break;
            }
        }
        return Mono.just(count);
    }

    private Mono<Long> validatePrincipalDiagonalLeftToRightSegment(String[] dnaSequence) {
        log.info("validatePrincipalDiagonalLeftToRightSegment Inicia: {}", System.currentTimeMillis());
        Long count = 0L;
        // Diagonal principal de izquierda a derecha
        for (int j = 0; j < dnaSequence.length; j++) {
            StringBuilder principalDiagonalRightToLeft = new StringBuilder();
            for (int i = 0; i < dnaSequence.length; i++) {
                if (i + j >= dnaSequence.length)
                    break;
                principalDiagonalRightToLeft.append(dnaSequence[i].charAt(i + j));
            }
            if (principalDiagonalRightToLeft.length() >= 4 && p.matcher(principalDiagonalRightToLeft.toString()).find()) {
                count += 1;
            }
            if (count > 1) {
                break;
            }
        }

        return Mono.just(count);
    }

    private Mono<Long> validatePrincipalDiagonalRightToLeftSegment(String[] dnaSequence) {
        log.info("validatePrincipalDiagonalRightToLeftSegment Inicia: {}", System.currentTimeMillis());
        Long count = 0L;

        // Diagonal principal de derecha a izquierda
        for (int j = 1; j < dnaSequence.length; j++) {
            StringBuilder principalDiagonalLeftToRight = new StringBuilder();
            for (int i = 0; i < dnaSequence.length; i++) {
                if (i + j >= dnaSequence.length)
                    break;
                principalDiagonalLeftToRight.append(dnaSequence[i + j].charAt(i));
            }
            if (principalDiagonalLeftToRight.length() >= 4 && p.matcher(principalDiagonalLeftToRight.toString()).find()) {
                count += 1;
            }
            if (count > 1) {
                break;
            }
        }

        return Mono.just(count);
    }

    private Mono<Long> validateSecondaryDiagonalLeftToRightFirstSegment(String[] dnaSequence) {
        log.info("validateSecondaryDiagonalLeftToRightFirstSegment Inicia: {}", System.currentTimeMillis());
        Long count = 0L;

        // Diagonal secundaria de izquierda a derecha
        for (int j = 0; j < dnaSequence.length; j++) {
            StringBuilder secondaryDiagonalLeftToRightFirstSegment = new StringBuilder();
            for (int i = j; i >= 0; i--) {
                secondaryDiagonalLeftToRightFirstSegment.append(dnaSequence[j - i].charAt(i));
            }
            if (secondaryDiagonalLeftToRightFirstSegment.length() >= 4 && p.matcher(secondaryDiagonalLeftToRightFirstSegment.toString()).find()) {
                count += 1;
            }
            if (count > 1) {
                break;
            }
        }

        return Mono.just(count);
    }

    private Mono<Long> validateSecondaryDiagonalLeftToRightSecondSegment(String[] dnaSequence) {
        log.info("validateSecondaryDiagonalLeftToRightSecondSegment Inicia: {}", System.currentTimeMillis());
        Long count = 0L;

        // Diagonal secundaria de izquierda a derecha
        for (int j = 1; j < dnaSequence.length; j++) {
            StringBuilder secondaryDiagonalLeftToRightSecondSegment = new StringBuilder();
            for (int i = j; i < dnaSequence.length; i++) {
                secondaryDiagonalLeftToRightSecondSegment.append(dnaSequence[dnaSequence.length - i + j - 1].charAt(i));
            }
            if (secondaryDiagonalLeftToRightSecondSegment.length() >= 4 && p.matcher(secondaryDiagonalLeftToRightSecondSegment.toString()).find()) {
                count += 1;
            }
            if (count > 1) {
                break;
            }
        }

        return Mono.just(count);
    }
}
