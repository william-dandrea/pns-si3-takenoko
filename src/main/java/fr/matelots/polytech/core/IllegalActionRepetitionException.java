package fr.matelots.polytech.core;

/**
 * @author Gabriel Cogne
 */
public class IllegalActionRepetitionException extends RuntimeException {
    public IllegalActionRepetitionException () {
        super ("The same action is being done twice");
    }
}
