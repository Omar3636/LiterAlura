package com.aluracursos.LiterAlura.modelos;

import jakarta.persistence.*;


@Entity
@Table
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private Long IdLibro;
    private String titulo;
    private String idioma;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    private Persona autor;

    public Libro() {}

    public Libro(Long id, String titulo, String idioma, Persona autor) {
        Id = id;
        this.titulo = titulo;
        this.idioma = idioma;
        this.autor = autor;
    }

    public Libro(DatosLibro datosLibro) {
        this.IdLibro = datosLibro.IdLibro();
        this.titulo = datosLibro.titulo();
        this.idioma = datosLibro.idioma().get(0);
        this.autor = new Persona(datosLibro.autor().get(0));
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Persona getAutor() {
        return autor;
    }

    public void setAutor(Persona autor) {
        this.autor = autor;
    }

    public Long getIdLibro() {
        return IdLibro;
    }

    public void setIdLibro(Long idLibro) {
        IdLibro = idLibro;
    }
}
