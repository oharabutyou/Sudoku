import java.util.LinkedList;

@SuppressWarnings("serial")
class TeamList extends LinkedList<Team> {
}

public class SudokuBoard2 extends SudokuBoard {
    TeamList[] teamList = new TeamList[msize + 1];

    SudokuBoard2(String filename) {
        super(filename);
        initList();
    }

    void initList() {
        for (int teamLen = 1; teamLen <= msize; teamLen++) {
            teamList[teamLen] = new TeamList();
        }
    }

    boolean isAllEmpty() {
        for (int teamLen = 1; teamLen <= msize; teamLen++) {
            if (!teamList[teamLen].isEmpty())
                return false;
        }
        return true;
    }

    boolean setUsed(Team t) {
        for (int rcm = 0; rcm < RCM; rcm++) {
            boolean diff = false;
            for (int len = 0; len < t.getLength(); len++) {
                if (t.getN(0) != t.getN(len))
                    diff = true;
            }
            if (diff)
                continue;
            // If same rcm group, run setUsed.
        }
        return false;
    }

    void findTeam() {
        for (int teamLen = 1; teamLen <= msize; teamLen++) {
            //
        }
    }

    @Override
    void solve() {
        boolean empty = false;
        while (!empty) {
            for (int teamLen = 1; teamLen <= msize; teamLen++) {
                if (!teamList[teamLen].isEmpty())
                    setUsed(teamList[teamLen].getFirst());
            }
        }
    }
}