package fr.cop1.studentpantrybackend.web.controllers;

import fr.cop1.studentpantrybackend.dtos.InventoryDTO;
import fr.cop1.studentpantrybackend.exceptions.ResourceNotFoundException;
import fr.cop1.studentpantrybackend.services.InventoryService;
import fr.cop1.studentpantrybackend.web.ApiConstants;
import fr.cop1.studentpantrybackend.web.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion de l'inventaire
 */
@RestController
@RequestMapping(ApiConstants.INVENTORY_ENDPOINT)
@RequiredArgsConstructor
@Validated
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * Récupérer tous les produits de l'inventaire (avec pagination)
     */
    @GetMapping
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'VOLUNTEER')")
    public ResponseEntity<ApiResponse<Page<InventoryDTO>>> getAllInventoryItems(
            @RequestParam(value = ApiConstants.PAGE_PARAM, defaultValue = "0") int page,
            @RequestParam(value = ApiConstants.SIZE_PARAM, defaultValue = "20") int size,
            @RequestParam(value = ApiConstants.SORT_PARAM, defaultValue = "productName") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<InventoryDTO> inventoryItems = inventoryService.findAll(pageable);

        Map<String, Object> meta = new HashMap<>();
        meta.put("totalPages", inventoryItems.getTotalPages());
        meta.put("totalElements", inventoryItems.getTotalElements());
        meta.put("size", inventoryItems.getSize());
        meta.put("page", inventoryItems.getNumber());

        return ResponseEntity.ok(ApiResponse.success("Liste des produits récupérée", inventoryItems, meta));
    }

    /**
     * Récupérer un produit par son ID
     */
    @GetMapping(ApiConstants.INVENTORY_ID_PATH)
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'VOLUNTEER')")
    public ResponseEntity<ApiResponse<InventoryDTO>> getInventoryItemById(@PathVariable Long id) throws ResourceNotFoundException {
        InventoryDTO inventoryItem = inventoryService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(inventoryItem));
    }

    /**
     * Récupérer les produits par type de panier
     */
    @GetMapping("/basket-type/{basketTypeId}")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'VOLUNTEER')")
    public ResponseEntity<ApiResponse<List<InventoryDTO>>> getInventoryItemsByBasketType(@PathVariable Long basketTypeId) throws ResourceNotFoundException {
        List<InventoryDTO> inventoryItems = inventoryService.findByBasketTypeId(basketTypeId);
        return ResponseEntity.ok(ApiResponse.success("Produits du type de panier", inventoryItems));
    }

    /**
     * Rechercher des produits par mot-clé
     */
    @GetMapping("/search")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'VOLUNTEER')")
    public ResponseEntity<ApiResponse<List<InventoryDTO>>> searchProducts(@RequestParam String keyword) {
        List<InventoryDTO> inventoryItems = inventoryService.searchProducts(keyword);
        return ResponseEntity.ok(ApiResponse.success("Résultats de recherche pour: " + keyword, inventoryItems));
    }

    /**
     * Récupérer les produits expirés
     */
    @GetMapping("/expired")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'VOLUNTEER')")
    public ResponseEntity<ApiResponse<List<InventoryDTO>>> getExpiredProducts() {
        List<InventoryDTO> expiredProducts = inventoryService.findExpiredProducts();
        return ResponseEntity.ok(ApiResponse.success("Produits expirés", expiredProducts));
    }

    /**
     * Récupérer les produits qui expirent entre deux dates
     */
    @GetMapping("/expiring-between")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'VOLUNTEER')")
    public ResponseEntity<ApiResponse<List<InventoryDTO>>> getProductsExpiringBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<InventoryDTO> expiringProducts = inventoryService.findProductsExpiringBetween(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Produits expirant entre " + startDate + " et " + endDate, expiringProducts));
    }

    /**
     * Récupérer les produits avec un stock faible
     */
    @GetMapping("/low-stock")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'VOLUNTEER')")
    public ResponseEntity<ApiResponse<List<InventoryDTO>>> getLowStockProducts(
            @RequestParam(required = false) Integer threshold) {

        List<InventoryDTO> lowStockProducts = inventoryService.findLowStockProducts(threshold);
        return ResponseEntity.ok(ApiResponse.success("Produits avec stock faible", lowStockProducts));
    }

    /**
     * Créer un nouveau produit dans l'inventaire
     */
    @PostMapping
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<InventoryDTO>> createInventoryItem(@Valid @RequestBody InventoryDTO inventoryDTO) throws ResourceNotFoundException {
        InventoryDTO createdItem = inventoryService.save(inventoryDTO);
        return new ResponseEntity<>(ApiResponse.success("Produit ajouté avec succès", createdItem), HttpStatus.CREATED);
    }

    /**
     * Mettre à jour un produit existant
     */
    @PutMapping(ApiConstants.INVENTORY_ID_PATH)
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<InventoryDTO>> updateInventoryItem(
            @PathVariable Long id,
            @Valid @RequestBody InventoryDTO inventoryDTO) throws ResourceNotFoundException {

        InventoryDTO updatedItem = inventoryService.update(id, inventoryDTO);
        return ResponseEntity.ok(ApiResponse.success("Produit mis à jour avec succès", updatedItem));
    }

    /**
     * Supprimer un produit de l'inventaire
     */
    @DeleteMapping(ApiConstants.INVENTORY_ID_PATH)
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteInventoryItem(@PathVariable Long id) throws ResourceNotFoundException {
        inventoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Produit supprimé avec succès", null));
    }

    /**
     * Ajouter du stock à un produit
     */
    @PutMapping("/{id}/add-stock")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'VOLUNTEER')")
    public ResponseEntity<ApiResponse<InventoryDTO>> addStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) throws ResourceNotFoundException {

        InventoryDTO updatedItem = inventoryService.addStock(id, quantity);
        return ResponseEntity.ok(ApiResponse.success("Stock ajouté avec succès", updatedItem));
    }

    /**
     * Retirer du stock d'un produit
     */
    @PutMapping("/{id}/remove-stock")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'VOLUNTEER')")
    public ResponseEntity<ApiResponse<InventoryDTO>> removeStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) throws ResourceNotFoundException {

        InventoryDTO updatedItem = inventoryService.removeStock(id, quantity);
        return ResponseEntity.ok(ApiResponse.success("Stock retiré avec succès", updatedItem));
    }

    /**
     * Obtenir des statistiques sur l'inventaire
     */
    @GetMapping("/stats")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getInventoryStats() {
        Map<String, Object> stats = new HashMap<>();

        // Niveaux de stock par type de panier
        stats.put("stockLevelsByBasketType", inventoryService.getStockLevelsByBasketType());

        // Produits qui expirent prochainement
        stats.put("expiringProducts", inventoryService.getExpiringProductCounts(30));

        return ResponseEntity.ok(ApiResponse.success("Statistiques de l'inventaire", stats));
    }
}
