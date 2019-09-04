package models;

public class DDLVSyntaxException extends Exception {
    private String syntaxString;

    public DDLVSyntaxException(String syntaxString) {
        this.syntaxString = syntaxString;
    }

    public String getSyntaxString() {
        return syntaxString;
    }
}
