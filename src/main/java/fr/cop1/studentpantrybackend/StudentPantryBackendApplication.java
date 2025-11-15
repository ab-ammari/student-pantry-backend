package fr.cop1.studentpantrybackend;

import fr.cop1.studentpantrybackend.dtos.UserDTO;
import fr.cop1.studentpantrybackend.enums.UserRole;
import fr.cop1.studentpantrybackend.enums.UserStatus;
import fr.cop1.studentpantrybackend.exceptions.EmailAlreadyExistsException;
import fr.cop1.studentpantrybackend.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.stream.Stream;

@SpringBootApplication
public class StudentPantryBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudentPantryBackendApplication.class, args);
	}

    @Bean
    CommandLineRunner commandLineRunner(UserService userService) {
        return args -> {
            Stream.of("user1", "user2", "admin").forEach(username -> {
                UserDTO userDTO = new UserDTO();
                userDTO.setFirstName(username);
                userDTO.setLastName(username);
                userDTO.setPassword("1234");
                userDTO.setEmail(username + "@studentpantry.com");
                userDTO.setStudentIdVerified(false);
                userDTO.setRole(UserRole.STUDENT);
                userDTO.setStatus(UserStatus.ACTIVE);
                try {
                    userService.save(userDTO);
                } catch (EmailAlreadyExistsException e) {
                    throw new RuntimeException(e);
                }
            });
        };
    }
}
