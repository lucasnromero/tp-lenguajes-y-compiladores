package lyc.compiler.tercetos;

public class Terceto {
    private final String op;
    private String arg1;
    private String arg2;
    private String type;

    public Terceto(String op, String arg1, String arg2, String type) {
        this.op = op;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.type = type;
    }

    public String getOp() {
        return op;
    }

    public String getArg1() {
        return arg1;
    }

    public String getArg2() {
        return arg2;
    }

    public void setArg1(String value) {
        this.arg1 = value;
    }

    public void setArg2(String value) {
        this.arg2 = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {

        if(type != null) {
            return "(" + op + "," + arg1 + "," + arg2 + "," + type + ")";
        }
        return "(" + op + "," + arg1 + "," + arg2 + ")";
    }
}