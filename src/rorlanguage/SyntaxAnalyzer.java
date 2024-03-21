/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rorlanguage;

import modules.LexResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import modules.ParseTreeNode;

/**
 *
 * @author Ivan
 */
public class SyntaxAnalyzer {

    private ArrayList<Integer> lineTraceback;
    private String lookAhead;
    private Queue<String> tokenQueue;
    private int lineTracebackPointer;
    private ParseTreeNode ptn;

    public SyntaxAnalyzer(LexResult lr) {
        this.tokenQueue = new ArrayDeque<>(lr.tokens);
        System.out.println(lr.tokens);
        this.lineTraceback = lr.lineTraceback;
        this.lookAhead = tokenQueue.poll();
        this.lineTracebackPointer = 0;
        this.ptn = new ParseTreeNode("P");
    }

    //<editor-fold defaultstate="collapsed" desc="Syntax Analyzer Functions">
    boolean parse() {
        try {
            ptn = new ParseTreeNode("p");
            P(ptn);
            System.out.println(ptn);
        } catch (SyntaxErrorException see) {
            see.printStackTrace();
        }
        if (tokenQueue.isEmpty()) {
            System.out.println("SUCCESS! Token List is empty");
            return true;
        } else {
            return false;
        }
    }

    void trace(String location) {
        System.out.println(location + ": " + lookAhead);
    }

    boolean match(String token, ParseTreeNode ptn) throws SyntaxErrorException {

        if (lookAhead.equals(token) && !tokenQueue.isEmpty()) {
            ptn.addChild(token);
            lookAhead = tokenQueue.poll();
            this.lineTracebackPointer++;
            return true;
        } else if (lookAhead.equals(token) && tokenQueue.isEmpty()){
            ptn.addChild(token);
            this.lineTracebackPointer++;
            return true;
        } else {
            handleError("Expected: " + token + "\nBut got: " + lookAhead);
            return false;
        }
    }

    boolean isInt(String token) {
        try {
            Integer.valueOf(token);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    void handleError(String error) throws SyntaxErrorException {
        String str, lineWithError, message;
        String output = "";
        ArrayList<String> outputTokens = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File("TestCases/InputProgram.txt")));
            int line = 0;
            int errors = 0;
            boolean groupCommentFound = false;
            lineWithError = "";
            while ((str = br.readLine()) != null) {
                if (line == lineTraceback.get(lineTracebackPointer - 1) - 1) {
                    lineWithError = str;
                    break;
                } else {
                    line++;
                }
            }

            message = new StringBuilder()
                    .append("Error occured at line " + line + ": " + lineWithError + "\n")
                    .append(error)
                    .toString();
            throw new SyntaxErrorException(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="OVERALL PROGRAM">
    // an entire program
    void P(ParseTreeNode ptn) throws SyntaxErrorException {
        S(ptn);
    }

    // for matching statements
    // here, we have to determine the type of statement 
    void S(ParseTreeNode ptn) throws SyntaxErrorException {
        ParseTreeNode curPtn = ptn.addChild("S");
        trace("S");
        // declaration
        if (lookAhead.equals("int") || lookAhead.equals("word") || lookAhead.equals("bool")) {
            D(curPtn);
        } // assignment
        else if (lookAhead.contains("id_")) {
            A(curPtn);
        } // conditional
        else if (lookAhead.equals("if")) {
            // I();
            int a;
        } // repeat
        else if (lookAhead.equals("repeat")) {
            // R();
            int b;
        } else if (lookAhead.equals("roar")) {
            // ROAR();
            int c;
        } else if (lookAhead.equals("nom")) {
            NOM(curPtn);
        } else {
            curPtn.addChild("!epsilon");
            return;
        }
        match("terminate", curPtn);
        S(curPtn);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="VARIABLE DECLERATION/ASSIGNMENT">
    void D(ParseTreeNode ptn) throws SyntaxErrorException {
        ParseTreeNode curPtn = ptn.addChild("D");
        trace("D");
        DT(curPtn);
        if (lookAhead.contains("id_")) {
            match(lookAhead, curPtn);
            match("assign_op", curPtn);
            ASSIGN(curPtn);
        } else {
            curPtn.addChild("!epsilon");
            return;
        }
    }

    void ASSIGN(ParseTreeNode ptn) throws SyntaxErrorException {
        ParseTreeNode curPtn = ptn.addChild("ASSIGN");
        trace("ASSIGN");

        // match literals
        if (lookAhead.contains("\"") || lookAhead.equals("true_bool") || lookAhead.equals("false_bool")) {
            match(lookAhead, curPtn);
        } // match input statement
        else if (lookAhead.equals("nom")) {
            NOM(curPtn);
        } // match operations
        else if (lookAhead.equals("add_op")) {
            ARITHMETIC_OPERATION(curPtn);
        } else {
            curPtn.addChild("!epsilon");
            return;
        }
    }

    void A(ParseTreeNode ptn) throws SyntaxErrorException {
        ParseTreeNode curPtn = ptn.addChild("A");
        trace("A");
        if (lookAhead.contains("id_")) {
            match(lookAhead, curPtn);
            ASSIGN(curPtn);
            if (lookAhead.equals("terminate")) {
                match("terminate", curPtn);
                System.out.print("Assignment recognized");
            } else {
                System.out.println("Invalid, not terminated");
                return;
            }
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="I/O STATEMENTS">
    void NOM(ParseTreeNode ptn) throws SyntaxErrorException {
        ParseTreeNode curPtn = ptn.addChild("NOM");
        trace("NOM");
        if (lookAhead.equals("nom")) {
            match("nom", curPtn);
            match("parenthesis_start", curPtn);
            match("parenthesis_end", curPtn);
        }
    }

    // </editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Arithmetic Operation">
    void ARITHMETIC_OPERATION(ParseTreeNode ptn) throws SyntaxErrorException {
        ParseTreeNode curPtn = ptn.addChild("ARITHMETIC_OPERATION");
        trace("ARITHMETIC_OPERATION");
        ARITHMETIC_TERM(curPtn);
        ARITHMETIC_OPERATION_(curPtn);
        System.out.println("Arithmetic operation recognized!");
    }

    void ARITHMETIC_OPERATION_(ParseTreeNode ptn) throws SyntaxErrorException {
        ParseTreeNode curPtn = ptn.addChild("ARITHMETIC_OPERATION_");
        trace("ARITHMETIC_OPERATION_");

        if (lookAhead.equals("add_op")) {
            match("add_op", curPtn);
            ARITHMETIC_TERM(curPtn);
        } else if (lookAhead.equals("sub_op")) {
            match("sub_op", curPtn);
            ARITHMETIC_TERM(curPtn);
        } else {
            curPtn.addChild("!epsilon");
            return;
        }
        ARITHMETIC_OPERATION_(curPtn);
    }

    void ARITHMETIC_TERM(ParseTreeNode ptn) throws SyntaxErrorException {
        ParseTreeNode curPtn = ptn.addChild("ARITHMETIC_TERM");
        trace("ARITHMETIC_TERM");
        ARITHMETIC_FACTOR(curPtn);
        ARITHMETIC_TERM_(curPtn);
    }

    void ARITHMETIC_TERM_(ParseTreeNode ptn) throws SyntaxErrorException {
        ParseTreeNode curPtn = ptn.addChild("ARITHMETIC_TERM_");
        trace("ARITHMETIC_TERM_");
        if (lookAhead.equals("mult_op")) {
            match("mult_op", curPtn);
            ARITHMETIC_FACTOR(curPtn);
        } else if (lookAhead.equals("div_op")) {
            match("div_op", curPtn);
            ARITHMETIC_FACTOR(curPtn);
        } else {
            curPtn.addChild("!epsilon");
            return;
        }
        ARITHMETIC_TERM_(curPtn);
    }

    void ARITHMETIC_FACTOR(ParseTreeNode ptn) throws SyntaxErrorException {
        ParseTreeNode curPtn = ptn.addChild("ARITHMETIC_FACTOR");
        trace("ARITHMETIC_FACTOR");
        if (isInt(lookAhead)) {
            match(lookAhead, curPtn);
        } else if (lookAhead.contains("id_")) {
            match(lookAhead, curPtn);
            OPTIONAL_OPERATOR(curPtn);
        } else if (lookAhead.matches("parenthesis_start")) {
            match("parenthesis_start", curPtn);
            ARITHMETIC_OPERATION(curPtn);
            match("parenthesis_end", curPtn);
        }
    }

    void OPTIONAL_OPERATOR(ParseTreeNode ptn) {
        ParseTreeNode curPtn = ptn.addChild("OPTIONAL_OPERATOR");
        trace("OPTIONAL_OPERATOR");
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Logical Operation">
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Relational Operation">
    //</editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DATATYPE MATCHING">
    void DT(ParseTreeNode ptn) throws SyntaxErrorException {
        ParseTreeNode curPtn = ptn.addChild("DT");
        trace("DT");
        if (lookAhead.equals("int")) {
            match("int", curPtn);
        } else if (lookAhead.equals("word")) {
            match("word", curPtn);
        } else if (lookAhead.equals("bool")) {
            match("bool", curPtn);
        }
    }
    // </editor-fold>
}

class SyntaxErrorException extends Exception {

    public SyntaxErrorException() {
        super();
    }

    public SyntaxErrorException(String message) {
        super(message);
    }
}
