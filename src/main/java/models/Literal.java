package models;

import java.util.Arrays;
import java.util.List;

public class Literal {
    private String predicate;
    private List<String> terms;
    private int arity;
    private boolean negated;

    public Literal(String literalString){
        literalString = literalString.trim();
        predicate = literalString.substring(0, literalString.indexOf("("));
        terms = Arrays.asList(literalString.substring(literalString.indexOf("(")+1, literalString.indexOf(")"))
                .split("\\s*,\\s*"));
        arity = terms.size();
        if (predicate.substring(0, 1).equals("-")) {
            negated = true;
            predicate = predicate.substring(1);
        }
        else {
            negated = false;
        }
    }

    public String getPredicate() {
        return predicate;
    }

    public List<String> getTerms() {
        return terms;
    }

    public int getArity() {
        return arity;
    }

    public boolean isNegated() {
        return negated;
    }

    public String getTermsString() {
        return terms.toString().substring(1).replace("]", "");
    }

    public String toString() {
        if (isNegated()){
            return "-" + predicate + "(" + getTermsString() + ")";
        }
        return predicate + "(" + getTermsString() + ")";
    }
}
