/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rorlanguage;

import java.util.ArrayList;
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
            tokens.add(ptn.name);
        }
        if (!ptn.name.equals("<S>")) {
            System.out.println(ptn.name);
        }
        for (ParseTreeNode child : ptn.getChildren()) {
            traverse(child);
        }
    }

    public void run() {
        while (true) {
            if (tokens.get(ptr).equals("<P>")) {
                ptr++;
            } else if (tokens.get(ptr++).equals("<D>")) {
                declare();
            }
        }
    }
    
    private void declare() {
        // TODO: Check if declared in symbol table
        ptr++;
        String datatype = tokens.get(ptr++);
        String identifier = tokens.get(ptr++);
        ptr++;
        Object value = null;
        if (tokens.get(ptr++).equals("<NOM>")) {
            
        }
//        st.updateTokenValue(tokens.get(ptr), "datatype", datatype);
    }
}