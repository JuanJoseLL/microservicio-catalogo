package co.analisys.biblioteca.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Schema(description = "Entidad que representa un libro en el catálogo de la biblioteca")
public class Libro {
    @EmbeddedId
    @Schema(description = "Identificador único del libro", example = "LIB001")
    private LibroId id;

    @Schema(description = "Título del libro", example = "Cien años de soledad")
    private String titulo;

    @Embedded
    @Schema(description = "Número ISBN del libro")
    private ISBN isbn;

    @Embedded
    @Schema(description = "Categoría del libro")
    private Categoria categoria;

    @Schema(description = "Estado de disponibilidad del libro para préstamo", example = "true")
    private boolean disponible;

    @ManyToMany(fetch = FetchType.EAGER)
    @Schema(description = "Lista de autores del libro")
    private List<Autor> autores;

    public void marcarComoDisponible() {
        this.disponible = true;
    }

    public void marcarComoNoDisponible() {
        this.disponible = false;
    }

    public void actualizarCategoria(Categoria nuevaCategoria) {
        this.categoria = nuevaCategoria;
    }
}
