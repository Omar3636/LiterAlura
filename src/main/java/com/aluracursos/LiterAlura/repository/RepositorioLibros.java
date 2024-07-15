package com.aluracursos.LiterAlura.repository;

import com.aluracursos.LiterAlura.modelos.Libro;
import com.aluracursos.LiterAlura.modelos.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Year;
import java.util.List;

public interface RepositorioLibros extends JpaRepository<Libro, Long> {

    @Query("SELECT l FROM Libro l WHERE l.IdLibro = :IdLibro")
    List<Libro> findByIdLibro(@Param("IdLibro") Long IdLibro);

    @Query("SELECT a FROM Persona a WHERE a.fechaNacimiento <= :year AND (a.fechaMuerte IS NULL OR a.fechaMuerte >= :year)")
    List<Persona> findAutoresVivos(@Param("year") Integer year);

    @Query("SELECT a FROM Persona a")
    List<Persona> findAutores();

    @Query("SELECT l FROM Libro l WHERE l.idioma LIKE %:idioma%")
    List<Libro> findByIdioma(@Param("idioma") String idioma);

    @Query("SELECT a FROM Persona a WHERE a.nombre = :nombre")
    List<Persona> findAutorPorNombre(@Param("nombre") String nombre);


}
