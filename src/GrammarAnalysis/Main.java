package GrammarAnalysis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import lexicalAnalysis.Scanner;

public class Main {
    @SuppressWarnings("resource")
    public static ArrayList<String> readCFresult() throws IOException {
        String input = "src/lexicalAnalysis/token.txt";

        FileInputStream incode = new FileInputStream(input);
        BufferedReader strcode = new BufferedReader(new InputStreamReader(incode));
        String line = "";
        ArrayList<String> result = new ArrayList<String>();
        while ((line = strcode.readLine()) != null) {
            result.add(line);
        }
        return result;
    }

    public static void main(String[] args) {
        new lexicalAnalysis.Scanner();
        Scanner.Lmain();
        closure clo = new closure();
        translate translate = new translate();
        try {
            System.out.println(translate.translateIt(readCFresult()));
            System.out.println(clo.YFmain(translate.translateIt(readCFresult())));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
