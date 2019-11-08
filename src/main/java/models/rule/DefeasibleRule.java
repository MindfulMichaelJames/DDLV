package models.rule;

import models.*;

public class DefeasibleRule extends Rule {
    public static final String IMPLICATION = ":~";

    public DefeasibleRule(String ruleString) throws DDLVSyntaxException {
        super(ruleString, IMPLICATION);
    }

    public String toString() {
        return super.toString(IMPLICATION);
    }

    public String getAsStrict() {
        return super.toString(":-");
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() == DefeasibleRule.class){
            DefeasibleRule defeasibleObj = (DefeasibleRule) obj;
            return (this.toString().equals(defeasibleObj.toString()));
        }
        else {
            return false;
        }
    }


}
