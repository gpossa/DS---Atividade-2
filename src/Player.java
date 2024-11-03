public class Player {
    private String name;
    private char team;
    private int numWins;

    public Player(String name, char team, int numWins) {
        this.name = name;
        this.team = team;
        this.numWins = numWins;
    }

    public String getName() {
        return name;
    }

    public char getTeam() {
        return team;
    }

    public int getWins() {
        return numWins;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTeam(char team) {
        this.team = team;
    }

    public void setWins(int numWins) {
        this.numWins = numWins;
    }
}