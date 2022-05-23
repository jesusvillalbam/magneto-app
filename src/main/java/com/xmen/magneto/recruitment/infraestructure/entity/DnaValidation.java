package com.xmen.magneto.recruitment.infraestructure.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.relational.core.mapping.Table;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table
public class DnaValidation {
    @Id
    private Long id;
    private String dnaSequence;
    private Boolean isMutant;
}
