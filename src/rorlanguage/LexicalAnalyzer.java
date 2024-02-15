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
            String str;
            String[] strArray;
            boolean matched;
            try {
                System.out.println("VALID RESERVED WORDS / TOKENS");
                br = new BufferedReader(new FileReader( "TestCases/keywords.txt"));
                while ((str = br.readLine()) != null) {
                    strArray = str.split(",");
                    matched = dfa.run(strArray[0].trim());
                    System.out.println("Input Token: " + strArray[0].trim() + ", Expected: " + strArray[1].trim() + "\nSTATUS: " + (matched ? "PASSED" : "FAILED") + "\n");
                }
                
                System.out.println("\n\n\n");
                System.out.println("VALID LITERALS / IDENTIFIERS");
                br = new BufferedReader(new FileReader( "TestCases/validTokens.txt"));
                while ((str = br.readLine()) != null) {
                    strArray = str.split(",");
                    matched = dfa.run(strArray[0].trim());
                    System.out.println("Input Token: " + strArray[0].trim() + ", Expected: " + strArray[1].trim() + "\nSTATUS: " + (matched ? "PASSED" : "FAILED") + "\n");
                }
                
                System.out.println("\n\n\n");
                System.out.println("INVALID TOKENS");
                br = new BufferedReader(new FileReader( "TestCases/invalidTokens.txt"));
                while ((str = br.readLine()) != null) {
                    strArray = str.split(",");
                    matched = dfa.run(strArray[0].trim());
                    System.out.println("Input Token: " + strArray[0].trim() + "\nSTATUS: " + (matched ? "FAILED" : "PASSED") + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
    
}