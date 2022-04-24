import java.util.Map;
import java.util.HashMap;
import java.util.Stack;


public class LLVMActions extends OwnLanguageBaseListener {
    private Map<String, VarType> variables = new HashMap<String, VarType>();
    private Stack<Value> stack = new Stack<Value>();

    @Override
    public void exitProg(OwnLanguageParser.ProgContext ctx) {
        System.out.println(LLVMGenerator.generate());
    }

    @Override
    public void exitDeclareInt(OwnLanguageParser.DeclareIntContext ctx) {
        String ID = ctx.ID().getText();
        variables.put(ID, VarType.INT);
        LLVMGenerator.declareInt(ID);
    }

    @Override
    public void exitDeclareDouble(OwnLanguageParser.DeclareDoubleContext ctx) {
        String ID = ctx.ID().getText();
        variables.put(ID, VarType.DOUBLE);
        LLVMGenerator.declareDouble(ID);
    }

    @Override
    public void exitAssign(OwnLanguageParser.AssignContext ctx) {
        String ID = ctx.ID().getText();
        Value v = stack.pop();
        variables.put(ID, v.getType());
        if (v.getType() == VarType.INT) {
            LLVMGenerator.declareInt(ID);
            LLVMGenerator.assignInt(ID, v.getName());
        } else if (v.getType() == VarType.DOUBLE) {
            LLVMGenerator.declareDouble(ID);
            LLVMGenerator.assignDouble(ID, v.getName());
        }
    }

    private void error(int line, String msg){
       System.err.println("Error! Line " + line + ": " + msg);
       System.exit(1);
   }

}
