package models;

public abstract class Implication {

    public abstract boolean equals(String query);

    public abstract String render();

    public abstract boolean isDefeasible();
}
