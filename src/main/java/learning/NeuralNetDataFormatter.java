package learning;

import java.util.ArrayList;

public abstract class NeuralNetDataFormatter {

    public NeuralNetDataFormatter() {}

    public abstract String getFormatNum();

    public abstract ArrayList<String> formatData(ArrayList<String> in, double fractionToUse, long seed);

}
