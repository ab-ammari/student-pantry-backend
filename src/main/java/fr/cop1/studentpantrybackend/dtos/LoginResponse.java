package fr.cop1.studentpantrybackend.dtos;


import lombok.Data;

/**
 * DTO pour une réponse de connexion réussie
 */
@Data
public class LoginResponse {

    private String token;
    private UserDTO user;
}