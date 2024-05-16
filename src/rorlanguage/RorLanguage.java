/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rorlanguage;

import modules.LexResult;
import modules.DFALex;
import modules.ParseTreeNode;
import modules.SymbolTable;

/**
 *
 * @author Ivan
 */
public class RorLanguage {
    public static void main(String[] args)  {
        LexicalAnalyzer la = new LexicalAnalyzer();   
        String dfaFile = "dfa.json";
        DFALex dfa = new DFALex(dfaFile);
        SymbolTable st = new SymbolTable();
        LexResult lr = la.runTestProgram(dfa, st);
        SyntaxAnalyzer sa = new SyntaxAnalyzer(lr);
        sa.parse();
        System.out.println(st);
        ParseTreeNode ptn = sa.getParseTree();
        Interpreter intptr = new Interpreter(ptn, st);
        intptr.run();
        System.out.println(st);
    }
}
