package fr.cop1.studentpantrybackend.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Structure de réponse standardisée pour l'API
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private String status;
    private String message;
    private LocalDateTime timestamp;
    private T data;
    private Map<String, Object> meta;
    private List<String> errors;

    /**
     * Création d'une réponse de succès avec données
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status("success")
                .message("Opération réussie")
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }

    /**
     * Création d'une réponse de succès avec données et message personnalisé
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .status("success")
                .message(message)
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }

    /**
     * Création d'une réponse de succès avec données, message et métadonnées
     */
    public static <T> ApiResponse<T> success(String message, T data, Map<String, Object> meta) {
        return ApiResponse.<T>builder()
                .status("success")
                .message(message)
                .timestamp(LocalDateTime.now())
                .data(data)
                .meta(meta)
                .build();
    }

    /**
     * Création d'une réponse d'erreur
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .status("error")
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Création d'une réponse d'erreur avec liste d'erreurs
     */
    public static <T> ApiResponse<T> error(String message, List<String> errors) {
        return ApiResponse.<T>builder()
                .status("error")
                .message(message)
                .timestamp(LocalDateTime.now())
                .errors(errors)
                .build();
    }
}
