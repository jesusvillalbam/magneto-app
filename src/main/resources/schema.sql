CREATE TABLE IF NOT EXISTS dna_validation
(id SERIAL PRIMARY KEY,
dna_sequence VARCHAR(1000),
is_mutant Boolean);