package models;

import java.util.List;

public interface Rules {
    public void add(String ruleString) throws DDLVSyntaxException;

    public List<Rule> getRules();

    public String toProgramString();

    public void replace(String oldRule, String newRule) throws DDLVSyntaxException;

    @Override
    public int hashCode();

    @Override
    public boolean equals(Object obj);
}
