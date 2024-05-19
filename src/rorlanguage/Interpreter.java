/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rorlanguage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    public ArrayList<ParseTreeNode> aops;
    public ArrayList<ArrayList> ifs;
    public int ifctr;

    public Interpreter(ParseTreeNode ptn, SymbolTable st, ArrayList<ArrayList> ifs, ArrayList<ParseTreeNode> aops) {
        this.st = st;
        tokens = new ArrayList<>();
        this.aops = aops;
        this.ifs = ifs;
        ifctr = ifs.size()-1;
        ptr = 0;
        this.ptn = ptn;
        this.index = 0;
        traverse(this.ptn, tokens, true);
    }

    public void traverse(ParseTreeNode ptn, ArrayList tokens, boolean addToTokens) {
        ptn.index = index;
        index++;
        if (ptn == null) {
            return;
        }
        
        // find a way to prevent statements within ifs from being added
        if (ptn.name.equals("<IF_STATEMENT>")) {
            ifs.add(ifstmtReconstructor(ptn));
            ifctr++;
            if (addToTokens)    {
                tokens.add("if_"+(ifs.size()-1));
            }
            addToTokens = false;
        }
        
        if (!ptn.name.equals("<S>") && !ptn.name.equals("<ARITHMETIC_OPERATION>") && !ptn.name.equals("<IF_STATEMENT>")) {
            if (addToTokens)    {
                tokens.add(ptn.name);
            }
        } else if (ptn.name.equals("<ARITHMETIC_OPERATION>")) {
            if (addToTokens)    {
                tokens.add("aop_" + aops.size());
            }
            ParseTreeNode ptnCopy = new ParseTreeNode("<ARITHMETIC_OPERATION>");
            ptnCopy.setChildren(ptn.getChildren());
            aops.add(ptnCopy);
            ptn.name = "aop_"+(aops.size()-1);
            ptn.removeChildren();
        }
        for (ParseTreeNode child : ptn.getChildren()) {
            traverse(child, tokens, addToTokens);
        }
    }

    public void run() {
        try {
            while (ptr < tokens.size() - 1) {
                if (match("<P>")) {
                } else if (match("<D>")) {
                    declare();
                } else if (match("<ROAR>")) {
                    roar();
                } else if (match("<A>")) {
                    assign();
                } else if (tokens.get(ptr).contains("if")) {
                    conditional();
                } else if (match("<ARITHMETIC_OPERATION>")) {

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
        } else if (tokens.get(ptr).contains("\"") || tokens.get(ptr).contains("bool")) {
            value = tokens.get(ptr);
        }
        st.updateTokenValue(identifier, value);
        ptr++;
    }

    // TODO: TYPE CHECKING
    private String nom() {
        ptr += 3;
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

    // DONE
    private void roar() {
        ptr += 2;
        if (tokens.get(ptr).startsWith("id_")) {
            System.out.println(st.getTokenValue(tokens.get(ptr), "value"));
            ptr++;
        } else if (tokens.get(ptr).contains("\"")) {
            System.out.println(tokens.get(ptr));
        }
        ptr += 2;
    }

    // DONE
    private int arithmeticOp(String aop, String literal) throws RuntimeErrorException {
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

    public void conditional() throws RuntimeErrorException {
        ArrayList node = ifs.get(Integer.parseInt(tokens.get(ptr).substring(3)));
        
        // evaluate conditions starting with first; move on to next if false, execute if true
        for (int i = 0; i < node.size(); i++) {
            ArrayList conditional = (ArrayList) node.get(i);

            // else
            if (i == node.size() - 1 && conditional.size() == 1) {
                ParseTreeNode statement = (ParseTreeNode) conditional.get(0);
                executeStatement(statement);
            }
            // if and else if
            else    {
                ParseTreeNode condition = (ParseTreeNode) conditional.get(0);
                ParseTreeNode statement = (ParseTreeNode) conditional.get(1);
                if (evaluateCondition(condition, statement)) {
                    executeStatement((ParseTreeNode) conditional.get(2));
                    break;
                }
            }
        }
        ptr++;
    }

    public boolean evaluateCondition(ParseTreeNode operand, ParseTreeNode conditionNode) throws RuntimeErrorException   {
        Object operandValue;
        if (operand.name.contains("id_"))   {
            operandValue = st.getTokenValue(operand.name, "value");
        }
        else if (operand.name.contains("aop_")) {
            operandValue = arithmeticOp(operand.name, operand.name);
        }
        else    {
            operandValue = operand.name;
        }

        // these cases are for determining if the condition is a logical or relational operation
        // this is for the case where the condition is a logical operation with a single boolean operand
        if (conditionNode.getChildren().isEmpty()) {
            try {
                return (boolean) operandValue;
            } catch (ClassCastException e) {
                String message = "Type Mismatch, type: "
                        + operandValue.getClass().getSimpleName()
                        + " is incompatible with datatype: "
                        + "Bool"
                        + " literal: "
                        + operand.name.substring(3);

                //throw new RuntimeErrorException(message);
            }
        }
        // this is for the case where the condition is a logical operation
        else if (conditionNode.getChildren().getFirst().name.equals("<LOGICAL_OPERATION>")) {
            //return logicalOp(operandValue);
        }
        else if (conditionNode.getChildren().getFirst().name.equals("<RELATIONAL_OPERATION>")) {
            int x = (int) operandValue;
            try {
                return relationalOp(x, conditionNode.getChildren().getFirst().getChildren().getFirst());
            } catch (RuntimeErrorException ex) {
                Logger.getLogger(Interpreter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else    {
            return false;
        }

        return false;
    }

    // TODO
    private boolean logicalOp(int operand1) {
        return false;
    }

    // DONE
    private boolean relationalOp(int operand1, ParseTreeNode root) throws RuntimeErrorException {
        boolean result;
        
        String operator = root.getChildren().get(0).name;
        int operand2 = arithmeticOp(root.getChildren().get(1).name, root.getChildren().get(1).name);

        switch (operator) {
            case "gte" ->
                result = operand1 >= operand2;
            case "lte" ->
                result = operand1 <= operand2;
            case "gt" ->
                result = operand1 > operand2;
            case "lt" ->
                result = operand1 < operand2;
            case "equal_rel" ->
                result = operand1 == operand2;
            case "not_equal_rel" ->
                result = operand1 != operand2;
            default ->
                result = false;
        }
        return result;
    }

    public void executeStatement(ParseTreeNode statement) {
       Interpreter intptr = new Interpreter(statement, this.st, ifs, aops);
//       System.out.println(intptr.ifs);
       intptr.run();
       this.st = intptr.st;
       this.aops = intptr.aops;
       this.ifs = intptr.ifs;
       ptr++;
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

    public ArrayList<ArrayList> ifstmtReconstructor(ParseTreeNode ptn) {
        ParseTreeNode root = ptn;
        root.getChildren().get(0).setName("if_" + ifctr);
        
        // structure: {conditions: statements to be executed}
        // for else, conditions will be empty
        // this structure will be returned and evaluated in the interpreter
        ArrayList<ArrayList> conditionals = new ArrayList<>();
        ArrayList<ParseTreeNode> conditions = new ArrayList<>();
        ParseTreeNode condition = null;
        ParseTreeNode statement = null;
        
        // get if
        conditions.add(root.getChildren().get(2));
        conditions.add(root.getChildren().get(3));
        conditions.add(root.getChildren().get(6));
        conditionals.add(conditions);
        
        // get elseifs and elses
        boolean endFound = false;
        while (!endFound) {
            conditions = new ArrayList<>();
            try {
                root = root.getChildren().getLast();
            } catch (NoSuchElementException e) {
                endFound = true;
            }

            for (ParseTreeNode node : root.getChildren()) {
                if (node.name.equals("<ELSE_STATEMENT_>"))  {
                    if (node.getChildren().getFirst().name.equals("if"))    {
                        conditions.add(node.getChildren().get(2));
                        conditions.add(node.getChildren().get(3));
                        conditions.add(node.getChildren().get(6));
                    }
                    else    {
                        conditions.add(node.getChildren().get(1));
                    }
                    conditionals.add(conditions);
                }
            }
        }
        
        root = ptn;
        while (!root.getChildren().getLast().getChildren().isEmpty()) {
              int lastIndex = root.getChildren().size() - 1;

              for (ParseTreeNode node : root.getChildren().getLast().getChildren()) {
                  if (node.name.equals("<ELSE_STATEMENT_>")) {
                      for (ParseTreeNode subnode : node.getChildren()) {
                          if (subnode.name.equals("if")) {
                              subnode.setName("if_" + ifctr);
                          }
                          root.addChild(subnode);
                      }
                  } else {
                      node.setName(node.name + "_" + ifctr);
                      root.addChild(node);
                  }
              }
              root.getChildren().remove(lastIndex);
          }
        return conditionals;
    }

}
// <editor-fold defaultstate="collapsed" desc="Helper Classes">

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
                case "and":
                    infix = infix + "And ";
                    break;
                case "or":
                    infix = infix + "Or ";
                    break;
                case "not":
                    infix = infix + "Not ";
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

    public ArrayList<String> getLOPArray() {
        return res;
    }

    public ArrayList<String> getPostFix() {
        return result;
    }

    private int prec(String s) {
        if (s.equals("not")) {
            return 3;
        } else if (s.equals("or ")) {
            return 2;
        } else if (s.equals("and")) {
            return 1;
        } else  {
            return -1;
        }
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
                while (!stack.isEmpty() && (prec(s.substring(0,2))) < prec(stack.peek())
                        || prec(s.substring(0,2)) == prec(stack.peek())
                    ) {
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
    // </editor-fold> 
