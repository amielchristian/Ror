package rorlanguage;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import modules.DFALex;

public class LexicalAnalyzer {
    public static void main(String[] args)  {
        String dfaFile = "dfa.json";
        
        DFALex dfa = new DFALex(dfaFile);
//        runTestProgram(dfa);
    
    }
    

    static void runTestProgram(DFALex dfa) {;
        String str, token;
        String output = "";
        try {
            str = new String(Files.readAllBytes(Paths.get("TestCases/InputProgram.txt")));
            str = str.replaceAll("\\(", " ( ");
            str = str.replaceAll("\\)", " ) ");
            str = str.replaceAll("\\-\\-",  " -- ");
            str = str.replaceAll("\\+\\+",  " ++ ");
            str = str.replaceAll("\\s+"," ");
            str = str.replaceAll(";", " ; ");
            str = str.replaceAll("#[^\\n\\r>]+?(?:\\*\\)|[\\n\\r])", "#");
            str = str.replaceAll("<#([^(#>)])*#>", "<# #>");
            List<String> matchList = new ArrayList<String>();
            System.out.println(str);
            Pattern regex = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'");
            Matcher regexMatcher = regex.matcher(str);
            while (regexMatcher.find()) {
                matchList.add(regexMatcher.group());
            }
            
            PrintWriter writer = new PrintWriter("output.txt");
            writer.print("");
            int i = 0;
            for (String lexeme : matchList) {
                token = dfa.run(lexeme);
                output += "\nINPUT TOKEN: " + lexeme + ",\t FOUND: " + ((!token.equals("")) ? token : "INVALID TOKEN" + ++i);
                
            }
                writer.println(output);
                writer.println("FOUND " + i + " INVALID TOKENS");
                writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    static void runShiftReduce(DFALex dfa) {
        
    }
    
}