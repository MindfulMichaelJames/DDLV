import controllers.LanternaController;
import it.unical.mat.wrapper.*;
import models.DDLVSyntaxException;
import views.LanternaView;

import java.io.IOException;

public class Main {
    DLVInputProgram inputProgram=new DLVInputProgramImpl();

    public static void main(String[] args) throws IOException, DDLVSyntaxException, DLVInvocationException {
//        StrictRules sr = new StrictRules();
        LanternaView view = new LanternaView();
        LanternaController controller = new LanternaController(view);
//        DLVInputProgram inputProgram = new DLVInputProgramImpl();
//        inputProgram.addText("bird(X) :- penguin(X).\n" +
//                "flies(X) :- bird(X).\n" +
//                "-flies(X) :- penguin(X).");
//        inputProgram.addText("bird(x).");
//        inputProgram.addText("-flies(x)?");
//        DLVInvocation invocation = DLVWrapper.getInstance().createInvocation(DDLVProgram.DLV_PATH);
//        try {
//            invocation.setInputProgram(inputProgram);
//        } catch (DLVInvocationException e) {
//            e.printStackTrace();
//        }
//        try {
//            invocation.run();
//        } catch (DLVInvocationException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            invocation.waitUntilExecutionFinishes();
//        } catch (DLVInvocationException e) {
//            e.printStackTrace();
//        }
//        if (invocation.haveModel()) {
//            System.out.println("Yes");
//        }
//        else {
//            System.out.println("No");
//        }
    }
}
