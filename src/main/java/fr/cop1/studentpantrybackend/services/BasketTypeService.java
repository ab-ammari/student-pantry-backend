package fr.cop1.studentpantrybackend.services;

import fr.cop1.studentpantrybackend.dtos.BasketTypeDTO;
import fr.cop1.studentpantrybackend.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BasketTypeService {

    // Opérations CRUD de base
    BasketTypeDTO findById(Long id) throws ResourceNotFoundException;
    List<BasketTypeDTO> findAll();
    Page<BasketTypeDTO> findAll(Pageable pageable);
    BasketTypeDTO save(BasketTypeDTO basketTypeDTO);
    BasketTypeDTO update(Long id, BasketTypeDTO basketTypeDTO) throws ResourceNotFoundException;
    void delete(Long id) throws ResourceNotFoundException;

    // Méthodes spécifiques
    List<BasketTypeDTO> findActiveBasketTypes();
    BasketTypeDTO findByName(String name) throws ResourceNotFoundException;
    List<BasketTypeDTO> searchBasketTypes(String keyword);

    // Méthodes de gestion
    BasketTypeDTO activateBasketType(Long id) throws ResourceNotFoundException;
    BasketTypeDTO deactivateBasketType(Long id) throws ResourceNotFoundException;

    // Méthodes liées aux inventaires
    BasketTypeDTO getBasketTypeWithInventory(Long id) throws ResourceNotFoundException;

    // Statistiques
    Long countActiveBasketTypes();
    Long countReservationsByBasketTypeId(Long id) throws ResourceNotFoundException;
}
