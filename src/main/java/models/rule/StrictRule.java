package models.rule;

import models.DDLVSyntaxException;
import models.Rule;

public class StrictRule extends Rule {

    public static final String IMPLICATION = ":-";

    public StrictRule(String ruleString) throws DDLVSyntaxException {
        super(ruleString, IMPLICATION);
    }

    public String toString() {
        return super.toString(IMPLICATION);
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
