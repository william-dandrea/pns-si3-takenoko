package fr.matelots.polytech;

import fr.matelots.polytech.core.game.Game;
import fr.matelots.polytech.core.players.Bot;
import fr.matelots.polytech.core.players.bots.QuintusBot;
import fr.matelots.polytech.core.players.bots.RushParcelBot;
import fr.matelots.polytech.engine.util.LogHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * @author AlexandreArcil
 * @author Gabriel Cogne
 */
public class Takenoko {

    public static void main(String[] args) {
        LogManager.getLogManager().reset();
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.addHandler(new LogHandler());

        final int NB_GAMES = 10;
        Map<String, Integer> results = new HashMap<>();
        int nbCanceledGame = 0;
        int nbDraw = 0;

        Game game;

        List<List<Bot>> ranksTmp;
        List<Bot> winnerTmp;

        for (int nbGame = 0; nbGame < NB_GAMES; nbGame++) {
            game = new Game();

            game.run(false);

            if (game.isCanceledGame()) {
                nbCanceledGame++;
            }

            ranksTmp = game.getRanks();
            if (!ranksTmp.isEmpty()) {
                winnerTmp = game.separate(ranksTmp.get(0));

                if (winnerTmp.size() > 1) {
                    nbDraw++;
                }

                winnerTmp.forEach(bot -> {
                    if (results.containsKey(bot.getName())) {
                        results.put(bot.getName(), results.get(bot.getName()) + 1);
                    }
                    else {
                        results.put(bot.getName(), 1);
                    }
                });
            }
        }

        StringBuilder builder = new StringBuilder("Résultats :\n");
        results.keySet().forEach(bot ->
                builder.append(bot).append(" : ").append(results.get(bot))
                        .append(" victoire(s).\n")
        );
        builder.append("Dont :\n");
        builder.append(nbCanceledGame).append(" partie(s) annulée(s).\n");
        builder.append(nbDraw).append(" égalité(s).\n");

        Logger.getGlobal().info(builder.toString());
    }

}
