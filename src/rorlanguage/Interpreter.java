/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rorlanguage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Stack;
import modules.ParseTreeNode;
import modules.SymbolTable;

/**
 *
 * @author Ivan
 */
public class Interpreter {

    private SymbolTable st;
    private ArrayList<String> tokens;
    private int ptr;
    private ParseTreeNode ptn;
    private int index;
    private ArrayList<ParseTreeNode> aops;

    public Interpreter(ParseTreeNode ptn, SymbolTable st) {
        this.st = st;
        tokens = new ArrayList<String>();
        aops = new ArrayList<ParseTreeNode>();
        ptr = 0;
        this.ptn = ptn;
        this.index = 0;
        traverse(this.ptn);
    }

    private void traverse(ParseTreeNode ptn) {
        ptn.index = index;
        index++;
        if (ptn == null) {
            return;
        }

        if (!ptn.name.equals("<S>") && !ptn.name.equals("<ARITHMETIC_OPERATION>")) {
            tokens.add(ptn.name);
        } else if (ptn.name.equals("<ARITHMETIC_OPERATION>")) {
            System.out.println(ptn);
            tokens.add("aop_" + aops.size());
            ParseTreeNode ptnCopy = new ParseTreeNode("<ARITHMETIC_OPERATION>");
            ptnCopy.setChildren(ptn.getChildren());
            aops.add(ptnCopy);
            ptn.removeChildren();
        }
        for (ParseTreeNode child : ptn.getChildren()) {
            traverse(child);
        }
    }

    public void run() {
        System.out.println("INTERPRETER -------------------------------");
        while (ptr < tokens.size() - 1) {
            if (match("<P>")) {
            } else if (match("<D>")) {
                declare();
            } else if (match("<ROAR>")) {
                roar();
            } else if (match("<A>")) {
                assign();
            } else {
                ptr++;
            }
        }
    }

    // reminder to me (amiel):
    // these are the only valid assignment/declaration ops:
    // Int x = 10;
    // x = 5;
    private void declare() {
        // TODO: Check if declared in symbol table
        match("<DT>");
        String datatype = tokens.get(ptr++);
        String identifier = tokens.get(ptr++);
        ptr++; // skips assign_op
        Object value = null;
        ptr++; // skips <ASSIGN>

        // match literals, input statements, and operations
        if (match("<NOM>")) {
            value = nom();
        } else if (tokens.get(ptr).startsWith("aop_")) {
            value = arithmeticOp(tokens.get(ptr));
            ptr++;
        } else if (match("")) {

        }
        st.setTokenDatatype(identifier, datatype);
        st.updateTokenValue(identifier, value);
        ptr++; // skips terminate (?)
    }

    // TODO
    // gusto ko mamaya gawing same logic lang yung declaration and assignment kasi datatype lang naman pinagkaiba nila
    private void assign() {
        String identifier = tokens.get(ptr++);
        ptr++; // skips assign_op
        Object value = null;
        ptr++; // skips <ASSIGN>
        if (match("<NOM>")) {
            value = nom();
        }
        st.updateTokenValue(identifier, value);
        ptr++;
    }

    // DONE
    private String nom() {
        ptr += 3;
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

    // i think dapat magdagdag tayo ng pwedeng iprint :<
    private void roar() {
        ptr += 2;
        if (tokens.get(ptr).startsWith("id_")) {
            System.out.print(st.getTokenValue(tokens.get(ptr), "value"));
            ptr++;
        } else if (tokens.get(ptr).contains("\"")) {
            System.out.print(tokens.get(ptr));
        }
        ptr += 2;
    }

    // 1. get parse tree of arithmetic operation
    private int arithmeticOp(String aop) {
        int result = 0;
        ParseTreeNode arithmeticTree = aops.get(Integer.parseInt(aop.substring(4)));
//        System.out.println(arithmeticTree);
//        result = compute(arithmeticTree);
        AOPReconstructor aopr = new AOPReconstructor(arithmeticTree);

//        System.out.println(aopr.getAOPArray());
//        System.out.println(result);
        return result;
    }

    boolean match(String token) {
        if (tokens.get(ptr).equals(token)) {
//            System.out.println("MATCHING: " + token + " WITH: " + tokens.get(ptr));
            ptr++;
            return true;
        } else {
            return false;
        }
    }
}

class AOPReconstructor {

    private ArrayList<String> res;
    private String infix;
    public AOPReconstructor(ParseTreeNode ptn) {
        this.res = new ArrayList<String>();
        traverse(ptn);
        infix = "";
        for (String str : res) {
            
            switch (str) {
                case "add_op":
                    infix = infix + "+ ";
                    break;
                case "sub_op":
                    infix = infix + "- ";
                    break;
                case "mult_op":
                    infix = infix + "* ";
                    break;
                case "div_op":
                    infix = infix + "/ ";
                    break;
                case "parenthesis_start":
                    infix = infix + "( ";
                    break;
                case "parenthesis_end":
                    infix = infix + ") ";
                    break;
                default:
                    infix = infix + str  + " ";
                    break;
            }
        }
        
        System.out.println(infixToPostfix(infix));
    }

    private void traverse(ParseTreeNode ptn) {
        if (ptn == null) {
            return;
        }

        String[] nonOps = {
            "<ARITHMETIC_OPERATION>",
            "<ARITHMETIC_OPERATION_>",
            "<ARITHMETIC_TERM>",
            "<ARITHMETIC_TERM_>",
            "<ARITHMETIC_FACTOR>",
            "<!epsilon>",};

        if (!Arrays.stream(nonOps).anyMatch(ptn.name::contains)) {
            res.add(ptn.name);
        }

        for (ParseTreeNode child : ptn.getChildren()) {
            traverse(child);
        }
    }

    public ArrayList<String> getAOPArray() {
        return res;
    }
    
    static int prec(char c) {
        if (c == '^')
            return 3;
        else if (c == '/' || c == '*')
            return 2;
        else if (c == '+' || c == '-')
            return 1;
        else
            return -1;
    }
 
    // Function to return associativity of operators
    static char associativity(char c) {
        if (c == '^')
            return 'R';
        return 'L'; // Default to left-associative
    }
 
    // The main function to convert infix expression to postfix expression
    static String infixToPostfix(String s) {
        StringBuilder result = new StringBuilder();
        Stack<Character> stack = new Stack<>();
 
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
 
            // If the scanned character is an operand, add it to the output string.
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
                result.append(c);
            }
            // If the scanned character is an ?(?, push it to the stack.
            else if (c == '(') {
                stack.push(c);
            }
            // If the scanned character is an ?)?, pop and add to the output string from the stack
            // until an ?(? is encountered.
            else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    result.append(stack.pop());
                }
                stack.pop(); // Pop '('
            }
            // If an operator is scanned
            else {
                while (!stack.isEmpty() && (prec(s.charAt(i)) < prec(stack.peek()) ||
                                             prec(s.charAt(i)) == prec(stack.peek()) &&
                                                 associativity(s.charAt(i)) == 'L')) {
                    result.append(stack.pop());
                }
                stack.push(c);
            }
        }
 
        // Pop all the remaining elements from the stack
        while (!stack.isEmpty()) {
            result.append(stack.pop());
        }
        
        return result.toString();
    }

}
