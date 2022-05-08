import java.util.Map;
import java.util.HashMap;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


public class LLVMActions extends OwnLanguageBaseListener {
    private Map<String, VarType> variables = new HashMap<String, VarType>();
    private Map<String, Integer> arrays = new HashMap<String, Integer>();
    private Map<String, String> strings = new HashMap<String, String>();
    private Stack<Value> stack = new Stack<Value>();
    private List<Value> prints = new ArrayList<Value>();

    @Override
    public void exitProg(OwnLanguageParser.ProgContext ctx) {
        System.out.println(LLVMGenerator.generate());
    }

    @Override
    public void exitDeclareVariable(OwnLanguageParser.DeclareVariableContext ctx) {
        String variableType = ctx.type().getChild(0).getText();
        String ID = ctx.ID().getText();
        VarType type = variables.get(ID);
        if (type == null) {
            if (variableType.equals("int")) {
                variables.put(ID, VarType.INT);
                LLVMGenerator.declareInt(ID);
            } else if(variableType.equals("double")) {
                variables.put(ID, VarType.DOUBLE);
                LLVMGenerator.declareDouble(ID);
            } else if(variableType.equals("string")) {
                variables.put(ID, VarType.STRING);
                strings.put(ID, null);
            }
        } else {
            StringBuilder msg = new StringBuilder();
            msg.append("variable \"").append(ID).append("\" was declared before");
            error(ctx.getStart().getLine(), msg.toString());
        }
    }

    @Override
    public void exitDeclareArray(OwnLanguageParser.DeclareArrayContext ctx) {
        String arrayType = ctx.numType().getChild(0).getText();
        String ID = ctx.arrayid().getChild(0).getText();
        int size = Integer.parseInt(ctx.arrayid().getChild(2).getText());
        VarType type = variables.get(ID);
        if (type == null) {
            if (arrayType.equals("int")) {
                variables.put(ID, VarType.INT);
                arrays.put(ID, size);
                LLVMGenerator.declareArrayInt(ID, size);
            } else if(arrayType.equals("double")) {
                variables.put(ID, VarType.DOUBLE);
                arrays.put(ID, size);
                LLVMGenerator.declareArrayDouble(ID, size);
            }
        } else {
            StringBuilder msg = new StringBuilder();
            msg.append("variable \"").append(ID).append("\" was declared before");
            error(ctx.getStart().getLine(), msg.toString());
        }
    }

    @Override
    public void exitInitialize(OwnLanguageParser.InitializeContext ctx) {
        String variableType = ctx.type().getChild(0).getText();
        String ID = ctx.ID().getText();
        Value v = stack.pop();
        if (variables.get(ID) == null) {
            if (v.getType() == VarType.INT && variableType.equals("int")) {
                variables.put(ID, VarType.INT);
                LLVMGenerator.declareInt(ID);
                LLVMGenerator.assignInt(ID, v.getName());
            } else if (v.getType() == VarType.DOUBLE && variableType.equals("double")) {
                variables.put(ID, VarType.DOUBLE);
                LLVMGenerator.declareDouble(ID);
                LLVMGenerator.assignDouble(ID, v.getName());
            } else if (v.getType() == VarType.STRING && variableType.equals("string")) {
                variables.put(ID, VarType.STRING);
                strings.put(ID, v.getName());
            } else {
                StringBuilder msg = new StringBuilder();
                msg.append("incorrect value assign to \"").append(ID)
                    .append("\" variable");
                error(ctx.getStart().getLine(), msg.toString());
            }
        } else {
            error(ctx.getStart().getLine(), "variable \"" + ID + "\" was declared before");
        }
    }

    @Override
    public void exitAssignArrayId(OwnLanguageParser.AssignArrayIdContext ctx) {
        String ID = ctx.arrayid().getChild(0).getText();
        int index = Integer.parseInt(ctx.arrayid().getChild(2).getText());
        Value v = stack.pop();
        VarType type = variables.get(ID);
        if (type != null) {
            int size = arrays.get(ID);
            if (index >= 0 && index < size) {
                if (type == v.getType()) {
                    if (type == VarType.INT) {
                        LLVMGenerator.getIntFromArray(ID, size, index);
                        LLVMGenerator.assignInt(Integer.toString(LLVMGenerator.getReg()), v.getName());
                    }
                    else if (type == VarType.DOUBLE) {
                        LLVMGenerator.getDoubleFromArray(ID, size, index);
                        LLVMGenerator.assignDouble(Integer.toString(LLVMGenerator.getReg()), v.getName());
                    }
                } else {
                    StringBuilder msg = new StringBuilder();
                    msg.append("incorrect assign - variable \"").append(ID).append("[]\" is ")
                        .append(type).append(" and assign value is ").append(v.getType());
                    error(ctx.getStart().getLine(), msg.toString());
                }
            } else {
                StringBuilder msg = new StringBuilder();
                msg.append("index ").append(index).append(" out of bound \"").
                    append(ID).append("[").append(size).append("]\" array");
                error(ctx.getStart().getLine(), msg.toString());
            }
        } else {
            StringBuilder msg = new StringBuilder();
            msg.append("variable \"").append(ID)
                .append("\" was not declared before");
            error(ctx.getStart().getLine(), msg.toString());
        }
    }


    @Override
    public void exitAssignId(OwnLanguageParser.AssignIdContext ctx) {
        String ID = ctx.ID().getText();
        Value v = stack.pop();
        VarType type = variables.get(ID);
        if (type != null) {
            if (type == v.getType()) {
                if (type == VarType.INT)
                    LLVMGenerator.assignInt(ID, v.getName());
                else if (type == VarType.DOUBLE)
                    LLVMGenerator.assignDouble(ID, v.getName());
                else if (type == VarType.STRING)
                    strings.put(ID, v.getName());
            } else {
                StringBuilder msg = new StringBuilder();
                msg.append("incorrect assign - variable \"").append(ID).append("\" is ")
                    .append(type).append(" and assign value is ").append(v.getType());
                error(ctx.getStart().getLine(), msg.toString());
            }
        } else {
            StringBuilder msg = new StringBuilder();
            msg.append("variable \"").append(ID)
                .append("\" was not declared before");
            error(ctx.getStart().getLine(), msg.toString());
        }
    }

    @Override
    public void exitArrayId(OwnLanguageParser.ArrayIdContext ctx) {
        String ID = ctx.arrayid().getChild(0).getText();
        int index = Integer.parseInt(ctx.arrayid().getChild(2).getText());
        VarType type = variables.get(ID);

        if (type != null) {
            int size = arrays.get(ID);
            if (index >= 0 && index < size) {
                if (type == VarType.INT) {
                    LLVMGenerator.getIntFromArray(ID, size, index);
                    LLVMGenerator.loadInt(Integer.toString(LLVMGenerator.getReg()));
                    String reg = "%" + Integer.toString(LLVMGenerator.getReg());
                    stack.push( new Value(reg, type));
                } else if (type == VarType.DOUBLE) {
                    LLVMGenerator.getDoubleFromArray(ID, size, index);
                    LLVMGenerator.loadDouble(Integer.toString(LLVMGenerator.getReg()));
                    String reg = "%" + Integer.toString(LLVMGenerator.getReg());
                    stack.push( new Value(reg, type));
                }
            } else {
                StringBuilder msg = new StringBuilder();
                msg.append("index ").append(index).append(" out of bound \"").
                    append(ID).append("[").append(size).append("]\" array");
                error(ctx.getStart().getLine(), msg.toString());
            }
        } else {
            StringBuilder msg = new StringBuilder();
            msg.append("variable \"").append(ID)
                .append("[]\" was not declared before");
            error(ctx.getStart().getLine(), msg.toString());
        }
    }

    @Override
    public void exitId(OwnLanguageParser.IdContext ctx) {
        String ID = ctx.ID().getText();
        VarType type = variables.get(ID);
        if (type != null) {
            if (type == VarType.INT) {
                LLVMGenerator.loadInt(ID);
                String reg = "%" + Integer.toString(LLVMGenerator.getReg());
                stack.push( new Value(reg, type));
            } else if (type == VarType.DOUBLE) {
                LLVMGenerator.loadDouble(ID);
                String reg = "%" + Integer.toString(LLVMGenerator.getReg());
                stack.push( new Value(reg, type));
            } else if (type == VarType.STRING) {
                String value = strings.get(ID);
                value = (value != null) ? value : "";
                stack.push( new Value(value, type));
            }
        } else {
            StringBuilder msg = new StringBuilder();
            msg.append("variable \"").append(ID)
                .append("\" was not declared before");
            error(ctx.getStart().getLine(), msg.toString());
        }
    }

    @Override
    public void exitString(OwnLanguageParser.StringContext ctx) {
        String str = ctx.STRING().getText();
        stack.push( new Value(str.substring(1, str.length() - 1), VarType.STRING));
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
                LLVMGenerator.loadDouble(ID);
                String loadedValue = "%" + LLVMGenerator.getReg();
                LLVMGenerator.fptosi(loadedValue);
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
                LLVMGenerator.loadInt(ID);
                String loadedValue = "%" + LLVMGenerator.getReg();
                LLVMGenerator.sitofp(loadedValue);
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
            } else if (v1.getType() == VarType.STRING) {
                String newStr = v2.getName() + v1.getName();
                stack.push(new Value(newStr, VarType.STRING));
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

    @Override
    public void exitMul(OwnLanguageParser.MulContext ctx) {
        Value v1 = stack.pop();
        Value v2 = stack.pop();
        if (v1.getType() == v2.getType()) {
            if (v1.getType() == VarType.INT) {
                LLVMGenerator.mulInt(v1.getName(), v2.getName());
                String valueName = "%" + LLVMGenerator.getReg();
                stack.push( new Value(valueName, VarType.INT) );
            } else if (v1.getType() == VarType.DOUBLE) {
                LLVMGenerator.mulDouble(v1.getName(), v2.getName());
                String valueName = "%" + LLVMGenerator.getReg();
                stack.push( new Value(valueName, VarType.DOUBLE) );
            }
        }  else {
            StringBuilder msg = new StringBuilder();
            msg.append("multiplication type mismatch - ").append(v1.getType())
                .append(" and ").append(v2.getType());
            error(ctx.getStart().getLine(), msg.toString());
        }
    }

    @Override
    public void exitDiv(OwnLanguageParser.DivContext ctx) {
        Value v1 = stack.pop();
        Value v2 = stack.pop();
        if (v1.getType() == v2.getType()) {
            if (v1.getType() == VarType.INT) {
                LLVMGenerator.divInt(v1.getName(), v2.getName());
                String valueName = "%" + LLVMGenerator.getReg();
                stack.push( new Value(valueName, VarType.INT) );
            } else if (v1.getType() == VarType.DOUBLE) {
                LLVMGenerator.divDouble(v1.getName(), v2.getName());
                String valueName = "%" + LLVMGenerator.getReg();
                stack.push( new Value(valueName, VarType.DOUBLE) );
            }
        }  else {
            StringBuilder msg = new StringBuilder();
            msg.append("division type mismatch - ").append(v1.getType())
                .append(" and ").append(v2.getType());
            error(ctx.getStart().getLine(), msg.toString());
        }
    }

    @Override
    public void exitPrint(OwnLanguageParser.PrintContext ctx) {
        String printType = ctx.printType().getChild(0).getText();
        Collections.reverse(prints);
        for (Value v : prints) {
            if (v.getType() != null) {
                if (v.getType() == VarType.INT) {
                    LLVMGenerator.printfInt(v.getName());
                } else if (v.getType() == VarType.DOUBLE) {
                    LLVMGenerator.printfDouble(v.getName());
                } else if (v.getType() == VarType.STRING) {
                    String str = v.getName();
                    String newStr = str.replace("\\n", "\\0A");
                    int len = newStr.length() - 2 * (newStr.length() - str.length());
                    LLVMGenerator.printfString(newStr, len);
                }
            } else {
                error(ctx.getStart().getLine(), "problem occurs");
            }
        }
        if (printType.equals("println")) {
            LLVMGenerator.printfString("\\0A", 1);
        }
        prints.clear();
    }

    @Override
    public void exitPrintval(OwnLanguageParser.PrintvalContext ctx) {
        Value v = stack.pop();
        prints.add(v);
    }

    @Override
    public void exitReadId(OwnLanguageParser.ReadIdContext ctx) {
        String ID = ctx.ID().getText();
        VarType type = variables.get(ID);
        if (type != null) {
            if (type == VarType.INT) {
                LLVMGenerator.scanfInt(ID);
            } else if (type == VarType.DOUBLE) {
                LLVMGenerator.scanfDouble(ID);
            }
        } else {
            StringBuilder msg = new StringBuilder();
            msg.append("variable \"").append(ID)
                .append("\" was not declared before");
        }
    }

    @Override
    public void exitReadArrayId(OwnLanguageParser.ReadArrayIdContext ctx) {
        String ID = ctx.arrayid().getChild(0).getText();
        int index = Integer.parseInt(ctx.arrayid().getChild(2).getText());
        VarType type = variables.get(ID);

        if (type != null) {
            int size = arrays.get(ID);
            if (index >= 0 && index < size) {
                if (type == VarType.INT) {
                    LLVMGenerator.getIntFromArray(ID, size, index);
                    String reg = Integer.toString(LLVMGenerator.getReg());
                    LLVMGenerator.scanfInt(reg);
                } else if (type == VarType.DOUBLE) {
                    LLVMGenerator.getDoubleFromArray(ID, size, index);
                    String reg = Integer.toString(LLVMGenerator.getReg());
                    LLVMGenerator.scanfDouble(reg);
                }
            } else {
                StringBuilder msg = new StringBuilder();
                msg.append("index ").append(index).append(" out of bound \"").
                    append(ID).append("[").append(size).append("]\" array");
                error(ctx.getStart().getLine(), msg.toString());
            }
        } else {
            StringBuilder msg = new StringBuilder();
            msg.append("variable \"").append(ID)
                .append("[]\" was not declared before");
        }
    }

    private void error(int line, String msg) {
       System.err.println("Error! Line " + line + ": " + msg);
       System.exit(1);
    }

}
