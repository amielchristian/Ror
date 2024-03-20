package rorlanguage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import modules.DFALex;
import modules.SymbolTable;

public class LexicalAnalyzer {
    public static void main(String[] args)  {
        String dfaFile = "dfa.json";
        
        SymbolTable st = new SymbolTable();
        DFALex dfa = new DFALex(dfaFile);
        runTestProgram(dfa, st);
        System.out.println(st.toString());
    }
    
    static void runTestProgram(DFALex dfa, SymbolTable st) {
        String str, token;
        String output = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File("TestCases/InputProgram.txt")));
            int line = 0;
            int errors = 0;
            boolean groupCommentFound = false;
            while ((str = br.readLine()) != null)   {
                line++;
                
                // inflation
                str = str.replaceAll("\\(", " ( ");
                str = str.replaceAll("\\)", " ) ");
                str = str.replaceAll("\\-\\-",  " -- ");
                str = str.replaceAll("\\+\\+",  " ++ ");
                str = str.replaceAll("\\s+"," ");
                str = str.replaceAll(";", " ; ");
                str = str.replaceAll("#>", " #>");
                str = str.replaceAll("<#", "<# ");
                str = str.replaceAll("(?<!<)#(?!>)", " # ");
                str = str.replaceAll(",", " , ");
                
                // add matched tokens in the line to a list
                List<String> matchList = new ArrayList<>();
                System.out.println(str);
                Pattern regex = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'");
                Matcher regexMatcher = regex.matcher(str);
                while (regexMatcher.find()) {
                    matchList.add(regexMatcher.group());
                }

                // write matched tokens in line to an output file
                loop: for (String lexeme : matchList) {
                    System.out.println(lexeme);
                    token = dfa.run(lexeme);
                    if (!groupCommentFound) {
                        switch (token)  {
                            default -> output += "\n"+token;
                            case "identifier" -> {
                                output += "\nid_"+lexeme;
                                st.addToken("id_"+lexeme);
                            }
                            case "" -> {
                                output += "\nINVALID TOKEN '"+lexeme+"' AT LINE "+line;
                                errors++;
                            }
                            case "single_comment" -> {
                                break loop; // move on to next line
                            }
                            case "group_comment_start" -> groupCommentFound = true;
                        }
                    }
                    else    {
                        if (token.equals("group_comment_end"))
                            groupCommentFound = false;
                    }
                    
                }
            }
            PrintWriter writer = new PrintWriter("output.txt");
            writer.print("");
            writer.println(output);
            writer.println("Total errors: "+errors);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
}