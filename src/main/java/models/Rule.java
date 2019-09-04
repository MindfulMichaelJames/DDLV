package models;

public abstract class Rule {
    private Head head;
    private Body body;

    public Rule(String ruleString, String implication) throws DDLVSyntaxException {
        if (ruleString.contains(implication)) {
            head = new Head(ruleString.substring(0, ruleString.indexOf(implication)).trim());
            String bodyString = ruleString.substring(ruleString.indexOf(implication)+2).trim().replace(".", "");
            body = new Body(bodyString);
        }
        else {
            throw new DDLVSyntaxException(ruleString);
        }
    }

    public Head getHead() {
        return head;
    }

    public Body getBody() {
        return body;
    }

    public String toString(String implication) {
        return head.toString() + " " + implication + " " + body.toString() + ".";
    }

    public abstract boolean isDefeasible();

}
