package rorlanguage;

import modules.DFALex;

public class LexicalAnalyzer {
    public static void main(String[] args)  {
        String s = "\"Hello\"";
        String filename = "dfa.json";
        
        DFALex dfa = new DFALex(filename);
        dfa.run(s);
    }
    
}