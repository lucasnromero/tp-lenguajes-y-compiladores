package lyc.compiler.tercetos;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TercetoManager {
    private static final List<Terceto> tercetos = new ArrayList<>();
    private static Stack<Integer> auxStack = new Stack<>();

    public static int add(String op, String arg1, String arg2) {
        tercetos.add(new Terceto(op, arg1, arg2));
        return tercetos.size() - 1; // índice
    }

    public static String ref(int index) {
        return "[" + index + "]";
    }

    public static List<Terceto> getAll() {
        return tercetos;
    }

    public static int nextIndex() {
    return tercetos.size();
    }

    public static void patch(int index, int argPos, String value) {
        Terceto t = tercetos.get(index);
        if(argPos == 1) t.setArg1(value);
        if(argPos == 2) t.setArg2(value);
    }

    public static void pushIndex(int index) {
    auxStack.push(index);
}

public static int popIndex() {
    return auxStack.pop();
}
}
