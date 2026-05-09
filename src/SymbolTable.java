import java.util.HashMap;

public class SymbolTable {

    // GLOBAL scope: class name → ClassInfo
    public HashMap<String, ClassInfo> classes = new HashMap<>();

}

/* SYMBOL TABLE CLASSES */
class ClassInfo {
    String name;
    String parentName;

    HashMap<String, VarInfo> fields;
    HashMap<String, MethodInfo> methods;
}

class MethodInfo {
    String name;
    String returnType;

    // Name , info
    HashMap<String, VarInfo> parameters;
    HashMap<String, VarInfo> locals;
}

class VarInfo {
    String name;
    String type;
}