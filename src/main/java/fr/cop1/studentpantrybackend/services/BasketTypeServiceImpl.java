package fr.cop1.studentpantrybackend.services;

import fr.cop1.studentpantrybackend.dtos.BasketTypeDTO;
import fr.cop1.studentpantrybackend.entities.BasketType;
import fr.cop1.studentpantrybackend.exceptions.ResourceNotFoundException;
import fr.cop1.studentpantrybackend.mappers.BasketTypeMapper;
import fr.cop1.studentpantrybackend.repositories.BasketTypeRepository;
import fr.cop1.studentpantrybackend.repositories.ReservationRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BasketTypeServiceImpl implements BasketTypeService {

    private final BasketTypeRepository basketTypeRepository;
    private final ReservationRepository reservationRepository;
    private final BasketTypeMapper basketTypeMapper;

    @Override
    public BasketTypeDTO findById(Long id) throws ResourceNotFoundException {
        BasketType basketType = basketTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type de panier non trouvé avec l'id: " + id));
        return basketTypeMapper.toDTO(basketType);
    }

    @Override
    public List<BasketTypeDTO> findAll() {
        return basketTypeRepository.findAll().stream()
                .map(basketTypeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<BasketTypeDTO> findAll(Pageable pageable) {
        return basketTypeRepository.findAll(pageable)
                .map(basketTypeMapper::toDTO);
    }

    @Override
    public BasketTypeDTO save(BasketTypeDTO basketTypeDTO) {
        // Vérifier si le nom existe déjà
        if (basketTypeRepository.findByName(basketTypeDTO.getName()) != null) {
            throw new ValidationException("Un type de panier avec ce nom existe déjà: " + basketTypeDTO.getName());
        }

        // Convertir DTO en entité
        BasketType basketType = basketTypeMapper.toEntity(basketTypeDTO);

        // Définir l'état initial si non spécifié
//        if (basketType.isActive() == null) {
//            basketType.setActive(true);
//        }

        // Définir l'horodatage
        basketType.setCreatedAt(LocalDateTime.now());

        // Enregistrer le type de panier
        BasketType savedBasketType = basketTypeRepository.save(basketType);

        return basketTypeMapper.toDTO(savedBasketType);
    }

    @Override
    public BasketTypeDTO update(Long id, BasketTypeDTO basketTypeDTO) throws ResourceNotFoundException {
        BasketType existingBasketType = basketTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type de panier non trouvé avec l'id: " + id));

        // Vérifier si le nom existe déjà pour un autre type de panier
        if (basketTypeDTO.getName() != null && !basketTypeDTO.getName().equals(existingBasketType.getName())) {
            BasketType existingByName = basketTypeRepository.findByName(basketTypeDTO.getName());
            if (existingByName != null && !existingByName.getId().equals(id)) {
                throw new ValidationException("Un type de panier avec ce nom existe déjà: " + basketTypeDTO.getName());
            }
        }

        // Mettre à jour avec les données du DTO
        basketTypeMapper.updateEntityFromDTO(basketTypeDTO, existingBasketType);

        // Enregistrer les modifications
        BasketType updatedBasketType = basketTypeRepository.save(existingBasketType);

        return basketTypeMapper.toDTO(updatedBasketType);
    }

    @Override
    public void delete(Long id) throws ResourceNotFoundException {
        BasketType basketType = basketTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type de panier non trouvé avec l'id: " + id));

        // Vérifier s'il y a des réservations pour ce type de panier
        if (basketType.getReservations() != null && !basketType.getReservations().isEmpty()) {
            throw new ValidationException("Impossible de supprimer un type de panier utilisé dans des réservations");
        }

        // Vérifier s'il y a des produits dans l'inventaire pour ce type de panier
        if (basketType.getInventoryItems() != null && !basketType.getInventoryItems().isEmpty()) {
            throw new ValidationException("Impossible de supprimer un type de panier ayant des produits dans l'inventaire");
        }

        basketTypeRepository.delete(basketType);
    }

    @Override
    public List<BasketTypeDTO> findActiveBasketTypes() {
        return basketTypeRepository.findByIsActive(true).stream()
                .map(basketTypeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BasketTypeDTO findByName(String name) throws ResourceNotFoundException {
        BasketType basketType = basketTypeRepository.findByName(name);
        if (basketType == null) {
            throw new ResourceNotFoundException("Type de panier non trouvé avec le nom: " + name);
        }
        return basketTypeMapper.toDTO(basketType);
    }

    @Override
    public List<BasketTypeDTO> searchBasketTypes(String keyword) {
        return basketTypeRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(basketTypeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BasketTypeDTO activateBasketType(Long id) throws ResourceNotFoundException {
        BasketType basketType = basketTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type de panier non trouvé avec l'id: " + id));

        basketType.setActive(true);
        BasketType updatedBasketType = basketTypeRepository.save(basketType);

        return basketTypeMapper.toDTO(updatedBasketType);
    }

    @Override
    public BasketTypeDTO deactivateBasketType(Long id) throws ResourceNotFoundException {
        BasketType basketType = basketTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type de panier non trouvé avec l'id: " + id));

        basketType.setActive(false);
        BasketType updatedBasketType = basketTypeRepository.save(basketType);

        return basketTypeMapper.toDTO(updatedBasketType);
    }

    @Override
    public BasketTypeDTO getBasketTypeWithInventory(Long id) throws ResourceNotFoundException {
        BasketType basketType = basketTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type de panier non trouvé avec l'id: " + id));

        return basketTypeMapper.toDTOWithInventory(basketType);
    }

    @Override
    public Long countActiveBasketTypes() {
        return (long) basketTypeRepository.findByIsActive(true).size();
    }

    @Override
    public Long countReservationsByBasketTypeId(Long id) throws ResourceNotFoundException {
        BasketType basketType = basketTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type de panier non trouvé avec l'id: " + id));

        return (long) basketType.getReservations().size();
    }
}
