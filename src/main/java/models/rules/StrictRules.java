package models.rules;

import models.DDLVSyntaxException;
import models.Rule;
import models.Rules;
import models.rule.StrictRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StrictRules implements Rules {
    private List<Rule> rules;

    public StrictRules() {
        rules = new ArrayList<Rule>();
    }

    public void add(String strictRuleString) {
        try {
            Rule strictRule = new StrictRule(strictRuleString);
            rules.add(strictRule);
        }
        catch (DDLVSyntaxException e) {
            System.out.println(e.getSyntaxString());
        }
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

    public Map<Integer, String> toRuleMap() {
        Map<Integer, String> ruleMap = new HashMap<>();
        int index = 0;
        for (Rule rule : rules) {
            ruleMap.put(index, rule.toString(StrictRule.IMPLICATION));
            index ++;
        }
        return ruleMap;
    }

    public void replace(String oldRule, String newRule) throws DDLVSyntaxException {
        int ruleIndex = rules.indexOf(new StrictRule(oldRule));
        rules.set(ruleIndex, new StrictRule(newRule));
    }

    public void remove(String ruleString) throws DDLVSyntaxException {
        int ruleIndex = rules.indexOf(new StrictRule(ruleString));
        if (ruleIndex >= 0) {
            rules.remove(ruleIndex);
        }
    }
}
