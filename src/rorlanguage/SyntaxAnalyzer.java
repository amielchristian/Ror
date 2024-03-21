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
        this.lineTraceback = lr.lineTraceback;
        this.lookAhead = tokenQueue.poll();
        this.lineTracebackPointer = 0;
        this.ptn = new ParseTreeNode("P");
    }

    //<editor-fold defaultstate="collapsed" desc="Syntax Analyzer Func tions">
    boolean parse() {
        try {
            ptn = new ParseTreeNode("<P>");
            P(ptn);
            System.out.println(ptn);
        } catch (SyntaxErrorException see) {
            see.printStackTrace();
            return false;
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
//        System.out.println("MATCHING: " + token + " WITH lookAhead: " + lookAhead);
        if (lookAhead.equals(token) && !tokenQueue.isEmpty()) {
            ptn.addNonTermChild(token);
            lookAhead = tokenQueue.poll();
            this.lineTracebackPointer++;
            return true;
        } else if (lookAhead.equals(token) && tokenQueue.isEmpty()) {
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
        } catch (Exception e) {
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
                    .append("Error occured at line " + line + 1 + ": " + lineWithError + "\n")
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
        try {
            S(ptn);
        } catch (SyntaxErrorException see) {
            throw new SyntaxErrorException(see.getMessage());
        }

    }

    // for matching statements
    // here, we have to determine the type of statement 
    void S(ParseTreeNode ptn) throws SyntaxErrorException {
        try {
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
                CONDITIONAL_STATEMENT(curPtn);
            } // repeat
            else if (lookAhead.equals("repeat")) {
                // R();
            } else if (lookAhead.equals("roar")) {
                ROAR(curPtn);
            } else if (lookAhead.equals("nom")) {
                NOM(curPtn);
            } else {
                curPtn.addChild("!epsilon");
                return;
            }
            if (lineTracebackPointer == lineTraceback.size() && lookAhead.equals("bracket_end")) {
                match("bracket_end", curPtn);
            } else {
                match("terminate", curPtn);
            } 
            
            S(curPtn);
        } catch (SyntaxErrorException see) {
            throw new SyntaxErrorException(see.getMessage());
        }

    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="VARIABLE DECLERATION/ASSIGNMENT">
    void D(ParseTreeNode ptn) throws SyntaxErrorException {
        try {
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
        } catch (SyntaxErrorException see) {
            throw new SyntaxErrorException(see.getMessage());
        }

    }

    void ASSIGN(ParseTreeNode ptn) throws SyntaxErrorException {
        try {
            ParseTreeNode curPtn = ptn.addChild("ASSIGN");
            trace("ASSIGN");

            // match literals
            if (lookAhead.contains("\"") || lookAhead.equals("true_bool") || lookAhead.equals("false_bool")) {
                match(lookAhead, curPtn);
            } // match input statement
            else if (lookAhead.equals("nom")) {
                NOM(curPtn);
            } // match operations
            else if (lookAhead.contains("id_") || isInt(lookAhead) || lookAhead.contains("parenthesis_start")) {
                ARITHMETIC_OPERATION(curPtn);
            } else {
                curPtn.addChild("!epsilon");
                return;
            }
        } catch (SyntaxErrorException see) {
            throw new SyntaxErrorException(see.getMessage());
        }

    }

    void A(ParseTreeNode ptn) throws SyntaxErrorException {
        try {
            ParseTreeNode curPtn = ptn.addChild("A");
            trace("A");
            if (lookAhead.contains("id_")) {
                match(lookAhead, curPtn);
                match("assign_op", curPtn);
                ASSIGN(curPtn);
            }
        } catch (SyntaxErrorException see) {
            throw new SyntaxErrorException(see.getMessage());
        }

    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="I/O STATEMENTS">
    void NOM(ParseTreeNode ptn) throws SyntaxErrorException {
        try {
            ParseTreeNode curPtn = ptn.addChild("NOM");
            trace("NOM");
            if (lookAhead.equals("nom")) {
                match("nom", curPtn);
                match("parenthesis_start", curPtn);
                match("parenthesis_end", curPtn);
            }
        } catch (SyntaxErrorException see) {
            throw new SyntaxErrorException(see.getMessage());
        }

    }

    void ROAR(ParseTreeNode ptn) throws SyntaxErrorException {
        try {
            ParseTreeNode curPtn = ptn.addChild("ROAR");
            trace("ROAR");
            if (lookAhead.equals("roar")) {
                match("roar", curPtn);
                match("parenthesis_start", curPtn);
                if (lookAhead.contains("\"")) {
                    match(lookAhead, curPtn);
                }
                match("parenthesis_end", curPtn);
            }
        } catch (SyntaxErrorException see) {
            throw new SyntaxErrorException(see.getMessage());
        }

    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="CONDITIONAL STATEMENT">
    void CONDITION(ParseTreeNode ptn) throws SyntaxErrorException {
        ParseTreeNode curPtn = ptn.addChild("CONDITION");
        trace("CONDITION");

        if (lookAhead.equals("or") || lookAhead.equals("and") || lookAhead.equals("not") || lookAhead.equals("parenthesis_start")) {
            LOGICAL_OPERATION__(curPtn);
        } else if (lookAhead.equals("equal_rel") || lookAhead.equals("not_equal_rel") || lookAhead.equals("lt") || lookAhead.equals("lte") || lookAhead.equals("gt") || lookAhead.equals("gte")) {
            RELATIONAL_OPERATION(curPtn);
        } else {
            return;
        }

    }

    void CONDITIONAL_STATEMENT(ParseTreeNode ptn) throws SyntaxErrorException {
        ParseTreeNode curPtn = ptn.addChild("CONDITIONAL_STATEMENT");
        trace("CONDITIONAL_STATEMENT");
        CONDITIONAL_STATEMENT_(curPtn);
        System.out.println("Conditional Statement Recognized!");

    }

    void CONDITIONAL_STATEMENT_(ParseTreeNode ptn) throws SyntaxErrorException {
        ParseTreeNode curPtn = ptn.addChild("CONDITIONAL_STATEMENT_");
        trace("CONDITIONAL_STATEMENT_");
        IF_STATEMENT(curPtn);
        ELSE_IF_STATEMENT_LOOP(curPtn);

    }

    void ELSE_STATEMENT(ParseTreeNode ptn) throws SyntaxErrorException {
        ParseTreeNode curPtn = ptn.addChild("ELSE_STATEMENT");
        trace("ELSE_STATEMENT");

        STATEMENT_LOOP(curPtn);
    }

    void IF_STATEMENT(ParseTreeNode ptn) throws SyntaxErrorException {
        ParseTreeNode curPtn = ptn.addChild("IF_STATEMENT");
        trace("IF_STATEMENT");
        if (lookAhead.equals("if")) {
            match("if", curPtn);
            match("parenthesis_start", curPtn);
            if (lookAhead.equals("parenthesis_start")) {
                ARITHMETIC_OPERATION(curPtn);
            } else if (lookAhead.contains("id_")) {
                match(lookAhead, curPtn);
            } else if (isInt(lookAhead)) {
                match(lookAhead, curPtn);
            }
            CONDITION(curPtn);
            match("parenthesis_end", curPtn);
            match("bracket_start", curPtn);
            STATEMENT_LOOP(curPtn);

        }
    }

    void ELSE_IF_STATEMENT_LOOP(ParseTreeNode ptn) throws SyntaxErrorException {
        ParseTreeNode curPtn = ptn.addChild("ELSE_IF_STATEMENT_LOOP");
        trace("ELSE_IF_STATEMENT_LOOP");
        if (lookAhead.equals("else")) {
            match("else", curPtn);
            if (lookAhead.equals("if")) {
                IF_STATEMENT(curPtn);
            } else if (lookAhead.equals("bracket_start")) {
                match("bracket_start", curPtn);
                ELSE_STATEMENT(curPtn);
            }
            ELSE_IF_STATEMENT_LOOP(curPtn);
        } else {
            return;
        }
    }

    void STATEMENT_LOOP(ParseTreeNode ptn) throws SyntaxErrorException {
        ParseTreeNode curPtn = ptn.addChild("STATEMENT_LOOP");
        trace("STATEMENT_LOOP");
        if (lookAhead.equals("bracket_end")) {
            match("bracket_end", curPtn);
        } else {
            S(curPtn);
            STATEMENT_LOOP(curPtn);
        }
    }

    // </editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Arithmetic Operation">
    void ARITHMETIC_OPERATION(ParseTreeNode ptn) throws SyntaxErrorException {
        try {
            ParseTreeNode curPtn = ptn.addChild("ARITHMETIC_OPERATION");
            trace("ARITHMETIC_OPERATION");
            ARITHMETIC_TERM(curPtn);
            ARITHMETIC_OPERATION_(curPtn);
        } catch (SyntaxErrorException see) {
            throw new SyntaxErrorException(see.getMessage());
        }

    }

    void ARITHMETIC_OPERATION_(ParseTreeNode ptn) throws SyntaxErrorException {
        try {
            ParseTreeNode curPtn = ptn.addChild("ARITHMETIC_OPERATION_");
            trace("ARITHMETIC_OPERATION_");

            if (lookAhead.equals("add_op")) {
                match("add_op", curPtn);
                if (!(lookAhead.contains("id_") || isInt(lookAhead) || lookAhead.contains("parenthesis_start"))) {
                    handleError("Expected: num_lit/identifier/parenthesis_start\nBut got: " + lookAhead);
                }
                ARITHMETIC_TERM(curPtn);
            } else if (lookAhead.equals("sub_op")) {
                match("sub_op", curPtn);
                if (!(lookAhead.contains("id_") || isInt(lookAhead) || lookAhead.contains("parenthesis_start"))) {
                    handleError("Expected: num_lit/identifier/parenthesis_start\nBut got: " + lookAhead);
                }
                ARITHMETIC_TERM(curPtn);
            } else {
                curPtn.addChild("!epsilon");
                return;
            }
            ARITHMETIC_OPERATION_(curPtn);
        } catch (SyntaxErrorException see) {
            throw new SyntaxErrorException(see.getMessage());
        }

    }

    void ARITHMETIC_TERM(ParseTreeNode ptn) throws SyntaxErrorException {
        try {
            ParseTreeNode curPtn = ptn.addChild("ARITHMETIC_TERM");
            trace("ARITHMETIC_TERM");
            ARITHMETIC_FACTOR(curPtn);
            ARITHMETIC_TERM_(curPtn);
        } catch (SyntaxErrorException see) {
            throw new SyntaxErrorException(see.getMessage());
        }

    }

    void ARITHMETIC_TERM_(ParseTreeNode ptn) throws SyntaxErrorException {
        try {
            ParseTreeNode curPtn = ptn.addChild("ARITHMETIC_TERM_");
            trace("ARITHMETIC_TERM_");
            if (lookAhead.equals("mult_op")) {
                match("mult_op", curPtn);
                if (!(lookAhead.contains("id_") || isInt(lookAhead) || lookAhead.contains("parenthesis_start"))) {
                    handleError("Expected: num_lit/identifier/parenthesis_start\nBut got: " + lookAhead);
                }
                ARITHMETIC_FACTOR(curPtn);
            } else if (lookAhead.equals("div_op")) {
                match("div_op", curPtn);
                if (!(lookAhead.contains("id_") || isInt(lookAhead) || lookAhead.contains("parenthesis_start"))) {
                    handleError("Expected: num_lit/identifier/parenthesis_start\nBut got: " + lookAhead);
                }
                ARITHMETIC_FACTOR(curPtn);
            } else if (lookAhead.equals("modulo_op")) {
                match("modulo_op", curPtn);
                if (!(lookAhead.contains("id_") || isInt(lookAhead) || lookAhead.contains("parenthesis_start"))) {
                    handleError("Expected: num_lit/identifier/parenthesis_start\nBut got: " + lookAhead);
                }
                ARITHMETIC_FACTOR(curPtn);
            } else {
                curPtn.addChild("!epsilon");
                return;
            }
            ARITHMETIC_TERM_(curPtn);
        } catch (SyntaxErrorException see) {
            throw new SyntaxErrorException(see.getMessage());
        }

    }

    void ARITHMETIC_FACTOR(ParseTreeNode ptn) throws SyntaxErrorException {
        try {
            ParseTreeNode curPtn = ptn.addChild("ARITHMETIC_FACTOR");
            trace("ARITHMETIC_FACTOR");
            if (isInt(lookAhead)) {
                match(lookAhead, curPtn);
            } else if (lookAhead.contains("id_")) {
                match(lookAhead, curPtn);
                if (lookAhead.equals("increment_op") || lookAhead.equals("decrement_op")) {
                    OPTIONAL_OPERATOR(curPtn);
                }

            } else if (lookAhead.matches("parenthesis_start")) {
                match("parenthesis_start", curPtn);
                ARITHMETIC_OPERATION(curPtn);
                match("parenthesis_end", curPtn);
            } else {
                handleError("Invalid Empty Parenthesis");
            }
        } catch (SyntaxErrorException see) {
            throw new SyntaxErrorException(see.getMessage());
        }

    }

    void OPTIONAL_OPERATOR(ParseTreeNode ptn) throws SyntaxErrorException {
        try {
            ParseTreeNode curPtn = ptn.addChild("OPTIONAL_OPERATOR");
            trace("OPTIONAL_OPERATOR");
            match(lookAhead, curPtn);
        } catch (SyntaxErrorException see) {
            throw new SyntaxErrorException(see.getMessage());
        }

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Logical Operation">
    void LOGICAL_OPERATION(ParseTreeNode ptn) throws SyntaxErrorException {
        ParseTreeNode curPtn = ptn.addChild("LOGICAL_OPERATION");
        trace("LO"); // pang track lang kung nasaang function
        LOGICAL_TERM(curPtn); // kapag non-terminal
        LOGICAL_OPERATION_(curPtn);
        System.out.println("Logical operation recognized!");

    }

    void LOGICAL_OPERATION_(ParseTreeNode ptn) throws SyntaxErrorException {
        ParseTreeNode curPtn = ptn.addChild("LOGICAL_OPERATION");
        trace("LO_");
        if (lookAhead.equals("or")) {
            match("or", curPtn);
        } else {
            curPtn.addChild("!epsilon");
            return;
        }

        LOGICAL_TERM(curPtn);
        LOGICAL_OPERATION_(curPtn);
    }
    
    void LOGICAL_OPERATION__(ParseTreeNode ptn) throws SyntaxErrorException {
        ParseTreeNode curPtn = ptn.addChild("LOGICAL_OPERATION");
        trace("LO_");
        if (lookAhead.equals("or")) {
            match("or", curPtn);
            LOGICAL_TERM(curPtn);
        } else {
            match("and", curPtn);
            LOGICAL_FACTOR(curPtn);
        }

        
        LOGICAL_OPERATION_(curPtn);
    }

    void LOGICAL_TERM(ParseTreeNode ptn) throws SyntaxErrorException {
        ParseTreeNode curPtn = ptn.addChild("LOGICAL_OPERATION");
        trace("LT");
        LOGICAL_FACTOR(curPtn);
        LOGICAL_TERM_(curPtn);
    }
    
    void LOGICAL_TERM_(ParseTreeNode ptn) throws SyntaxErrorException {
        ParseTreeNode curPtn = ptn.addChild("LOGICAL_OPERATION");
        trace("LT_");
        if (lookAhead.equals("and")) {
            match("and", curPtn);
        } else {
            return;
        }

        LOGICAL_FACTOR(curPtn);
        LOGICAL_TERM_(curPtn);
    }
    
    void LOGICAL_FACTOR(ParseTreeNode ptn) throws SyntaxErrorException {
        ParseTreeNode curPtn = ptn.addChild("LOGICAL_OPERATION");
        trace("LF");
        if (lookAhead.equals("not")) {
            match("not", curPtn);
        }
        LOGICAL_FACTOR_(curPtn);

    }
    

    void LOGICAL_FACTOR_(ParseTreeNode ptn) throws SyntaxErrorException {
        ParseTreeNode curPtn = ptn.addChild("LOGICAL_OPERATION");
        trace("LF_");
        if (lookAhead.equals("parenthesis_start")) {
            match("parenthesis_start", curPtn);
            LOGICAL_OPERATION(curPtn);
            if (lookAhead.equals("parenthesis_end")) {
                match("parenthesis_end", curPtn);
            }
        } else if (lookAhead.contains("id_")) {
            match(lookAhead, curPtn);
        } else if (lookAhead.equals("true_bool")) {
            match("true_bool", curPtn);
        } else if (lookAhead.equals("false_bool")) {
            match("false_bool", curPtn);
        } else {
            RELATIONAL_OPERATION(curPtn);
        }
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Relational Operation">
    void RELATIONAL_OPERATION(ParseTreeNode ptn) throws SyntaxErrorException {
        ParseTreeNode curPtn = ptn.addChild("RELATIONAL_OPERATION");
        trace("RELATIONAL_OPERATION");
        RELATIONAL_OPERATORS(curPtn);
        if (lookAhead.equals("parenthesis_start")) {
            ARITHMETIC_OPERATION(curPtn);
        } else if (lookAhead.contains("id_")) {
            match(lookAhead, curPtn);
        } else if (isInt(lookAhead)) {
            match(lookAhead, curPtn);
        }
        System.out.println("Relational operation recognized!");
    }

    void RELATIONAL_OPERATORS(ParseTreeNode ptn) throws SyntaxErrorException {
        ParseTreeNode curPtn = ptn.addChild("RELATIONAL_OPERATORS");
        trace("RELATIONAL_OPERATORS");
        if (lookAhead.equals("equal_rel")) {
            match(lookAhead, curPtn);
        } else if (lookAhead.equals("not_equal_rel")) {
            match(lookAhead, curPtn);
        }
        if (lookAhead.equals("lt")) {
            match(lookAhead, curPtn);
        }
        if (lookAhead.equals("lte")) {
            match(lookAhead, curPtn);
        }
        if (lookAhead.equals("gt")) {
            match(lookAhead, curPtn);
        }
        if (lookAhead.equals("gte")) {
            match(lookAhead, curPtn);
        }
        ARITHMETIC_OPERATION(curPtn);
    }

    //</editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DATATYPE MATCHING">
    void DT(ParseTreeNode ptn) throws SyntaxErrorException {
        try {
            ParseTreeNode curPtn = ptn.addChild("DT");
            trace("DT");
            if (lookAhead.equals("int")) {
                match("int", curPtn);
            } else if (lookAhead.equals("word")) {
                match("word", curPtn);
            } else if (lookAhead.equals("bool")) {
                match("bool", curPtn);
            }
        } catch (SyntaxErrorException see) {
            throw new SyntaxErrorException(see.getMessage());

        }

    }
    // </editor-fold>
}

class SyntaxErrorException extends Throwable {

    public SyntaxErrorException() {
        super();
    }

    public SyntaxErrorException(String message) {
        super(message);
    }
}
