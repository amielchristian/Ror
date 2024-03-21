/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rorlanguage;

import java.util.ArrayList;
import modules.DFALex;
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
        ArrayList<String> tokenArray = la.runTestProgram(dfa, st);
        SyntaxAnalyzer sa = new SyntaxAnalyzer(tokenArray);
        sa.parse();
    }
}
