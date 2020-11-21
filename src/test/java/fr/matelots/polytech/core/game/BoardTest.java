package fr.matelots.polytech.core.game;

import fr.matelots.polytech.core.game.parcels.Parcel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Gabriel Cogne
 */
public class BoardTest {
    private Board board;

    @BeforeEach
    public void init () {
        board = new Board();
    }

    @Test
    public void testEtangEnPlaceACreationDuPlateau () {
        assertNotNull(board.getParcel(0, 0, 0));
    }

    @Test
    public void testValiditePositionPourVoisineALEtang () {
        assertTrue(board.isPlaceValid(1, -1, 0));
        assertTrue(board.isPlaceValid(0, -1, +1));
        assertTrue(board.isPlaceValid(-1, 0, 1));
        assertTrue(board.isPlaceValid(-1, 1, 0));
        assertTrue(board.isPlaceValid(0, 1, -1));
        assertTrue(board.isPlaceValid(+1, 0, -1));
    }

    @Test
    public void testValidePositionPour2VoisinesSiNonVoisineALEtang () {
        board.addParcel(1, -1, 0, new Parcel());
        board.addParcel(0, -1, 1, new Parcel());
        assertTrue(board.isPlaceValid(1, -2, 1));
    }

    @Test
    public void testInvalidePositionPour1VoisineSiNonVoisineALEtang () {
        board.addParcel(1, -1, 0, new Parcel());
        assertFalse(board.isPlaceValid(1, -2, 1));
    }

    @Test
    public void testInvalidPositionCarPasVoisine () {
        assertFalse(board.isPlaceValid(2, -1, -1));
    }

    @Test
    public void testPoseParcelVoisineALEtang () {
        assertTrue(board.addParcel(1, -1, 0, new Parcel()));
        assertNotNull(board.getParcel(1, -1, 0));
    }

    @Test
    public void testPoseParcelVoisineARien () {
        assertFalse(board.addParcel(2, -1, -1, new Parcel()));
        assertNull(board.getParcel(2, -1, -1));
    }

    @Test
    public void testPoseParcelVoisineAVoisineEtang () {
        assertTrue(board.addParcel(1, -1, 0, new Parcel()));
        assertTrue(board.addParcel(1, 0, -1, new Parcel()));
        assertTrue(board.addParcel(2, -1, -1, new Parcel()));
        assertNotNull(board.getParcel(2, -1, -1));
    }
}
