package models;

public class Body {
    private String bodyString;
    private String[] atoms;

    public Body(String bodyString) throws DDLVSyntaxException{
        this.bodyString = bodyString;
        if (!checkSyntax()) {
//            throw new DDLVSyntaxException(bodyString);
        }
        atoms = bodyString.split("\\s*,\\s*");
    }

    public boolean checkSyntax() {
        return bodyString.matches(
                "-*[a-z]+\\(\\s*[A-Za-z]+(\\s*,\\s*[A-Za-z]+)*\\s*\\)(\\s*,\\s*-*[a-z]+\\(\\s*[A-Za-z]+(\\s*,\\s*[A-Za-z]+)*\\s*\\))*");
    }

    public String toString(){
        return bodyString;
    }

    public String instantiate() {
        StringBuilder instantiationStringBuilder = new StringBuilder();
        for (String atom : atoms) {
            instantiationStringBuilder.append(atom.toLowerCase()).append(".");
        }
        return instantiationStringBuilder.toString();
    }
}
