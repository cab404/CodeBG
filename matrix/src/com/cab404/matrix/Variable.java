package com.cab404.matrix;

/**
 * @author cab404
 */
public class Variable implements CodeCreator.CodePart {
    String name;
    Type type;
    CodeCreator parent;

    public Variable(CodeCreator parent) {
        name = parent.getName();
        this.parent = parent;
        type = Type.random();
    }

    @Override
    public CodeCreator.EId getIdentity(int codeOffset) {
        CodeCreator.EId id = new CodeCreator.EId();
        id.name = name;
        id.off = codeOffset;
        return id;
    }

    public String getEquation() {
        String out = "";
        out += name + "&var& = &sign&";
        out += type.representation(parent);
        out += ";";
        return out;
    }

    public String getConstructor() {
        String out = "";
        out += type.name + "&kw&";
        out += " " + name + "&var& = ";
        out += type.representation(parent);
        out += ";";
        return out;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass().equals(this.getClass()) && ((Variable) obj).name.equals(name);
    }

}