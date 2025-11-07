package fr.cop1.studentpantrybackend.exceptions;

import org.jetbrains.annotations.NotNull;

public class ResourceNotFoundException extends Exception {
    public ResourceNotFoundException(@NotNull String message) {
        super(message);
    }
}
