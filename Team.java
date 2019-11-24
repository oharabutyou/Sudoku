public class Team {
    int[] team;
    boolean[] rcm;
    final int RCM = 3;

    Team(int[] team) {
        this.team = team.clone();
    }

    int getLength() {
        return team.length;
    }

    int getN(int n) {
        return team[n];
    }
}