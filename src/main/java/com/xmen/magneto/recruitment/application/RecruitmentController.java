package com.xmen.magneto.recruitment.application;

import com.xmen.magneto.recruitment.domain.service.RecruitmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/recruitment")
public class RecruitmentController {

    private final RecruitmentService recruitmentService;

    public RecruitmentController(RecruitmentService recruitmentService) {
        this.recruitmentService = recruitmentService;
    }

    @Operation(description = "Validate if a human is a mutant using its DNA sequence",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Human is a Mutant"),
                    @ApiResponse(responseCode = "403", description = "Human is not Mutant. Be aware"),
                    @ApiResponse(responseCode = "400", description = "Bad request")
            })
    @PostMapping("/mutant")
    public Mono<RecruitmentResponse> validateIfHumanIsMutant(@RequestBody Mono<RecruitmentRequest> recruitmentRequest) {
        return recruitmentRequest
                .flatMap(recruitmentService::validateIfHumanIsMutant);
    }
}
