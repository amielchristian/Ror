/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rorlanguage;

import java.util.ArrayList;

/**
 *
 * @author Ivan
 */
public class SyntaxAnalyzer {
    private ArrayList<String> tokenList;
    private String lookAhead;
    private int ptr;
    
    public SyntaxAnalyzer(ArrayList<String> tokenList) {
        this.tokenList = tokenList;
        this.lookAhead = tokenList.get(0);
        this.ptr = 0;
    }
    boolean parse() {
        AO();
        if (ptr+1 == tokenList.size()) {
            System.out.println("SUCCESS! Token List is empty");
            return true;
        } else {
            return false;
        }
    }

    void trace(String location) {
        System.out.println(location + ": " + lookAhead);
    }
    
    //    <arithmetic_operation> => <arithmetic_term> {(<add_op> | sub_op>) <arithmetic_term>}
    //    <arithmetic_term> => <arithmetic_factor> { (<mul_op> | <div_op> | <mod_op>) <arithmetic_factor> }
    //    <arithmetic_factor> => <int_literal> | <variable> [increment_operator | <decrement_operator>] | "(" <arithmetic_operation> ")"
    void AO() {
        trace("AO");
        AT();
        AO_();
    }
    
    void AO_() {
        trace("AO_");
        
        if (lookAhead.equals("add_op")) {
            match("add_op");
            AT();
        } else if (lookAhead.equals("sub_op")) {
            match("sub_op");
            AT();
        } else {
            return;
        }
        AO_();
    }
    void AT() {
        trace("AT");
        AF();
        AT_();
    }
    
    void AT_() {
        trace("AT_");
        if (lookAhead.equals("mult_op")) {
            match("mult_op");
            AF();
        } else if (lookAhead.equals("div_op")) {
            match("div_op");
            AF();
        } else {
            return;
        }
        AT_();
    }
    
    void AF() {
        trace("AF");
        if (lookAhead.equals("num_lit")) {
            match("num_lit");
        } else if (lookAhead.equals("identifier")) {
            match("identifier");
        } else if (lookAhead.matches("parenthesis_start")) {
            match("parenthesis_start");
            AO();
            match("parenthesis_end");
        }
    }
    
    
    boolean match(String token) {
        trace("Matching: " + token + " With: ");
        if (lookAhead.equals(token) && (ptr+1 < tokenList.size())) {
            lookAhead = tokenList.get(++ptr);
            return true;
        } else {
            return false;
        }
    }
}
