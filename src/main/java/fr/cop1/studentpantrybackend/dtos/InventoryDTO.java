package fr.cop1.studentpantrybackend.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryDTO {
    private Long id;

    @NotBlank(message = "Le nom du produit est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom du produit doit être entre 2 et 100 caractères")
    private String productName;

    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 0, message = "La quantité ne peut pas être négative")
    private Integer quantity;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;

    private Long basketTypeId;
    private String basketTypeName; // Nom du type de panier (non stocké en base)

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Champs calculés pour l'UI
    private Boolean isExpired;
    private Boolean isLowStock;

    public Boolean getIsExpired() {
        return expirationDate != null && expirationDate.isBefore(LocalDate.now());
    }

    public Boolean getIsLowStock() {
        // Seuil arbitraire, à ajuster selon les besoins
        return quantity < 10;
    }
}
