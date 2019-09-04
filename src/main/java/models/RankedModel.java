package models;

import it.unical.mat.wrapper.*;
import models.rule.DefeasibleRule;
import models.rule.StrictRule;
import models.rules.DefeasibleRules;
import models.rules.StrictRules;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RankedModel {
    private StrictRules infiniteRank;
    private Map<Integer, DefeasibleRules> defeasibleRanks = new HashMap<Integer, DefeasibleRules>();
    private DLVInputProgram inputProgram;
    private DLVInvocation dlvInvocation;

    public RankedModel(DDLVProgram program) throws DLVInvocationException, IOException, DDLVSyntaxException {
        inputProgram = new DLVInputProgramImpl();
        infiniteRank = program.getStrictRules();
        defeasibleRanks.put(0, program.getDefeasibleRules());
        computeRanking();
//        printOutDefeasibleRankings();
    }

    private DefeasibleRules exceptional(int rank) throws DLVInvocationException, IOException {
        DefeasibleRules exceptionalDefeasibleSet = new DefeasibleRules();
        System.out.println(rank);
        for (Rule defeasibleRule : defeasibleRanks.get(rank).getRules()) {
            System.out.println(defeasibleRule.toString(":~"));
//            System.out.println(defeasibleRule.toString(":~"));
            // If defeasibleSet and infiniteRank and instantiation of body give no model, then exceptional
            inputProgram.clean();
            System.out.println("Conjunction: " + getRankConjunctionOf(0, rank));
            inputProgram.addText(defeasibleRanks.get(rank).toProgramString());
//            inputProgram.addText(getRankConjunctionOf(0, rank));
            inputProgram.addText(infiniteRank.toProgramString());
            inputProgram.addText(defeasibleRule.getBody().instantiate());
            dlvInvocation = DLVWrapper.getInstance().createInvocation(DDLVProgram.DLV_PATH);
            dlvInvocation.setInputProgram(inputProgram);
            dlvInvocation.run();
            dlvInvocation.waitUntilExecutionFinishes();
            if (!dlvInvocation.haveModel()) {
                exceptionalDefeasibleSet.add(defeasibleRule);
            }
        }
        return exceptionalDefeasibleSet;
    }

    private void computeRanking() throws DLVInvocationException, IOException, DDLVSyntaxException {
//        DefeasibleRules previous = defeasibleRanks.get(0);
        int rankCounter = 1;
        defeasibleRanks.put(rankCounter, exceptional(rankCounter-1));
        defeasibleRanks.put(rankCounter - 1,
                setRemove(defeasibleRanks.get(rankCounter-1), defeasibleRanks.get(rankCounter)));
        while ((!defeasibleRanks.get(rankCounter-1).toProgramString().equals(defeasibleRanks.get(rankCounter).toProgramString()))
                && defeasibleRanks.get(rankCounter).getRules().size() > 0) {
//        while ((!previous.toProgramString().equals(current.toProgramString())) && current.getRules().size() > 0) {
            rankCounter ++;
            defeasibleRanks.put(rankCounter, exceptional(rankCounter-1));
            defeasibleRanks.put(rankCounter - 1,
                    setRemove(defeasibleRanks.get(rankCounter-1), defeasibleRanks.get(rankCounter)));


//            defeasibleRanks.put(rankCounter, current);
//            previous = current;
//            current = exceptional(previous);

        }
        if (defeasibleRanks.get(rankCounter).getRules().size() == 0) {
            defeasibleRanks.remove(rankCounter);
        }
    }

    private String getRankConjunctionOf(int bottom, int top) {
        switch (top-bottom) {
            case 0:
                return defeasibleRanks.get(top).toProgramString();
            case 1:
                return defeasibleRanks.get(top).toProgramString() + defeasibleRanks.get(bottom).toProgramString();
            default:
                int mid = (top + bottom) / 2;
                return getRankConjunctionOf(bottom, mid) + getRankConjunctionOf(mid+1, top);
        }
    }

    private DefeasibleRules setRemove(DefeasibleRules lower, DefeasibleRules higher) throws DDLVSyntaxException {
        for (Rule rule : higher.getRules()){
            lower.remove(rule.toString(":~"));
        }
        return lower;
    }

    public Map<Integer, String> getRankingStrings() {
        Map<Integer, String> rankingStrings =  new HashMap<>();
        for (int rankNumber = 0; rankNumber < defeasibleRanks.size(); rankNumber ++) {
            rankingStrings.put(rankNumber, String.format("Defeasible Rules - Rank %d", rankNumber));
        }
        if (infiniteRank.getRules().size() > 0) {
            rankingStrings.put(rankingStrings.size(), "Strict Rules");
        }
        return rankingStrings;
    }

    public Map<Integer, String> getRuleStrings(int ranking) {
        Map<Integer, String> ruleStrings = new HashMap<>();
        if (ranking == defeasibleRanks.size()) {
            return infiniteRank.toRuleMap();
        }
        else {
            return defeasibleRanks.get(ranking).toRuleMap();
        }
    }

    public StrictRules getInfiniteRank() {
        return infiniteRank;
    }

    public DefeasibleRules getDefeasibleRank(int rank) {
        return defeasibleRanks.get(rank);
    }

//    public void replaceRule(int rank, String oldRule, String newRule) throws DDLVSyntaxException {
//        if (rank == ) {
//            infiniteRank.replace(oldRule, newRule);
//        }
//        else {
//            DefeasibleRules editedRules = defeasibleRanks.get(rank);
//            editedRules.replace(oldRule, newRule);
//            defeasibleRanks.replace(rank, editedRules);
//        }
//    }
//
//    public void removeRule(int rank, )

    private boolean rationalClosure(DefeasibleRule queryRule) throws DLVInvocationException, IOException {
        int currentRank = 0;
        int ranks = defeasibleRanks.size();
        while (currentRank < ranks) {
            inputProgram.clean();
            inputProgram.addText(defeasibleRanks.get(currentRank).toProgramString());
            inputProgram.addText(infiniteRank.toProgramString());
            inputProgram.addText(queryRule.getBody().instantiate());
            dlvInvocation.setInputProgram(inputProgram);
            dlvInvocation.run();
            dlvInvocation.waitUntilExecutionFinishes();
            if (dlvInvocation.haveModel()) {
                break;
            }
            currentRank ++;
        }
        inputProgram.addText(queryRule.getHead().toQueryString());
        dlvInvocation.setInputProgram(inputProgram);
        dlvInvocation.run();
        dlvInvocation.waitUntilExecutionFinishes();
        return dlvInvocation.haveModel();
    }

    private boolean strictQuery(StrictRule strictRule) throws DLVInvocationException, IOException {
        inputProgram.clean();
        inputProgram.addText(infiniteRank.toProgramString());
        for (DefeasibleRules defeasibleRank : defeasibleRanks.values()) {
            inputProgram.addText(defeasibleRank.toProgramString());
        }
        inputProgram.addText(strictRule.getBody().instantiate());
        dlvInvocation.setInputProgram(inputProgram);
        dlvInvocation.run();
        dlvInvocation.waitUntilExecutionFinishes();
        return dlvInvocation.haveModel();
    }

    public boolean query(String queryRule) throws DDLVSyntaxException, DLVInvocationException, IOException {
        if (queryRule.contains(DDLVProgram.STRICT_IMPLICATION)) {
            StrictRule strictQueryRule = new StrictRule(queryRule);
            return strictQuery(strictQueryRule);
        }
        else if (queryRule.contains(DDLVProgram.DEFEASIBLE_IMPLICATION)) {
            DefeasibleRule defeasibleQueryRule = new DefeasibleRule(queryRule);
            return rationalClosure(defeasibleQueryRule);
        }
        else {
            throw new DDLVSyntaxException(queryRule);
        }
    }


    public void printOutDefeasibleRankings() {
        for (int ruleRank : defeasibleRanks.keySet()) {
            System.out.println(ruleRank);
            System.out.println(defeasibleRanks.get(ruleRank).toProgramString());
        }
    }
}
