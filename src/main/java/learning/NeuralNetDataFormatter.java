package learning;

import java.util.ArrayList;

/**
 * Abstract class representing a class for formatting data into a csv file for deep learning.
 */
public abstract class NeuralNetDataFormatter {

    public NeuralNetDataFormatter() {}

    public abstract String getFormatNum();

    /**
     * Turns an ArrayList of scripts into a csv file of states.
     */
    public abstract ArrayList<String> formatData(ArrayList<String> in, double fractionToUse, long seed);

}
