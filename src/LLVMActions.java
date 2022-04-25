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
    public void exitInitializeInt(OwnLanguageParser.InitializeIntContext ctx) {
        String ID = ctx.ID().getText();
        Value v = stack.pop();
        if (variables.get(ID) == null) {
            if (v.getType() == VarType.INT) {
                variables.put(ID, VarType.INT);
                LLVMGenerator.declareInt(ID);
                LLVMGenerator.assignInt(ID, v.getName());
            } else {
                StringBuilder msg = new StringBuilder();
                msg.append("incorrect value assign to \"").append(ID)
                    .append("\" which is INT");
                error(ctx.getStart().getLine(), msg.toString());
            }
        } else {
            error(ctx.getStart().getLine(), "variable \"" + ID + "\" was declared before");
        }
    }

    @Override
    public void exitInitializeDouble(OwnLanguageParser.InitializeDoubleContext ctx) {
        String ID = ctx.ID().getText();
        Value v = stack.pop();
        if (variables.get(ID) == null) {
            if (v.getType() == VarType.DOUBLE) {
                variables.put(ID, VarType.DOUBLE);
                LLVMGenerator.declareDouble(ID);
                LLVMGenerator.assignDouble(ID, v.getName());
            } else {
                StringBuilder msg = new StringBuilder();
                msg.append("incorrect value assign to \"").append(ID)
                    .append("\" which is DOUBLE");
                error(ctx.getStart().getLine(), msg.toString());
            }
        } else {
            error(ctx.getStart().getLine(), "variable \"" + ID + "\" was declared before");
        }
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

    @Override
    public void exitInt(OwnLanguageParser.IntContext ctx) {
        stack.push( new Value(ctx.INT().getText(), VarType.INT));
    }

    @Override
    public void exitDouble(OwnLanguageParser.DoubleContext ctx) {
        stack.push( new Value(ctx.DOUBLE().getText(), VarType.DOUBLE));
    }

    @Override
    public void exitIntToInt(OwnLanguageParser.IntToIntContext ctx) {
        String valueName = "%" + LLVMGenerator.getReg();
        stack.push( new Value(valueName, VarType.INT) );
    }

    @Override
    public void exitDoubleToDouble(OwnLanguageParser.DoubleToDoubleContext ctx) {
        String valueName = "%" + LLVMGenerator.getReg();
        stack.push( new Value(valueName, VarType.DOUBLE) );
    }

	@Override
    public void exitDoubleToInt(OwnLanguageParser.DoubleToIntContext ctx) {
        LLVMGenerator.fptosi( ctx.DOUBLE().getText() );
        String valueName = "%" + LLVMGenerator.getReg();
        stack.push( new Value(valueName, VarType.INT) );
    }

    @Override
    public void exitIntToDouble(OwnLanguageParser.IntToDoubleContext ctx) {
        LLVMGenerator.sitofp( ctx.INT().getText() );
        String valueName = "%" + LLVMGenerator.getReg();
        stack.push( new Value(valueName, VarType.DOUBLE) );
    }

    @Override
    public void exitIdToInt(OwnLanguageParser.IdToIntContext ctx) {
        String ID = ctx.ID().getText();
        VarType type = variables.get(ID);
        if (type != null) {
            if (type == VarType.INT) {
                String valueName = "%" + LLVMGenerator.getReg();
                stack.push( new Value(valueName, VarType.INT) );
            } else if (type == VarType.DOUBLE) {
                LLVMGenerator.fptosi(ID);
                String valueName = "%" + LLVMGenerator.getReg();
                stack.push( new Value(valueName, VarType.INT) );
            } else {
                StringBuilder msg = new StringBuilder();
                msg.append("mismatch type of variable \"").append(ID)
                    .append("\" which is ").append(type);
                error(ctx.getStart().getLine(), msg.toString());
            }
        } else {
            error(ctx.getStart().getLine(), "undeclared variable " + ID);
        }
    }

    @Override
    public void exitIdToDouble(OwnLanguageParser.IdToDoubleContext ctx) {
        String ID = ctx.ID().getText();
        VarType type = variables.get(ID);
        if (type != null) {
            if (type == VarType.INT) {
                LLVMGenerator.sitofp(ID);
                String valueName = "%" + LLVMGenerator.getReg();
                stack.push( new Value(valueName, VarType.DOUBLE) );
            } else if (type == VarType.DOUBLE) {
                String valueName = "%" + LLVMGenerator.getReg();
                stack.push( new Value(valueName, VarType.DOUBLE) );
            } else {
                StringBuilder msg = new StringBuilder();
                msg.append("mismatch type of variable \"").append(ID)
                    .append("\" which is ").append(type);
                error(ctx.getStart().getLine(), msg.toString());
            }
        } else {
            error(ctx.getStart().getLine(), "undeclared variable " + ID);
        }
    }

    @Override
    public void exitAdd(OwnLanguageParser.AddContext ctx) {
        Value v1 = stack.pop();
        Value v2 = stack.pop();
        if (v1.getType() == v2.getType()) {
            if (v1.getType() == VarType.INT) {
                LLVMGenerator.addInt(v1.getName(), v2.getName());
                String valueName = "%" + LLVMGenerator.getReg();
                stack.push( new Value(valueName, VarType.INT) );
            } else if (v1.getType() == VarType.DOUBLE) {
                LLVMGenerator.addDouble(v1.getName(), v2.getName());
                String valueName = "%" + LLVMGenerator.getReg();
                stack.push( new Value(valueName, VarType.DOUBLE) );
            }
        }  else {
            StringBuilder msg = new StringBuilder();
            msg.append("addition type mismatch - ").append(v1.getType())
                .append(" and ").append(v2.getType());
            error(ctx.getStart().getLine(), msg.toString());
        }
    }

    @Override
    public void exitSub(OwnLanguageParser.SubContext ctx) {
        Value v1 = stack.pop();
        Value v2 = stack.pop();
        if (v1.getType() == v2.getType()) {
            if (v1.getType() == VarType.INT) {
                LLVMGenerator.subInt(v1.getName(), v2.getName());
                String valueName = "%" + LLVMGenerator.getReg();
                stack.push( new Value(valueName, VarType.INT) );
            } else if (v1.getType() == VarType.DOUBLE) {
                LLVMGenerator.subDouble(v1.getName(), v2.getName());
                String valueName = "%" + LLVMGenerator.getReg();
                stack.push( new Value(valueName, VarType.DOUBLE) );
            }
        }  else {
            StringBuilder msg = new StringBuilder();
            msg.append("substraction type mismatch - ").append(v1.getType())
                .append(" and ").append(v2.getType());
            error(ctx.getStart().getLine(), msg.toString());
        }
    }

    private void error(int line, String msg){
       System.err.println("Error! Line " + line + ": " + msg);
       System.exit(1);
   }

}
