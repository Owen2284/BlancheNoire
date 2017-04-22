package learning;

public class StateDataStore {

    private String state;
    private int wins;
    private int total;

    public StateDataStore(String state) {
        this.state = state;
        this.wins = 0;
        this.total = 0;
    }

    public String getState() {return this.state;}

    public int getWins() {return this.wins;}

    public int getTotal() {return this.total;}

    public void add(int playerResult) {
        this.wins += playerResult;
        this.total += 1;
    }

    public float getWinPercent() {
        return ((float)wins)/((float)total);
    }

}
