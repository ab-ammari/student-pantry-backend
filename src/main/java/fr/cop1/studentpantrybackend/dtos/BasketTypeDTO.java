package fr.cop1.studentpantrybackend.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BasketTypeDTO {
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit être entre 2 et 100 caractères")
    private String name;

    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;

    private Boolean isActive;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    // Pour les inventaires liés (optionnel)
    private List<InventoryDTO> inventoryItems = new ArrayList<>();

    // Champs calculés pour l'UI
    private Integer totalStock;
    private Integer reservationCount;

    public Integer getTotalStock() {
        if (inventoryItems == null || inventoryItems.isEmpty()) {
            return 0;
        }
        return inventoryItems.stream()
                .mapToInt(InventoryDTO::getQuantity)
                .sum();
    }
}
