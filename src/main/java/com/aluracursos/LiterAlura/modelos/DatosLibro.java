package com.aluracursos.LiterAlura.modelos;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosLibro (
        @JsonAlias("title") String titulo,
        @JsonAlias("download_count") Double descargas,
        @JsonAlias("languages") List<String> idioma,
        @JsonAlias("authors") List<DatosPersona> autor,
        @JsonAlias("id") Long IdLibro) {
}
