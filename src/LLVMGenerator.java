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
        text.append(headerText.toString());
        text.append("define i32 @main() nounwind{\n");
        text.append(mainText.toString());
        text.append("ret i32 0 }\n");
        return text.toString();
    }

    private static void configureHeader() {
        headerText.append("declare i32 @printf(i8*, ...)\n")
            .append("declare i32 @__isoc99_scanf(i8*, ...)\n")
            .append("@strpi = constant [4 x i8] c\"%d\\0A\\00\"\n")
            .append("@strpd = constant [4 x i8] c\"%f\\0A\\00\"\n")
            .append("@strs = constant [3 x i8] c\"%d\\00\"\n")
            .append("@strsd = constant [4 x i8] c\"%lf\\00\"\n");
    }

    public static void init() {
        configureHeader();
    }

    public static void declareInt(String id) {
        mainText.append("%").append(id).append(" = alloca i32\n");
    }

    public static void declareDouble(String id) {
        mainText.append("%").append(id).append(" = alloca double\n");
    }

    public static void declareArrayInt(String id, int size) {
        mainText.append("%").append(id).append(" = alloca [")
            .append(Integer.toString(size)).append(" x i32]\n");
    }

    public static void declareArrayDouble(String id, int size) {
        mainText.append("%").append(id).append(" = alloca [")
            .append(Integer.toString(size)).append(" x double]\n");
    }

    public static void assignInt(String id, String value) {
        mainText.append("store i32 ").append(value).append(", i32* %")
            .append(id).append("\n");
    }

    public static void assignDouble(String id, String value) {
        mainText.append("store double ").append(value).append(", double* %")
            .append(id).append("\n");
    }

    public static void sitofp(String id) {
        reg++;
        mainText.append("%").append(reg).append(" = sitofp i32 ")
            .append(id).append(" to double\n");
    }

    public static void sitofpId(String id) {
        reg++;
        mainText.append("%").append(reg).append(" = load i32, i32* %")
            .append(id).append("\n");
        reg++;
        mainText.append("%").append(reg).append(" = sitofp i32 %")
            .append(reg-1).append(" to double\n");
    }

    public static void fptosi(String id) {
        reg++;
        mainText.append("%").append(reg).append(" = fptosi double ")
            .append(id).append(" to i32\n");
    }

    public static void fptosiId(String id) {
        reg++;
        mainText.append("%").append(reg).append(" = load double, double* %")
            .append(id).append("\n");
        reg++;
        mainText.append("%").append(reg).append(" = fptosi double %")
            .append(reg-1).append(" to i32\n");
    }

    public static void addInt(String val1, String val2) {
        reg++;
        mainText.append("%").append(reg).append(" = add i32 ")
            .append(val1).append(", ").append(val2).append("\n");
    }

    public static void addDouble(String val1, String val2){
        reg++;
        mainText.append("%").append(reg).append(" = fadd double ")
            .append(val1).append(", ").append(val2).append("\n");
    }

    public static void subInt(String val1, String val2) {
        reg++;
        mainText.append("%").append(reg).append(" = sub i32 ")
            .append(val2).append(", ").append(val1).append("\n");
    }

    public static void subDouble(String val1, String val2) {
        reg++;
        mainText.append("%").append(reg).append(" = fsub double ")
            .append(val2).append(", ").append(val1).append("\n");
    }

    public static void mulInt(String val1, String val2) {
        reg++;
        mainText.append("%").append(reg).append(" = mul i32 ").append(val1)
            .append(", ").append(val2).append("\n");
    }

    public static void mulDouble(String val1, String val2) {
        reg++;
        mainText.append("%").append(reg).append(" = fmul double ").append(val1)
            .append(", ").append(val2).append("\n");
    }

    public static void divInt(String val1, String val2) {
        reg++;
        mainText.append("%").append(reg).append(" = sdiv i32 ").append(val2)
            .append(", ").append(val1).append("\n");
    }

    public static void divDouble(String val1, String val2) {
        reg++;
        mainText.append("%").append(reg).append(" = fdiv double ").append(val2)
            .append(", ").append(val1).append("\n");
    }

    public static void loadInt(String id) {
        reg++;
        mainText.append("%").append(reg).append(" = load i32, i32* %")
            .append(id).append("\n");
    }

    public static void loadDouble(String id) {
        reg++;
        mainText.append("%").append(reg).append(" = load double, double* %")
            .append(id).append("\n");
    }

    public static void printfInt(String id) {
        reg++;
        mainText.append("%").append(reg)
            .append(" = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @strs, i32 0, i32 0), i32 ")
            .append(id).append(")\n");
    }

    public static void printfDouble(String id) {
        reg++;
        mainText.append("%").append(reg)
            .append(" = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @strsd, i32 0, i32 0), double ")
            .append(id).append(")\n");
    }

    public static void printfString(String text, int len) {
        reg++;
        String type = "[" + (len+1) + " x i8]";
        headerText.append("@str").append(reg).append(" = constant ").append(type).append(" c\"").append(text)
            .append("\\00\"\n");
        mainText.append("call i32 (i8*, ...) @printf(i8* getelementptr inbounds ( ").append(type).append(", ")
            .append(type).append("* @str").append(reg).append(", i32 0, i32 0))\n");
    }

    public static void scanfInt(String id) {
        reg++;
        mainText.append("%").append(reg)
            .append(" = call i32 (i8*, ...) @__isoc99_scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @strs, i32 0, i32 0), i32* %")
            .append(id).append(")\n");
    }

    public static void scanfDouble(String id) {
        reg++;
        mainText.append("%").append(reg)
            .append(" = call i32 (i8*, ...) @__isoc99_scanf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @strsd, i32 0, i32 0), double* %")
            .append(id).append(")\n");
    }

    public static void getIntFromArray(String id, int size, int index) {
        reg++;
        mainText.append("%").append(reg)
            .append(" = getelementptr inbounds [").append(Integer.toString(size)).append(" x i32], [").append(Integer.toString(size))
            .append(" x i32]* %").append(id).append(", i64 0, i64 ").append(Integer.toString(index)).append("\n");
    }

    public static void getDoubleFromArray(String id, int size, int index) {
        reg++;
        mainText.append("%").append(reg)
            .append(" = getelementptr inbounds [").append(Integer.toString(size)).append(" x double], [").append(Integer.toString(size))
            .append(" x double]* %").append(id).append(", i64 0, i64 ").append(Integer.toString(index)).append("\n");
    }

}
