package controllers;

import enums.InterfaceFunctions;
import it.unical.mat.wrapper.DLVInvocationException;
import models.DDLVProgram;
import models.DDLVSyntaxException;
import models.RankedModel;
import views.LanternaView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class LanternaController {
    private String filename;
    private LanternaView view;
    private RankedModel model = null;
    private DDLVProgram program;

    public LanternaController(LanternaView view) throws IOException, DDLVSyntaxException, DLVInvocationException {
        this.view = view;
        mainScreen();
    }

    public void executeCommand(InterfaceFunctions command) throws DLVInvocationException, DDLVSyntaxException, IOException {
        switch (command) {
            case MAIN:
                mainScreen();
                break;
            case LOAD:
                loadProgram();
                break;
            case CREATE:
                createProgram();
                break;
            case VIEW:
                viewProgram();
                break;
            case EDIT:
                editProgram();
                break;
            case QUERY:
                query();
                break;
            case NULL:
                break;
        }
    }

    public void mainScreen() throws IOException, DDLVSyntaxException, DLVInvocationException {
        Map<Integer, String> menuDict = new HashMap<Integer, String>();
        Map<Integer, InterfaceFunctions> returnDict = new HashMap<Integer, InterfaceFunctions>();

        menuDict.put(0, "Load DDLV program");
        menuDict.put(1, "New DDLV program");
        returnDict.put(0, InterfaceFunctions.LOAD);
        returnDict.put(1, InterfaceFunctions.CREATE);
        int selected = view.itemSelect(menuDict);
        while (selected != -1) {
            switch (selected) {
                case 0:
                    loadProgram();
                    break;
                case 1:
                    createProgram();
                    break;
            }
            selected = view.itemSelect(menuDict);
        }
        view.exit();
        System.exit(0);
    }

    public void loadProgram() throws IOException, DDLVSyntaxException, DLVInvocationException {
        filename = view.showLoadScreen();
        while (!filename.equals("EXIT")) {
            try {
                program = new DDLVProgram(new String(Files.readAllBytes(Paths.get("testfiles", filename))));
                model = new RankedModel(program);
                programOptions();
            }
            catch (IOException e) {
                filename = view.showLoadErrorScreen();
            }
        }
    }

    public void createProgram() throws IOException, DLVInvocationException, DDLVSyntaxException {
        filename = view.showNewProgramScreen();
        if (!filename.equals("EXIT")) {
            program = new DDLVProgram(new String(Files.readAllBytes(Paths.get("testfiles", filename))));
            programOptions();
        }
    }

    public void programOptions() throws IOException, DLVInvocationException, DDLVSyntaxException {
        Map<Integer, String> optionsMap = new HashMap<>();
        optionsMap.put(0, "View program");
        optionsMap.put(1, "Edit Program");
        optionsMap.put(2, "Query Program");
        int selected = view.itemSelect(optionsMap);
        while (selected != -1) {
            switch (selected) {
                case 0:
                    viewProgram();
                    break;
                case 1:
                    editOptions();
                    break;
                case 2:
                    query();
                    break;
            }
            selected = view.itemSelect(optionsMap);
        }
        view.exit();
        System.exit(0);
    }

    public void editOptions() throws IOException, DLVInvocationException, DDLVSyntaxException {
        Map<Integer, String> optionsMap = new HashMap<>();
        optionsMap.put(0, "Add rule");
        optionsMap.put(1, "Edit rule");
        optionsMap.put(2, "Remove rule");
        int selected = view.itemSelect(optionsMap);
        while (selected != -1) {
            switch (selected) {
                case 0:
                    addRule();
                    break;
                case 1:
                    editProgram();
                    break;
                case 2:
                    viewRemoveProgram();
                    break;
            }
            selected = view.itemSelect(optionsMap);
        }
    }

    public void addRule() throws IOException, DDLVSyntaxException, DLVInvocationException {
        String newRule = view.addRuleScreen();
        while (!newRule.equals("EXIT")){
            try {
                program.addRule(newRule);
                model = new RankedModel(program);
                newRule = view.addRuleScreen();
            }
            catch (DDLVSyntaxException e) {
                newRule = view.addRuleErrorScreen();
            }
        }
    }

    public void viewRemoveProgram() throws IOException, DLVInvocationException, DDLVSyntaxException {
        int selected = view.itemSelect(model.getRankingStrings());
        while (selected != -1 && model.getRuleStrings(selected)!=null) {
            viewRemoveRanking(selected);
            model = new RankedModel(program);
            selected = view.itemSelect(model.getRankingStrings());
        }
    }

    public void viewRemoveRanking(int ranking) throws IOException, DLVInvocationException, DDLVSyntaxException {
        Map<Integer, String> ruleStrings = model.getRuleStrings(ranking);
        int selected;
        if (ruleStrings.size() == 0) {
            return;
        }
        else {
            selected = view.itemSelect(ruleStrings);
        }
        while (selected != -1) {
            removeRule(ruleStrings.get(selected));
            ruleStrings.remove(selected);
//            ruleStrings = model.getRuleStrings(ranking);
            if (ruleStrings.size() == 0) {
                break;
            }
            else {
                selected = view.itemSelect(ruleStrings);
            }
        }
    }

    public void removeRule(String rule) throws DDLVSyntaxException, DLVInvocationException, IOException {
        program.removeRule(rule);
    }

    public void viewProgram() throws IOException, DLVInvocationException, DDLVSyntaxException {
        model.printOutDefeasibleRankings();
        int selected = view.itemSelect(model.getRankingStrings());
        while (selected != -1) {
            viewRanking(selected);
            selected = view.itemSelect(model.getRankingStrings());
        }
    }

    public void viewRanking(int ranking) throws IOException, DLVInvocationException, DDLVSyntaxException {
        Map<Integer, String> ruleStrings = model.getRuleStrings(ranking);
//        System.out.println(ruleStrings);
        int selected = view.itemSelect(ruleStrings);
        while (selected != -1) {
            viewRule(ruleStrings.get(selected));
            selected = view.itemSelect(ruleStrings);
        }
    }

    public void viewRule(String rule) throws IOException {
        int returned = view.ruleView(rule);
    }

    public void editProgram() throws IOException, DLVInvocationException, DDLVSyntaxException {
        int selected = view.itemSelect(model.getRankingStrings());
        while (selected != -1) {
            editRanking(selected);
            selected = view.itemSelect(model.getRankingStrings());
        }
    }

    public void editRanking(int ranking) throws IOException, DLVInvocationException, DDLVSyntaxException {
        Map<Integer, String> ruleStrings = model.getRuleStrings(ranking);
        int selected = view.itemSelect(ruleStrings);
        while (selected != -1) {
            editRule(ruleStrings.get(selected));
            ruleStrings = model.getRuleStrings(ranking);
            selected = view.itemSelect(ruleStrings);
        }
        editProgram();
    }

    public void editRule(String rule) throws IOException, DDLVSyntaxException, DLVInvocationException {
        String editedRule = view.ruleEditView(rule);
        if (!editedRule.equals("EXIT")) {
            program.replaceRule(rule, editedRule);
            model = new RankedModel(program);
        }
    }

    public void query() throws IOException, DLVInvocationException, DDLVSyntaxException {
        //handle strict and defeasible
        String queryString = view.queryScreen();
        while (!queryString.equals("EXIT")){
            view.showResultScreen(model.query(queryString));
            queryString = view.queryScreen();
        }
    }

    public void makeDefeasible(String queryString){

    }

    public void makeStrict(String queryString){

    }


//    public void run() {
//        new CliView().accept(textIO, null);
//    }
}
