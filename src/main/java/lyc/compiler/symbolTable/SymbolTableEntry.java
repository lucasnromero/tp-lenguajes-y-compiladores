package lyc.compiler.symbolTable;

public class SymbolTableEntry {
    private final String name;
    private String type;  // Int / Float / String
    private Object value; // si es constante
    private int length; // si es string

    public SymbolTableEntry(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public String getType() {
    return type;
}
    public void setType(String type) { this.type = type; }

    public Object getValue() { return value; }
    public void setValue(Object value) { this.value = value; }

    public int getLength() { return length; }
    public void setLength(int length) { this.length = length; }
}