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
import enums.InterfaceFunctions;
import models.DDLVSyntaxException;
import models.RankedModel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LanternaView {
    private Terminal terminal;
    private Screen screen;
    private static TextGraphics tg;
    private boolean keepRunning = true;
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

    public String showLoadErrorScreen() throws IOException {
        return textPrompt("File does not exist. Please enter name of existing file:");
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
        screen.clear();
        int cursorPosition = 0;
        int endPosition = rule.length() - 1;
        screen.setCursorPosition(null);
        tg.setBackgroundColor(background).setForegroundColor(foreground).putCSIStyledString(0, 0, rule);
        screen.refresh();
        while (true) {
            KeyStroke keyPressed = terminal.pollInput();
            if (keyPressed != null) {
                switch (keyPressed.getKeyType()) {
                    case ArrowRight:
                        if (cursorPosition < endPosition) {
                            cursorPosition ++;
                            screen.setCursorPosition(new TerminalPosition(cursorPosition, 0));
                        }
                        break;
                    case ArrowLeft:
                        if (cursorPosition > 0) {
                            cursorPosition --;
                            screen.setCursorPosition(new TerminalPosition(cursorPosition, 0));
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
        screen.clear();
        int cursorPosition = 0;
        int endPosition = rule.length() - 1;
        screen.setCursorPosition(new TerminalPosition(cursorPosition, 0));
        tg.setBackgroundColor(background).setForegroundColor(foreground).putCSIStyledString(0, 0, rule);
        screen.refresh();
        while (true) {
            KeyStroke keyPressed = terminal.pollInput();
            if (keyPressed != null) {
                switch (keyPressed.getKeyType()) {
                    case ArrowRight:
                        if (cursorPosition < endPosition) {
                            cursorPosition ++;
                            screen.setCursorPosition(new TerminalPosition(cursorPosition, 0));
                        }
                        break;
                    case ArrowLeft:
                        if (cursorPosition > 0) {
                            cursorPosition --;
                            screen.setCursorPosition(new TerminalPosition(cursorPosition, 0));
                        }
                        break;
                    case Backspace:
                        rule = rule.substring(0, cursorPosition) + rule.substring(cursorPosition + 1);
                        screen.clear();
                        tg.setBackgroundColor(background).setForegroundColor(foreground).putCSIStyledString(0, 0, rule);
                        endPosition --;
                        if (cursorPosition > endPosition){
                            cursorPosition --;
                        }
                        screen.setCursorPosition(new TerminalPosition(cursorPosition, 0));
                        break;
                    case Character:
                        rule = rule.substring(0, cursorPosition) + keyPressed.getCharacter() + rule.substring(cursorPosition);
                        screen.clear();
                        tg.setBackgroundColor(background).setForegroundColor(foreground).putCSIStyledString(0, 0, rule);
                        endPosition ++;
                        cursorPosition ++;
                        screen.setCursorPosition(new TerminalPosition(cursorPosition, 0));
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

    public String queryScreen() throws IOException {
        return textPrompt("Enter query:");
    }

    public void setForeground(TextColor foreground) {
        this.foreground = foreground;
    }

    public void setBackground(TextColor background) {
        this.background = background;
    }

    public void showRankScreen() {

    }

    public void editProgramScreen() {

    }

    public void exit() throws IOException {
        screen.stopScreen();
    }
}
