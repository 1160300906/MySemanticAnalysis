package GrammarAnalysis;

import java.util.ArrayList;
import java.util.List;

public class action_table {

    List<Character> ch = new ArrayList<Character>();
    List<String> value = new ArrayList<String>();

    public action_table() {
    }

    public List<Character> getCh() {
        return ch;
    }

    public void setCh(List<Character> ch) {
        this.ch = ch;
    }

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }
}
