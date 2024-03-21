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
        //AO();
        P();
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
    
    boolean match(String token) {
        trace("Matching: " + token + " With: ");
        if (lookAhead.equals(token) && (ptr+1 < tokenList.size())) {
            lookAhead = tokenList.get(++ptr);
            return true;
        } else    {
            return false;
        }
    }
    
    boolean isInt(String token) {
        try {
            Integer.valueOf(token);
        }
        catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    // an entire program
    void P()    {
        S();
    }
    
    // for matching statements
    // here, we have to determine the type of statement 
    void S()    {
        trace("S");
        
        // declaration
        if (lookAhead.equals("int") || lookAhead.equals("word") || lookAhead.equals("bool"))    {
            D();
        }
        // assignment
        else if (lookAhead.contains("id_"))  {
            A();
        }
        // conditional
        else if (lookAhead.equals("if"))    {
            // I();
            int a;
        }
        // repeat
        else if (lookAhead.equals("repeat"))    {
            // R();
            int b;
        }
        else if (lookAhead.equals("roar"))  {
            // ROAR();
            int c;
        }
        else if (lookAhead.equals("nom"))   {
            NOM();
            if (lookAhead.equals("terminate"))  {
                match("terminate");
            }
        }
        else    {
            return;
        }
        
        System.out.println("Statement recognized!");
        
        S();
    }
    
    void NOM()  {
        trace("NOM");
        if (lookAhead.equals("nom"))  {
            match("nom");
            if (lookAhead.equals("parenthesis_start")) {
                match("parenthesis_start");
                if (lookAhead.equals("parenthesis_end"))    {
                    match("parenthesis_end");
                    System.out.println("Input statement recognized");
                }
            }
        }
    }

    // declaration/assignment statement
    // modified: assignment can't have datatype
    // <declaration> => <datatype> <identifier> [ assign_op (<literal> | <operation> | <input>) ] terminate
    // <assignment> => <identifier> assign_op (<literal> | <operation> | <input>) terminate
    void D()    {
        trace("D");
        DT();
        if (lookAhead.contains("id_"))  {
            match(lookAhead);
            
            // allow the declaration to have an assignment  
            if (lookAhead.equals("assign_op"))  {
                ASSIGN();
            }
            
            // always terminate
            if (lookAhead.equals("terminate"))  {
                match("terminate");
                System.out.print("Declaration recognized");
            }
            else    {
                System.out.println("Invalid, not terminated");
                return;
            }
        }
        else    {
            System.out.println("Invalid.");
            return;
        }
    }
    void A()    {
        trace("A");
        if (lookAhead.contains("id_"))  {
            match(lookAhead);
            ASSIGN();
            if (lookAhead.equals("terminate"))  {
                match("terminate");
                System.out.print("Assignment recognized");
            }
            else    {
                System.out.println("Invalid, not terminated");
                return;
            }
        }
    }
    // "=" ( num_lit | string_lit | bool_lit | identifier | input_stmt )
    void ASSIGN()   {
        trace("ASSIGN");
        if (lookAhead.equals("assign_op"))   {
            match("assign_op");
            
            // match literals
            if (lookAhead.contains("\"") || isInt(lookAhead) || lookAhead.equals("true_bool") || lookAhead.equals("false_bool"))    {
                match(lookAhead);
            }
            // match input statement
            else if (lookAhead.equals("nom"))   {
                NOM();
            }
            // match operations
            else    {
                return;
            }
        }
        else    {
            return;
        }
    }
    
    // pano makakadistinguish between boolean and arithmetic?
    // gawin ko arithmetic muna
    void O()    {
        trace("O");
        AO();
    }
    
    //    <arithmetic_operation> => <arithmetic_term> {(<add_op> | sub_op>) <arithmetic_term>}
    //    <arithmetic_term> => <arithmetic_factor> { (<mul_op> | <div_op> | <mod_op>) <arithmetic_factor> }
    //    <arithmetic_factor> => <int_literal> | <variable> [increment_operator | <decrement_operator>] | "(" <arithmetic_operation> ")"
    // AO, AO_, AT, AT_, and AF are for matching arithmetic operations
    void AO() {
        trace("AO");
        AT();
        AO_();
        System.out.println("Arithmetic operation recognized!");
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
        if (isInt(lookAhead)) {
            match(lookAhead);
        } else if (lookAhead.contains("id_")) {
            match(lookAhead);
        } else if (lookAhead.matches("parenthesis_start")) {
            match("parenthesis_start");
            AO();
            match("parenthesis_end");
        }
    }
    
    // for matching datatypes
    void DT()   {
        trace("DT");
        if (lookAhead.equals("int"))    {
            match("int");
        }
        else if (lookAhead.equals("word"))  {
            match("word");
        }
        else if (lookAhead.equals("bool"))  {
            match("bool");
        }
    }
}
