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

    public Interpreter(ParseTreeNode ptn, SymbolTable st) {
        this.st = st;
        tokens = new ArrayList<String>();
        ptr = 0;
        traverse(ptn);
    }

    private void traverse(ParseTreeNode ptn) {
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
            }
        }
    }

    private void declare() {
        // TODO: Check if declared in symbol table
        match("<DT>");
        String datatype = tokens.get(ptr++);
        String identifier = tokens.get(ptr++);
        ptr++;
        Object value = null;
        ptr++;
        if (match("<NOM>")) {
            value = nom();
        } else if (match("<ARITHMETIC_OPERATION>")) {
            // TODO
            ptr++;
            value = 69;
        }
        st.setTokenDatatype(identifier, datatype);
        st.updateTokenValue(identifier, value);
        ptr++;
    }

    private String nom() {
        ptr += 3;
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

    private void roar() {
        ptr += 2;
        if (tokens.get(ptr).startsWith("id_")) {
            System.out.println(st.getTokenValue(tokens.get(ptr), "value"));
            ptr++;
        }
//        System.out.println(tokens.get(ptr));
        ptr += 2;
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
