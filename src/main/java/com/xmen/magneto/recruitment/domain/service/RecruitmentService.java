package com.xmen.magneto.recruitment.domain.service;

import com.xmen.magneto.recruitment.application.RecruitmentRequest;
import com.xmen.magneto.recruitment.application.RecruitmentResponse;
import com.xmen.magneto.recruitment.application.StatsResponse;
import reactor.core.publisher.Mono;

public interface RecruitmentService {
    Mono<RecruitmentResponse> validateIfHumanIsMutant(RecruitmentRequest recruitmentRequest);

    Mono<StatsResponse> getRecruitmentProcessStats();
}
