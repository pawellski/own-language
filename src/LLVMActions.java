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

}
