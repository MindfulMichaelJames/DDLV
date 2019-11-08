package models.rules;

import models.DDLVSyntaxException;
import models.Rule;
import models.Rules;
import models.rule.DefeasibleRule;
import models.rule.StrictRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefeasibleRules implements Rules {
    private List<Rule> rules;

    public DefeasibleRules() {
        rules = new ArrayList<Rule>();
    }

    public DefeasibleRules(DefeasibleRules inputRules) {
        rules = new ArrayList<Rule>(inputRules.getRules());
    }

    public DefeasibleRules(List<Rule> inputRules) {
        rules = inputRules;
    }

    public void add(String defeasibleRuleString) throws DDLVSyntaxException {
        Rule defeasibleRule = new DefeasibleRule(defeasibleRuleString);
        rules.add(defeasibleRule);
    }

    public List<Rule> getRules() {
        return rules;
    }

    public String toProgramString() {
        StringBuilder programStringBuilder = new StringBuilder();
        for (Rule rule : rules) {
            programStringBuilder.append(rule.toString(StrictRule.IMPLICATION));
        }
        return programStringBuilder.toString();
    }

    public String toRepresentationString() {
        StringBuilder programStringBuilder = new StringBuilder();
        for (Rule rule : rules) {
            programStringBuilder.append(rule.toString(DefeasibleRule.IMPLICATION));
        }
        return programStringBuilder.toString();
    }

    public Map<Integer, String> toRuleMap() {
        Map<Integer, String> ruleMap = new HashMap<>();
        int index = 0;
        for (Rule rule : rules) {
            ruleMap.put(index, rule.toString(DefeasibleRule.IMPLICATION));
            index ++;
        }
        return ruleMap;
    }

    public void replace(String oldRuleString, String newRuleString) throws DDLVSyntaxException {
        Rule oldRule = new DefeasibleRule(oldRuleString);
        Rule newRule = new DefeasibleRule(newRuleString);
        int ruleIndex = rules.indexOf(oldRule);
        rules.set(ruleIndex, newRule);
    }

    public void remove(String ruleString) throws DDLVSyntaxException {
        int ruleIndex = rules.indexOf(new DefeasibleRule(ruleString));
        if (ruleIndex >= 0) {
            rules.remove(ruleIndex);
        }
    }

    @Override
    public int hashCode() {
        return this.toRepresentationString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() == DefeasibleRules.class){
            DefeasibleRules defeasibleObj = (DefeasibleRules) obj;
            return (this.toRepresentationString().equals(defeasibleObj.toRepresentationString()));
        }
        else {
            return false;
        }
    }
}
