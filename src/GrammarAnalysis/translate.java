package GrammarAnalysis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class translate {

    public static String transform(int tag) {
        String[] tab = { "", "y", "z", "x", "@", "$", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "t", "u", "m",
                "o", "p", "", "+", "-", "*", "/", "<", ">", "&", "|", "~", "^", "!", "%", "++", "--", ">>", "<<", "l",
                "n", "!=", ">=", "<=", "+=", "-=", "*=", "/=", "=", ",", ";", "[", "]", "(", ")", "{", "}" };
        return tab[tag];
    }

    public String translateIt(ArrayList<String> cood) {

        // System.out.println(coodArray.length);
        String alltoken = "";
        for (int i = 0; i < cood.size(); i++) {
            String pattern = "<(\\d+),";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(cood.get(i));
            if (m.find()) {
                alltoken += transform(Integer.parseInt(m.group(1)));
            }
        }
        return alltoken;
    }

    @SuppressWarnings("resource")
    public String find_error(int ip) {
        String input = "src/lexicalAnalysis/token.txt";
        String input1 = "src/lexicalAnalysis/input.txt";
        ArrayList<String> list = new ArrayList<String>();

        try {
            FileInputStream incode = new FileInputStream(input);
            BufferedReader strcode = new BufferedReader(new InputStreamReader(incode));
            FileInputStream incode1 = new FileInputStream(input1);
            BufferedReader strcode1 = new BufferedReader(new InputStreamReader(incode1));
            String line1 = "";
            while ((line1 = strcode1.readLine()) != null) {
                list.add(line1);
            }
            String line = "";
            int tag = 0;
            int lineNum = 0;

            while ((line = strcode.readLine()) != null) {
                tag++;
                if (tag == ip + 1) {
                    String pattern = "line(\\d+)";
                    Pattern r = Pattern.compile(pattern);
                    Matcher m = r.matcher(line);
                    if (m.find()) {
                        lineNum = Integer.parseInt(m.group(1));
                    }
                    return "Error at Line [ " + (lineNum - 1) + " ]: " + list.get(lineNum - 2);
                }
            }
        } catch (Exception e) {

        }
        return "";
    }
    }