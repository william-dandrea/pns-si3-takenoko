package fr.matelots.polytech.core.players.bots;

import fr.matelots.polytech.core.game.Board;
import fr.matelots.polytech.core.game.Game;
import fr.matelots.polytech.core.game.goalcards.CardObjective;
import fr.matelots.polytech.core.game.goalcards.CardObjectiveParcel;
import fr.matelots.polytech.core.game.goalcards.pattern.Patterns;
import fr.matelots.polytech.core.game.parcels.BambooColor;
import fr.matelots.polytech.core.players.bots.logger.TurnLog;
import fr.matelots.polytech.engine.util.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class RushParcelBotTest {

    private RushParcelBot bot;
    private Game game;
    private TurnLog turnLog;

    @BeforeEach
    public void init () {
        game = new Game();
        bot = new RushParcelBot(game);
        turnLog = new TurnLog(bot);
        bot.setTurnLogger(turnLog);


    }

    @Test
    public void testInilializeOrUpdateListOfCurrentsObjectiveWithAnyPreviousObjectivesInTheList() {

        List<Optional<CardObjective>> currentList = bot.getListOfCurrentsObjectives();
        assertEquals(0, currentList.size());

        bot.inilializeOrUpdateListOfCurrentsObjective();

        assertTrue(currentList.get(0).isPresent());
        assertTrue(currentList.get(1).isPresent());

    }

    @Test
    public void testInilializeOrUpdateListOfCurrentsObjectiveWith2PreviousObjectivesInTheList() {
        // On tente de rajouter des objectifs
        List<Optional<CardObjective>> currentList = bot.getListOfCurrentsObjectives();


        // Pick 2 objectives
        bot.inilializeOrUpdateListOfCurrentsObjective();
        bot.setCurrentNumberOfAction(0);
        bot.inilializeOrUpdateListOfCurrentsObjective();
        bot.setCurrentNumberOfAction(0);
        bot.inilializeOrUpdateListOfCurrentsObjective();
        bot.setCurrentNumberOfAction(0);
        bot.inilializeOrUpdateListOfCurrentsObjective();


        assertTrue(currentList.get(0).isPresent());
        assertTrue(currentList.get(1).isPresent());
        assertTrue(currentList.get(2).isPresent());
        assertTrue(currentList.get(3).isPresent());
        assertTrue(currentList.get(4).isPresent());

    }

    @Test
    public void testEasiestObjectiveToResolve() {
        RushParcelBot mockBot = mock(RushParcelBot.class);
        List<Optional<CardObjective>> cardList = mock(List.class);
        TurnLog mockLog = mock(TurnLog.class);
        Board mockBoard = mock(Board.class);

        cardList.add(Optional.of(new CardObjectiveParcel(mockBoard, 2, Patterns.TRIANGLE, BambooColor.GREEN, BambooColor.GREEN, BambooColor.GREEN)));
        cardList.add(Optional.of(new CardObjectiveParcel(mockBoard, 3, Patterns.TRIANGLE, BambooColor.YELLOW, BambooColor.YELLOW, BambooColor.YELLOW)));
        cardList.add(Optional.of(new CardObjectiveParcel(mockBoard, 4, Patterns.LINE, BambooColor.PINK, BambooColor.PINK, BambooColor.PINK)));
        cardList.add(Optional.of(new CardObjectiveParcel(mockBoard, 5, Patterns.RHOMBUS, BambooColor.PINK, BambooColor.PINK, BambooColor.PINK, BambooColor.PINK)));
        cardList.add(Optional.of(new CardObjectiveParcel(mockBoard, 4, Patterns.C, BambooColor.PINK, BambooColor.PINK, BambooColor.PINK)));

        verify(cardList).add(Optional.of(new CardObjectiveParcel(mockBot.getBoard(), 2, Patterns.TRIANGLE, BambooColor.GREEN, BambooColor.GREEN, BambooColor.GREEN)));


        mockBot.placeParcel(new Position(1,1,1), BambooColor.GREEN, mockLog);

        //Whitebox.setInternalState(mockBot, "listOfCurrentsObjectives", cardList);


        mockBot.easiestObjectiveToResolve();

        System.out.println(mockBot.getListOfCurrentsObjectives());

        //assertTrue(Whitebox.getInternalState(mockBot, "cardWeActuallyTryToResolve") != null);

    }

}