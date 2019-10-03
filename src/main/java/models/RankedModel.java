package models;

import it.unical.mat.wrapper.*;
import models.rule.DefeasibleRule;
import models.rule.StrictRule;
import models.rules.DefeasibleRules;
import models.rules.StrictRules;

import java.io.IOException;
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
//    private ModelHandler modelHandler;
//    private DLVInvocation.DLVInvocationState dlvInvocationState;

    public RankedModel(DDLVProgram program) throws DLVInvocationException, IOException, DDLVSyntaxException {
        inputProgram = new DLVInputProgramImpl();
        infiniteRank = program.getStrictRules();
        defeasibleRanks.put(0, new DefeasibleRules(program.getDefeasibleRules()));
        computeRanking();
//        printOutDefeasibleRankings();
    }



//    private DefeasibleRules exceptional2(int rank) throws DLVInvocationException, IOException {
//        DefeasibleRules exceptionalDefeasibleSet = new DefeasibleRules();
//        ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
////        inputProgram.clean();
////        inputProgram.addText(defeasibleRanks.get(rank).toProgramString());
////        inputProgram.addText(infiniteRank.toProgramString());
//        String currentRankAsString = defeasibleRanks.get(rank).toProgramString() + infiniteRank.toProgramString();
//        for (Rule defeasibleRule : defeasibleRanks.get(rank).getRules()) {
//            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
//                try {
//                    TimeUnit.SECONDS.sleep(1);
//                } catch (InterruptedException e) {
//                    throw new IllegalStateException(e);
//                }
//                return "Result of the asynchronous computation";
//            });
//
//            String result = future.get();
//            System.out.println(result);
//            es.execute(new ExceptionalityRunnable(defeasibleRule, currentRankAsString));
//        }
//        System.out.println("here");
//        es.shutdown();
//        try {
//            es.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return exceptionalDefeasibleSet;
//    }



    private DefeasibleRules exceptional(int rank) throws DLVInvocationException, IOException {
        DefeasibleRules exceptionalDefeasibleSet = new DefeasibleRules();
        ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//        inputProgram.clean();
//        inputProgram.addText(defeasibleRanks.get(rank).toProgramString());
//        inputProgram.addText(infiniteRank.toProgramString());
        String currentRankAsString = defeasibleRanks.get(rank).toProgramString() + infiniteRank.toProgramString();
        for (Rule defeasibleRule : defeasibleRanks.get(rank).getRules()) {
            class ExceptionalityReasoner implements Runnable
            {
                private Rule currentDefeasibleRule;
                private DLVInputProgram currentInputProgram;

//                boolean finished = es.awaitTermination(1, TimeUnit.MINUTES);
                private ExceptionalityReasoner(Rule currentDefeasibleRule, String rankInputProgram) {
                    this.currentDefeasibleRule = currentDefeasibleRule;
                    currentInputProgram = new DLVInputProgramImpl();
                    currentInputProgram.addText(defeasibleRanks.get(rank).toProgramString());
                    currentInputProgram.addText(infiniteRank.toProgramString());
                    currentInputProgram.addText(defeasibleRule.getBody().instantiate());
                }

                public void run()
                {
                    DLVInvocation dlvInvocation = DLVWrapper.getInstance().createInvocation(DDLVProgram.DLV_PATH);
                    try {
                        dlvInvocation.setInputProgram(currentInputProgram);
                    } catch (DLVInvocationException e) {
                        e.printStackTrace();
                    }
                    ModelHandler modelHandler = (dlvInvocation2, modelResult) -> {
                        try {
                            synchronized (dlvInvocation2) {
                                dlvInvocation2.killDlv();
//                                System.out.println("here");
                                dlvInvocation2.notify();
                            }
                        } catch (DLVInvocationException e) {
                            e.printStackTrace();
                        }
//                        System.out.println("at least here");
                    };
                    DlvFinishedHandler finishedHandler = (dlvInvocation2, modelResult) -> {
                        synchronized (dlvInvocation2) {
//                                System.out.println("here");
                            dlvInvocation2.notify();
                        }
//                        System.out.println("at least here");
                    };
                    try {
                        dlvInvocation.subscribe(modelHandler);
                        dlvInvocation.subscribe(finishedHandler);
                    } catch (DLVInvocationException e) {
                        e.printStackTrace();
                    }
                    try {
                        dlvInvocation.run();
                    } catch (DLVInvocationException | IOException e) {
                        e.printStackTrace();
                    }

                    while (dlvInvocation.getState().name().equals("RUNNING") | dlvInvocation.getState().name().equals("READY") ) {
                        synchronized (dlvInvocation) {
                            try {
                                dlvInvocation.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
//                dlvInvocationState = dlvInvocation.getState();
//                System.out.println(dlvInvocationState);
                    }
//                    System.out.println("Done");
//            dlvInvocation.waitUntilExecutionFinishes();
                    if (!dlvInvocation.haveModel()) {
                        exceptionalDefeasibleSet.add(currentDefeasibleRule);
                    }
                }
        }
//            System.out.println(defeasibleRule.toString(":~"));
//            System.out.println(defeasibleRule.toString(":~"));
            // If defeasibleSet and infiniteRank and instantiation of body give no model, then exceptional
//            inputProgram.clean();
////            System.out.println("Conjunction: " + getRankConjunctionOf(0, rank));
//            inputProgram.addText(defeasibleRanks.get(rank).toProgramString());
////            inputProgram.addText(getRankConjunctionOf(0, rank));
//            inputProgram.addText(infiniteRank.toProgramString());
//            inputProgram.addText(defeasibleRule.getBody().instantiate());
//            dlvInvocation = DLVWrapper.getInstance().createInvocation(DDLVProgram.DLV_PATH);
//            dlvInvocation.setInputProgram(inputProgram);
//            modelHandler = (dlvInvocation2, modelResult) -> {
//                try {
//                    dlvInvocation.killDlv();
//                    synchronized (dlvInvocation) {
//                        System.out.println("here");
//                        dlvInvocation.notifyAll();
//                    }
//                } catch (DLVInvocationException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("at least here");
//            };
//            dlvInvocation.subscribe(modelHandler);
//            dlvInvocation.run();
////            synchronized (exceptionalDefeasibleSet) {
////                while (exceptionalDefeasibleSet.getRules().size() == exceptionalSetSize) {
////                    System.out.println("bluh");
////                    try {
////                        exceptionalDefeasibleSet.wait();
////                        System.out.println("bluh2");
////                    } catch (InterruptedException e) {
////                        e.printStackTrace();
////                    }
////                }
////            }
//            dlvInvocationState = dlvInvocation.getState();
////            System.out.println(DLVInvocation.DLVInvocationState.valueOf("KILLED"));
//            while (dlvInvocation.getState().name().equals("RUNNING")) {
//                synchronized (dlvInvocation) {
//                    try {
//                        dlvInvocation.wait();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
////                dlvInvocationState = dlvInvocation.getState();
////                System.out.println(dlvInvocationState);
//            }
//            System.out.println("Done");
////            dlvInvocation.waitUntilExecutionFinishes();
//            if (!dlvInvocation.haveModel()) {
//                exceptionalDefeasibleSet.add(defeasibleRule);
//            }
//            ExceptionalityReasoner exceptionalityReasoner = new ExceptionalityReasoner(defeasibleRule, inputProgram);
            es.execute(new ExceptionalityReasoner(defeasibleRule, currentRankAsString));
        }
        System.out.println("here");
        es.shutdown();
        try {
            es.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return exceptionalDefeasibleSet;
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
//        DefeasibleRules previous = defeasibleRanks.get(0);
        int rankCounter = 1;
        try {
            defeasibleRanks.put(rankCounter, getExceptionalRank(rankCounter-1));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        setRemove(defeasibleRanks.get(rankCounter-1), defeasibleRanks.get(rankCounter));
        while ((!defeasibleRanks.get(rankCounter-1).equals(defeasibleRanks.get(rankCounter)))
                && defeasibleRanks.get(rankCounter).getRules().size() > 0) {
//        while ((!previous.toProgramString().equals(current.toProgramString())) && current.getRules().size() > 0) {
            rankCounter ++;
            try {
                defeasibleRanks.put(rankCounter, getExceptionalRank(rankCounter-1));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            if (!defeasibleRanks.get(rankCounter-1).equals(defeasibleRanks.get(rankCounter))) {
                setRemove(defeasibleRanks.get(rankCounter-1), defeasibleRanks.get(rankCounter));
            }


//            defeasibleRanks.put(rankCounter, current);
//            previous = current;
//            current = exceptional(previous);

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
//            System.out.println(rule.toString("~"));
//            printOutDefeasibleRankings();
            lower.remove(rule.toString(":~"));
//            printOutDefeasibleRankings();
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
            try {
                return defeasibleRanks.get(ranking).toRuleMap();
            }
            catch (NullPointerException e) {
                return null;
            }
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
        DLVInvocation dlvInvocation;
        while (currentRank < ranks) {
            inputProgram.clean();
            inputProgram.addText(getRankConjunctionOf(currentRank, ranks-1));
//            inputProgram.addText(defeasibleRanks.get(currentRank).toProgramString());
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
