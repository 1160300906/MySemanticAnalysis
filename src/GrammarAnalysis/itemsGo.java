package GrammarAnalysis;

import java.util.ArrayList;
import java.util.List;

public class itemsGo {

    List<closure> Iitem = new ArrayList<closure>();
    int name;
    int from;
    char connect;

    public itemsGo() {
    }

    public itemsGo(List<closure> iitem, int name, int from, char connect) {
        super();
        Iitem = iitem;
        this.name = name;
        this.from = from;
        this.connect = connect;
    }

    public List<closure> getIitem() {
        return Iitem;
    }

    public void setIitem(List<closure> iitem) {
        Iitem = iitem;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public char getConnect() {
        return connect;
    }

    public void setConnect(char connect) {
        this.connect = connect;
    }

    @Override
    public String toString() {
        String string = "";
        string = "[name=I" + name + ", from=T" + from + ", connect=" + connect + "]\n";
        for (int i = 0; i < Iitem.size(); i++) {
            string = string + Iitem.get(i).toString() + "\n";
        }
        return string;
    }
}
