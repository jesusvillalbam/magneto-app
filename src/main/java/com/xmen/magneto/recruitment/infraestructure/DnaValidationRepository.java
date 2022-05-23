package com.xmen.magneto.recruitment.infraestructure;

import com.xmen.magneto.recruitment.infraestructure.entity.DnaValidation;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface DnaValidationRepository extends ReactiveCrudRepository<DnaValidation, Long> {
}
