package core.game;

import core.players.Player;
import core.players.VirtualPlayer;

import java.util.ArrayList;
import java.util.List;

public class Game {
    // Attributes
    private final List<Player> players;
    private final Board board;
    private Player winner;

    // Constructors
    public Game () {
        players = new ArrayList<>();
        board = new Board();
    }

    public Player getWinner () {
        return winner;
    }

    // Methods
    public void run () {
        System.out.print("Joueurs: ");
        players.forEach(System.out::println);
        System.out.println();

        System.out.println(board);

        int i = 0;

        while (getWinner() == null) {
            for (Player p : players) {
                if (p instanceof VirtualPlayer) {
                    ((VirtualPlayer) p).playTurn();
                }
            }

            if (i > 0) {
                break;
            }
            i++;
        }
    }
}
