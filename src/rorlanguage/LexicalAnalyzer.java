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
//        runTestCases(dfa);
//        Scanner sc = new Scanner(System.in);
//        String input;
//        while(true) {
//            System.out.print("INPUT: ");
//            input = sc.nextLine();
//            dfa.run(input);
//        }
        runTestProgram(dfa);
    }
    
//    static void runTestCases(DFALex dfa) {
//        BufferedReader br;
//            String input, expected;
//            String[] strArray;
//            String output = "output";
//            boolean matched;
//            try {
//                output += "VALID RESERVED WORDS / TOKENS";
//                System.out.println("VALID RESERVED WORDS / TOKENS");
//                br = new BufferedReader(new FileReader( "TestCases/keywords.txt"));
//                while ((input = br.readLine()) != null && (expected = br.readLine()) != null) {
//                    matched = dfa.run(input.trim());
//                    System.out.println("Input Token: " + input.trim() + ", Expected: " + expected.trim() + "\nSTATUS: " + (matched ? "PASSED" : "FAILED") + "\n");
//                    output += "\nInput Token: " + input.trim() + ", Expected: " + expected.trim() + "\nSTATUS: " + (matched ? "PASSED" : "FAILED") + "\n";
//                }
//                
//                
//                output += "\n\n\nVALID LITERALS / IDENTIFIERS";
//                System.out.println("\n\n\n");
//                System.out.println("VALID LITERALS / IDENTIFIERS");
//                br = new BufferedReader(new FileReader( "TestCases/validTokens.txt"));
//                while ((input = br.readLine()) != null && (expected = br.readLine()) != null) {
//                    matched = dfa.run(input.trim());
//                    System.out.println("Input Token: " + input.trim() + ", Expected: " + expected.trim() + "\nSTATUS: " + (matched ? "PASSED" : "FAILED") + "\n");
//                    output += "\nInput Token: " + input.trim() + ", Expected: " + expected.trim() + "\nSTATUS: " + (matched ? "PASSED" : "FAILED") + "\n";
//                }
//                
//                System.out.println("\n\n\n");
//                System.out.println("INVALID TOKENS");
//                output += "\n\n\n INVALID TOKENS";
//                br = new BufferedReader(new FileReader( "TestCases/invalidTokens.txt"));
//                while ((input = br.readLine()) != null) {
//                    matched = dfa.run(input.trim());
//                    System.out.println("Input Token: " + input.trim() + "\nSTATUS: " + (matched ? "FAILED" : "PASSED") + "\n");
//                    output += "\nInput Token: " + input.trim() + "\nSTATUS: " + (matched ? "FAILED" : "PASSED") + "\n";
//                }
//                
//                PrintWriter writer = new PrintWriter("output.txt");
//                writer.println(output);
//                writer.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//    }
//    
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
    
}