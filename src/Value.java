public class Value {
    private String name;
    private VarType type;

    public Value(String name, VarType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public VarType getType() {
        return this.type;
    }
}
