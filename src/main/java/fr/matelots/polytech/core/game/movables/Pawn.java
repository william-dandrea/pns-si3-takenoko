package fr.matelots.polytech.core.game.movables;

import fr.matelots.polytech.core.UnreachableParcelException;
import fr.matelots.polytech.core.game.Board;
import fr.matelots.polytech.engine.util.Position;

/**
 * @author Gabriel Cogne
 */
public abstract class Pawn {
    private final Board board;
    private Position position;


    public Pawn (Board board, Position position) {
        this.board = board;
        this.position = position;
    }

    /**
     * Move the pawn to the given location and apply is action
     * @param x location on x axis
     * @param y location on y axis
     * @param z location on z axis
     * @return is the movement success ?
     */
    public boolean moveTo (int x, int y, int z) {
        Position tmp = new Position(x, y, z);

        if (!isReachablePlace(tmp)) {
            throw new UnreachableParcelException();
        }

        return moveToAbsolute(x,y,z);
    }

    public boolean moveToAbsolute (int x, int y, int z) {
        Position tmp = new Position(x, y, z);

        if (getBoard().placePawn(this, tmp)) {
            setPosition(tmp);
            makeAction();
            return true;
        } else
            return false;
    }

    abstract void makeAction ();

    Board getBoard () {
        return board;
    }

    void setPosition (Position position) {
        this.position = position;
    }

    public Position getPosition () {
        return position;
    }

    /**
     * Tell if you are allowed to move to a specific position
     * @param goal the position you want to reach
     * @return if the goal is in the list of all reachable position from your current position
     */
    boolean isReachablePlace (Position goal) {
        return board.getReachablePositionFrom(position).contains(goal);
    }
}
