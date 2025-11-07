package fr.cop1.studentpantrybackend.repositories;

import fr.cop1.studentpantrybackend.entities.User;
import fr.cop1.studentpantrybackend.enums.UserRole;
import fr.cop1.studentpantrybackend.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(UserRole role);
    List<User> findByStatus(UserStatus status);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.status = :status")
    List<User> findByRoleAndStatus(UserRole role, UserStatus status);

    boolean existsByEmail(String email);

    // advanced search for students
    List<User> findByRoleAndSchool(UserRole role, String school);
    List<User> findByRoleAndStudentIdVerified(UserRole role, boolean studentIdVerified);
}
