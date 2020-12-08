package fr.matelots.polytech.core.players.bots;

import fr.matelots.polytech.core.game.Game;
import fr.matelots.polytech.core.game.goalcards.CardObjective;
import fr.matelots.polytech.core.game.goalcards.CardObjectivePanda;
import fr.matelots.polytech.core.game.goalcards.CardObjectiveParcel;
import fr.matelots.polytech.core.game.movables.Panda;
import fr.matelots.polytech.core.game.parcels.BambooColor;
import fr.matelots.polytech.core.players.Bot;
import fr.matelots.polytech.engine.util.Position;

import java.util.*;

/**
 * This bot goal is to rush the panda objective.
 * - [OK] Tant que le tableau d'objectif n'est pas plein, tirer carte objectif panda
 * - [ ] Essayer de résoudre l'objectif panda
 *      - [OK] Si il n'y a aucune parcelle, placer une parcelle
 *      - [OK] Regarder la différence entre le nombre de bambou en stock (de chaque couleurs) et l'objectif de bambou en stock (on va prendre 3)
 *          - [OK] En extraire une couleur favorite, couleur a laquelle nous allons essayer de combler le trou
 *          - [OK] Regarder sur le board de jeu l'endroit ou il y a un bambou de cette couleur
 *          - [ ] Si il y a une parcelle avec des bambou de cette couleur
 *              - [ ] Deplacer le panda sur cette parcelle
 *          - [ ] Sinon, s'il n'y a pas de parcelle avec des bambou de cette couleur
 *              - [ ] Regarder si il y a une parcelles avec des bambou d'une autre couleur
 *              - [ ] S'il y a une parcelle avec des bambou d'une autre couleur
 *                  - [ ] Deplacer le panda sur celle-ci
 *              - [ ] Sinon
 *                  - [ ] Si le nombre de parcelle est inferieur à 6
 *                      - [ ] Placer une parcelle n'importe ou
 *                  - [ ] Sinon
 *                      - [ ] Deplacer le jardinier n'importe ou
 * @author williamdandrea
 */
public class FifthBot extends Bot {

    private CardObjectivePanda currentObjective;
    private List<CardObjectivePanda> unfinishedBotPandasObjectives;
    private BotAction action = BotAction.NONE;
    private Panda panda;

    private int OBJECTIVE_NUMBER_OF_BAMBOO_STOCK = 3;
    private int MINIMAL_NUMBER_OF_PARCELS_IN_THE_BOARD_TO_TRY_TO_RESOLVE_OBJECTIVES = 3;




    public FifthBot(Game game) {
        super(game);
        unfinishedBotPandasObjectives = new ArrayList<>();
        panda = board.getPanda();
    }

    @Override
    public void playTurn() {
        action = BotAction.NONE;

        // 1 // We pick objectives to have in total 5 objectives.
        pickThe5TheObjectives();

        // 2 // Try to resolve panda objective

        tryToResolvePandaObjective();


    }

    void tryToResolvePandaObjective() {
        // 1 // Si il n'y a aucune parcelle, placer une parcelle
        if (board.getParcelCount() <= MINIMAL_NUMBER_OF_PARCELS_IN_THE_BOARD_TO_TRY_TO_RESOLVE_OBJECTIVES) { placeAnParcelAnywhere(); }
        else {
            // 2.1 // Regarder la différence entre le nombre de bambou en stock (de chaque couleurs) et l'objectif de bambou en stock (on va prendre 3)
            BambooColor colorWeWillTryToStock = watchTheDifferenceBetweenTheWantedNumberOfBambooInStockAndTheNumberOfBambooInIndividualBoard();

            // 2.1.1 // Si on a plusieurs bamboo en stock, alors colorWeWillTryToStock == null, et donc nous bougeons le panda n'importe ou
            moveThePandaAnywhere();

            // 2.2 // Regarder sur le board de jeu l'endroit ou il y a un bambou de cette couleur
            Position goodPlace = searchTheParcelWhereWeHaveABambooWithTheGoodColor(colorWeWillTryToStock);

        }

    }

    /**
     * This method move the gardener anywhere in the game board
     */
    void moveThePandaAnywhere() {
        Optional<Position> position = board.getPositions().stream()
                .filter(p ->
                        !board.getParcel(p).isPond())
                .findAny();

        panda.setCurrentPlayer(this);
        panda.moveTo(position.get().getX(), position.get().getY(),position.get().getZ());
    }


    /**
     * This method move the gardener anywhere in the game board
     */
    void moveTheGardenerAnywhere() {
        Optional<Position> position = board.getPositions().stream()
                .filter(p ->
                        !board.getParcel(p).isPond())
                .max(Comparator.comparingInt( (Position p) -> board.getParcel(p).getBambooSize() ));

        if(!position.isEmpty()){
            // si cette parcelle existe alors on bouge le jardinier dessus.
            board.getGardener().moveTo(position.get().getX(), position.get().getY(), position.get().getZ());
        }

    }

    /**
     * This method search a plca where a colorWeTryToFind bamboo is placed
     * @param colorWeTryToFind is the color we want to find in the map
     * @return the position where the panda we'll can eat a bamboo, null if we find nothing
     */
    Position searchTheParcelWhereWeHaveABambooWithTheGoodColor(BambooColor colorWeTryToFind) {
        // We recover all the positions
        Set<Position> allThePositions = board.getPositions();

        Position goodPosition = allThePositions.stream().filter(position ->
                board.getParcel(position).getBambooSize() > 0
                && board.getParcel(position).getBambooColor().equals(colorWeTryToFind)
        ).findAny().orElse(null);

        return goodPosition;

    }


    /**
     * This method return the color (identifier) of the bamboo color with the less stock of bamboo in the individual board
     * @return null if the bamboo stock of each color is superior at OBJECTIVE_NUMBER_OF_BAMBOO_STOCK, else return the identifier :
     */
    BambooColor watchTheDifferenceBetweenTheWantedNumberOfBambooInStockAndTheNumberOfBambooInIndividualBoard() {
        // We recover the number of bamboo in stock an we compare it to the OBJECTIVE_NUMBER_OF_BAMBOO_STOCK
        int differenceOfGreenBamboo = OBJECTIVE_NUMBER_OF_BAMBOO_STOCK - individualBoard.getGreenEatenBamboo();
        int differenceOfYellowBamboo = OBJECTIVE_NUMBER_OF_BAMBOO_STOCK - individualBoard.getYellowEatenBamboo();
        int differenceOfPinkBamboo = OBJECTIVE_NUMBER_OF_BAMBOO_STOCK - individualBoard.getPinkEatenBamboo();

        Map<BambooColor, Integer> pairsOfBamboo = new HashMap<>();
        pairsOfBamboo.put(BambooColor.GREEN, differenceOfGreenBamboo);
        pairsOfBamboo.put(BambooColor.YELLOW, differenceOfYellowBamboo);
        pairsOfBamboo.put(BambooColor.PINK, differenceOfPinkBamboo);

        int max = 0;
        BambooColor finalColorIdentifier = null;
        for (Map.Entry<BambooColor, Integer> pairs : pairsOfBamboo.entrySet()) {
            BambooColor identifier = pairs.getKey();
            int value = pairs.getValue();

            if (value >= max) {
                max = value;
                finalColorIdentifier = identifier;
            }
        }

        return finalColorIdentifier;
    }


    public List<CardObjectivePanda> getUnfinishedBotPandasObjectives() {
        return unfinishedBotPandasObjectives;
    }

    /**
     * pick a new objective and add it to the player board and to the unfinishedBotPandasObjectives
     * @return true if we pick a new objective, false if not
     */
    boolean pickAnPandaObjectiveAndAddToThePlayerBoard() {
        if (individualBoard.countUnfinishedPandaObjectives() < 5) {
            pickPandaObjective();
            updateUnfinishedPandasObjectives();
            return true;
        }
        updateUnfinishedPandasObjectives();
        return false;

    }

    /**
     * This method will complete the list of objective to have in total 5 objectives in the individual board
     */
    void pickThe5TheObjectives() {
        while (pickAnPandaObjectiveAndAddToThePlayerBoard());
    }

    /**
     * update the list unfinishedPandasObjectives with the individualBoard Current objectives
     * @return true if all the objectives are in progress, else return false
     */
    boolean updateUnfinishedPandasObjectives() {
        unfinishedBotPandasObjectives = getIndividualBoard().getUnfinishedPandaObjectives();

        //We verify if all the objectives are in progress
        for (int i = 0; i < unfinishedBotPandasObjectives.size() ; i++) {
            if (checkObjective(unfinishedBotPandasObjectives.get(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * This function check the current objective
     * @return true if the currentObjective is in progress or false if there is any currentObjective or if
     * the currentObjective is finish
     */
    boolean checkObjective(CardObjectivePanda objective) {
        // If the currentObjective == null, or if the current goal is finish (the function verify return true if an objective is completed)
        // Else, we return true because the objective in progress
        if (objective != null) {
            objective.setIndividualBoard(individualBoard);
            return !objective.verify();
        }
        return false;
    }



    @Override
    public boolean canPlay() {
        return false;
    }

    @Override
    public String getTurnMessage() {
        return null;
    }
}