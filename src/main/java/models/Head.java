package models;

public class Head {
    private String headString;

    public Head(String headString) throws DDLVSyntaxException{
        this.headString = headString;
        if (!checkSyntax()){
            throw new DDLVSyntaxException(headString);
        }
    }

    public boolean checkSyntax() {
        return headString.matches("-*[a-z]+\\([A-Za-z]+\\)(\\s+v\\s+-*[a-z]+\\([A-Za-z]+\\))*");
    }

    public String toString(){
        return headString;
    }

    public String toQueryString() {
        return headString.toLowerCase() + "?";
    }
}
