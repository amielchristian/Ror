package rorlanguage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;
import modules.DFALex;

public class LexicalAnalyzer {
    public static void main(String[] args)  {
        String dfaFile = "dfa.json";
        
        DFALex dfa = new DFALex(dfaFile);
        runTestCases(dfa);
        Scanner sc = new Scanner(System.in);
        String input;
        while(true) {
            System.out.print("INPUT: ");
            input = sc.nextLine();
            dfa.run(input);
        }
    }
    
    static void runTestCases(DFALex dfa) {
        BufferedReader br;
            String input, expected;
            String[] strArray;
            boolean matched;
            try {
                System.out.println("VALID RESERVED WORDS / TOKENS");
                br = new BufferedReader(new FileReader( "TestCases/keywords.txt"));
                while ((input = br.readLine()) != null && (expected = br.readLine()) != null) {
                    matched = dfa.run(input.trim());
                    System.out.println("Input Token: " + input.trim() + ", Expected: " + expected.trim() + "\nSTATUS: " + (matched ? "PASSED" : "FAILED") + "\n");
                }
                
                System.out.println("\n\n\n");
                System.out.println("VALID LITERALS / IDENTIFIERS");
                br = new BufferedReader(new FileReader( "TestCases/validTokens.txt"));
                while ((input = br.readLine()) != null && (expected = br.readLine()) != null) {
                    matched = dfa.run(input.trim());
                    System.out.println("Input Token: " + input.trim() + ", Expected: " + expected.trim() + "\nSTATUS: " + (matched ? "PASSED" : "FAILED") + "\n");
                }
                
                System.out.println("\n\n\n");
                System.out.println("INVALID TOKENS");
                br = new BufferedReader(new FileReader( "TestCases/invalidTokens.txt"));
                while ((input = br.readLine()) != null) {
                    matched = dfa.run(input.trim());
                    System.out.println("Input Token: " + input.trim() + "\nSTATUS: " + (matched ? "FAILED" : "PASSED") + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
    
}