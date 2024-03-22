package rorlanguage;

import modules.LexResult;
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
    public LexResult runTestProgram(DFALex dfa, SymbolTable st) {
        String str, token;
        String output = "";
        ArrayList<Integer> lineTraceback = new ArrayList<>();
        ArrayList<String> outputTokens = new ArrayList<>();
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
                str = str.replaceAll("\\s+"," ");
                str = str.replaceAll(";", " ; ");
                str = str.replaceAll("#>", " #>");
                str = str.replaceAll("<#", "<# ");
                str = str.replaceAll("(?<!<)#(?!>)", " # ");
                str = str.replaceAll("\\+", " + ");
                str = str.replaceAll("\\-", " - ");
                str = str.replaceAll("\\/", " / ");
                str = str.replaceAll("\\*", " * ");
                str = str.replaceAll(",", " , ");
                str = str.replaceAll(" \\-  \\- ",  " -- ");
                str = str.replaceAll(" \\+  \\+ ",  " ++ ");
                str = str.replaceAll("\\{", " { ");
                str = str.replaceAll("\\}", " } ");
                
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
                            default -> {
                                output += "\n"+token;
                                outputTokens.add(token);
                            }
                            case "identifier" -> {
                                output += "\nid_"+lexeme;
                                st.addToken("id_"+lexeme);
                                outputTokens.add("id_"+lexeme);
                            }
                            case "num_lit" ->   {
                                output += "\n"+lexeme;
                                st.addToken(lexeme);
                                outputTokens.add(lexeme);
                            }
                            case "string_lit" ->   {
                                output += "\n"+lexeme;
                                st.addToken(lexeme);
                                outputTokens.add(lexeme);
                            }
                            case "" -> {
                                output += "\nINVALID TOKEN '"+lexeme+"' AT LINE "+line;
                                errors++;
                                return null;
                            }
                            case "single_comment" -> {
                                break loop; // move on to next line
                            }
                            case "group_comment_start" -> groupCommentFound = true;
                        }
                    lineTraceback.add(line);
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
        return new LexResult(outputTokens, lineTraceback);
    }
    
}
