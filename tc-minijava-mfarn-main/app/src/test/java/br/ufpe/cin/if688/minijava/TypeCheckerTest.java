/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package br.ufpe.cin.if688.minijava;

import br.ufpe.cin.if688.minijava.antlr.MiniJavaGrammarLexer;
import br.ufpe.cin.if688.minijava.antlr.MiniJavaGrammarParser;
import br.ufpe.cin.if688.minijava.ast.Program;
import br.ufpe.cin.if688.minijava.exceptions.*;
import br.ufpe.cin.if688.minijava.symboltable.SymbolTable;
import br.ufpe.cin.if688.minijava.util.Programs;
import br.ufpe.cin.if688.minijava.visitor.BuildSymbolTableVisitor;
import br.ufpe.cin.if688.minijava.visitor.MiniJavaVisitor;
import br.ufpe.cin.if688.minijava.visitor.TypeCheckVisitor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.Exception;
import java.util.HashMap;

import static org.junit.Assert.*;

public class TypeCheckerTest {

    private SymbolTable symbolTable;
    private final InputStream systemIn = System.in;
    private final PrintStream systemOut = System.out;

    private ByteArrayInputStream testIn;
    private ByteArrayOutputStream testOut;
    private ByteArrayOutputStream testErr;


    @Before
    public void setUpStreams() {
        symbolTable = new SymbolTable();
        testOut = new ByteArrayOutputStream();
        testErr = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
        System.setErr(new PrintStream(testErr));
    }

    private void provideInput(String data) {
        testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    @After
    public void restoreStreams() {
        System.setIn(System.in);
        System.setOut(System.out);
        System.setErr(System.err);
        System.out.println("finalizou");
    }
    /**/

    private Program getProgram(String input) {
        Program p = new MiniJavaVisitor().visit(
                new MiniJavaGrammarParser(
                        new CommonTokenStream(
                                new MiniJavaGrammarLexer(
                                        CharStreams.fromString(input)
                                )
                        )
                ).program());
        return p;
    }


    @Test
    public void testInput1() {
        Program p = getProgram(Programs.input1);
        SymbolTable symbolTable = buildSymbolTable(p);
        System.out.println(symbolTable);
        assertEquals(Programs.outputSymbolTable1, testOut.toString());

        Exception e = assertThrows(TypeMatchException.class, () -> {
            typeCheck(p, symbolTable);
        });

        assertEquals(Programs.outputTypeChecker1, e.getMessage());
    }

    @Test
    public void testInput2() {
        Program p = getProgram(Programs.input2);
        SymbolTable symbolTable = buildSymbolTable(p);
        System.out.println(symbolTable);
        assertEquals(Programs.outputSymbolTable2, testOut.toString());

        Exception e = assertThrows(TooManyArgumentsException.class, () -> {
            typeCheck(p, symbolTable);
        });

        assertEquals(Programs.outputTypeChecker2, e.getMessage());
    }

    @Test
    public void testInput3() {
        Program p = getProgram(Programs.input3);
        SymbolTable symbolTable = buildSymbolTable(p);
        System.out.println(symbolTable);
        assertEquals(Programs.outputSymbolTable3, testOut.toString());

        Exception e = assertThrows(TooFewArgumentsException.class, () -> {
            typeCheck(p, symbolTable);
        });

        assertEquals(Programs.outputTypeChecker3, e.getMessage());
    }

    @Test(expected = Test.None.class)
    public void testInput4() {
        Program p = getProgram(Programs.input4);
        SymbolTable symbolTable = buildSymbolTable(p);
        System.out.println(symbolTable);
        assertEquals(Programs.outputSymbolTable4, testOut.toString());
        typeCheck(p, symbolTable);
    }

    @Test
    public void testInput5() {
        Program p = getProgram(Programs.input5);
        SymbolTable symbolTable = buildSymbolTable(p);
        System.out.println(symbolTable);
        assertEquals(Programs.outputSymbolTable5, testOut.toString());

        Exception e = assertThrows(IdNotFoundException.class, () -> {
            typeCheck(p, symbolTable);
        });

        assertEquals(Programs.outputTypeChecker5, e.getMessage());
    }

    @Test
    public void testInput6() {
        Program p = getProgram(Programs.input6);
        SymbolTable symbolTable = buildSymbolTable(p);
        System.out.println(symbolTable);
        assertEquals(Programs.outputSymbolTable6, testOut.toString());

        Exception e = assertThrows(IdNotFoundException.class, () -> typeCheck(p, symbolTable));

        assertEquals(Programs.outputTypeChecker6, e.getMessage());
    }

    @Test
    public void testInput7() {
        Program p = getProgram(Programs.input7);
        Exception e = assertThrows(DuplicateParameterException.class, () -> buildSymbolTable(p));
        assertEquals(Programs.outputSymbolTable7, e.getMessage());
    }

    @Test
    public void testInput8() {
        Program p = getProgram(Programs.input8);
        Exception e = assertThrows(DuplicateClassException.class, () -> buildSymbolTable(p));
        assertEquals(Programs.outputSymbolTable8, e.getMessage());
    }

    @Test(expected = Test.None.class)
    public void testInput9() {
        Program p = getProgram(Programs.input9);
        SymbolTable symbolTable = buildSymbolTable(p);
        System.out.println(symbolTable);
        assertEquals(Programs.outputSymbolTable9, testOut.toString());
        typeCheck(p, symbolTable);
    }

    @Test
    public void testInput10() {
        Program p = getProgram(Programs.input10);
        SymbolTable symbolTable = buildSymbolTable(p);
        System.out.println(symbolTable);
        assertEquals(Programs.outputSymbolTable10, testOut.toString());

        Exception e = assertThrows(UndefinedVariableException.class, () -> typeCheck(p, symbolTable));

        assertEquals(Programs.outputTypeChecker10, e.getMessage());
    }

    private SymbolTable buildSymbolTable(Program p) {
        BuildSymbolTableVisitor buildSymbolTableVisitor = new BuildSymbolTableVisitor();
        buildSymbolTableVisitor.visit(p);
        return buildSymbolTableVisitor.getSymbolTable();
    }

    private void typeCheck(Program p, SymbolTable st) {
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(st);
        typeCheckVisitor.visit(p);
    }


    private void runVisitors(String src) {
        Program program = getProgram(src);
        BuildSymbolTableVisitor buildSymbolTableVisitor = new BuildSymbolTableVisitor();
        buildSymbolTableVisitor.visit(program);
        System.out.println(buildSymbolTableVisitor.getSymbolTable());
        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(buildSymbolTableVisitor.getSymbolTable());
        typeCheckVisitor.visit(program);
    }
}
