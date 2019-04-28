package lexicalAnalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Scanner {
    public static int symbol_pos = 0;
    public static int symbol_pos1 = 0;
    public static int value_pos = -1;
    public static Map<String, Integer> symbol = new HashMap<String, Integer>();
    // 关键字：种别码从6开始
    public static String keywords[] = { "int", "float", "char", "blooean", "String", "void", "while", "return", "true",
            "false", "if", "else", "do", "call", "proc", "struct" };
    // public static String keywordstoken[] = { "INT","FLOAT", "DOUBLE",
    // "IF","ELSE","DO","WHILE",
    // "CONTINUE","BREAK","TYPEDEF","STRUCT","CONST","CHAR","STATIC"};
    // 运算符
    public static char operator[] = { '+', '-', '*', '=', '<', '>', '&', '|', '~', '^', '!', '%' };
    // 种别码：22开始
    public static String operatortoken[] = { "+", "-", "*", "/", "<", ">", "&", "|", "~", "^", "!", "%", "++", "--",
            ">>", "<<", "&&", "||", "!=", ">=", "<=", "+=", "-=", "*=", "/=" };

    // 运算符后可加等于
    public static Boolean isPlusEqu(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '=' || ch == '>' || ch == '<' || ch == '&'
                || ch == '|' || ch == '^' || ch == '!';
    }

    // 可以连续两个运算符一样
    public static Boolean isPlusSame(char ch) {
        return ch == '+' || ch == '-' || ch == '&' || ch == '|' || ch == '<' || ch == '>';
    }

    // 界符：种别码47开始
    public static char boundary[] = { '=', ',', ';', '[', ']', '(', ')', '{', '}' };
    public static String boundarytoken[] = { "=", ",", ";", "[", "]", "(", ")", "{", "}" };

    // 判断字母及下划线
    public static Boolean isAlpha(char ch) {
        return ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_');
    }

    // 判断数字
    public static Boolean isDigit(char ch) {
        return (ch >= '0' && ch <= '9');
    }

    // 判断是否是运算符
    public static Boolean isOp(char ch) {
        for (int i = 0; i < operator.length; i++) {
            if (ch == operator[i]) {
                return true;
            }
        }
        return false;
    }

    // 判断是否是界符，‘=’已经在运算符里处理过了，所以现在不考虑等号
    public static Boolean isBound(char ch) {
        for (int i = 0; i < boundary.length; i++) {
            if (ch == boundary[i] && ch != '=') {
                return true;
            }
        }
        return false;
    }

    // 判断关键字,返回该关键字的种别码
    public static int isMatchKeyword(String str) {
        // Boolean flag = false;
        int n = -1;
        for (int i = 0; i < keywords.length; i++) {
            if (str.equals(keywords[i])) {
                n = i;
                break;
            }
        }
        return n + 6;
    }

    // 判断运算字符的种别码
    public static int isMatchOP(String str) {
        int n = -1;
        for (int i = 0; i < operatortoken.length; i++) {
            if (str.equals(operatortoken[i])) {
                n = i;
                break;
            }
        }
        return n + 22;
    }

    // 判断界符的种别码
    public static int isMatchbound(String str) {
        int n = -1;
        for (int i = 0; i < boundarytoken.length; i++) {
            if (str.equals(boundarytoken[i])) {
                n = i;
                break;
            }
        }
        return n + 47;
    }

    // 数字常量DFA获得下一个状态
    public static int digitGetNextStrustate(int startstate, char ch) {
        int nextstate = 7;
        switch (startstate) {
        case 1:
            if (isDigit(ch))
                nextstate = 1;
            else if (ch == 'e')
                nextstate = 4;
            else if (ch == '.')
                nextstate = 2;
            break;
        case 2:
            if (isDigit(ch))
                nextstate = 3;
            break;
        case 3:
            if (isDigit(ch))
                nextstate = 3;
            else if (ch == 'e')
                nextstate = 4;
            break;
        case 4:
            if (ch == '-' || ch == '+' || isDigit(ch))
                nextstate = 5;
            break;
        case 5:
            if (isDigit(ch))
                nextstate = 6;
            break;
        case 6:
            if (isDigit(ch))
                nextstate = 6;
            break;
        }
        return nextstate;
    }

    // 注释的DFA获得下一个状态
    public static int noteGetNextStrustate(int startstate, char ch) {
        int nextstate = -1;
        switch (startstate) {
        case 1:
            if (ch == '*')
                nextstate = 2;
            else
                nextstate = 1;
            break;
        case 2:
            if (ch == '/')
                nextstate = 3;
            else if (ch == '*')
                nextstate = 2;
            else
                nextstate = 1;
            break;
        }
        return nextstate;
    }

    // 字符串常量DFA获得下一个状态
    public static int StringGetNextStrustate(int startstate, char ch) {
        int nextstate = -1;
        switch (startstate) {
        case 1:
            if (ch == '"')
                nextstate = 2;
            else
                nextstate = 1;
            break;
        }
        return nextstate;
    }

    // 字符常量DFA获得下一个状态
    public static int charGetNextStrustate(int startstate, char ch) {
        int nextstate = -1;
        switch (startstate) {
        case 1:
            if (ch == '\'')
                nextstate = 2;
            else
                nextstate = 1;
            break;
        }
        return nextstate;
    }

    // 读文件
    public static ArrayList<String> readfile(File file) {
        ArrayList<String> strings = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));// 构造一个BufferedReader类来读取文件
            String s = null;
            while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
                strings.add(s);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strings;
    }

    public static void Lmain() {
        String filepath = "src/lexicalAnalysis/input.txt";
        File file = new File(filepath);
        symbol.clear();
        ArrayList<String> texts = new ArrayList<String>();
        // int stack1=0;//用于判断'('是否封闭
        // int stack2=0;//用于判断'['是否封闭
        // int stack3=0;//用于判断'{'是否封闭
        texts = readfile(file);
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("src/lexicalAnalysis/token.txt"));
            BufferedWriter out1 = new BufferedWriter(new FileWriter("src/lexicalAnalysis/symbol.txt"));
            BufferedWriter out2 = new BufferedWriter(new FileWriter("src/lexicalAnalysis/error.txt"));
            BufferedWriter out3 = new BufferedWriter(new FileWriter("src/lexicalAnalysis/value.txt"));
            // symbol.txt是符号表
            // String str = "";
            // for(int j = 0; j < texts.size(); j++) {
            // str=str+texts.get(j);
            // }
            for (int j = 0; j < texts.size(); j++) {
                String str = texts.get(j);
                char[] strline = str.toCharArray();
                for (int i = 0; i < strline.length; i++) {
                    // 遍历strline中的每个字符
                    char ch = strline[i];
                    // 初始化token字符串为空
                    String token = "";
                    // 判断标识和关键字
                    if (isAlpha(ch)) {
                        do {
                            token += ch;
                            i++;
                            if (i >= strline.length)
                                break;
                            ch = strline[i];
                        } while (ch != '\0' && (isAlpha(ch) || isDigit(ch)));
                        --i;// 指针回退，列计数减1
                        // 是关键字
                        int pos = isMatchKeyword(token.toString());
                        if (pos != 5) {
                            out.write(token + "\t<" + (pos) + ", _ >\tline" + (j + 1) + "\n");
                            // System.out.println(token+"\t<"+(pos)+", _ >");
                        }
                        // 是标识符,标识符的种别码为1
                        else {
                            // 如果符号表为空或符号表中不包含当前token，则加入
                            if (symbol.isEmpty() || (!symbol.isEmpty() && !symbol.containsKey(token))) {
                                symbol.put(token, symbol_pos);
                                // out1.write(token+"\t"+symbol_pos+"\n");
                                symbol_pos++;
                            }
                            symbol_pos1++;
                            out1.write(token + "\t" + (j+1) + "\n");
                            
                            out.write(token + "\t<" + 1 + ", " + symbol.get(token) + " >\tline" + (j + 1) + "\n");
                            // System.out.println(token+"\t<"+1+", "+symbol.get(token)+" >");
                        }
                        token = "";
                    }
                    // 判断数字常量
                    else if (isDigit(ch)) {
                        // 初始化进入1状态
                        int state = 1;
                        Boolean isfloat = false;
                        token += ch;
                        i++;
                        if (i >= strline.length)
                            break;
                        ch = strline[i];//
                        while ((ch != '\0') && (isDigit(ch) || ch == '.' || ch == 'e' || ch == '-' || ch == '+')) {
                            if (ch == '.' || ch == 'e') {
                                isfloat = true;
                            }
                            state = digitGetNextStrustate(state, ch);
                            // System.out.println(state);
                            if (state > 6)
                                break;
                            else
                                token += ch;
                            // 列计数加1
                            i++;
                            if (i >= strline.length)
                                break;
                            ch = strline[i];
                        }
                        Boolean haveMistake = false;
                        if (state == 2 || state == 4 || state == 5 || state == 7) {
                            haveMistake = true;
                        }
                        // 错误处理
                        if (haveMistake) {
                            // 一直到“可分割”的字符结束
                            while (ch != '\0' && !isBound(ch) && ch != ' ') {
                                token += ch;
                                i++;
                                if (i >= strline.length)
                                    break;
                                ch = strline[i];
                            }
                            out2.write("第" + (j + 1) + "行\t" + token + "\t请确认无符号常数输入正确\n");
                            // System.out.println("第"+(j+1)+"行\t"+token + "\t请确认无符号常数输入正确");
                        } else {
                            if (isfloat) {
                                value_pos++;
                                out.write(token + "\t<3, " + token + " >\tline" + (j + 1) + "\n");
                                out3.write(token + "     float    " + (j+1) + "\n");
                                // System.out.println(token+"\t<3, "+token+" >");// 浮点型常量
                            } else {
                                value_pos++;
                                out.write(token + "\t<2, " + token + " >\tline" + (j + 1) + "\n");
                                out3.write(token + "     int    " + (j+1) + "\n");
                                // System.out.println(token+"\t<2, "+token+" >");//整形常量
                            }
                        }
                        i--;
                        token = "";
                    }
                    // 识别运算符
                    else if (isOp(ch)) {
                        token += ch;
                        // 后面可以用一个"="
                        if (isPlusEqu(ch)) {
                            i++;
                            if (i >= strline.length)
                                break;
                            ch = strline[i];
                            if (ch == '=')
                                token += ch;
                            else
                            // 判断该符号后面是不是可以连着和自己一样的
                            {
                                if (isPlusSame(strline[i - 1]) && ch == strline[i - 1]) {
                                    token += ch;
                                } else
                                    --i;
                            }
                        }
                        // 界符，赋值语句里的等于
                        if (token.equals("=")) {
                            int pos = isMatchbound(token);
                            out.write(token + "\t<" + pos + ", " + " _ >\tline" + (j + 1) + "\n");
                            // System.out.println(token+"\t<"+pos+", "+token+" >");
                        }
                        // 运算符
                        else {
                            int pos = isMatchOP(token);
                            out.write(token + "\t<" + pos + ", " + " _ >\tline" + (j + 1) + "\n");
                            // System.out.println(token+"\t<"+pos+", "+token+" >");
                        }
                        token = "";
                    }
                    // 识别界符
                    else if (isBound(ch)) {
                        token += ch;
                        // if(stack1<0) {
                        // stack1=0;
                        // out2.write(token + "\t没有与之匹配的左括号\n");
                        // System.out.println(token + "\t没有与之匹配的左括号");
                        // }
                        // if(stack2<0) {
                        // stack2=0;
                        // out2.write(token + "\t没有与之匹配的左括号\n");
                        // System.out.println(token + "\t没有与之匹配的左括号");
                        // }
                        // if(stack3<0) {
                        // stack3=0;
                        // out2.write(token + "\t没有与之匹配的左括号\n");
                        // System.out.println(token + "\t没有与之匹配的左括号");
                        // }
                        // if(ch=='(')
                        // stack1++;
                        // else if(ch==')')
                        // stack1--;
                        // if(ch=='[')
                        // stack2++;
                        // else if(ch==']')
                        // stack2--;
                        // if(ch=='{')
                        // stack3++;
                        // else if(ch=='}')
                        // stack3--;
                        int pos = isMatchbound(token);
                        out.write(token + "\t<" + pos + ", _ >\tline" + (j + 1) + "\n");
                        // System.out.println(token+"\t<"+pos+", "+token+" >");
                        token = "";
                    }
                    // 识别注释
                    else if (ch == '/') {
                        token += ch;
                        i++;
                        if (i >= strline.length)
                            break;
                        ch = strline[i];
                        if (ch != '*') {
                            // 此时识别到的/当作运算符中的除法来处理
                            if (ch == '=') {
                                token += ch; // /=
                            } else {
                                --i; // / 列计数减1
                            }
                            int pos = isMatchbound(token);
                            // out.write(token+"\t<"+pos+", "+" _ >\tline"+(j+1)+"\n");
                            System.out.println(token + "\t<" + pos + ", " + token + " >");
                        }
                        // 否则识别到/*
                        else {
                            Boolean haveMistake = false;
                            // 初始化进入1状态
                            int state = 1;
                            i++;
                            if (i >= strline.length)
                                break;
                            ch = strline[i];
                            token = "/*";
                            while (true) {
                                state = noteGetNextStrustate(state, ch);
                                if (state == 3) {
                                    token += ch;
                                    break;
                                } else {
                                    i++;
                                }
                                token += ch;
                                if (i >= strline.length && j < texts.size() - 1) {
                                    j++;
                                    str = texts.get(j);
                                    strline = str.toCharArray();
                                    i = 0;
                                    ch = strline[i];
                                } else if (i < strline.length && j < texts.size())
                                    ch = strline[i];
                                else {
                                    break;
                                }
                            }
                            // 如果结束的时候注释的自动机处于1或2状态，则有错，说明没有检测到*/
                            if (state == 1 || state == 2) {
                                haveMistake = true;
                            }
                            if (haveMistake) {
                                out2.write("第" + (j + 1) + "行\t" + token + "\t注释/*不封闭\n");
                                // System.out.println("第"+(j+1)+"行\t"+token + "\t注释/*不封闭");
                            } else {
                                // out.write(token+"\t<"+55+", "+" _ >\n");//注释的种别码记为55
                                // System.out.println("注释： "+token);
                            }
                        }
                        token = "";
                    }
                    // 判断是否是字符串类型
                    else if (ch == '"') {
                        Boolean haveMistake = false;
                        token += ch;
                        int state = 1;
                        i++;
                        if (i >= strline.length)
                            break;
                        ch = strline[i];//
                        token = "\"";
                        while (true) {
                            state = StringGetNextStrustate(state, ch);
                            if (state == 2) {
                                token += ch;
                                break;
                            } else {
                                i++;
                            }
                            token += ch;
                            if (i >= strline.length)
                                break;
                            ch = strline[i];
                        }
                        if (state == 1) {
                            haveMistake = true;
                        }
                        if (haveMistake) {
                            out2.write("第" + (j + 1) + "行\t" + token + "\t字符串\"不封闭\n");
                            // System.out.println("第"+(j+1)+"行\t"+token + "\t字符串\"不封闭");
                        } else {
                            out.write(token + "\t<" + 5 + ", " + " _ >\tline" + (j + 1) + "\n");
                            value_pos++;
                            out3.write(token + "     String    " + value_pos + "\n");
                            // System.out.println(token+"\t<"+5+", "+" _ >");
                        }
                        token = "";
                    }
                    // 判断是否是字符串类型
                    else if (ch == '\'') {
                        int haveMistake = -1;
                        token += ch;
                        int state = 1;
                        i++;
                        if (i >= strline.length)
                            break;
                        ch = strline[i];//
                        token = "\'";
                        while (true) {
                            state = charGetNextStrustate(state, ch);
                            if (state == 2) {
                                token += ch;
                                break;
                            } else {
                                i++;
                            }
                            token += ch;
                            if (i >= strline.length)
                                break;
                            ch = strline[i];
                        }
                        if (state == 1) {
                            haveMistake = 1;
                        } else {
                            if (token.length() > 3)
                                haveMistake = 2;
                        }
                        if (haveMistake == 1) {
                            out2.write("第" + (j + 1) + "行\t" + token + "\t字符\'不封闭\n");
                            // System.out.println("第"+(j+1)+"行\t"+token + "\t字符\'不封闭");
                        } else if (haveMistake == 2) {

                            out2.write("第" + (j + 1) + "行\t" + token + "\t字符的长度只能为1\n");
                            // System.out.println("第"+(j+1)+"行\t"+token + "\t字符的长度只能为1");
                        } else {
                            out.write(token + "\t<" + 4 + ", " + " _ >\tline" + (j + 1) + "\n");
                            value_pos++;
                            out3.write(token + "     char    " + value_pos + "\n");
                            // System.out.println(token+"\t<"+5+", "+" _ >");
                        }
                        token = "";
                    }
                    // 不合法字符
                    else {
                        if (ch != ' ' && ch != '\t' && ch != '\0' && ch != '\n' && ch != '\r') {
                            out2.write("第" + (j + 1) + "行\t" + ch + "\t存在不合法字符\n");
                            // System.out.println("第"+(j+1)+"行\t"+token + "\t存在不合法字符");
                        }
                    }
                }
            }
            // if(stack1!=0) {
            // out2.write("(\t(不封闭\n");
            // System.out.println("(\t(不封闭\n");
            // }
            // if(stack2!=0) {
            // out2.write("[\t[不封闭\n");
            // System.out.println("[\t[不封闭\n");
            // }
            // if(stack3!=0) {
            // out2.write("[\t{不封闭\n");
            // System.out.println("[\t{不封闭\n");
            // }
            out.close();
            out1.close();
            out2.close();
            out3.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
