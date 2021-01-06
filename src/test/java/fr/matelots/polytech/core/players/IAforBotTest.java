package fr.matelots.polytech.core.players;

import fr.matelots.polytech.core.game.Board;
import fr.matelots.polytech.core.game.Config;
import fr.matelots.polytech.core.game.Game;
import fr.matelots.polytech.core.game.goalcards.CardObjectiveParcel;
import fr.matelots.polytech.core.game.goalcards.pattern.PatternChecker;
import fr.matelots.polytech.core.game.goalcards.pattern.Patterns;
import fr.matelots.polytech.core.game.goalcards.pattern.PositionColored;
import fr.matelots.polytech.core.game.parcels.BambooColor;
import fr.matelots.polytech.core.game.parcels.BambooPlantation;
import fr.matelots.polytech.core.game.parcels.Parcel;
import fr.matelots.polytech.core.players.bots.PremierBot;
import fr.matelots.polytech.core.players.bots.logger.TurnLog;
import fr.matelots.polytech.engine.util.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class IAforBotTest {


    private Game game;
    private Board board;
    private IAforBot ia;

    @BeforeEach
    public void init () {
        game = new Game();
        board = new Board();
    }



    @Test
    public void resolvePatternLineWhenWeCanPlaceAnParcelWhereIsTheMissingPosition() {


        game.getBoard().addParcel(0,1,-1, new BambooPlantation(BambooColor.GREEN));
        game.getBoard().addParcel(1,0,-1, new BambooPlantation(BambooColor.GREEN));
        game.getBoard().addParcel(1,1,-2, new BambooPlantation(BambooColor.GREEN));
        game.getBoard().addParcel(2,-1,-1, new BambooPlantation(BambooColor.GREEN));
        game.getBoard().addParcel(2,1,-1, new BambooPlantation(BambooColor.GREEN));

        CardObjectiveParcel cardObjectiveParcelLine = new CardObjectiveParcel(game.getBoard(), 2, Patterns.LINE, BambooColor.GREEN, BambooColor.GREEN, BambooColor.GREEN);
        cardObjectiveParcelLine.verify();

        assertTrue(IAforBot.findTheBestPlaceToPlaceAnParcel(cardObjectiveParcelLine, game.getBoard()) != null);

    }

    @Test
    public void resolvePatternLineWhenWeCanPlaceAnParcelWhereIsTheMissingPositionWithLessParcels() {


        game.getBoard().addParcel(0,1,-1, new BambooPlantation(BambooColor.GREEN));

        CardObjectiveParcel cardObjectiveParcelLine = new CardObjectiveParcel(game.getBoard(), 2, Patterns.LINE, BambooColor.GREEN, BambooColor.GREEN, BambooColor.GREEN);
        cardObjectiveParcelLine.verify();

        PositionColored pos = new PositionColored(new Position(1,0,-1), BambooColor.GREEN);
        assertEquals(pos, IAforBot.findTheBestPlaceToPlaceAnParcel(cardObjectiveParcelLine, game.getBoard()));

    }

    @Test
    public void resolvePatternLineWhenTheObjIsAlreadyResolve() {
        game.getBoard().addParcel(0,1,-1, new BambooPlantation(BambooColor.GREEN));
        game.getBoard().addParcel(1,0,-1, new BambooPlantation(BambooColor.GREEN));
        game.getBoard().addParcel(1,1,-2, new BambooPlantation(BambooColor.GREEN));
        game.getBoard().addParcel(1,-1,0, new BambooPlantation(BambooColor.GREEN));

        CardObjectiveParcel cardObjectiveParcelLine = new CardObjectiveParcel(game.getBoard(), 2, Patterns.LINE, BambooColor.GREEN, BambooColor.GREEN, BambooColor.GREEN);
        cardObjectiveParcelLine.verify();

        assertNull(IAforBot.findTheBestPlaceToPlaceAnParcel(cardObjectiveParcelLine, game.getBoard()));
    }

    @Test
    public void resolvePatternLineWhenWeCanPlaceAnParcelWhereIsTheMissingPositionWithMoreParcels() {

        game.getBoard().addParcel(0,1,-1, new BambooPlantation(BambooColor.GREEN));
        game.getBoard().addParcel(1,0,-1, new BambooPlantation(BambooColor.GREEN));
        game.getBoard().addParcel(-1,1,0, new BambooPlantation(BambooColor.GREEN));

        CardObjectiveParcel cardObjectiveParcelLine = new CardObjectiveParcel(game.getBoard(), 2, Patterns.LINE, BambooColor.GREEN, BambooColor.GREEN, BambooColor.GREEN);
        cardObjectiveParcelLine.verify();

        PositionColored potentialPos1 = new PositionColored(new Position(1,1,-2), BambooColor.GREEN);
        PositionColored potentialPos2 = new PositionColored(new Position(1,-1,0), BambooColor.GREEN);
        PositionColored potentialPos3 = new PositionColored(new Position(-1,0,1), BambooColor.GREEN);
        PositionColored potentialPos4 = new PositionColored(new Position(-1,-1,2), BambooColor.GREEN);

        PositionColored test = IAforBot.findTheBestPlaceToPlaceAnParcel(cardObjectiveParcelLine, game.getBoard());

        assertTrue(test.equals(potentialPos1) || test.equals(potentialPos2) || test.equals(potentialPos3) ||test.equals(potentialPos4));

    }

    @Test
    public void resolvePatternLineWithALotOfParcels() {

        game.getBoard().addParcel(0,1,-1, new BambooPlantation(BambooColor.GREEN));
        game.getBoard().addParcel(1,0,-1, new BambooPlantation(BambooColor.YELLOW));
        game.getBoard().addParcel(1,-1,0, new BambooPlantation(BambooColor.PINK));
        game.getBoard().addParcel(0,-1,1, new BambooPlantation(BambooColor.GREEN));
        game.getBoard().addParcel(-1,0,1, new BambooPlantation(BambooColor.YELLOW));
        game.getBoard().addParcel(-1,1,0, new BambooPlantation(BambooColor.PINK));
        game.getBoard().addParcel(1,1,-2, new BambooPlantation(BambooColor.YELLOW));
        game.getBoard().addParcel(-1,2,-1, new BambooPlantation(BambooColor.GREEN));
        game.getBoard().addParcel(0,2,-2, new BambooPlantation(BambooColor.GREEN));
        game.getBoard().addParcel(1,-2,1, new BambooPlantation(BambooColor.YELLOW));
        game.getBoard().addParcel(1,-1,2, new BambooPlantation(BambooColor.PINK));



        CardObjectiveParcel cardObjectiveParcelLine = new CardObjectiveParcel(game.getBoard(), 2, Patterns.LINE, BambooColor.GREEN, BambooColor.GREEN, BambooColor.GREEN);
        cardObjectiveParcelLine.verify();

        PositionColored placeWeWant = new PositionColored(new Position(1,2,-3), BambooColor.GREEN);

        PositionColored test = IAforBot.findTheBestPlaceToPlaceAnParcel(cardObjectiveParcelLine, game.getBoard());
        assertEquals(placeWeWant, test);


    }

    @Test
    public void resolvePatternLineWithCObjective() {

        game.getBoard().addParcel(0,1,-1, new BambooPlantation(BambooColor.GREEN));
        game.getBoard().addParcel(1,0,-1, new BambooPlantation(BambooColor.YELLOW));
        game.getBoard().addParcel(1,-1,0, new BambooPlantation(BambooColor.PINK));
        game.getBoard().addParcel(0,-1,1, new BambooPlantation(BambooColor.GREEN));
        game.getBoard().addParcel(-1,0,1, new BambooPlantation(BambooColor.YELLOW));
        game.getBoard().addParcel(-1,1,0, new BambooPlantation(BambooColor.PINK));
        game.getBoard().addParcel(1,1,-2, new BambooPlantation(BambooColor.YELLOW));
        game.getBoard().addParcel(-1,2,-1, new BambooPlantation(BambooColor.GREEN));
        game.getBoard().addParcel(0,2,-2, new BambooPlantation(BambooColor.GREEN));
        game.getBoard().addParcel(1,-2,1, new BambooPlantation(BambooColor.YELLOW));
        game.getBoard().addParcel(1,-1,2, new BambooPlantation(BambooColor.PINK));

        CardObjectiveParcel cardObjectiveParcelC = new CardObjectiveParcel(game.getBoard(), 2, Patterns.C, BambooColor.GREEN, BambooColor.GREEN, BambooColor.GREEN);
        cardObjectiveParcelC.verify();

        PositionColored placeWeWant = new PositionColored(new Position(1,2,-3), BambooColor.GREEN);

        PositionColored test = IAforBot.findTheBestPlaceToPlaceAnParcel(cardObjectiveParcelC, game.getBoard());
        assertEquals(placeWeWant, test);

    }
    @Test
    public void resolvePatternLineWithTriangleObjective() {

        game.getBoard().addParcel(0,1,-1, new BambooPlantation(BambooColor.GREEN));
        game.getBoard().addParcel(1,0,-1, new BambooPlantation(BambooColor.GREEN));
        game.getBoard().addParcel(1,-1,0, new BambooPlantation(BambooColor.YELLOW));
        game.getBoard().addParcel(-1,1,0, new BambooPlantation(BambooColor.PINK));

        CardObjectiveParcel cardObjectiveParcelLine = new CardObjectiveParcel(game.getBoard(), 2, Patterns.TRIANGLE, BambooColor.GREEN, BambooColor.GREEN, BambooColor.GREEN);
        cardObjectiveParcelLine.verify();

        PositionColored placeWeWant = new PositionColored(new Position(1,1,-2), BambooColor.GREEN);
        PositionColored test = IAforBot.findTheBestPlaceToPlaceAnParcel(cardObjectiveParcelLine, game.getBoard());
        assertEquals(placeWeWant, test);

    }

    @Test
    public void resolvePatternTriangleBesideTheBoardWithJust3ParcelsInTriangleAndOneGreen() {

        game.getBoard().addParcel(-1,0,1, new BambooPlantation(BambooColor.YELLOW));
        game.getBoard().addParcel(0,-1,1, new BambooPlantation(BambooColor.YELLOW));
        game.getBoard().addParcel(-1,-1,2, new BambooPlantation(BambooColor.GREEN));

        CardObjectiveParcel cardObjectiveParcelLine = new CardObjectiveParcel(game.getBoard(), 2, Patterns.TRIANGLE, BambooColor.GREEN, BambooColor.GREEN, BambooColor.GREEN);
        cardObjectiveParcelLine.verify();

        //PositionColored placeWeWant = new PositionColored(new Position(1,1,-2), BambooColor.GREEN);

        PositionColored placeWeWant1 = new PositionColored(new Position(0,-2,2), BambooColor.GREEN);
        PositionColored test1 = IAforBot.findTheBestPlaceToPlaceAnParcel(cardObjectiveParcelLine, game.getBoard());
        assertEquals(placeWeWant1, test1);

        game.getBoard().addParcel(test1.getPosition(), new BambooPlantation(BambooColor.GREEN));

        PositionColored placeWeWant2 = new PositionColored(new Position(-1,-2,3), BambooColor.GREEN);
        PositionColored test2 = IAforBot.findTheBestPlaceToPlaceAnParcel(cardObjectiveParcelLine, game.getBoard());
        assertEquals(placeWeWant2, test2);

        game.getBoard().addParcel(test2.getPosition(), new BambooPlantation(BambooColor.GREEN));

        PositionColored placeWeWant3 = new PositionColored(new Position(-2,-1,3), BambooColor.GREEN);
        PositionColored test3 = IAforBot.findTheBestPlaceToPlaceAnParcel(cardObjectiveParcelLine, game.getBoard());
        assertEquals(placeWeWant3, test3);

    }


    @Test
    public void resolvePatternRhombusWithOneParcelWithTheSameColor() {


        game.getBoard().addParcel(0,1,-1, new BambooPlantation(BambooColor.YELLOW));

        List<PositionColored> positions = new ArrayList<>();

        CardObjectiveParcel cardObjectiveParcelLine = new CardObjectiveParcel(board, 3, Patterns.RHOMBUS, BambooColor.YELLOW, BambooColor.YELLOW, BambooColor.GREEN, BambooColor.GREEN);
        cardObjectiveParcelLine.verify();


        PositionColored test1 = IAforBot.findTheBestPlaceToPlaceAnParcel(cardObjectiveParcelLine, game.getBoard());
        game.getBoard().addParcel(test1.getPosition(), new BambooPlantation(test1.getColor()));
        positions.add(test1);
        PositionColored test2 = IAforBot.findTheBestPlaceToPlaceAnParcel(cardObjectiveParcelLine, game.getBoard());
        game.getBoard().addParcel(test2.getPosition(), new BambooPlantation(test2.getColor()));
        positions.add(test2);
        PositionColored test3 = IAforBot.findTheBestPlaceToPlaceAnParcel(cardObjectiveParcelLine, game.getBoard());
        game.getBoard().addParcel(test3.getPosition(), new BambooPlantation(test3.getColor()));
        positions.add(test3);
        PositionColored test4 = IAforBot.findTheBestPlaceToPlaceAnParcel(cardObjectiveParcelLine, game.getBoard());
        game.getBoard().addParcel(test4.getPosition(), new BambooPlantation(test4.getColor()));
        positions.add(test4);
        PositionColored test5 = IAforBot.findTheBestPlaceToPlaceAnParcel(cardObjectiveParcelLine, game.getBoard());
        game.getBoard().addParcel(test5.getPosition(), new BambooPlantation(test5.getColor()));
        positions.add(test5);
        PositionColored test6 = IAforBot.findTheBestPlaceToPlaceAnParcel(cardObjectiveParcelLine, game.getBoard());
        game.getBoard().addParcel(test6.getPosition(), new BambooPlantation(test6.getColor()));
        positions.add(test6);

        int yellow = 0;
        int green = 0;
        for (PositionColored pos: positions) {
            if (pos.getColor().equals(BambooColor.GREEN)) {
                green++;
            }
            if (pos.getColor().equals(BambooColor.YELLOW)) {
                yellow++;
            }
        }

        assertTrue(green >=2);
        assertTrue(yellow >=2);

    }




}