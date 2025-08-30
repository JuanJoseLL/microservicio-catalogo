package co.analisys.biblioteca.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Identificador único compuesto para un libro")
public class LibroId implements Serializable {

    @Schema(description = "Valor del identificador del libro", example = "LIB001")
    private String libroid_value;
}
