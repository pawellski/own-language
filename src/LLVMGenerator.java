public class LLVMGenerator {
    private static StringBuilder headerText = new StringBuilder();
    private static StringBuilder mainText = new StringBuilder();
    private static int reg = 0;

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

}
