package br.ufpe.cin.if688.minijava.visitor;

import br.ufpe.cin.if688.minijava.ast.*;
import br.ufpe.cin.if688.minijava.exceptions.PrintException;
import br.ufpe.cin.if688.minijava.symboltable.Class;
import br.ufpe.cin.if688.minijava.symboltable.Method;
import br.ufpe.cin.if688.minijava.symboltable.SymbolTable;

public class BuildSymbolTableVisitor implements IVisitor<Void> {

    SymbolTable symbolTable;

    public BuildSymbolTableVisitor() {
        symbolTable = new SymbolTable();
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    private Class currClass;
    private Method currMethod;

    // MainClass m;
    // ClassDeclList cl;
    public Void visit(Program n) {
        n.m.accept(this);
        for (int i = 0; i < n.cl.size(); i++) {
            n.cl.elementAt(i).accept(this);
        }
        return null;
    }

    // Identifier i1,i2;
    // Statement s;
    public Void visit(MainClass n) {
        if (!symbolTable.addClass(n.i1.s, null)) {
            PrintException.duplicateClass(n.i1.s);
        }
        symbolTable.addClass(n.i1.s, null);
        currClass = symbolTable.getClass(n.i1.s);
        currClass.addMethod("main", new IdentifierType("Void"));
        currMethod = currClass.getMethod("main");
        currMethod.addParam(n.i2.s, new IdentifierType("String[]"));
        n.i1.accept(this);
        n.i2.accept(this);
        n.s.accept(this);
        return null;
    }

    // Identifier i;
    // VarDeclList vl;
    // MethodDeclList ml;
    public Void visit(ClassDeclSimple n) {
        if (!symbolTable.addClass(n.i.s, null)) {
            PrintException.duplicateClass(n.i.s);
        }
            currClass = symbolTable.getClass(n.i.s);
            n.i.accept(this);
            for (int i = 0; i < n.vl.size(); i++) {
                n.vl.elementAt(i).accept(this);
            }
            for (int i = 0; i < n.ml.size(); i++) {
                n.ml.elementAt(i).accept(this);
            }
        currMethod = null;
        return null;
    }

    // Identifier i;
    // Identifier j;
    // VarDeclList vl;
    // MethodDeclList ml;
    public Void visit(ClassDeclExtends n) {
        if (!symbolTable.addClass(n.i.s, n.j.s)) {
            PrintException.duplicateClass(n.i.s);
        }
        currClass = symbolTable.getClass(n.i.s);
        n.i.accept(this);
        n.j.accept(this);
        for (int i = 0; i < n.vl.size(); i++) {
            n.vl.elementAt(i).accept(this);
        }
        for (int i = 0; i < n.ml.size(); i++) {
            MethodDecl currentMethod = n.ml.elementAt(i);
            if(!(currClass.addMethod(currentMethod.i.s, currentMethod.t))){
                PrintException.duplicateMethod(currentMethod.i.s);
            }
            currMethod = symbolTable.getMethod(currentMethod.i.s, currClass.getId());
            n.ml.elementAt(i).accept(this);
        }
        currMethod = null;
        return null;
    }

    // Type t;
    // Identifier i;
    public Void visit(VarDecl n) {
        if (currMethod == null){
            if (currClass.containsVar(n.i.s)){
                PrintException.duplicateVariable(n.i.s);
            }
            currClass.addVar(n.i.s, n.t);
        }else{
            String v = n.i.s;
            if (currMethod.containsVar(n.i.s)){
                PrintException.duplicateVariable(n.i.s);
                PrintException.duplicateVariable(n.i.s);
            }
            currMethod.addVar(n.i.s, n.t);
        }
        n.t.accept(this);
        n.i.accept(this);
        return null;
    }

    // Type t;
    // Identifier i;
    // FormalList fl;
    // VarDeclList vl;
    // StatementList sl;
    // Exp e;
    public Void visit(MethodDecl n) {
        boolean exists =  currClass.addMethod(n.i.s, n.t);
        if (!exists)
            return null;
        currMethod = currClass.getMethod(n.i.s);
        n.t.accept(this);
        n.i.accept(this);
        for (int i = 0; i < n.fl.size(); i++) {
            n.fl.elementAt(i).accept(this);
        }
        for (int i = 0; i < n.vl.size(); i++) {
            n.vl.elementAt(i).accept(this);
        }
        for (int i = 0; i < n.sl.size(); i++) {
            n.sl.elementAt(i).accept(this);
        }
        n.e.accept(this);
        return null;
    }
    // Type t;
    // Identifier i;
    public Void visit(Formal n) {
        if (currMethod.containsParam(n.i.s)){
            PrintException.duplicateParameter(n.i.s);
        }
        currMethod.addParam(n.i.s, n.t);
        n.t.accept(this);
        n.i.accept(this);
        return null;
    }

    public Void visit(IntArrayType n) {
        return null;
    }

    public Void visit(BooleanType n) {
        return null;
    }

    public Void visit(IntegerType n) {
        return null;
    }

    // String s;
    public Void visit(IdentifierType n) {
        return null;
    }

    // StatementList sl;
    public Void visit(Block n) {
        for (int i = 0; i < n.sl.size(); i++) {
            n.sl.elementAt(i).accept(this);
        }
        return null;
    }

    // Exp e;
    // Statement s1,s2;
    public Void visit(If n) {
        n.e.accept(this);
        n.s1.accept(this);
        n.s2.accept(this);
        return null;
    }

    // Exp e;
    // Statement s;
    public Void visit(While n) {
        n.e.accept(this);
        n.s.accept(this);
        return null;
    }

    // Exp e;
    public Void visit(Print n) {
        n.e.accept(this);
        return null;
    }

    // Identifier i;
    // Exp e;
    public Void visit(Assign n) {
        n.i.accept(this);
        n.e.accept(this);
        return null;
    }

    // Identifier i;
    // Exp e1,e2;
    public Void visit(ArrayAssign n) {
        n.i.accept(this);
        n.e1.accept(this);
        n.e2.accept(this);
        return null;
    }

    // Exp e1,e2;
    public Void visit(And n) {
        n.e1.accept(this);
        n.e2.accept(this);
        return null;
    }

    // Exp e1,e2;
    public Void visit(LessThan n) {
        n.e1.accept(this);
        n.e2.accept(this);
        return null;
    }

    // Exp e1,e2;
    public Void visit(Plus n) {
        n.e1.accept(this);
        n.e2.accept(this);
        return null;
    }

    // Exp e1,e2;
    public Void visit(Minus n) {
        n.e1.accept(this);
        n.e2.accept(this);
        return null;
    }

    // Exp e1,e2;
    public Void visit(Times n) {
        n.e1.accept(this);
        n.e2.accept(this);
        return null;
    }

    // Exp e1,e2;
    public Void visit(ArrayLookup n) {
        n.e1.accept(this);
        n.e2.accept(this);
        return null;
    }

    // Exp e;
    public Void visit(ArrayLength n) {
        n.e.accept(this);
        return null;
    }

    // Exp e;
    // Identifier i;
    // ExpList el;
    public Void visit(Call n) {
        n.e.accept(this);
        n.i.accept(this);
        for (int i = 0; i < n.el.size(); i++) {
            n.el.elementAt(i).accept(this);
        }
        return null;
    }

    // int i;
    public Void visit(IntegerLiteral n) {
        return null;
    }

    public Void visit(True n) {
        return null;
    }

    public Void visit(False n) {
        return null;
    }

    // String s;
    public Void visit(IdentifierExp n) {
        return null;
    }

    public Void visit(This n) {
        return null;
    }

    // Exp e;
    public Void visit(NewArray n) {
        n.e.accept(this);
        return null;
    }

    // Identifier i;
    public Void visit(NewObject n) {
        return null;
    }

    // Exp e;
    public Void visit(Not n) {
        n.e.accept(this);
        return null;
    }

    // String s;
    public Void visit(Identifier n) {
        return null;
    }
}
