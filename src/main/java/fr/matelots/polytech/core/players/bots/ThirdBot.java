package fr.matelots.polytech.core.players.bots;

import fr.matelots.polytech.core.game.Game;
import fr.matelots.polytech.core.game.goalcards.CardObjectiveGardener;
import fr.matelots.polytech.core.game.goalcards.CardObjectiveParcel;
import fr.matelots.polytech.core.game.parcels.BambooPlantation;
import fr.matelots.polytech.core.game.parcels.Parcel;
import fr.matelots.polytech.core.players.Bot;
import fr.matelots.polytech.engine.util.Position;

import java.util.*;

public class ThirdBot extends Bot {
    private static final int NGARDENER = 2;
    private static final int NPARCEL = 2;

    private boolean filling = false;
    private boolean havePlayed = false;

    private CardObjectiveParcel parcelGoal;
    private CardObjectiveGardener gardenerGoal;

    private final Random rnd;

    public ThirdBot(Game game) {
        super(game);
        rnd = new Random();
    }

    // Bot actions
    private void pickParcelGoal() {
        havePlayed = pickParcelObjective();
    }
    private void pickGardenerGoal() {
        havePlayed = pickGardenerObjective();
    }
    private void moveGardener(Position position) {
        havePlayed = board.getGardener().moveTo(position.getX(), position.getY(), position.getZ());
    }
    private void placeParcel(Position position, Parcel parcel) {
        havePlayed = board.addParcel(position, parcel);
    }
    private void placeBambooPlantation(Position position) {
        havePlayed = board.addBambooPlantation(position);
    }

    // Random functions
    private <T> T getRandomIn(List<? super T> objs) {
        return (T)objs.get(rnd.nextInt(objs.size()));
    }
    private void placeParcelSomewhere(Parcel parcel) {
        List<Position> positions = new ArrayList<>(board.getValidPlaces());
        var position = getRandomIn(positions);
        placeParcel(position, parcel);
    }
    private void placeBambooSomewhere() {
        List<Position> positions = new ArrayList<>(board.getValidPlaces());
        var position = getRandomIn(positions);
        placeBambooPlantation(position);
    }

    // Bot strategies
    private void attemptResolveParcel() {
        if(parcelGoal != null && parcelGoal.verify()) parcelGoal = null;
        if(parcelGoal == null) return;

        List<Position> accessibleMissing = new ArrayList<>();

        var missing = parcelGoal.getMissingPositionsToComplete();
        missing.stream().filter(p -> board.isPlaceValid(p)).forEach(accessibleMissing::add);

        if(accessibleMissing.isEmpty()) {
            // est insoluble
            parcelGoal = getSolvableGoal();
            return;
        }
        else {
            var position = getRandomIn(accessibleMissing);
            placeBambooPlantation(position);
        }

        if(parcelGoal.verify()) parcelGoal = null;
    }
    private void attemptResolveGardener() {
        if(gardenerGoal == null) return;

        var position = board.getPositions().stream()
                .filter(p ->
                        !board.getParcel(p).isPond() &&
                        board.getParcel(p).getBambooColor().equals(gardenerGoal.getColor()) &&
                        board.getParcel(p).getBambooSize() < gardenerGoal.getSize())
                .max(Comparator.comparingInt( (Position p) -> board.getParcel(p).getBambooSize() ));

        if(position.isEmpty()) {
            // Aucune parcel ne peut resoudre l'objectif : soit la hauteur de bamboo est trop grande soit elle n'est pas de la bonne couleur.
            placeParcelSomewhere(new BambooPlantation(gardenerGoal.getColor()));
        }
        else {
            // si cette parcelle existe alors on bouge le jardinier dessus.
            moveGardener(position.get());
        }

        if(gardenerGoal.verify()) gardenerGoal = null;
    }

    private boolean parcelGoalIsSolvableNow(CardObjectiveParcel parcelG) {
        List<Position> accessibleMissing = new ArrayList<>();

        var missing = parcelGoal.getMissingPositionsToComplete();
        missing.stream().filter(p -> board.isPlaceValid(p)).forEach(accessibleMissing::add);

        return !accessibleMissing.isEmpty();
    }
    private boolean gardenerGoalIsSolvable(CardObjectiveGardener gardenerG) {
        var position = board.getPositions().stream()
                .filter(p ->
                        !board.getParcel(p).isPond() &&
                        board.getParcel(p).getBambooColor().equals(gardenerG.getColor()) &&
                        board.getParcel(p).getBambooSize() < gardenerG.getSize())
                .max(Comparator.comparingInt( (Position p) -> board.getParcel(p).getBambooSize() ));

        if(position.isEmpty() ) // il n'y a pas de parcels qui peuvent accueillir le bambou
            return board.getParcelLeftToPlace() == 0; // si on peut placer une parcel alors c'est bon !
        else return true; // la il y a un endroit que l'on peut valider pour finir !
    }
    /**
     * If the bot have not gardenerGoal and all of his parcelGoal are unsolvable,
     * the bot will placeAnywhere else the bot will pass the goal
     */
    private CardObjectiveParcel getSolvableGoal() {
        if(getIndividualBoard().countUnfinishedGardenerObjectives() == 0) {

            var result = getIndividualBoard().getUnfinishedParcelObjectives().stream().filter(this::parcelGoalIsSolvableNow);
            if(result.count() == 0) {
                placeBambooSomewhere();
                return parcelGoal;
            } else {
                return result.findFirst().get();
            }
        }
        return null;// Sera redefinie automatiquement plus tard
    }


    private void attemptResolveGoal() {
        attemptResolveParcel();
        if(!havePlayed)
            attemptResolveGardener();
    }
    private void fill() {
        if(getIndividualBoard().countUnfinishedGardenerObjectives() < NGARDENER) pickGardenerGoal();
        else if(getIndividualBoard().countUnfinishedParcelObjectives() < NPARCEL) pickParcelGoal();
        else filling = false;
    }

    // Check goals
    private boolean canSelectGoal() {
        return getIndividualBoard().countUnfinishedObjectives() > 0;
    }

    private void selectGoal() {
        if(!canSelectGoal() || filling) {
            filling = true;
        }
        else {
            var unfinishedGardeners = getIndividualBoard().getUnfinishedGardenerObjectives();
            var unfinishedParcels = getIndividualBoard().getUnfinishedParcelObjectives();

            if(unfinishedGardeners.size() == 0) {
                // Parcels
                parcelGoal = getRandomIn(unfinishedParcels);
            }
            else if(unfinishedParcels.size() == 0) {
                // Jardinier
                gardenerGoal = getRandomIn(unfinishedGardeners);
            }
            else {
                // On choisit entre les deux

                if(rnd.nextBoolean()) { // William D'Andrea, ici on choisi entre si on resout un objectif jardinier ou un objectif parcel (si les deux sont disponible).
                    // Jardinier
                    gardenerGoal = getRandomIn(unfinishedGardeners);
                }
                else {
                    // Parcel
                    parcelGoal = getRandomIn(unfinishedParcels);
                }
            }
        }
    }
    private void checkCurrentGoals() {
        if((parcelGoal == null || parcelGoal.verify()) && (gardenerGoal == null || gardenerGoal.verify())) {
            selectGoal();
        }
    }

    // Check  Can Play
    private boolean canPlaceParcel() {
        return board.getParcelLeftToPlace() > 0;
    }
    private boolean haveParcelGoal() {
        return board.getDeckParcelObjective().canPick() || getIndividualBoard().countUnfinishedParcelObjectives() > 0;
    }
    private boolean canPickGardener() {
        return board.getDeckGardenerObjective().canPick() && getIndividualBoard().countUnfinishedGardenerObjectives() < NPARCEL;
    }
    private boolean haveGardenerGoal() {
        return canPickGardener() || getIndividualBoard().getUnfinishedGardenerObjectives().stream().anyMatch(this::gardenerGoalIsSolvable);
    }

    @Override
    public void playTurn() {
        havePlayed = false;
        if(!canPlay()) return;
        if(board.getParcelCount() == 1)
            placeBambooSomewhere();

        while(!havePlayed) {
            checkCurrentGoals();

            if(filling)
                fill();
            else
                attemptResolveGoal();
        }
    }

    @Override
    public boolean canPlay() {
        return haveGardenerGoal() || (haveParcelGoal() && canPlaceParcel());
    }
}