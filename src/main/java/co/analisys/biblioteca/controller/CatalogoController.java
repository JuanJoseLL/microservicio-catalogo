package co.analisys.biblioteca.controller;

import co.analisys.biblioteca.model.Libro;
import co.analisys.biblioteca.model.LibroId;
import co.analisys.biblioteca.service.CatalogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/libros")
@Tag(name = "Catálogo de Libros", description = "API para la gestión del catálogo de libros de la biblioteca")
@SecurityRequirement(name = "Bearer Authentication")
public class CatalogoController {
    private final CatalogoService catalogoService;

    @Autowired
    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN') or hasRole('ROLE_USER')")
    @Operation(
        summary = "Obtener información de un libro",
        description = "Recupera la información completa de un libro específico usando su ID único. " +
                     "Incluye título, ISBN, categoría, autores y estado de disponibilidad."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Libro encontrado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Libro.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Libro no encontrado con el ID proporcionado",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autorizado - Token JWT requerido",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Acceso denegado - Rol LIBRARIAN o USER requerido",
            content = @Content
        )
    })
    public ResponseEntity<Libro> obtenerLibro(
        @Parameter(
            description = "ID único del libro",
            required = true,
            example = "LIB001"
        )
        @PathVariable String id
    ) {
        Libro libro = catalogoService.obtenerLibro(new LibroId(id));
        return libro != null ? ResponseEntity.ok(libro) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/disponible")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN') or hasRole('ROLE_USER')")
    @Operation(
        summary = "Verificar disponibilidad de un libro",
        description = "Consulta si un libro específico está disponible para préstamo. " +
                     "Retorna true si el libro existe y está disponible, false en caso contrario."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Consulta exitosa - retorna estado de disponibilidad",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    type = "boolean",
                    description = "Estado de disponibilidad del libro",
                    example = "true"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autorizado - Token JWT requerido",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Acceso denegado - Rol LIBRARIAN o USER requerido",
            content = @Content
        )
    })
    public ResponseEntity<Boolean> isLibroDisponible(
        @Parameter(
            description = "ID único del libro a consultar",
            required = true,
            example = "LIB001"
        )
        @PathVariable String id
    ) {
        Libro libro = catalogoService.obtenerLibro(new LibroId(id));
        boolean disponible = libro != null && libro.isDisponible();
        return ResponseEntity.ok(disponible);
    }

    @PutMapping("/{id}/disponibilidad")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    @Operation(
        summary = "Actualizar disponibilidad de un libro",
        description = "Permite a los bibliotecarios actualizar el estado de disponibilidad de un libro. " +
                     "Se utiliza típicamente cuando se realiza o devuelve un préstamo."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Disponibilidad actualizada exitosamente",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Libro no encontrado con el ID proporcionado",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autorizado - Token JWT requerido",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Acceso denegado - Solo bibliotecarios pueden actualizar disponibilidad",
            content = @Content
        )
    })
    public ResponseEntity<Void> actualizarDisponibilidad(
        @Parameter(
            description = "ID único del libro a actualizar",
            required = true,
            example = "LIB001"
        )
        @PathVariable String id,
        @Parameter(
            description = "Nuevo estado de disponibilidad",
            required = true,
            schema = @Schema(
                type = "boolean",
                description = "true para disponible, false para no disponible",
                example = "false"
            )
        )
        @RequestBody boolean disponible
    ) {
        catalogoService.actualizarDisponibilidad(new LibroId(id), disponible);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN') or hasRole('ROLE_USER')")
    @Operation(
        summary = "Buscar libros por criterio",
        description = "Realiza una búsqueda de libros basada en un criterio de texto libre. " +
                     "La búsqueda incluye título, autor, ISBN y categoría. " +
                     "Retorna una lista de todos los libros que coincidan con el criterio."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Búsqueda realizada exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    type = "array",
                    implementation = Libro.class,
                    description = "Lista de libros que coinciden con el criterio de búsqueda"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Criterio de búsqueda inválido o vacío",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autorizado - Token JWT requerido",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Acceso denegado - Rol LIBRARIAN o USER requerido",
            content = @Content
        )
    })
    public ResponseEntity<List<Libro>> buscarLibros(
        @Parameter(
            description = "Criterio de búsqueda (título, autor, ISBN, categoría)",
            required = true,
            example = "García Márquez"
        )
        @RequestParam String criterio
    ) {
        if (criterio == null || criterio.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<Libro> libros = catalogoService.buscarLibros(criterio);
        return ResponseEntity.ok(libros);
    }
}
