package models;

import models.rule.DefeasibleRule;
import models.rule.StrictRule;
import models.rules.DefeasibleRules;
import models.rules.StrictRules;

import java.util.regex.Matcher;

public class DDLVProgram {
    private StrictRules strictRules;
    private DefeasibleRules defeasibleRules;
    static final String STRICT_IMPLICATION = ":-";
    static final String DEFEASIBLE_IMPLICATION = ":~";
    public static final String DLV_PATH = "/Users/Michael/Desktop/dlv/dlv";
    private RankedModel rankedModel;
    private boolean unchanged = false;

    public DDLVProgram() {
        strictRules = new StrictRules();
        defeasibleRules = new DefeasibleRules();
    }

    public DDLVProgram(String inputProgram) throws DDLVSyntaxException{
        strictRules = new StrictRules();
        defeasibleRules = new DefeasibleRules();
        String[] inputRules = inputProgram.split("\\s*\\.\\s*");
        for (String inputRule: inputRules) {
            addRule(inputRule);
        }
    }

    public void addRule(String inputRule) throws DDLVSyntaxException {
        if (inputRule.contains(STRICT_IMPLICATION)) {
            strictRules.add(inputRule);
        }
        else if (inputRule.contains(DEFEASIBLE_IMPLICATION)) {
            defeasibleRules.add(inputRule);
        }
        else{
            throw new DDLVSyntaxException(inputRule);
        }
        unchanged = false;
    }

    public void removeRule(String rule) throws DDLVSyntaxException {
        if (rule.contains(STRICT_IMPLICATION)) {
            strictRules.remove(rule);
        }
        else if (rule.contains(DEFEASIBLE_IMPLICATION)) {
            defeasibleRules.remove(rule);
        }
    }

    public void replaceRule(String oldRule, String newRule) throws DDLVSyntaxException  {
        if (oldRule.contains(STRICT_IMPLICATION)) {
            if (newRule.contains(DEFEASIBLE_IMPLICATION)) {
                defeasibleRules.add(newRule);
                strictRules.remove(oldRule);
            }
            else if (newRule.contains(STRICT_IMPLICATION)) {
                strictRules.replace(oldRule, newRule);
            }
            else {
                throw new DDLVSyntaxException(newRule);
            }
        }
        else if (oldRule.contains(DEFEASIBLE_IMPLICATION)) {
            if (newRule.contains(DEFEASIBLE_IMPLICATION)) {
                defeasibleRules.replace(oldRule, newRule);
            }
            else if (newRule.contains(STRICT_IMPLICATION)) {
                strictRules.add(newRule);
                defeasibleRules.remove(oldRule);
            }
            else {
                throw new DDLVSyntaxException(newRule);
            }
        }
        else {
            throw new DDLVSyntaxException(oldRule);
        }
        unchanged = false;
    }

    public DefeasibleRules getDefeasibleRules() {
        return defeasibleRules;
    }

    public StrictRules getStrictRules() {
        return strictRules;
    }

    public void saveToFile() {

    }
}
