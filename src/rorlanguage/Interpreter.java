/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rorlanguage;

import java.util.ArrayList;
import java.util.Scanner;
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
    
    public Interpreter(ParseTreeNode ptn, SymbolTable st) {
        this.st = st;
        tokens = new ArrayList<String>();
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

        if (!ptn.name.equals("<S>")) {
            tokens.add(ptn.name);
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
            } else if (match("<A>"))    {
                assign();
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
        } else if (match("<ARITHMETIC_OPERATION>")) {
            // TODO
            arithmeticOp();
            ptr++;
            value = 69;
        } else if (match(""))   {
            
        }
        st.setTokenDatatype(identifier, datatype);
        st.updateTokenValue(identifier, value);
        ptr++; // skips terminate (?)
    }
    
    // TODO
    // gusto ko mamaya gawing same logic lang yung declaration and assignment kasi datatype lang naman pinagkaiba nila
    private void assign()   {
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
        }
        else if (tokens.get(ptr).contains("\""))   {
            System.out.print(tokens.get(ptr));
        }
        ptr += 2;
    }
    
    // 1. get parse tree of arithmetic operation
    private int arithmeticOp()  {
        int result = 0;
        ParseTreeNode arithmeticTree = ptn.getNodeFromDescendant(ptr);
        System.out.println(arithmeticTree);
        result = compute(arithmeticTree);
        System.out.println(result);
        return result;
    }
    private int compute(ParseTreeNode ptn)   {
        int result = 0;

        for (ParseTreeNode child : ptn.getChildren())   {
            if (child.name.equals("<!epsilon>"))    {
                result = -1;
            }
        }
        
        // GRRRRAAAAAAAAAAAAAAAAAH
        if (ptn.name.equals("<ARITHMETIC_TERM>")) {
            ArrayList<ParseTreeNode> children = ptn.getChildren();
            
            int op = 0;
            String opPosition = children.get(1).getChildren().get(0).name;
            if (opPosition.contains("_op"))  {
                if (opPosition.contains("mul")) {
                    op = 0;
                }
                else if (opPosition.contains("div"))   {
                    op = 1;
                }
                else if (opPosition.contains("mod"))   {
                    op = 2;
                }
            }
            else    {
                return compute(children.get(0));
            }
            
            int operand1 = compute(children.get(0));
            int operand2 = compute(children.get(1).getChildren().get(1));
            System.out.println("performing "+operand1+" "+op+" "+operand2);
            switch (op) {
                case 0:
                    result = operand1*operand2;
                    break;
                case 1:
                    result =  operand1/operand2;
                    break;
                case 2:
                    result =  operand1%operand2;
                    break;
                default:
                    result =  operand1;
                    break;
            }
            children.get(1).getChildren().get(1).name = Integer.toString(result);
            result = compute(children.get(1));
        }
        else if (ptn.name.equals("<ARITHMETIC_TERM_>")) {
            ArrayList<ParseTreeNode> children = ptn.getChildren();
            
            int op = 0;
            String opPosition = children.get(2).getChildren().get(0).name;
            if (opPosition.contains("_op"))  {
                if (opPosition.contains("mul")) {
                    op = 0;
                }
                else if (opPosition.contains("div"))   {
                    op = 1;
                }
                else if (opPosition.contains("mod"))   {
                    op = 2;
                }
            }
            else    {
                return compute(children.get(1));
            }
            
            int operand1 = compute(children.get(1));
            int operand2 = compute(children.get(2));
            System.out.println("performing "+operand1+" "+op+" "+operand2);
            switch (op) {
                case 0:
                    result = operand1*operand2;
                    break;
                case 1:
                    result =  operand1/operand2;
                    break;
                case 2:
                    result =  operand1%operand2;
                    break;
                default:
                    result =  operand1;
                    break;
            }
        }
        else if (ptn.name.equals("<ARITHMETIC_OPERATION>")) {
            for (ParseTreeNode child : ptn.getChildren())   {
                return compute(child);
            }
        }
//        else if (ptn.name.equals("<ARITHMETIC_OPERATION_>"))
        else if (ptn.name.equals("<ARITHMETIC_FACTOR>"))    {
            String name = ptn.getChildren().getFirst().name;
            if (name.contains("id_"))    {  // int id
                result =  (Integer) st.getTokenValue(tokens.get(ptr), "value");
            }
            else if (name.equals("parenthesis_start"))  { // arithmetic op
                result =  compute(ptn.getChildren().get(1));
            }
            else    { // int literal
                result =  Integer.parseInt(name);
            }
        }
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
