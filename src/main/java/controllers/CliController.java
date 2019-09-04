package controllers;

import models.RankedModel;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import views.CliView;
import asg.cliche.Command;

public class CliController {
    private final RankedModel model;
    private final CliView view;

    public CliController(RankedModel model, CliView view) {
        this.model = model;
        this.view = view;
    }

    public void executeCommand(){
        String command = view.askForCommand();

    }

    public void loadProgram(String filename) {

    }

    public void createProgram(String filename) {

    }

    public void editProgram(String filename) {

    }

    public boolean query(String queryString) {
        //handle strict and defeasible
        return false;
    }

    public void makeDefeasible(String queryString){

    }

    public void makeStrict(String queryString){

    }


//    public void run() {
//        new CliView().accept(textIO, null);
//    }

}
