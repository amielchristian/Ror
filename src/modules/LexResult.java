package modules;
import java.util.ArrayList;

public class LexResult {
    public ArrayList<String> tokens;
    public ArrayList<Integer> lineTraceback;
    
    public LexResult(ArrayList<String> tokens, ArrayList<Integer> lineTraceback)    {
        this.tokens = tokens;
        this.lineTraceback = lineTraceback;
    }
}