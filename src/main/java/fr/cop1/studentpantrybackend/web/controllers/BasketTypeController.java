package fr.cop1.studentpantrybackend.web.controllers;

import fr.cop1.studentpantrybackend.dtos.BasketTypeDTO;
import fr.cop1.studentpantrybackend.exceptions.ResourceNotFoundException;
import fr.cop1.studentpantrybackend.services.BasketTypeService;
import fr.cop1.studentpantrybackend.web.ApiConstants;
import fr.cop1.studentpantrybackend.web.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des types de paniers
 */
@RestController
@RequestMapping(ApiConstants.BASKET_TYPES_ENDPOINT)
@RequiredArgsConstructor
@Validated
public class BasketTypeController {

    private final BasketTypeService basketTypeService;

    /**
     * Récupérer tous les types de paniers (avec pagination)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<BasketTypeDTO>>> getAllBasketTypes(
            @RequestParam(value = ApiConstants.PAGE_PARAM, defaultValue = "0") int page,
            @RequestParam(value = ApiConstants.SIZE_PARAM, defaultValue = "20") int size,
            @RequestParam(value = ApiConstants.SORT_PARAM, defaultValue = "name") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<BasketTypeDTO> basketTypes = basketTypeService.findAll(pageable);

        Map<String, Object> meta = new HashMap<>();
        meta.put("totalPages", basketTypes.getTotalPages());
        meta.put("totalElements", basketTypes.getTotalElements());
        meta.put("size", basketTypes.getSize());
        meta.put("page", basketTypes.getNumber());

        return ResponseEntity.ok(ApiResponse.success("Liste des types de paniers récupérée", basketTypes, meta));
    }

    /**
     * Récupérer un type de panier par son ID
     */
    @GetMapping(ApiConstants.BASKET_TYPE_ID_PATH)
    public ResponseEntity<ApiResponse<BasketTypeDTO>> getBasketTypeById(@PathVariable Long id) throws ResourceNotFoundException {
        BasketTypeDTO basketType = basketTypeService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(basketType));
    }

    /**
     * Récupérer un type de panier avec son inventaire
     */
    @GetMapping("/{id}/with-inventory")
    public ResponseEntity<ApiResponse<BasketTypeDTO>> getBasketTypeWithInventory(@PathVariable Long id) throws ResourceNotFoundException {
        BasketTypeDTO basketType = basketTypeService.getBasketTypeWithInventory(id);
        return ResponseEntity.ok(ApiResponse.success("Type de panier avec inventaire", basketType));
    }

    /**
     * Récupérer les types de paniers actifs
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<BasketTypeDTO>>> getActiveBasketTypes() {
        List<BasketTypeDTO> basketTypes = basketTypeService.findActiveBasketTypes();
        return ResponseEntity.ok(ApiResponse.success("Types de paniers actifs", basketTypes));
    }

    /**
     * Rechercher des types de paniers par mot-clé
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<BasketTypeDTO>>> searchBasketTypes(@RequestParam String keyword) {
        List<BasketTypeDTO> basketTypes = basketTypeService.searchBasketTypes(keyword);
        return ResponseEntity.ok(ApiResponse.success("Résultats de recherche pour: " + keyword, basketTypes));
    }

    /**
     * Créer un nouveau type de panier
     */
    @PostMapping
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<BasketTypeDTO>> createBasketType(@Valid @RequestBody BasketTypeDTO basketTypeDTO) {
        BasketTypeDTO createdBasketType = basketTypeService.save(basketTypeDTO);
        return new ResponseEntity<>(ApiResponse.success("Type de panier créé avec succès", createdBasketType), HttpStatus.CREATED);
    }

    /**
     * Mettre à jour un type de panier existant
     */
    @PutMapping(ApiConstants.BASKET_TYPE_ID_PATH)
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<BasketTypeDTO>> updateBasketType(
            @PathVariable Long id,
            @Valid @RequestBody BasketTypeDTO basketTypeDTO) throws ResourceNotFoundException {

        BasketTypeDTO updatedBasketType = basketTypeService.update(id, basketTypeDTO);
        return ResponseEntity.ok(ApiResponse.success("Type de panier mis à jour avec succès", updatedBasketType));
    }

    /**
     * Supprimer un type de panier
     */
    @DeleteMapping(ApiConstants.BASKET_TYPE_ID_PATH)
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBasketType(@PathVariable Long id) throws ResourceNotFoundException {
        basketTypeService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Type de panier supprimé avec succès", null));
    }

    /**
     * Activer un type de panier
     */
    @PutMapping("/{id}/activate")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<BasketTypeDTO>> activateBasketType(@PathVariable Long id) throws ResourceNotFoundException {
        BasketTypeDTO activatedBasketType = basketTypeService.activateBasketType(id);
        return ResponseEntity.ok(ApiResponse.success("Type de panier activé", activatedBasketType));
    }

    /**
     * Désactiver un type de panier
     */
    @PutMapping("/{id}/deactivate")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<BasketTypeDTO>> deactivateBasketType(@PathVariable Long id) throws ResourceNotFoundException {
        BasketTypeDTO deactivatedBasketType = basketTypeService.deactivateBasketType(id);
        return ResponseEntity.ok(ApiResponse.success("Type de panier désactivé", deactivatedBasketType));
    }

    /**
     * Obtenir des statistiques sur les types de paniers
     */
    @GetMapping("/stats")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBasketTypeStats() {
        Map<String, Object> stats = new HashMap<>();

        // Nombre de types de paniers actifs
        stats.put("activeCount", basketTypeService.countActiveBasketTypes());

        return ResponseEntity.ok(ApiResponse.success("Statistiques des types de paniers", stats));
    }

    /**
     * Obtenir des statistiques sur un type de panier spécifique
     */
    @GetMapping("/{id}/stats")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBasketTypeStatsById(@PathVariable Long id) throws ResourceNotFoundException {
        Map<String, Object> stats = new HashMap<>();

        // Nombre de réservations pour ce type de panier
        stats.put("reservationCount", basketTypeService.countReservationsByBasketTypeId(id));

        return ResponseEntity.ok(ApiResponse.success("Statistiques du type de panier", stats));
    }
}