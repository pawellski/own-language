import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("You should pass path to source code!");
            System.exit(-1);
        }

        ANTLRFileStream input = new ANTLRFileStream(args[0]);

        OwnLanguageLexer lexer = new OwnLanguageLexer(input);

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        OwnLanguageParser parser = new OwnLanguageParser(tokens);

        ParseTree tree = parser.prog();

        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new LLVMActions(), tree);
    }

}
