package rorlanguage;

import java.util.LinkedList;
import java.util.Queue;

public class LexicalAnalyzer {
    public static void main(String[] args)  {
        String str = "Roar";
        
        Queue<Character> charsQueue = new LinkedList<Character>();
        for (char c : str.toCharArray()) {
            charsQueue.offer(c);
        }
       
    }
}