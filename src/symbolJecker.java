import syntaxtree.*;
import visitor.*;

public class symbolJecker extends GJDepthFirst<String, SymbolTable> {
    /** Jeck list:
     *      ~ Type checking
     *         - expressions
     *         - assignments
     *         - method calls
     *         - return types
     *      ~ Scope checking
     *         - var declaration
     *      ~ CFV
     *         - if
     *         - while
     *         - for (maybe?)
     */
}