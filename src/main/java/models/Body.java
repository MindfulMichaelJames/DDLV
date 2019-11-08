package models;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Body {
    private String bodyString;
    private List<String> literals = new LinkedList<>();
    private Set<String> terms = new HashSet<>();
    private Map<String, String> SYNTAX = new HashMap<>();
    private Pattern bodyPattern;


    public Body(String bodyString) throws DDLVSyntaxException{
        this.bodyString = bodyString;
        initBodyPattern();
        if (!checkSyntax()) {
            throw new DDLVSyntaxException(bodyString);
        }
        setTerms(bodyString);
        Pattern pattern = Pattern.compile(SYNTAX.get("LITERAL"));
        Matcher matcher = pattern.matcher(bodyString);
        while (matcher.find()) {
            literals.add(matcher.group());
        }
    }

    private void initSyntaxMap() {
        SYNTAX.put("CONSTANT", "[a-z0-9][a-zA-Z0-9_]*");
        SYNTAX.put("VARIABLE", "[A-Z][a-zA-Z0-9_]*");
        SYNTAX.put("ANONYMOUS_TERM", "_");
        SYNTAX.put("TERM", "("+SYNTAX.get("ANONYMOUS_TERM")+"|[A-Za-z0-9][a-zA-Z0-9_]*)");
        SYNTAX.put("PREDICATE", "[a-z][a-zA-Z0-9_]*");
        SYNTAX.put("LITERAL", "-*"+SYNTAX.get("PREDICATE")+"(?:\\(\\s*"+
                SYNTAX.get("TERM")+"(\\s*,\\s*"+SYNTAX.get("TERM")+")*\\s*\\))?");
        SYNTAX.put("VARIABLE_LITERAL", "-*"+SYNTAX.get("PREDICATE")+"(?:\\(\\s*"+
                SYNTAX.get("VARIABLE")+"(\\s*,\\s*"+SYNTAX.get("VARIABLE")+")*\\s*\\))?");
        SYNTAX.put("ANONYMOUS_LITERAL", "-*"+SYNTAX.get("PREDICATE")+"(?:\\(\\s*"+
                SYNTAX.get("ANONYMOUS_TERM")+"(\\s*,\\s*"+SYNTAX.get("ANONYMOUS_TERM")+")*\\s*\\))?");
        SYNTAX.put("BODY", SYNTAX.get("LITERAL")+"(?:\\s*,\\s*"+SYNTAX.get("LITERAL")+")*");
    }

    private void initBodyPattern() {
        initSyntaxMap();
        bodyPattern = Pattern.compile(SYNTAX.get("BODY"));
    }

    public void setTerms(String bodyString) {
        while (bodyString.contains("(")) {
            bodyString = bodyString.substring(bodyString.indexOf("(")+1);
            terms.add(bodyString.substring(0, bodyString.indexOf(")")));
        }
    }

    public Set<String> getTerms() {
        return terms;
    }

    public boolean checkSyntax() {
        Matcher bodyInput = bodyPattern.matcher(bodyString);
        return bodyInput.matches();
    }

    public String toString(){
        return bodyString;
    }

    private String groundLiteral(String nonGroundLiteral) {
        if (nonGroundLiteral.contains("(")) {
            String[] predicateAndTerms = nonGroundLiteral.split("\\(");
            StringBuilder groundLiteral = new StringBuilder(predicateAndTerms[0].trim()).append("(");
            String termsString = predicateAndTerms[1].replace(")", "").trim();
            Pattern pattern = Pattern.compile(SYNTAX.get("TERM"));
            Matcher matcher = pattern.matcher(termsString);
            while (matcher.find()){
                groundLiteral.append(matcher.group().substring(0,1).toLowerCase())
                        .append(matcher.group().substring(1)).append(", ");
            }
            return groundLiteral.toString().substring(0, groundLiteral.length()-2) + ")";
        }
        else {
            return nonGroundLiteral;
        }
    }

    private String groundAnonymousLiteral(String nonGroundAnonymousLiteral) {
        int termIndex = nonGroundAnonymousLiteral.indexOf("(")+1;
        return nonGroundAnonymousLiteral.substring(0, termIndex) +
                "anonymous_term" +
                nonGroundAnonymousLiteral.substring(termIndex+1);
    }

    public String instantiate() {
        StringBuilder instantiationStringBuilder = new StringBuilder();
        Pattern variablePattern = Pattern.compile(SYNTAX.get("VARIABLE_LITERAL"));
        Pattern anonymousPattern = Pattern.compile(SYNTAX.get("ANONYMOUS_LITERAL"));
        for (String literal : literals) {
            Matcher variableMatcher = variablePattern.matcher(literal);
            if (variableMatcher.matches()) {
                instantiationStringBuilder.append(groundLiteral(literal)).append(".");
            }
            else {
                Matcher anonymousMatcher = anonymousPattern.matcher(literal);
                if (anonymousMatcher.matches()) {
                    instantiationStringBuilder.append(groundAnonymousLiteral(literal)).append(".");
                }
            }
        }
        return instantiationStringBuilder.toString();
    }
}
