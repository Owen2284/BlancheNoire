package learning;

import games.GameScript;
import games.GameState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Creates data that is composed of a GameState and a label representing whether or not the black player wins.
 */
public class ClassificationType2Formatter extends NeuralNetDataFormatter {

    public String getFormatNum() {return "2";}

    public ArrayList<String> formatData(ArrayList<String> in, double fractionToUse, long seed) {
        ArrayList<String> allScripts = new ArrayList<String>(in);
        int gameCounter = 1;
        int scriptsToUse = Math.min(allScripts.size(), (int)(allScripts.size() * fractionToUse));
        Random r = new Random(seed);
        Map<String, Integer> results = new HashMap<String, Integer>();

        System.out.println(" " + scriptsToUse + " of the " + allScripts.size() + " scripts provided will be used.");

        // Loop for all scripts provided.
        for (int n = 0; n < scriptsToUse; ++n) {

            // Fetch GameScript and store necessary values.
            String game = allScripts.get(r.nextInt(allScripts.size()));
            allScripts.remove(game);
            GameScript gs = new GameScript(game);
            int darkResult = Math.max(0, gs.darkResult());

            // Loop through all games states in the script.
            for (int turn = 0; turn < gs.getTotalMoves(); ++turn) {

                // Stores the data about the GameState in the maps.
                GameState g = gs.generateStateAfterTurn(turn);
                String gamestate = g.toFlatString(GameState.COUNTER_DARK, GameState.COUNTER_LIGHT, ",");
                results.put(gamestate, darkResult);

            }
            if (gameCounter % 1000 == 0) {
                System.out.println(" Game " + gameCounter + "/" + scriptsToUse + " complete.");
            }
            ++gameCounter;
        }

        // Pray that Java didn't run out of memory, then analyse the maps.
        System.out.println(" Analysing " + results.keySet().size() + " states.");
        ArrayList<String> csvLines = new ArrayList<String>();
        for (String gamestate : results.keySet()) {
            String newline = results.get(gamestate) + "," + gamestate;
            csvLines.add(newline);
        }

        return csvLines;
    }
}
