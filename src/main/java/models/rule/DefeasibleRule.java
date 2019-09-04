package models.rule;

import models.*;

public class DefeasibleRule extends Rule {
    public static final String IMPLICATION = ":~";

    public DefeasibleRule(String ruleString) throws DDLVSyntaxException {
        super(ruleString, IMPLICATION);

//        if (ruleString.contains(DEFEASIBLE_IMPLICATION)) {
//            head = new Head(ruleString.substring(0, ruleString.indexOf(DEFEASIBLE_IMPLICATION)).trim());
//            body = new Body(ruleString.substring(ruleString.indexOf(DEFEASIBLE_IMPLICATION)+1,
//                    ruleString.indexOf(".")).trim());
//        }
//        else {
//            throw new DDLVSyntaxException(ruleString);
//        }
    }

    public String toString() {
        return super.toString(IMPLICATION);
//        return head.toString() + " " + DEFEASIBLE_IMPLICATION + " " + body.toString() + ".";
    }

    @Override
    public boolean isDefeasible() {
        return true;
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
