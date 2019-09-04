package models.rule;

import models.DDLVSyntaxException;
import models.Rule;

public class StrictRule extends Rule {
//    private Head head;
//    private Body body;
    public static final String IMPLICATION = ":-";

    public StrictRule(String ruleString) throws DDLVSyntaxException {
        super(ruleString, IMPLICATION);
//        if (ruleString.contains(STRICT_IMPLICATION)) {
//            head = new Head(ruleString.substring(0, ruleString.indexOf(STRICT_IMPLICATION)).trim());
//            body = new Body(ruleString.substring(ruleString.indexOf(STRICT_IMPLICATION)+1,
//                    ruleString.indexOf(".")).trim());
//        }
//        else {
//            throw new DDLVSyntaxException(ruleString);
//        }
    }

    public String toString() {
        return super.toString(IMPLICATION);
//        return head.toString() + " " + STRICT_IMPLICATION + " " + body.toString() + ".";
    }

    @Override
    public boolean isDefeasible() {
        return false;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() == StrictRule.class){
            StrictRule strictObj = (StrictRule) obj;
            return (this.toString().equals(strictObj.toString()));
        }
        else {
            return false;
        }
    }
}
