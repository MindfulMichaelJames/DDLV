package models;

import java.util.ArrayList;
import java.util.List;

public class Rank {
    private List<Rule> rules;

    private boolean defeasible;

    private int ranking;

    public Rank(List<Rule> rules, boolean defeasible) {
        this.rules = rules;
        this.defeasible = defeasible;
    }

    public Rank(List<Rule> rules, boolean defeasible, int ranking) {
        this.rules = rules;
        this.defeasible = defeasible;
        this.ranking = ranking;
    }

    public boolean isDefeasible() {
        return defeasible;
    }

    public int getRanking() {
        return ranking;
    }

    public List<Rule> getRules() {
        return rules;
    }
}
