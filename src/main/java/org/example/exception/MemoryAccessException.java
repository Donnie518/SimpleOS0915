package org.example.exception;

public class MemoryAccessException extends RuntimeException {
    public MemoryAccessException(String message) {
        super(message);
        outOfBoundsHandling();
    }

    void outOfBoundsHandling(){
        System.out.println("Out of bounds!");
    }
}
