package learning;

import games.GameScript;
import games.GameState;

import java.util.*;

/**
 * Creates data that is composed of a GameState and a score between 0 and 10.
 */
public class ClassificationType3Formatter extends NeuralNetDataFormatter {

    public String getFormatNum() {return "3";}

    public ArrayList<String> formatData(ArrayList<String> in, double fractionToUse, long seed) {
        ArrayList<String> allScripts = new ArrayList<String>(in);
        int gameCounter = 1;
        int scriptsToUse = Math.min(allScripts.size(), (int)(allScripts.size() * fractionToUse));
        Random r = new Random(seed);
        Map<String, WinDataStore> states = new HashMap<String, WinDataStore>();
        long startTimestamp = System.currentTimeMillis();

        System.out.println(" " + scriptsToUse + " of the " + allScripts.size() + " scripts provided will be used.");

        // Loop for all scripts provided.
        for (int n = 0; n < scriptsToUse; ++n) {

            // Fetch GameScript and store necessary values.
            String game = allScripts.get(r.nextInt(allScripts.size()));
            allScripts.remove(game);
            GameScript gs = new GameScript(game);
            int[] darkResults = {Math.max(0, gs.darkResult()), 1 - Math.max(0, gs.darkResult())};
            int[][] idPairs = {{GameState.COUNTER_DARK, GameState.COUNTER_LIGHT}, {GameState.COUNTER_LIGHT, GameState.COUNTER_DARK}};

            // Loop through all games states in the script.
            for (int turn = 0; turn < gs.getTotalMoves(); ++turn) {

                // Stores the data about the GameState in the maps, for both the provided
                // state and the inverted state. States are inverted to increase available data.
                GameState g = gs.generateStateAfterTurn(turn);
                GameState f = g.flip(true);
                for (int inversion = 0; inversion < 2; ++inversion) {
                    String[] allVariants = {
                            g.toFlatString(idPairs[inversion][0], idPairs[inversion][1], ","),
                            g.rotate(1).toFlatString(idPairs[inversion][0], idPairs[inversion][1], ","),
                            g.rotate(2).toFlatString(idPairs[inversion][0], idPairs[inversion][1], ","),
                            g.rotate(3).toFlatString(idPairs[inversion][0], idPairs[inversion][1], ","),
                            f.toFlatString(idPairs[inversion][0], idPairs[inversion][1], ","),
                            f.rotate(1).toFlatString(idPairs[inversion][0], idPairs[inversion][1], ","),
                            f.rotate(2).toFlatString(idPairs[inversion][0], idPairs[inversion][1], ","),
                            f.rotate(3).toFlatString(idPairs[inversion][0], idPairs[inversion][1], ",")
                    };

                    boolean placed = false;
                    for (String rotatedState : allVariants) {
                        if (states.containsKey(rotatedState)) {
                            states.get(rotatedState).add(darkResults[inversion]);
                            placed = true;
                            break;
                        }
                    }
                    if (!placed) {
                        WinDataStore wd = new WinDataStore();
                        wd.add(darkResults[inversion]);
                        states.put(allVariants[0], wd);
                    }
                }
            }
            int BATCH = 1000;
            if (gameCounter % BATCH == 0) {
                double rate = ((double)(System.currentTimeMillis() - startTimestamp) / gameCounter);
                System.out.println(" Game " + gameCounter + "/" + scriptsToUse + " complete. Estimated time remaining:- " + (int)((rate * (scriptsToUse - gameCounter))/ 1000) + " seconds.");
            }
            ++gameCounter;
        }

        // Pray that Java didn't run out of memory, then analyse the map.
        System.out.println(" Analysing " + states.keySet().size() + " games.");
        ArrayList<String> csvLines = new ArrayList<String>();

        // First, find the average times that a state appeared.
        int totalStates = 0;
        for (String state : states.keySet()) {
            WinDataStore stateData = states.get(state);
            totalStates += stateData.getTotal();
        }
        float averageAppearanceOfStates = (float)totalStates / (float)states.keySet().size();
        System.out.println("Num appearances:- " + totalStates + ", num states:- " + states.keySet().size() + ". Average = " + averageAppearanceOfStates);

        // Then allocate bins and determine their size.
        float binSize =  1 / (float)(Math.ceil(averageAppearanceOfStates)+1);
        float[] binBoundaries = new float[(int)Math.ceil(averageAppearanceOfStates)];
        for (int binNum = 0; binNum < binBoundaries.length; ++binNum) {
            binBoundaries[binNum] = (binNum+1) * binSize;
        }

        // Finally, convert each win percent into bin category.
        System.out.println("Converting win data into bins.");
        Iterator<Map.Entry<String, WinDataStore>> iter = states.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, WinDataStore> state = iter.next();
            WinDataStore stateData = state.getValue();
            float winRatio = stateData.getWinPercent();
            int binCategory = 0;
            while (binCategory < binBoundaries.length && winRatio > binBoundaries[binCategory]) {
                binCategory += 1;
            }
            String newline = binCategory + "," + state.getKey();
            csvLines.add(newline);
            iter.remove();
            if (states.keySet().size() % 100000 == 0) {
                System.out.println(" " + (states.keySet().size()) + " states remaining.");
            }
        }

        return csvLines;
    }

}
