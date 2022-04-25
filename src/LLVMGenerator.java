public class LLVMGenerator {
    private static StringBuilder headerText = new StringBuilder();
    private static StringBuilder mainText = new StringBuilder();
    private static int reg = 0;

    public static void incReg() {
        reg += 1;
    }

    public static int getReg() {
        return reg;
    }

    public static String generate() {
        StringBuilder text = new StringBuilder();
        text.append("\n");
        text.append(mainText.toString());
        return text.toString();
    }

    public static void declareInt(String id) {
        mainText.append("%").append(id).append(" = alloca i32\n");
    }

    public static void declareDouble(String id) {
        mainText.append("%").append(id).append(" = alloca double\n");
    }

   public static void assignInt(String id, String value) {
       mainText.append("store i32 ").append(value).append(", i32* %")
        .append(id).append("\n");
    }

   public static void assignDouble(String id, String value) {
       mainText.append("store double ").append(value).append(", double* %")
        .append(id).append("\n");
    }

    public static void sitofp(String id){
        reg++;
        mainText.append("%").append(reg).append(" = sitofp i32 ")
        .append(id).append(" to double\n");
    }

   public static void fptosi(String id){
       reg++;
       mainText.append("%").append(reg).append(" = fptosi double ")
        .append(id).append(" to i32\n");
    }

    public static void addInt(String val1, String val2){
        reg++;
        mainText.append("%").append(reg).append(" = add i32 ")
            .append(val1).append(", ").append(val2).append("\n");
    }

    public static void addDouble(String val1, String val2){
        reg++;
        mainText.append("%").append(reg).append(" = fadd double ")
            .append(val1).append(", ").append(val2).append("\n");
    }

}
