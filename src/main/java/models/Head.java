package models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Head {
    private String headString;
    private Set<String> terms = new HashSet<>();
    private Map<String, String> SYNTAX = new HashMap<>();
    private Pattern headPattern;


    public Head(String headString) throws DDLVSyntaxException{
        this.headString = headString;
        initHeadPattern();
        if (!checkSyntax()){
            throw new DDLVSyntaxException(headString);
        }
        setTerms(headString);
    }

    private void initSyntaxMap() {
        SYNTAX.put("TERM", "[a-zA-Z0-9][a-zA-Z0-9_]*");
        SYNTAX.put("PREDICATE", "[a-z][a-zA-Z0-9_]*");
        SYNTAX.put("LITERAL", "-*"+SYNTAX.get("PREDICATE")+"(?:\\(\\s*"+SYNTAX.get("TERM")+"(\\s*,\\s*"+SYNTAX.get("TERM")+")*\\s*\\))?");
        SYNTAX.put("HEAD", SYNTAX.get("LITERAL")+"(?:\\s*v\\s*"+SYNTAX.get("LITERAL")+")*");
    }

    private void initHeadPattern() {
        initSyntaxMap();
        headPattern = Pattern.compile(SYNTAX.get("HEAD"));
    }

    private void setTerms(String headString) {
        while (headString.contains("(")) {
            headString = headString.substring(headString.indexOf("(")+1);
            terms.add(headString.substring(0, headString.indexOf(")")));
        }
    }

    public Set<String> getTerms() {
        return terms;
    }

    public boolean checkSyntax() {
        Matcher headInput = headPattern.matcher(headString);
        return headInput.matches();
    }

    public String toString(){
        return headString;
    }

    public String toQueryString() {
        return headString.toLowerCase() + "?";
    }
}
