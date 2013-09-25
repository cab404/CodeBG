package com.cab404.matrix;

import com.badlogic.gdx.utils.Array;

/**
 * @author cab404
 */
public class Method implements CodeCreator.CodePart {
    Type out_Type;
    String name;
    CodeCreator parent;
    Array<Type> params = new Array<>();

    public CodeCreator.EId getIdentity(int codeOffset) {
        CodeCreator.EId iden = new CodeCreator.EId();
        iden.name = name;
        iden.off = codeOffset;
        return iden;
    }

    public Method(CodeCreator parent) {
        this(Type.random(), parent);
    }

    public Method(Type out, CodeCreator parent) {
        this.parent = parent;
        name = parent.getName();
        out_Type = out;

        int i = (int) (Math.random() * 3);

        for (; i >= 0; i--) {
            params.add(Type.random());
        }

    }

    @Override
    public String toString() {
        String out = "";
        out += name + "&name&";
        out += "(&sign&";
        for (int i = 0; i != params.size; i++) {
            int j = 5;
            if (parent.vars.size > 0)
                for (j = 0; j != 5; j++) {
                    Variable rnd = parent.getRandomVar();
                    if (rnd.type == params.get(i)) {
                        out += rnd.name + "&var&";
                        break;
                    }
                }
            if (j == 5)
                out += params.get(i).representation(parent);

            if (i != params.size - 1) out += ", &sign&";
        }
        out += ")&sign&";
        return out;
    }
}