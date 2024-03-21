/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rorlanguage;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Set;

/**
 *
 * @author Ivan
 */
public class SyntaxAnalyzer {

    private ArrayList<String> tokenList;
    private String lookAhead;
    private Queue<String> tokenQueue;

    public SyntaxAnalyzer(ArrayList<String> tokenList) {
        this.tokenQueue = new ArrayDeque<>(tokenList);
        this.lookAhead = tokenQueue.peek();
    }

    //<editor-fold defaultstate="collapsed" desc="Syntax Functions">
    boolean parse() {
        P();
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

    boolean match(String token) {
        trace("Matching: " + token + " With: ");
        if (lookAhead.equals(token) && !tokenQueue.isEmpty()) {
            lookAhead = tokenQueue.poll();
            return true;
        } else {
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
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="MAIN PROGRAM">
    // an entire program
    void P() {
        S();
    }

    // for matching statements
    // here, we have to determine the type of statement 
    void S() {
        trace("S");

        // declaration
        if (lookAhead.equals("int") || lookAhead.equals("word") || lookAhead.equals("bool")) {
            D();
        } // assignment
        else if (lookAhead.contains("id_")) {
            A();
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
            NOM();
            if (lookAhead.equals("terminate")) {
                match("terminate");
            }
        } else {
            return;
        }

        System.out.println("Statement recognized!");

        S();
    }
    // </editor-fold>

    // 
    void NOM() {
        trace("NOM");
        if (lookAhead.equals("nom")) {
            match("nom");
            if (lookAhead.equals("parenthesis_start")) {
                match("parenthesis_start");
                if (lookAhead.equals("parenthesis_end")) {
                    match("parenthesis_end");
                    System.out.println("Input statement recognized");
                }
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="VARIABLE DECLERATION/ASSIGNMENT">
    void D() {
        trace("D");
        DT();
        if (lookAhead.contains("id_")) {
            match(lookAhead);

            // allow the declaration to have an assignment  
            if (lookAhead.equals("assign_op")) {
                match("assign_op");
                ASSIGN();
            }

            // always terminate
            if (lookAhead.equals("terminate")) {
                match("terminate");
                System.out.print("Declaration recognized");
            } else {
                System.out.println("Invalid, not terminated");
                return;
            }
        } else {
            System.out.println("Invalid.");
            return;
        }
    }

    void ASSIGN() {
        trace("ASSIGN");

        // match literals
        if (lookAhead.contains("\"") || isInt(lookAhead) || lookAhead.equals("true_bool") || lookAhead.equals("false_bool")) {
            match(lookAhead);
        } // match input statement
        else if (lookAhead.equals("nom")) {
            NOM();
        } // match operations
        else if (lookAhead.equals("add_op")) {
            ARITHMETIC_OPERATION();
        } else {
            return;
        }
    }

    void A() {
        trace("A");
        if (lookAhead.contains("id_")) {
            match(lookAhead);
            ASSIGN();
            if (lookAhead.equals("terminate")) {
                match("terminate");
                System.out.print("Assignment recognized");
            } else {
                System.out.println("Invalid, not terminated");
                return;
            }
        }
    }

    // </editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Arithmetic Operation">
    void ARITHMETIC_OPERATION() {
        trace("ARITHMETIC_OPERATION");
        ARITHMETIC_TERM();
        ARITHMETIC_OPERATION_();
        System.out.println("Arithmetic operation recognized!");
    }

    void ARITHMETIC_OPERATION_() {
        trace("ARITHMETIC_OPERATION_");

        if (lookAhead.equals("add_op")) {
            match("add_op");
            ARITHMETIC_TERM();
        } else if (lookAhead.equals("sub_op")) {
            match("sub_op");
            ARITHMETIC_TERM();
        } else {
            return;
        }
        ARITHMETIC_OPERATION_();
    }

    void ARITHMETIC_TERM() {
        trace("ARITHMETIC_TERM");
        ARITHMETIC_FACTOR();
        ARITHMETIC_TERM_();
    }

    void ARITHMETIC_TERM_() {
        trace("ARITHMETIC_TERM_");
        if (lookAhead.equals("mult_op")) {
            match("mult_op");
            ARITHMETIC_FACTOR();
        } else if (lookAhead.equals("div_op")) {
            match("div_op");
            ARITHMETIC_FACTOR();
        } else {
            return;
        }
        ARITHMETIC_TERM_();
    }

    void ARITHMETIC_FACTOR() {
        trace("ARITHMETIC_FACTOR");
        if (isInt(lookAhead)) {
            match(lookAhead);
        } else if (lookAhead.contains("id_")) {
            match(lookAhead);
            OPTIONAL_OPERATOR();
        } else if (lookAhead.matches("parenthesis_start")) {
            match("parenthesis_start");
            ARITHMETIC_OPERATION();
            match("parenthesis_end");
        }
    }

    void OPTIONAL_OPERATOR() {
        trace("OPTIONAL_OPERATOR");
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DATATYPE MATCHING">
    void DT() {
        trace("DT");
        if (lookAhead.equals("int")) {
            match("int");
        } else if (lookAhead.equals("word")) {
            match("word");
        } else if (lookAhead.equals("bool")) {
            match("bool");
        }
    }
    // </editor-fold>
}
