package com.xmen.magneto.recruitment.domain.service;

import reactor.core.publisher.Mono;

public interface MutantService {
    Mono<Boolean> humanIsMutant(String[] dnaSequence);
}
