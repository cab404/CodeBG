package com.cab404.matrix;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

/**
 * Эта махина генерирует подобие кода. YAY
 *
 * @author cab404
 */
public class CodeCreator {
    private Array<String> parts;
    private int codeOffset = 0, deltaOffset = 0;
    private Random rand = new Random();
    protected ArrayMap<EId, Variable> vars;

    public Variable getVar() {
        Variable var;
        do {
            var = new Variable(this);
        } while (vars.containsValue(var, false));
        return var;
    }

    public Variable getRandomVar() {
        if (vars.size > 0) {
            int rnd = (int) Math.floor(rand.nextInt(vars.size));
            return vars.getValueAt(rnd);
        }
        return null;
    }

    public Variable getRandomVar(Type type) {
        if (vars.size > 0) {
            Variable out = null;
            Array<Integer> ids = new Array<>();

            for (int i = 0; i != vars.size; i++) {
                if (vars.getValueAt(i).type.equals(type))
                    ids.add(i);
            }

            if (ids.size != 0) {
                int rnd = (int) Math.floor(rand.nextInt(ids.size));
                return vars.getValueAt(ids.get(rnd));
            }
        }
        return null;
    }

    public CodeCreator() {
        parts = new Array<>();
        vars = new ArrayMap<>();
        FileHandle prt = Gdx.files.internal("data/name_parts.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(prt.read()));

        try {
            while (reader.ready()) {
                parts.add(reader.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Выполняет самую сложную работу - придумывает имена переменным.
     */
    public String getName() {
        String out = "";

        int i = (int) (rand.nextInt(3)) + 1;

        for (; i >= 0; i--) {
            out += parts.random() + (i == 0 ? "" : "_");
        }

        return out;

    }

    public static class EId {
        /**
         * Блок, в котором находится код.
         */
        int off;
        String name;
    }

    public static interface CodePart {
        public abstract EId getIdentity(int codeOffset);
    }

    private Array<String> buffer = new Array<>();

    /**
     * Генерирует или достаёт из буфера следующюю строку кода
     */
    public String nextLine() {
        String line = "";

        if (buffer.size > 0) line = buffer.pop();
        else {
            double chances = Math.random();

            if (chances > 0.88 && codeOffset > 0) {
                line = closeBlock();
            } else if (chances > 0.84) {
                line = openFor();
            } else if (chances > 0.8) {
                line = openWhile();

            } else if (chances > 0.7) {
                line = setVar();
            } else if (chances > 0.6) {
                line = createVar();
            } else if (chances > 0.5) {
                line = useVar();
            } else if (chances > 0.4) {
                line = deleteVar();

            } else if (chances > 0.2) {
                line += new Method(this).toString() + ";";
            } else if (chances > 0.1) {
                line = genIfElseChain();
            }
            // Иначе вставляем пустую строку.
        }


        if (deltaOffset < 0)
            codeOffset += deltaOffset;
        for (int i = 0; i != codeOffset; i++) line = "  " + line;
        if (deltaOffset > 0)
            codeOffset += deltaOffset;
        deltaOffset = 0;
        return line;
    }

    /**
     * Пытается закрыть блок
     */
    public String closeBlock() {
        String line = "";

        deltaOffset--;
        line += "}&sign&";

        for (EId var : vars.keys()) {
            if (var.off >= codeOffset)
                vars.removeKey(var);
        }

        return line;
    }

    /**
     * Открывает цикл while
     */
    public String openWhile() {
        String line = "";

        deltaOffset++;
        Variable var = getRandomVar(Type.BOOLEAN);
        if (var == null) var = new Variable(this);
        var.type = Type.BOOLEAN;
        line += "while&kw& (&sign&" + var.name + "&var&) {&sign&";

        return line;
    }

    /**
     * Открывает цикл for
     */
    public String openFor() {
        String line = "";

        deltaOffset++;
        Variable var = getVar();
        var.type = Type.INTEGER;

        line += "for&kw& (&sign&int&kw& ";
        line += var.name + "&var& = " + Type.INTEGER.representation(this);
        line += "; &sign&" + var.name + "&var& != &sign&";
        line += Type.INTEGER.representation(this) + "; ";
        line += var.name + "&var&++) {&sign&";

        return line;
    }

    /**
     * Присваивает случайной переменной случайное значение
     */
    public String setVar() {
        String line = "";

        Variable var = getRandomVar();
        if (var != null) {
            line = var.getEquation();
            vars.put(var.getIdentity(codeOffset), var);
        }

        return line;
    }

    /**
     * Придумывает метод и использует его как метод класса случайной переменной.
     */
    public String useVar() {
        String line = "";

        Variable var = getRandomVar();
        if (var == null) var = new Variable(this);
        Method sub = new Method(this);
        line += var.name + "&var&.&sign&" + sub.toString() + "&name&;";

        return line;
    }

    /**
     * Создаёт переменную
     */
    public String createVar() {
        String line;

        Variable var = getVar();
        boolean is_final = Math.random() > 0.9;

        line = var.getConstructor();

        if (!is_final)
            vars.put(var.getIdentity(codeOffset), var);
        else
            line = "final&kw& " + line;

        return line;
    }

    /**
     * Присваивает переменной null
     */
    public String deleteVar() {
        String line = "";
        Variable var = getRandomVar();

        if (var != null) {
            line = var.name + "&var& =&sign& null&kw&;";
            vars.removeValue(var, true);
        }

        if (line.isEmpty())
            line = createVar();

        assert false;

        return line;
    }

    /**
     * Пытается наскрести цепочку if-else-ов из тех переменных, что есть, иначе делает рандомный assert
     */
    public String genIfElseChain() {
        String line = "";

        Array<String> bools = new Array<>();

        int size = rand.nextInt(10);

        if (vars.size < size) {
            return "assert&kw& "
                    + Type.BOOLEAN.representation(this)
                    + " == &sign&"
                    + Type.BOOLEAN.representation(this)
                    + ";&sign&";
        }
        for (int i = 0; i != size; i++) {
            Type type = Type.values()[rand.nextInt(Type.values().length)];
            switch (type) {
                case BOOLEAN:
                    bools.add(type.representation(this) + "&name&");
                    break;
                case INTEGER:
                case FLOAT:
                case LONG:
                case BYTE:
                    switch (rand.nextInt(6)) {
                        case 0:
                            bools.add(type.representation(this) + "&name& > &sign&" + type.representation(this));
                            break;
                        case 1:
                            bools.add(type.representation(this) + "&name& < &sign&" + type.representation(this));
                            break;
                        case 2:
                            bools.add(type.representation(this) + "&name& == &sign&" + type.representation(this));
                            break;
                        case 3:
                            bools.add(type.representation(this) + "&name& >= &sign&" + type.representation(this));
                            break;
                        case 4:
                            bools.add(type.representation(this) + "&name& <= &sign&" + type.representation(this));
                            break;
                        case 5:
                            bools.add(type.representation(this) + "&name& != &sign&" + type.representation(this));
                            break;
                    }
                    break;
            }

        }
        if (bools.size > 1) {
            line = "if&kw& (&sign&" + bools.pop() + ")&sign&";
            buffer.add("  " + useVar());

            for (String bool : bools) {
                buffer.add("else if&kw& (&sign&" + bool + ")&sign&");
                buffer.add("  " + useVar());
            }
        }

        return line;
    }

}
