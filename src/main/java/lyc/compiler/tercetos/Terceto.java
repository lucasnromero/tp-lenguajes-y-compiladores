package lyc.compiler.tercetos;

public class Terceto {
    private final String op;
    private String arg1;
    private String arg2;

    public Terceto(String op, String arg1, String arg2) {
        this.op = op;
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    @Override
    public String toString() {
        return "(" + op + ", " + arg1 + ", " + arg2 + ")";
    }

    public void setArg1(String value) {
        // TODO Auto-generated method stub
        this.arg1 = value;
    }

    public void setArg2(String value) {
        // TODO Auto-generated method stub
        this.arg2 = value;
    }
}