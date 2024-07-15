package com.aluracursos.LiterAlura.principal;

import com.aluracursos.LiterAlura.modelos.DatosLibro;
import com.aluracursos.LiterAlura.modelos.Libro;
import com.aluracursos.LiterAlura.modelos.Persona;
import com.aluracursos.LiterAlura.repository.RepositorioLibros;
import com.aluracursos.LiterAlura.service.ConsumoAPI;
import com.aluracursos.LiterAlura.service.ConvierteDatos;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Principal {
    Scanner teclado = new Scanner(System.in);
    @Autowired
    private RepositorioLibros repositorioLibros;
    @Autowired
    private ConvierteDatos conversor;
    @Autowired
    private ConsumoAPI consumoAPI;
    private String URL_BASE = "https://gutendex.com/books?search=";

    public Principal(RepositorioLibros repositorioLibros, ConsumoAPI consumoAPI, ConvierteDatos conversor) {
        this.repositorioLibros = repositorioLibros;
        this.consumoAPI = consumoAPI;
        this.conversor = conversor;
    }
public void muestraElMenu() {

    System.out.println("""
            -------------------------------------------------------------------
            Bienvenido a LiterAlura, por favor selecciona una opción del menú:
            -------------------------------------------------------------------
                                 Menú
                       1- Buscar un libro por el título.
                       2- Mostrar libros registrados.
                       3- Mostrar autores registrados.
                       4- Mostrar autores vivos por año.
                       5- Mostrar libros por idioma.
                       0- Salir
            """);
}

    public void opcionesMenu() {
        boolean flag = true;
        while (flag) {
            muestraElMenu();
            var opcion= teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1 -> buscaLibros();
                case 2 -> muestraLibrosRegistrados();
                case 3 -> muestraAutoresRegistrados();
                case 4 -> muestrarAutoresVivos();
                case 5 -> muestraLibroPorIdioma();
                case 0 -> {
                    System.out.println("Cerrando la aplicación LiterAlura...");
                    flag = false;
                }
                default -> System.out.println("Opción invalida!");
            }
        }
    }


    private void GuardarLibros(List<Libro> libros) {

        libros.forEach(repositorioLibros::save);
    }

public void buscaLibros () {

    try {
        System.out.println("Escriba el titulo del libro que desea agregar");
        var nombreLibro = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE + nombreLibro.replace(" ", "+"));

        JsonNode rootNode = conversor.getObjectMapper().readTree(json);
        JsonNode resultsNode = rootNode.path("results");

        if (resultsNode.isEmpty()) {
            System.out.println("No fue posible encontrar el libro buscado.");
            return;
        }
        List<DatosLibro> datos = conversor.getObjectMapper()
                .readerForListOf(DatosLibro.class)
                .readValue(resultsNode);

        System.out.println(datos);

        for (DatosLibro libro : datos) {
            List<Libro> librosExistentes = repositorioLibros.findByIdLibro(libro.IdLibro());
            if (!librosExistentes.isEmpty()) {
                for (Libro libroex : librosExistentes) {
                    datos.removeIf(d -> libroex.getIdLibro() == d.IdLibro());
                }
            }
        }

        if (!datos.isEmpty()) {
            System.out.println("Guardando nuevos libros encontrados...");

            List<Libro> nuevosLibros = datos.stream().map(Libro::new).collect(Collectors.toList());
            nuevosLibros.forEach(n ->{
                var autor = repositorioLibros.findAutorPorNombre(n.getAutor().getNombre());
                if(!autor.isEmpty()){
                      n.setAutor(autor.get(0));

                }
            });
         //  var session = HibernateUtil.getSessionFactory().openSession();
        //    session.beginTransaction();


            GuardarLibros(nuevosLibros);

            System.out.println("Libros guardados con exito!");
        } else {
            System.out.println("Todos los libros ya estan registrados en la base de datos");
        }

        // Muestra los libros encontrados
        if (!datos.isEmpty()) {
            System.out.println("Libros encontrados:");
            Set<String> titulosEncontrados = new HashSet<>(); // Para controlar títulos ya exibidos
            for (DatosLibro libro : datos) {
                if (!titulosEncontrados.contains(libro.titulo())) {
                    System.out.println(libro.titulo());
                    titulosEncontrados.add(libro.titulo());
                }
            }
        }

    } catch (Exception e) {
        System.out.println("Error en la busqueda de libros: " + e.getMessage());
    }

    }

    private void muestraLibrosRegistrados() {
        List<Libro> libros = repositorioLibros.findAll();
        if (libros.isEmpty()) {
            System.out.println("No existen libros registrados.");
        } else {
            libros.forEach(l-> {
                String titulo = l.getTitulo();
                String idioma = l.getIdioma();
                String autor = l.getAutor().getNombre();
                System.out.println("Titlo: "+titulo + ", Idioma: "
                        + idioma + ", Autor: " + autor +".");
            });
        }
    }

    private void muestraAutoresRegistrados() {
        List<Persona> autores = repositorioLibros.findAutores();
        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados.");
        } else {
            autores.forEach(a -> {
                    String nombreAutor = a.getNombre();
            String fechaNacimiento = a.getFechaNacimiento().toString();
            String fechaFallecimiento = a.getFechaMuerte().toString();
            System.out.println("Nombre: "+nombreAutor + ", Año de nacimiento: "
                    + fechaNacimiento + ", Año de fallecimiento: " + fechaFallecimiento +".");
            });
        }
    }

    private void muestrarAutoresVivos() {
        System.out.println("Ingrese el año: ");
        Integer year = teclado.nextInt();
        teclado.nextLine();

        List<Persona> autores = repositorioLibros.findAutoresVivos(year);
        if (autores.isEmpty()) {
            System.out.println("Todos estan muertos para ese año");
        } else {
            System.out.println("Listado de autores vivos a partir del año: " + year + ":\n");

            autores.forEach(autor -> {
                    String nombreAutor = autor.getNombre();
                    String fechaNacimiento = autor.getFechaNacimiento().toString();
                    String fechaFallecimiento = autor.getFechaMuerte().toString();
                    System.out.println("Nombre: "+nombreAutor + ", Año de nacimiento: "
                            + fechaNacimiento + ", Año de fallecimiento: " + fechaFallecimiento +".");

            });
        }
    }

    private void muestraLibroPorIdioma() {
        System.out.println("""
            Ingrese el idioma que desea
            Ingles (en)
            Portugues (pt)
            Español (es)
            Frances (fr)
            Aleman (de)
            """);
        String idioma = teclado.nextLine();

        List<Libro> libros = repositorioLibros.findByIdioma(idioma);
        if (libros.isEmpty()) {
            System.out.println("No se encontraron libros en el idioma seleccionado");
        } else {
            System.out.println("Se encontraron: "+libros.stream().count()+" libros." );
            libros.forEach(libro -> {
                String titulo = libro.getTitulo();
                String autor = libro.getAutor().getNombre();
                String idiomaLibro = libro.getIdioma();
                System.out.println("Título: " + titulo);
                System.out.println("Autor: " + autor);
                System.out.println("Idioma: " + idiomaLibro);
                System.out.println("----------------------------------------");
            });
        }
    }

}


