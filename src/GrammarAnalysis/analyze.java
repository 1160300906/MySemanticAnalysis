package GrammarAnalysis;

public class analyze {

    char ch;
    int status;

    public analyze() {
    }

    public analyze(char ch, int status) {
        this.ch = ch;
        this.status = status;
    }

    public char getCh() {
        return ch;
    }

    public void setCh(char ch) {
        this.ch = ch;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}