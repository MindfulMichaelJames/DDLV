package models;

import it.unical.mat.wrapper.*;
import models.rule.DefeasibleRule;
import models.rule.StrictRule;
import models.rules.DefeasibleRules;
import models.rules.StrictRules;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class RankedModel {
    private StrictRules infiniteRank;
    private Map<Integer, DefeasibleRules> defeasibleRanks = new HashMap<Integer, DefeasibleRules>();
    private DLVInputProgram inputProgram;

    public RankedModel(DDLVProgram program) throws DLVInvocationException, IOException, DDLVSyntaxException {
        inputProgram = new DLVInputProgramImpl();
        infiniteRank = program.getStrictRules();
        defeasibleRanks.put(0, new DefeasibleRules(program.getDefeasibleRules()));
        computeRanking();
    }

    private CompletableFuture<Rule> isExceptional(Rule currentRule, String currentProgram) {
        return CompletableFuture.supplyAsync(() -> {
            DLVInputProgram currentInputProgram = new DLVInputProgramImpl();
            DLVInvocation currentDlvInvocation = DLVWrapper.getInstance().createInvocation(DDLVProgram.DLV_PATH);
            currentInputProgram.addText(currentProgram);
            currentInputProgram.addText(currentRule.getBody().instantiate());

            try {
                currentDlvInvocation.setInputProgram(currentInputProgram);
                currentDlvInvocation.setNumberOfModels(1);
                currentDlvInvocation.run();
                currentDlvInvocation.waitUntilExecutionFinishes();
            } catch (DLVInvocationException | IOException e) {
                e.printStackTrace();
            }


            if (!currentDlvInvocation.haveModel()) {
                return currentRule;
            }
            else {
                return null;
            }
        });
    }

    private DefeasibleRules getExceptionalRank(int rank) throws ExecutionException, InterruptedException {
        String currentRankAsString = defeasibleRanks.get(rank).toProgramString() + infiniteRank.toProgramString();

        // Get exceptional rules asynchronously
        List<CompletableFuture<Rule>> exceptionalRankFutures = defeasibleRanks.get(rank).getRules().stream()
                .map((Rule currentRule) -> isExceptional(currentRule, currentRankAsString))
                .collect(Collectors.toList());


        // Create a combined Future using allOf()
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                exceptionalRankFutures.toArray(new CompletableFuture[exceptionalRankFutures.size()])
        );


        // When all the Futures are completed, call `future.join()` to get their results and collect the results in a list -
        CompletableFuture<List<Rule>> allExceptionalRankFuture = allFutures.thenApply(v -> exceptionalRankFutures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        return new DefeasibleRules(allExceptionalRankFuture.get());

    }

    private void computeRanking() throws DLVInvocationException, IOException, DDLVSyntaxException {
        int rankCounter = 1;
        try {
            defeasibleRanks.put(rankCounter, getExceptionalRank(rankCounter-1));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        if (!defeasibleRanks.get(rankCounter - 1).equals(defeasibleRanks.get(rankCounter))) {
            setRemove(defeasibleRanks.get(rankCounter-1), defeasibleRanks.get(rankCounter));

            while ((!defeasibleRanks.get(rankCounter - 1).equals(defeasibleRanks.get(rankCounter)))
                    && defeasibleRanks.get(rankCounter).getRules().size() > 0) {
                rankCounter++;
                try {
                    defeasibleRanks.put(rankCounter, getExceptionalRank(rankCounter - 1));
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                if (!defeasibleRanks.get(rankCounter - 1).equals(defeasibleRanks.get(rankCounter))) {
                    setRemove(defeasibleRanks.get(rankCounter - 1), defeasibleRanks.get(rankCounter));
                }
            }
        }
        if (defeasibleRanks.get(rankCounter).getRules().size() == 0) {
            defeasibleRanks.remove(rankCounter);
        }
        else {
            defeasibleRanks.remove(rankCounter);
            for (Rule hiddenStrictRule : defeasibleRanks.get(rankCounter-1).getRules()) {
                infiniteRank.add(hiddenStrictRule.toString(":-"));
            }
            defeasibleRanks.remove(rankCounter-1);
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
        if (ranking == defeasibleRanks.size()) {
            return infiniteRank.toRuleMap();
        }
        else {
            try {
                return defeasibleRanks.get(ranking).toRuleMap();
            }
            catch (NullPointerException e) {
                return null;
            }
        }
    }

    private boolean rationalClosure(DefeasibleRule queryRule) throws DLVInvocationException, IOException {
        int currentRank = 0;
        int ranks = defeasibleRanks.size();
        DLVInvocation dlvInvocation;
        while (currentRank < ranks) {
            inputProgram.clean();
            inputProgram.addText(getRankConjunctionOf(currentRank, ranks-1));
            inputProgram.addText(infiniteRank.toProgramString());
            inputProgram.addText(queryRule.getBody().instantiate());
            dlvInvocation = DLVWrapper.getInstance().createInvocation(DDLVProgram.DLV_PATH);
            dlvInvocation.setInputProgram(inputProgram);
            dlvInvocation.run();
            dlvInvocation.waitUntilExecutionFinishes();
            if (dlvInvocation.haveModel()) {
                break;
            }
            currentRank ++;
        }
        inputProgram.addText(queryRule.getHead().toQueryString());
        dlvInvocation = DLVWrapper.getInstance().createInvocation(DDLVProgram.DLV_PATH);
        dlvInvocation.setInputProgram(inputProgram);
        dlvInvocation.run();
        dlvInvocation.waitUntilExecutionFinishes();
        return dlvInvocation.haveModel();
    }

    private boolean strictQuery(StrictRule strictRule) throws DLVInvocationException, IOException {
        inputProgram.clean();
        inputProgram.addText(infiniteRank.toProgramString());
        inputProgram.addText(strictRule.getBody().instantiate());
        DLVInvocation dlvInvocation = DLVWrapper.getInstance().createInvocation(DDLVProgram.DLV_PATH);
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
            Instant start = Instant.now();
            boolean result = rationalClosure(defeasibleQueryRule);
            Instant finish = Instant.now();
            long rankingTime = Duration.between(start, finish).toMillis();
            System.out.println(rankingTime);
            return result;
        }
        else {
            throw new DDLVSyntaxException(queryRule);
        }
    }
}
