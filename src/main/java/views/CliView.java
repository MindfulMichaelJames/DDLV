package views;

import asg.cliche.ShellFactory;
import asg.cliche.Command;
import java.io.IOException;
import java.util.Scanner;

import controllers.CliController;
import models.RankedModel;

public class CliView {
    private RankedModel model;
    private Scanner userInput;

    public CliView(RankedModel model) {
        this.model = model;
        userInput = new Scanner(System.in);
    }

    public String askForCommand() {
        return userInput.nextLine();
    }

}



//interface Model {
//    public void setName(String name);
//}
//
//interface View {
//    public String prompt(String prompt);
//}
//
//class Controller {
//
//    private final Model model;
//    private final View view;
//
//    public Controller(Model model, View view) {
//        this.model = model;
//        this.view = view;
//    }
//
//    public void run() {
//        String name;
//
//        while ((name = view.prompt("\nmvc demo> ")) != null) {
//            model.setName(name);
//        }
//    }
//}



//class Person extends Observable implements Model {
//
//    private String name;
//
//    public Person() {
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String newName) {
//        this.name = newName;
//        setChanged();
//        notifyObservers(newName);
//    }
//}
//
//class TUI implements Observer, View { // textual UI
//
//    private final BufferedReader br;
//
//    public TUI(Reader reader) {
//        this.br = new BufferedReader(reader);
//    }
//
//    public void update(Observable o, Object arg) {
//        System.out.println("\n => person updated to " + arg);
//    }
//
//    public String prompt(String prompt) {
//        try {
//            System.out.print(prompt);
//            return br.readLine();
//        } catch (IOException ex) {
//            throw new RuntimeException(ex);
//        }
//    }
//}


//    TUI view = new TUI(new StringReader("David\nDamian\nBob\n"));
//    Person model = new Person();
//model.addObserver(view);
//        Controller controller = new Controller(model, view);
//        controller.run();