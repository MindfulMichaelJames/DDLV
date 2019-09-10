package models.rules;

import it.unical.mat.wrapper.DLVInputProgram;
import it.unical.mat.wrapper.DLVInvocation;
import it.unical.mat.wrapper.DLVInvocationException;
import it.unical.mat.wrapper.DLVWrapper;
import models.DDLVProgram;
import models.DDLVSyntaxException;
import models.Rule;
import models.Rules;
import models.rule.DefeasibleRule;
import models.rule.StrictRule;

import java.io.IOException;
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

    public void add(String defeasibleRuleString) {
        try {
            Rule defeasibleRule = new DefeasibleRule(defeasibleRuleString);
            rules.add(defeasibleRule);
        }
        catch (DDLVSyntaxException e) {
            System.out.println(e.getSyntaxString());
        }
    }

    public void add(Rule defeasibleRule) {
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
//        System.out.println(oldRule.toString(":-"));
        int ruleIndex = rules.indexOf(oldRule);
        rules.set(ruleIndex, newRule);
    }

    public void remove(String ruleString) throws DDLVSyntaxException {
//        System.out.println("Called on " + ruleString);
        int ruleIndex = rules.indexOf(new DefeasibleRule(ruleString));
        if (ruleIndex >= 0) {
            rules.remove(ruleIndex);
        }
    }

    public DefeasibleRules returnThis() {
        return this;
    }

    public DefeasibleRules exceptional(DLVInputProgram inputProgram, StrictRules infiniteRank, DLVInvocation dlvInvocation) throws DLVInvocationException, IOException, DDLVSyntaxException {
        DefeasibleRules exceptionalDefeasibleSet = new DefeasibleRules();
        for (Rule defeasibleRule : this.getRules()) {
            // If defeasibleSet and infiniteRank and instantiation of body give no model, then exceptional
            inputProgram.clean();
            inputProgram.addText(this.toProgramString());
            inputProgram.addText(infiniteRank.toProgramString());
            inputProgram.addText(defeasibleRule.getBody().instantiate());
            dlvInvocation = DLVWrapper.getInstance().createInvocation(DDLVProgram.DLV_PATH);
            dlvInvocation.setInputProgram(inputProgram);
            dlvInvocation.run();
            dlvInvocation.waitUntilExecutionFinishes();
            if (!dlvInvocation.haveModel()) {
                exceptionalDefeasibleSet.add(defeasibleRule);
                this.remove(defeasibleRule.toString(":~"));
            }
        }
        return exceptionalDefeasibleSet;
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
