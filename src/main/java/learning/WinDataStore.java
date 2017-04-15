package learning;

public class WinDataStore {

    private int wins;
    private int total;

    public WinDataStore() {
        this.wins = 0;
        this.total = 0;
    }

    public void add(int playerResult) {
        this.wins += playerResult;
        this.total += 1;
    }

    public float getWinPercent() {
        return ((float)wins)/((float)total);
    }

}
