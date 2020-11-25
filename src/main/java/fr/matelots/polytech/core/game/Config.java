package fr.matelots.polytech.core.game;

import fr.matelots.polytech.engine.util.Position;

import java.util.Arrays;
import java.util.List;

public class Config {

    /**Nombre de parcel qu'on peut poser*/
    public static final int NB_PLACEABLE_PARCEL = 27;

    public static final List<Position> CUBE_DIRECTIONS = Arrays.asList(
                                                                new Position(1, -1, 0),
                                                                new Position(0, -1, 1),
                                                                new Position(-1, 0, 1),
                                                                new Position(-1, 1, 0),
                                                                new Position(0, 1, -1),
                                                                new Position(1, 0, -1)
                                                        );

    /**Taille du paquet de carte objectifs*/
    public static final int DECK_SIZE = 15;

    /**Position de l'étang*/
    public static final Position BOND_POSITION = new Position(0, 0, 0);

    private Config () throws InstantiationException {
        throw new InstantiationException("This is a static class");
    }
}