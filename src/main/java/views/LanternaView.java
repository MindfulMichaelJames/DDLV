package views;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import models.DDLVSyntaxException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LanternaView {
    private Terminal terminal;
    private Screen screen;
    private static TextGraphics tg;
    private TextColor foreground = TextColor.ANSI.GREEN;
    private TextColor background = TextColor.ANSI.BLACK;

    public LanternaView() throws IOException, DDLVSyntaxException {
        terminal = new DefaultTerminalFactory().createTerminal();
        screen = new TerminalScreen(terminal);
        tg = screen.newTextGraphics();
        screen.startScreen();
    }

    public String showLoadScreen() throws IOException {
        return textPrompt("Enter name of file:");
    }

    public String showLoadErrorScreen(String filename) throws IOException {
        return textPrompt(String.format("The file \"%s\" does not exist. Please enter name of existing file:", filename));
    }

    public String showLoadErrorScreen(String filename, DDLVSyntaxException e) throws IOException {
        return textPrompt(String.format(
                "\"%s\" in file \"%s\" is not a valid DDLV rule. Please enter the name of a valid DDLV program file:",
                e.getSyntaxString(), filename));
    }

    public void showResultScreen(boolean success) throws IOException {
        String resultString = "";
        if (success) {
            resultString = "Query is entailed";
        }
        else {
            resultString = "Query is not entailed";
        }
        screen.clear();
        screen.setCursorPosition(null);
        tg.setBackgroundColor(TextColor.ANSI.BLACK).setForegroundColor(TextColor.ANSI.GREEN).putCSIStyledString(0, 0, resultString);
        screen.refresh();

        while (true) {
            KeyStroke keyPressed = terminal.pollInput();
            if (keyPressed != null) {
                if (keyPressed.getKeyType() == KeyType.Escape || keyPressed.getKeyType() == KeyType.Enter) {
                    break;
                }
            }
        }
    }

    public String showNewProgramScreen() throws IOException {
        return textPrompt("Enter name of new DDLV program:");
    }

    public String addRuleScreen() throws IOException {
        return textPrompt("Enter new rule:");
    }

    public String addRuleErrorScreen() throws IOException {
        return textPrompt("Rule does not conform to DDLV syntax. Please enter new valid DDLV rule:");
    }

    private String textPrompt(String prompt) throws IOException {
        screen.clear();

        screen.setCursorPosition(new TerminalPosition(0, 1));
        tg.setBackgroundColor(TextColor.ANSI.BLACK).setForegroundColor(TextColor.ANSI.GREEN).putCSIStyledString(0, 0, prompt);
        screen.refresh();
        StringBuilder promptResponse = new StringBuilder();
        while (true) {
            KeyStroke keyPressed = terminal.pollInput();
            if (keyPressed != null) {
                switch (keyPressed.getKeyType()) {
                    case Character:
                        promptResponse.append(keyPressed.getCharacter());
                        break;
                    case Backspace:
                        if (promptResponse.length() > 0) {
                            promptResponse.deleteCharAt(promptResponse.length() - 1);
                        }
                        break;
                    case Enter:
                        return promptResponse.toString();
                    case Escape:
                        return "EXIT";
                }
                screen.clear();
                screen.setCursorPosition(new TerminalPosition(promptResponse.length(), 1));
                tg.setBackgroundColor(TextColor.ANSI.BLACK).setForegroundColor(TextColor.ANSI.GREEN).putCSIStyledString(0, 0, prompt);
                tg.setBackgroundColor(TextColor.ANSI.BLACK).setForegroundColor(TextColor.ANSI.GREEN).putCSIStyledString(0, 1, promptResponse.toString());
                screen.refresh();
            }
        }
    }

    public int itemSelect(Map<Integer, String> itemMap) throws IOException {
        int selected = 0;
        itemSelectDisplay(itemMap, selected);
        int maxSelected = itemMap.size() - 1;
        while (true) {
            KeyStroke keyPressed = terminal.pollInput();
            if (keyPressed != null) {
                switch (keyPressed.getKeyType()) {
                    case ArrowDown:
                        if (selected != maxSelected) {
                            selected ++;
                            itemSelectDisplay(itemMap, selected);
                        }
                        break;
                    case ArrowUp:
                        if (selected != 0) {
                            selected --;
                            itemSelectDisplay(itemMap, selected);
                        }
                        break;
                    case Enter:
                        return selected;
                    case Escape:
                        return - 1;
                }
            }
        }
    }

    public void screenSizeTest() throws IOException {
        screen.clear();
        screen.setCursorPosition(null);
        String ruleString = "suuuuuuuuuuuuuuuuuuuuuupeeeeeeeeeeeeeeerrrrrrrrloooooooonnnnnggggggggruuuuuuuullllleeeee(X) :- blah(X), bleh(X), blurgh(X)";
//        for (int row = 0; row < terminal.getTerminalSize().getRows(); row ++) {
//            for (int column = 0; column < terminal.getTerminalSize().getColumns(); column ++) {
//                Integer outputVal = column*row;
//                tg.setBackgroundColor(background).setForegroundColor(foreground).putCSIStyledString(column, row,
//                        outputVal.toString());
//            }
//        }
        if (ruleString.length() >= terminal.getTerminalSize().getColumns()) {
            tg.setBackgroundColor(background).setForegroundColor(foreground).putCSIStyledString(0, 0,
                        ruleString.substring(0, terminal.getTerminalSize().getColumns()-1));
            tg.setBackgroundColor(background).setForegroundColor(foreground).putCSIStyledString(0, 1,
                        ruleString.substring(terminal.getTerminalSize().getColumns()));
        }
        screen.refresh();
    }

    private void itemSelectDisplay(Map<Integer, String> itemMap, int selected) throws IOException {
        screen.clear();
        screen.setCursorPosition(null);
        for (int row = 0; row < itemMap.size(); row++ ) {
            if (row == selected) {
                tg.setBackgroundColor(foreground).setForegroundColor(background).putCSIStyledString(0, row, itemMap.get(row));
            }
            else {
                tg.setBackgroundColor(background).setForegroundColor(foreground).putCSIStyledString(0, row, itemMap.get(row));
            }
        }
        screen.refresh();
    }

    public int ruleView(String rule) throws IOException {
        int cursorRowPosition = 0;
        int cursorColumnPosition = 0;
        Map<Integer, String> ruleStringMap = placeRule(rule);
        int rows = ruleStringMap.size()-1;

        while (true) {
            KeyStroke keyPressed = terminal.pollInput();
            if (keyPressed != null) {
                switch (keyPressed.getKeyType()) {
                    case ArrowRight:
                        if (cursorColumnPosition < ruleStringMap.get(cursorRowPosition).length()-1) {
                            cursorColumnPosition ++;
                            screen.setCursorPosition(new TerminalPosition(cursorColumnPosition, cursorRowPosition));
                        }
                        break;
                    case ArrowLeft:
                        if (cursorColumnPosition > 0) {
                            cursorColumnPosition --;
                            screen.setCursorPosition(new TerminalPosition(cursorColumnPosition, cursorRowPosition));
                        }
                        break;
                    case ArrowDown:
                        if (cursorRowPosition < rows) {
                            cursorRowPosition ++;
                            if (cursorColumnPosition >= ruleStringMap.get(cursorRowPosition).length()-1) {
                                cursorColumnPosition = ruleStringMap.get(cursorRowPosition).length()-1;
                            }
                            screen.setCursorPosition(new TerminalPosition(cursorColumnPosition, cursorRowPosition));
                        }
                        break;
                    case ArrowUp:
                        if (cursorRowPosition > 0) {
                            cursorRowPosition --;
                            if (cursorColumnPosition >= ruleStringMap.get(cursorRowPosition).length()-1) {
                                cursorColumnPosition = ruleStringMap.get(cursorRowPosition).length()-1;
                            }
                            screen.setCursorPosition(new TerminalPosition(cursorColumnPosition, cursorRowPosition));
                        }
                        break;
                    case Escape:
                        return - 1;
                }
            }
            screen.refresh();
        }
    }

    public String ruleEditView(String rule) throws IOException {
        int cursorRowPosition = 0;
        int cursorColumnPosition = 0;
        Map<Integer, String> ruleStringMap = placeRule(rule);
        int rows = ruleStringMap.size()-1;

        while (true) {
            KeyStroke keyPressed = terminal.pollInput();
            if (keyPressed != null) {
                switch (keyPressed.getKeyType()) {
                    case ArrowRight:
                        if (cursorColumnPosition < ruleStringMap.get(cursorRowPosition).length()-1) {
                            cursorColumnPosition ++;
                            screen.setCursorPosition(new TerminalPosition(cursorColumnPosition, cursorRowPosition));
                        }
                        break;
                    case ArrowLeft:
                        if (cursorColumnPosition > 0) {
                            cursorColumnPosition --;
                            screen.setCursorPosition(new TerminalPosition(cursorColumnPosition, cursorRowPosition));
                        }
                        break;
                    case ArrowDown:
                        if (cursorRowPosition < rows) {
                            cursorRowPosition ++;
                            if (cursorColumnPosition >= ruleStringMap.get(cursorRowPosition).length()-1) {
                                cursorColumnPosition = ruleStringMap.get(cursorRowPosition).length()-1;
                            }
                            screen.setCursorPosition(new TerminalPosition(cursorColumnPosition, cursorRowPosition));
                        }
                        break;
                    case ArrowUp:
                        if (cursorRowPosition > 0) {
                            cursorRowPosition --;
                            if (cursorColumnPosition >= ruleStringMap.get(cursorRowPosition).length()-1) {
                                cursorColumnPosition = ruleStringMap.get(cursorRowPosition).length()-1;
                            }
                            screen.setCursorPosition(new TerminalPosition(cursorColumnPosition, cursorRowPosition));
                        }
                        break;
                    case Backspace:
                        ruleStringMap.replace(cursorRowPosition,
                                ruleStringMap.get(cursorRowPosition).substring(0, cursorColumnPosition)
                                        + ruleStringMap.get(cursorRowPosition).substring(cursorColumnPosition + 1));
                        ruleStringMap = placeRule(rule);
                        if (cursorColumnPosition > ruleStringMap.get(cursorRowPosition).length()){
                            cursorColumnPosition --;
                        }
                        screen.setCursorPosition(new TerminalPosition(cursorColumnPosition, cursorRowPosition));
                        break;
                    case Character:
                        ruleStringMap.replace(cursorRowPosition,
                                ruleStringMap.get(cursorRowPosition).substring(0, cursorColumnPosition)
                                        + keyPressed.getCharacter()
                                        + ruleStringMap.get(cursorRowPosition).substring(cursorColumnPosition));
                        ruleStringMap = placeRule(rule);
                        cursorColumnPosition ++;
                        screen.setCursorPosition(new TerminalPosition(cursorColumnPosition, cursorRowPosition));
                        break;
                    case Enter:
                        return rule;
                    case Escape:
                        return "EXIT";
                }
            }
            screen.refresh();
        }
    }

    public Map<Integer, String> placeRule(String rule) throws IOException {
        int row = 0;
        int terminalWidth = terminal.getTerminalSize().getColumns();
        Map<Integer, String> ruleStringMap = new HashMap<>();
        screen.clear();
        screen.setCursorPosition(null);
        int cutoff;
        while (rule.length() >= terminalWidth) {
            cutoff = rule.substring(0, terminal.getTerminalSize().getColumns()-1).lastIndexOf(" ");
            ruleStringMap.put(row, rule.substring(0, cutoff));
            tg.setBackgroundColor(background).setForegroundColor(foreground).
                    putCSIStyledString(0, row, ruleStringMap.get(row));
            rule = rule.substring(cutoff+1);
            row++;
        }
        tg.setBackgroundColor(background).setForegroundColor(foreground).putCSIStyledString(0, row, rule);
        ruleStringMap.put(row, rule);
        screen.refresh();
        return ruleStringMap;
    }

    public String queryScreen() throws IOException {
        return textPrompt("Enter query:");
    }

    public void exit() throws IOException {
        screen.stopScreen();
    }
}
