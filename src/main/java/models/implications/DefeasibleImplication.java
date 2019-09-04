package models.implications;

import models.Implication;

public class DefeasibleImplication extends Implication {
    public static final String TEXT = ":~";

    public boolean equals(String query){
        return query.equals(TEXT);
    }

    public String render() {
        return TEXT;
    }

    public boolean isDefeasible(){
        return true;
    }
}
