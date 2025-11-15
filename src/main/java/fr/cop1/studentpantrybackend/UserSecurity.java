/*
package fr.cop1.studentpantrybackend;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

*/
/**
 * Classe utilitaire pour les vérifications de sécurité concernant les utilisateurs
 *//*

@Component
public class UserSecurity {

    */
/**
     * Vérifie si l'utilisateur authentifié est le propriétaire du compte
     *//*

    public boolean isCurrentUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return false;
        }

        // Dans une implémentation réelle, récupérer l'ID de l'utilisateur à partir du token
        // Par exemple, en utilisant un JwtTokenProvider

        // Pour l'instant, nous retournons une valeur par défaut
        return false;
    }
}*/
