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
        try {
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
        } catch (RuntimeErrorException ree) {
            ree.printStackTrace();
        }
    }

    // DONE
    private void declare() throws RuntimeErrorException {
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
            value = arithmeticOp(tokens.get(ptr), identifier);
            ptr++;
        } else if (tokens.get(ptr).contains("\"")) {
            value = tokens.get(ptr);
        } else {
            value = tokens.get(ptr);
        }

        // TYPE CHECKING
        typeCheck(datatype, value, identifier.substring(3));

        st.setTokenDatatype(identifier, datatype);

        st.updateTokenValue(identifier, value);
        ptr++; // skips terminate (?)
    }

    // DONE
    private void assign() throws RuntimeErrorException {
        String identifier = tokens.get(ptr++);
        ptr++; // skips assign_op
        Object value = null;
        ptr++; // skips <ASSIGN>

        // match literals, input statements, and operations
        if (match("<NOM>")) {
            value = nom();
        } else if (tokens.get(ptr).startsWith("aop_")) {
            value = arithmeticOp(tokens.get(ptr), identifier);
            ptr++;
        } else if (tokens.get(ptr).contains("\"") || tokens.get(ptr).equals("True") || tokens.get(ptr).equals("False")) {
            value = tokens.get(ptr);
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

    // DONE
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

    // DONE
    private int arithmeticOp(String aop, String literal) throws RuntimeErrorException {
        int result = 0;
        ParseTreeNode arithmeticTree = aops.get(Integer.parseInt(aop.substring(4)));
        AOPReconstructor aopr = new AOPReconstructor(arithmeticTree);
        ArrayList<String> postfix = aopr.getPostFix();
        Stack<Integer> stk = new Stack<>();
        for (String s : postfix) {
            if (Character.isDigit(s.charAt(0))) {
                stk.push(Integer.parseInt(s));
            } else if (s.startsWith("id_")) {
                if (!st.checkInitialization(s)) {
                    referenceError(s.substring(3));
                }
                Object val = st.getTokenValue(s, "value");
                typeCheck("int", val, literal.substring(3));
                stk.push((Integer) val);
            } else {
                int val1 = stk.pop();
                int val2 = stk.pop();
                switch (s.charAt(0)) {
                    case '+':
                        stk.push(val2 + val1);
                        break;
                    case '-':
                        stk.push(val2 - val1);
                        break;
                    case '/':
                        stk.push(val2 / val1);
                        break;
                    case '*':
                        stk.push(val2 * val1);
                        break;
                }
            }
        }

        return stk.pop();
    }

    private void logicalOp() {

    }

    private void loop() {

    }

    private void conditional() {

    }

    private boolean match(String token) {
        if (tokens.get(ptr).equals(token)) {
//            System.out.println("MATCHING: " + token + " WITH: " + tokens.get(ptr));
            ptr++;
            return true;
        } else {
            return false;

        }
    }

    private void typeCheck(String datatype, Object value, String literal) throws RuntimeErrorException {

        if (datatype.equals("word")) {
            return;
        }

        String valueType = "word";

        if (value instanceof Integer) {
            valueType = "Int";
        }

        if (value.equals("true_bool")
                || value.equals("false_bool")) {
            valueType = "Bool";
        }
        if (datatype.equals(valueType.toLowerCase())) {
            return;
        } else {
            String message = "Type Mismatch, type: "
                    + valueType
                    + " is incompatible with datatype: "
                    + datatype
                    + " literal: "
                    + literal;

            throw new RuntimeErrorException(message);
        }
    }

    private void referenceError(String literal) throws RuntimeErrorException {
        String message = "Null Pointer Exception!, variable: "
                + literal 
                + " is not initialized";

        throw new RuntimeErrorException(message);
    }

}

class AOPReconstructor {

    private ArrayList<String> res;
    private String infix;
    private ArrayList<String> result;

    public AOPReconstructor(ParseTreeNode ptn) {
        this.res = new ArrayList<String>();
        traverse(ptn);
        infix = "";
        for (String str : res) {

            switch (str) {
                case "add_op":
                    infix = infix + "+ ";
                    break;
                case "minus_op":
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
                    infix = infix + str + " ";
                    break;
            }
        }

        result = infixToPostfix(infix.split(" "));
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

    public ArrayList<String> getPostFix() {
        return result;
    }

    private int prec(char c) {
        if (c == '^') {;
            return 3;
        } else if (c == '/' || c == '*') {
            return 2;
        } else if (c == '+' || c == '-') {
            return 1;
        } else {
            return -1;
        }
    }

    // Function to return associativity of operators
    private char associativity(char c) {
        if (c == '^') {
            return 'R';
        }
        return 'L'; // Default to left-associative
    }

    // The main function to convert infix expression to postfix expression
    private ArrayList<String> infixToPostfix(String[] infix) {
        ArrayList<String> result = new ArrayList<>();
        Stack<String> stack = new Stack<>();

        for (String s : infix) {
            if ((s.charAt(0) >= 'a' && s.charAt(0) <= 'z') || (s.charAt(0) >= 'A' && s.charAt(0) <= 'Z') || (s.charAt(0) >= '0' && s.charAt(0) <= '9')) {
                result.add(s);
            } else if (s.charAt(0) == '(') {
                stack.push(s);
            } else if (s.charAt(0) == ')') {
                while (!stack.isEmpty() && stack.peek().charAt(0) != '(') {
                    result.add(stack.pop());
                }
                stack.pop(); // Pop '('
            } else {
                while (!stack.isEmpty() && (prec(s.charAt(0)) < prec(stack.peek().charAt(0))
                        || prec(s.charAt(0)) == prec(stack.peek().charAt(0))
                        && associativity(s.charAt(0)) == 'L')) {
                    result.add(stack.pop());
                }
                stack.push(s);
            }
        }

        // Pop all the remaining elements from the stack
        while (!stack.isEmpty()) {
            result.add(stack.pop());
        }

        return result;
    }

}

class LOPReconstructor {

    private ArrayList<String> res;
    private String infix;
    private ArrayList<String> result;

    public LOPReconstructor(ParseTreeNode ptn) {
        this.res = new ArrayList<String>();
        traverse(ptn);
        infix = "";
        for (String str : res) {

            switch (str) {
                case "add_op":
                    infix = infix + "+ ";
                    break;
                case "minus_op":
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
                    infix = infix + str + " ";
                    break;
            }
        }

        result = infixToPostfix(infix.split(" "));
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

    public ArrayList<String> getPostFix() {
        return result;
    }

    private int prec(char c) {
        if (c == '^') {;
            return 3;
        } else if (c == '/' || c == '*') {
            return 2;
        } else if (c == '+' || c == '-') {
            return 1;
        } else {
            return -1;
        }
    }

    // Function to return associativity of operators
    private char associativity(char c) {
        if (c == '^') {
            return 'R';
        }
        return 'L'; // Default to left-associative
    }

    // The main function to convert infix expression to postfix expression
    private ArrayList<String> infixToPostfix(String[] infix) {
        ArrayList<String> result = new ArrayList<>();
        Stack<String> stack = new Stack<>();

        for (String s : infix) {
            if ((s.charAt(0) >= 'a' && s.charAt(0) <= 'z') || (s.charAt(0) >= 'A' && s.charAt(0) <= 'Z') || (s.charAt(0) >= '0' && s.charAt(0) <= '9')) {
                result.add(s);
            } else if (s.charAt(0) == '(') {
                stack.push(s);
            } else if (s.charAt(0) == ')') {
                while (!stack.isEmpty() && stack.peek().charAt(0) != '(') {
                    result.add(stack.pop());
                }
                stack.pop(); // Pop '('
            } else {
                while (!stack.isEmpty() && (prec(s.charAt(0)) < prec(stack.peek().charAt(0))
                        || prec(s.charAt(0)) == prec(stack.peek().charAt(0))
                        && associativity(s.charAt(0)) == 'L')) {
                    result.add(stack.pop());
                }
                stack.push(s);
            }
        }

        // Pop all the remaining elements from the stack
        while (!stack.isEmpty()) {
            result.add(stack.pop());
        }

        return result;
    }

}

class RuntimeErrorException extends Throwable {

    public RuntimeErrorException() {
        super();
    }

    public RuntimeErrorException(String message) {
        super(message);
    }
}
