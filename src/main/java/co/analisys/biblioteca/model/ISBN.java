package co.analisys.biblioteca.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Número Internacional Normalizado del Libro (ISBN)")
public class ISBN {

    @Schema(description = "Valor del número ISBN", example = "978-84-376-0494-7")
    private String isbn_value;
}
