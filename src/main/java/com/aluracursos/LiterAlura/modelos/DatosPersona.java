package com.aluracursos.LiterAlura.modelos;

import com.fasterxml.jackson.annotation.JsonAlias;

public record DatosPersona(@JsonAlias("name") String nombre,
                           @JsonAlias("birth_year")Integer fechaNacimiento,
                           @JsonAlias("death_year")Integer fechaMuerte) {
}
