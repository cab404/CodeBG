package com.cab404.matrix;

import java.util.UUID;

/**
 * @author cab404
 */
public enum Type {
    INTEGER("int"),
    LONG("long"),
    BYTE("byte"),
    FLOAT("float"),
    BOOLEAN("boolean"),
    STRING("String");

    String name;

    Type(String name) {
        this.name = name;
    }

    public static Type random() {
        return values()[((int) (Math.random() * values().length))];
    }

    public String representation(CodeCreator res) {
        if (Math.random() > 0.95)
            return new Method(this, res).toString();
        switch (this) {
            case LONG:
                return (long) (Math.random() * 50000 - 25000) + "L&num&";
            case FLOAT:
                return (float) (Math.random() * 50000 - 25000) + "f&num&";
            case INTEGER:
                return (int) (Math.random() * 500000 - 250000) + "&num&";
            case STRING:
                return "\"" + UUID.randomUUID().toString().substring(19,28) + "\"&str&";
            case BOOLEAN:
                return (Math.random() > 0.5) + "&kw&";
            case BYTE:
                return (byte) (Math.random() * 255 - 128) + "&num&";
        }
        return "";
    }
}