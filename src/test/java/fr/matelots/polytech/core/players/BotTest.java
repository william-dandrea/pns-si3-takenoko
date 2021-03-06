package fr.matelots.polytech.core.players;

import fr.matelots.polytech.core.NoParcelLeftToPlaceException;
import fr.matelots.polytech.core.game.Config;
import fr.matelots.polytech.core.game.Game;
import fr.matelots.polytech.core.game.goalcards.CardObjective;
import fr.matelots.polytech.core.game.goalcards.CardObjectiveGardener;
import fr.matelots.polytech.core.game.goalcards.CardObjectivePanda;
import fr.matelots.polytech.core.game.goalcards.CardObjectiveParcel;
import fr.matelots.polytech.core.game.goalcards.pattern.Patterns;
import fr.matelots.polytech.core.game.goalcards.pattern.PositionColored;
import fr.matelots.polytech.core.game.parcels.BambooColor;
import fr.matelots.polytech.core.game.parcels.BambooPlantation;
import fr.matelots.polytech.core.game.parcels.Layout;
import fr.matelots.polytech.core.game.parcels.Side;
import fr.matelots.polytech.core.players.bots.ThirdBot;
import fr.matelots.polytech.core.players.bots.logger.BotActionType;
import fr.matelots.polytech.core.players.bots.logger.TurnLog;
import fr.matelots.polytech.engine.util.AbsolutePositionIrrigation;
import fr.matelots.polytech.engine.util.Position;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Yann Clodong
 * @author williamdandrea
 */
public class BotTest {
    private Bot bot;
    private Game game;
    private TurnLog turnLog;

    @BeforeEach
    public void init () {
        game = new Game();
        bot = new ThirdBot(game);
        turnLog = new TurnLog(bot);
    }



    @Test
    public void testGetUnfinishedParcelObjectives () {
        assertTrue(bot.getIndividualBoard().getUnfinishedParcelObjectives().isEmpty());

        assertTrue(bot.pickParcelObjective(turnLog).isPresent());

        assertFalse(bot.getIndividualBoard().getUnfinishedParcelObjectives().isEmpty());
    }

    @Test
    public void testRandomPlaceableColor() {
        for (int i = 0; i < Config.NB_PLACEABLE_PARCEL; i++)
            bot.board.addParcel(bot.board.getValidPlaces().iterator().next(), new BambooPlantation(bot.getRandomPlaceableColor()));
        assertEquals(0, bot.board.getParcelLeftToPlace(BambooColor.GREEN));
        assertEquals(0, bot.board.getParcelLeftToPlace(BambooColor.YELLOW));
        assertEquals(0, bot.board.getParcelLeftToPlace(BambooColor.PINK));
        assertNull(bot.getRandomPlaceableColor());
    }

    @Test
    public void testPlaceAnParcelAnywhereRandomColor() {
        int initialNumber = bot.getBoard().getParcelCount();
        bot.placeAnParcelAnywhere(turnLog);
        assertEquals(bot.getBoard().getParcelCount(), initialNumber + 1);

        bot.setCurrentNumberOfAction(0);

        // We verify if we can't place more than 27 parcels
        for (int i = 0; i < Config.NB_PLACEABLE_PARCEL - 1; i++) {
            bot.setCurrentNumberOfAction(0);
            if (BotActionType.PLACE_PARCEL.equals(bot.getLastAction()))
                bot.movePanda(turnLog, Config.POND_POSITION);
            assertNotEquals(Optional.empty(), bot.placeAnParcelAnywhere(turnLog));
        }
        if (BotActionType.PLACE_PARCEL.equals(bot.getLastAction()))
            bot.movePanda(turnLog, Config.POND_POSITION);
        assertEquals(Config.MAX_PARCEL_ON_BOARD, bot.getBoard().getParcelCount());
        assertEquals(Optional.empty(), bot.placeAnParcelAnywhere(turnLog));
    }

    /**
     * In total, we have at the maximum 11 GREEN PARCELS ; 7 PINK PARCELS ; 9 YELLOW PARCELS
     */
    @Test
    public void testPlaceAnParcelAnywhereChosenColor() {
        bot.setCurrentNumberOfAction(0);
        // We verify if we can't place more than 27 parcels
        for (int i = 0; i < 50 ; i++) {
            if (BotActionType.PLACE_PARCEL.equals(bot.getLastAction()))
                bot.movePanda(turnLog, Config.POND_POSITION);
            bot.placeAnParcelAnywhere(BambooColor.GREEN, turnLog);
            bot.setCurrentNumberOfAction(0);
        }
        for (int i = 0; i < 50 ; i++) {
            if (BotActionType.PLACE_PARCEL.equals(bot.getLastAction()))
                bot.movePanda(turnLog, Config.POND_POSITION);
            bot.placeAnParcelAnywhere(BambooColor.YELLOW, turnLog);
            bot.setCurrentNumberOfAction(0);
        }
        for (int i = 0; i < 50 ; i++) {
            if (BotActionType.PLACE_PARCEL.equals(bot.getLastAction()))
                bot.movePanda(turnLog, Config.POND_POSITION);
            bot.setCurrentNumberOfAction(0);
            bot.placeAnParcelAnywhere(BambooColor.PINK, turnLog);
        }

        assertEquals(Config.NB_MAX_GREEN_PARCELS, bot.getBoard().getParcelCount(BambooColor.GREEN));
        assertEquals(Config.NB_MAX_YELLOW_PARCELS, bot.getBoard().getParcelCount(BambooColor.YELLOW) );
        assertEquals(Config.NB_MAX_PINK_PARCELS, bot.getBoard().getParcelCount(BambooColor.PINK));
    }



    /**
     * We try to verify if an objective is resolve or not
     */
    @Test
    public void testCheckCurrentObjective(){

        // We try the function with a null objective
        Optional<CardObjective> currentCardObjective = null;
        Assertions.assertThrows(IllegalArgumentException.class, () -> {bot.checkObjective(currentCardObjective);});

        // Now, we will create a objective and check if this objective is in progress (return true) or not (return false)
        Optional<CardObjective> currentCardObjective2 = Optional.of(game.getNextParcelObjective());
        assertTrue(bot.checkObjective(currentCardObjective2));
    }

    /**
     * We try if we really pick a new parcel objective
     */
    @Test
    public void testPickParcelObjective() {
        int numberOfObjectiveAtStart = bot.getIndividualBoard().countUnfinishedObjectives() + bot.getIndividualBoard().countCompletedObjectives();
        assertEquals(0, numberOfObjectiveAtStart);

        // We test with just one objective and the number of actions we do
        int numberOfActions = bot.getCurrentNumberOfAction();
        Optional<CardObjectiveParcel> cardObjective = bot.pickParcelObjective(turnLog);
        assertTrue(cardObjective.get() instanceof CardObjectiveParcel);


        numberOfActions = bot.getCurrentNumberOfAction();
        assertEquals(1, numberOfActions);
        numberOfObjectiveAtStart = bot.getIndividualBoard().countUnfinishedObjectives() + bot.getIndividualBoard().countCompletedObjectives();
        assertEquals(1, numberOfObjectiveAtStart);

        // We can't have more than 5 objectives into the individualBoard
        for (int i = 0; i < 6; i++) {
            if (Config.isPickAction(bot.getLastAction()))
                bot.movePanda(turnLog, Config.POND_POSITION);
            cardObjective = bot.pickParcelObjective(turnLog);
        }
        assertEquals(Optional.empty(), cardObjective);
    }

    /**
     * We try if we really pick a new gardener objective
     */
    @Test
    public void testPickGardenerObjective() {
        int numberOfObjectiveAtStart = bot.getIndividualBoard().countUnfinishedObjectives() + bot.getIndividualBoard().countCompletedObjectives();
        assertEquals(0, numberOfObjectiveAtStart);

        // We test with just one objective and the number of actions we do
        int numberOfActions = bot.getCurrentNumberOfAction();
        Optional<CardObjectiveGardener> cardObjective = bot.pickGardenerObjective(turnLog);
        assertTrue(cardObjective.get() instanceof CardObjectiveGardener);


        numberOfActions = bot.getCurrentNumberOfAction();
        assertEquals(1, numberOfActions);
        numberOfObjectiveAtStart = bot.getIndividualBoard().countUnfinishedObjectives() + bot.getIndividualBoard().countCompletedObjectives();
        assertEquals(1, numberOfObjectiveAtStart);

        // We can't have more than 5 objectives into the individualBoard
        for (int i = 0; i < 6; i++) {
            if (Config.isPickAction(bot.getLastAction()))
                bot.movePanda(turnLog, Config.POND_POSITION);
            cardObjective = bot.pickGardenerObjective(turnLog);
        }
        assertEquals(Optional.empty(), cardObjective);
    }

    /**
     * We try if we really pick a new gardener objective
     */
    @Test
    public void testPickPandaObjective() {
        int numberOfObjectiveAtStart = bot.getIndividualBoard().countUnfinishedObjectives() + bot.getIndividualBoard().countCompletedObjectives();
        assertEquals(0, numberOfObjectiveAtStart);

        // We test with just one objective and the number of actions we do

        Optional<CardObjectivePanda> cardObjective = bot.pickPandaObjective(turnLog);
        assertTrue(cardObjective.get() instanceof CardObjectivePanda);


        int numberOfActions = bot.getCurrentNumberOfAction();
        assertEquals(1, numberOfActions);

        numberOfObjectiveAtStart = bot.getIndividualBoard().countUnfinishedObjectives() + bot.getIndividualBoard().countCompletedObjectives();
        assertEquals(1, numberOfObjectiveAtStart);

        // We can't have more than 5 objectives into the individualBoard
        for (int i = 0; i < 6; i++) {
            if (Config.isPickAction(bot.getLastAction()))
                bot.movePanda(turnLog, Config.POND_POSITION);
            cardObjective = bot.pickPandaObjective(turnLog);
        }
        assertEquals(Optional.empty(), cardObjective);
    }

    /*@Test
    public void testGetTheColorsWhoseComposeAnCardbjectiveParcel() {
        CardObjective cardObjective = new CardObjectiveParcel(bot.getBoard(), 2, Patterns.TRIANGLE, BambooColor.GREEN, BambooColor.GREEN, BambooColor.GREEN);
        BambooColor[] list1 = bot.getTheColorsWhoseComposeAnCardbjectiveParcel(Optional.of(cardObjective));
        for (BambooColor bambooColor: list1) {
            assertTrue(bambooColor.equals(bambooColor.GREEN));
        }

        CardObjective cardObjective2 = new CardObjectiveParcel(bot.getBoard(), 3, Patterns.RHOMBUS, BambooColor.YELLOW, BambooColor.YELLOW, BambooColor.GREEN, BambooColor.GREEN);
        BambooColor[] list2 = bot.getTheColorsWhoseComposeAnCardbjectiveParcel(Optional.of(cardObjective2));
        for (BambooColor bambooColor: list2) {
            assertTrue(bambooColor.equals(bambooColor.GREEN) || bambooColor.equals(bambooColor.YELLOW));
        }
    }*/

    @Test
    public void testRecoverTheMissingsPositionsToCompleteForParcelObjective() {
        CardObjectiveParcel cardObjective = new CardObjectiveParcel(bot.getBoard(), 2, Patterns.TRIANGLE, BambooColor.GREEN, BambooColor.GREEN, BambooColor.GREEN);

        for (int i = 0; i < 3; i++) {
            if (BotActionType.PLACE_PARCEL.equals(bot.getLastAction()))
                bot.movePanda(turnLog, Config.POND_POSITION);
            bot.placeAnParcelAnywhere(BambooColor.GREEN, turnLog);
        }

        Set<PositionColored> missingPositionsToComplete = bot.recoverTheMissingsPositionsToCompleteForParcelObjective(cardObjective);
        assertTrue(missingPositionsToComplete.size() > 0);

        CardObjectiveParcel cardObjective2 = new CardObjectiveParcel(bot.getBoard(), 3, Patterns.RHOMBUS, BambooColor.YELLOW, BambooColor.YELLOW, BambooColor.GREEN, BambooColor.GREEN);

        for (int i = 0; i < 4; i++) {
            if (BotActionType.PLACE_PARCEL.equals(bot.getLastAction()))
                bot.movePanda(turnLog, Config.POND_POSITION);
            bot.placeAnParcelAnywhere(BambooColor.YELLOW, turnLog);
        }

        Set<PositionColored> missingPositionsToComplete2 = bot.recoverTheMissingsPositionsToCompleteForParcelObjective(cardObjective2);
        assertTrue(missingPositionsToComplete2.size() > 0);


    }

    @Test
    void placeParcelTestLimitOfColoredParcelVersion() {
        for(int i = 0; i < Config.NB_MAX_GREEN_PARCELS; i++) {
            assertTrue(bot.getBoard().addParcel(bot.getBoard().getValidPlaces().stream().findAny().get(), new BambooPlantation(BambooColor.GREEN)));
        }

        assertThrows(NoParcelLeftToPlaceException.class, () -> bot.placeParcel(bot.getBoard().getValidPlaces().stream().findAny().get(), BambooColor.GREEN, turnLog));
    }

    @Test
    void placeParcelTestInvalidPlace() {
        bot.moveGardener(Config.POND_POSITION, turnLog);
        assertFalse(bot.placeParcel(new Position(50, 50, 50), BambooColor.GREEN, turnLog));
    }

    @Test
    void placeParcelNumberOfActions() {
        for(int i = 0; i < Config.TOTAL_NUMBER_OF_ACTIONS; i++) {
            bot.placeParcel(bot.getBoard().getValidPlaces().stream().findAny().get(), BambooColor.GREEN, turnLog);
            if (BotActionType.PLACE_PARCEL.equals(bot.getLastAction()))
                bot.movePanda(turnLog, Config.POND_POSITION);
        }
        assertFalse(bot.placeParcel(bot.getBoard().getValidPlaces().stream().findAny().get(), BambooColor.GREEN, turnLog));
    }

    @Test
    public void testCantMoveGardener() {
        Position pos = new Position(0, 1, -1);
        bot.board.addBambooPlantation(pos);
        bot.setCurrentNumberOfAction(Config.TOTAL_NUMBER_OF_ACTIONS);
        assertFalse(bot.moveGardener(pos, turnLog));
    }

    @Test
    public void testMoveGardenerNotSuccess() {
        Position pos = new Position(0, 1, -1);
        assertFalse(bot.moveGardener(pos, turnLog));
    }

    @Test
    public void testMoveGardenerSuccess() {
        Position pos = new Position(0, 1, -1);
        bot.board.addBambooPlantation(pos);
        assertTrue(bot.moveGardener(pos, turnLog));
    }

    @Test
    public void testIrrigation() {
        AbsolutePositionIrrigation api = new AbsolutePositionIrrigation(new Position(1, 0, -1), Side.LEFT, game.getBoard());
        BambooPlantation plantation = new BambooPlantation(BambooColor.GREEN);
        BambooPlantation plantation2 = new BambooPlantation(BambooColor.GREEN);
        game.getBoard().addParcel(new Position(1, 0, -1), plantation);
        game.getBoard().addParcel(new Position(0, 1, -1), plantation2);

        // Check can't irrigate
        assertFalse(bot.irrigate(api, turnLog));
        assertFalse(api.isIrrigate());

        // Check pickIrrigation
        assertTrue(bot.pickIrrigation(turnLog));

        // Check last action
        var lastOpt = turnLog.getLastAction();
        assertTrue(lastOpt.isPresent());
        assertEquals(lastOpt.get().getType(), BotActionType.PICK_IRRIGATION);

        // Check number of irrigations
        assertEquals(bot.currentNumberOfAction, 1);

        // Check numberOfIrrigation
        assertEquals(bot.getBoard().getIrrigationLeft(), Config.NB_IRRIGATION - 1);
        assertEquals(bot.getIndividualBoard().getNumberOfIrrigations(), 1);

        assertTrue(bot.irrigate(api, turnLog));

        // Check last action
        lastOpt = turnLog.getLastAction();
        assertTrue(lastOpt.isPresent());
        assertEquals(lastOpt.get().getType(), BotActionType.PLACE_IRRIGATION);

        // Check numberOfAction
        assertEquals(bot.currentNumberOfAction, 1);

        // Check the side has been irrigated
        assertTrue(api.isIrrigate());

        // Check numberOfIrrigation
        assertEquals(bot.getIndividualBoard().getNumberOfIrrigations(), 0);
        assertFalse(bot.getIndividualBoard().canPlaceIrrigation());

        AbsolutePositionIrrigation api2 = new AbsolutePositionIrrigation(new Position(0, 1, -1), Side.LEFT, game.getBoard());

        bot.movePanda(turnLog, Config.POND_POSITION);
        assertFalse(bot.irrigate(api2, turnLog));
    }

    @Test
    public void testWeatherCaseRainInitial() {

        bot.getBoard().addParcel(1,-1,0, new BambooPlantation(BambooColor.GREEN));
        bot.weatherCaseRainInitial();
        assertEquals(2, bot.getBoard().getParcel(new Position(1,-1,0)).getBambooSize());


    }

    @Test
    public void placeLayout() {
        Layout layout = Layout.ENCLOSURE;
        this.bot.individualBoard.addLayouts(layout);
        BambooPlantation plantation = new BambooPlantation(BambooColor.GREEN);
        this.bot.board.addParcel(new Position(0, 1, -1), plantation);
        assertTrue(this.bot.placeLayout(turnLog, plantation, layout));
        assertEquals(0, this.bot.getIndividualBoard().getLayouts().size());
    }

    @Test
    public void placeLayoutNoStock() {
        Layout layout = Layout.ENCLOSURE;
        BambooPlantation plantation = new BambooPlantation(BambooColor.GREEN);
        this.bot.board.addParcel(new Position(0, 1, -1), plantation);
        assertFalse(this.bot.placeLayout(turnLog, plantation, layout));
    }

    //setLayout return true même si la parcelle a déjà un layout
    /*@Test
    public void placeLayoutAlreadyLayout() {
        Layout layout = Layout.ENCLOSURE;
        this.bot.individualBoard.addLayouts(layout);
        BambooPlantation plantation = new BambooPlantation(BambooColor.GREEN, layout);
        this.bot.board.addParcel(new Position(0, 1, -1), plantation);
        assertFalse(this.bot.placeLayout(turnLog, plantation, layout));
    }*/

    @Test
    public void placeNullLayout() {
        Layout layout = Layout.ENCLOSURE;
        this.bot.individualBoard.addLayouts(layout);
        BambooPlantation plantation = new BambooPlantation(BambooColor.GREEN);
        this.bot.board.addParcel(new Position(0, 1, -1), plantation);
        assertFalse(this.bot.placeLayout(turnLog, plantation, null));
    }

}
