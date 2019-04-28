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
    // �ؼ��֣��ֱ����6��ʼ
    public static String keywords[] = { "int", "float", "char", "blooean", "String", "void", "while", "return", "true",
            "false", "if", "else", "do", "call", "proc", "struct" };
    // public static String keywordstoken[] = { "INT","FLOAT", "DOUBLE",
    // "IF","ELSE","DO","WHILE",
    // "CONTINUE","BREAK","TYPEDEF","STRUCT","CONST","CHAR","STATIC"};
    // �����
    public static char operator[] = { '+', '-', '*', '=', '<', '>', '&', '|', '~', '^', '!', '%' };
    // �ֱ��룺22��ʼ
    public static String operatortoken[] = { "+", "-", "*", "/", "<", ">", "&", "|", "~", "^", "!", "%", "++", "--",
            ">>", "<<", "&&", "||", "!=", ">=", "<=", "+=", "-=", "*=", "/=" };

    // �������ɼӵ���
    public static Boolean isPlusEqu(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '=' || ch == '>' || ch == '<' || ch == '&'
                || ch == '|' || ch == '^' || ch == '!';
    }

    // �����������������һ��
    public static Boolean isPlusSame(char ch) {
        return ch == '+' || ch == '-' || ch == '&' || ch == '|' || ch == '<' || ch == '>';
    }

    // ������ֱ���47��ʼ
    public static char boundary[] = { '=', ',', ';', '[', ']', '(', ')', '{', '}' };
    public static String boundarytoken[] = { "=", ",", ";", "[", "]", "(", ")", "{", "}" };

    // �ж���ĸ���»���
    public static Boolean isAlpha(char ch) {
        return ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_');
    }

    // �ж�����
    public static Boolean isDigit(char ch) {
        return (ch >= '0' && ch <= '9');
    }

    // �ж��Ƿ��������
    public static Boolean isOp(char ch) {
        for (int i = 0; i < operator.length; i++) {
            if (ch == operator[i]) {
                return true;
            }
        }
        return false;
    }

    // �ж��Ƿ��ǽ������=���Ѿ���������ﴦ����ˣ��������ڲ����ǵȺ�
    public static Boolean isBound(char ch) {
        for (int i = 0; i < boundary.length; i++) {
            if (ch == boundary[i] && ch != '=') {
                return true;
            }
        }
        return false;
    }

    // �жϹؼ���,���ظùؼ��ֵ��ֱ���
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

    // �ж������ַ����ֱ���
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

    // �жϽ�����ֱ���
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

    // ���ֳ���DFA�����һ��״̬
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

    // ע�͵�DFA�����һ��״̬
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

    // �ַ�������DFA�����һ��״̬
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

    // �ַ�����DFA�����һ��״̬
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

    // ���ļ�
    public static ArrayList<String> readfile(File file) {
        ArrayList<String> strings = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));// ����һ��BufferedReader������ȡ�ļ�
            String s = null;
            while ((s = br.readLine()) != null) {// ʹ��readLine������һ�ζ�һ��
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
        // int stack1=0;//�����ж�'('�Ƿ���
        // int stack2=0;//�����ж�'['�Ƿ���
        // int stack3=0;//�����ж�'{'�Ƿ���
        texts = readfile(file);
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("src/lexicalAnalysis/token.txt"));
            BufferedWriter out1 = new BufferedWriter(new FileWriter("src/lexicalAnalysis/symbol.txt"));
            BufferedWriter out2 = new BufferedWriter(new FileWriter("src/lexicalAnalysis/error.txt"));
            BufferedWriter out3 = new BufferedWriter(new FileWriter("src/lexicalAnalysis/value.txt"));
            // symbol.txt�Ƿ��ű�
            // String str = "";
            // for(int j = 0; j < texts.size(); j++) {
            // str=str+texts.get(j);
            // }
            for (int j = 0; j < texts.size(); j++) {
                String str = texts.get(j);
                char[] strline = str.toCharArray();
                for (int i = 0; i < strline.length; i++) {
                    // ����strline�е�ÿ���ַ�
                    char ch = strline[i];
                    // ��ʼ��token�ַ���Ϊ��
                    String token = "";
                    // �жϱ�ʶ�͹ؼ���
                    if (isAlpha(ch)) {
                        do {
                            token += ch;
                            i++;
                            if (i >= strline.length)
                                break;
                            ch = strline[i];
                        } while (ch != '\0' && (isAlpha(ch) || isDigit(ch)));
                        --i;// ָ����ˣ��м�����1
                        // �ǹؼ���
                        int pos = isMatchKeyword(token.toString());
                        if (pos != 5) {
                            out.write(token + "\t<" + (pos) + ", _ >\tline" + (j + 1) + "\n");
                            // System.out.println(token+"\t<"+(pos)+", _ >");
                        }
                        // �Ǳ�ʶ��,��ʶ�����ֱ���Ϊ1
                        else {
                            // ������ű�Ϊ�ջ���ű��в�������ǰtoken�������
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
                    // �ж����ֳ���
                    else if (isDigit(ch)) {
                        // ��ʼ������1״̬
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
                            // �м�����1
                            i++;
                            if (i >= strline.length)
                                break;
                            ch = strline[i];
                        }
                        Boolean haveMistake = false;
                        if (state == 2 || state == 4 || state == 5 || state == 7) {
                            haveMistake = true;
                        }
                        // ������
                        if (haveMistake) {
                            // һֱ�����ɷָ���ַ�����
                            while (ch != '\0' && !isBound(ch) && ch != ' ') {
                                token += ch;
                                i++;
                                if (i >= strline.length)
                                    break;
                                ch = strline[i];
                            }
                            out2.write("��" + (j + 1) + "��\t" + token + "\t��ȷ���޷��ų���������ȷ\n");
                            // System.out.println("��"+(j+1)+"��\t"+token + "\t��ȷ���޷��ų���������ȷ");
                        } else {
                            if (isfloat) {
                                value_pos++;
                                out.write(token + "\t<3, " + token + " >\tline" + (j + 1) + "\n");
                                out3.write(token + "     float    " + (j+1) + "\n");
                                // System.out.println(token+"\t<3, "+token+" >");// �����ͳ���
                            } else {
                                value_pos++;
                                out.write(token + "\t<2, " + token + " >\tline" + (j + 1) + "\n");
                                out3.write(token + "     int    " + (j+1) + "\n");
                                // System.out.println(token+"\t<2, "+token+" >");//���γ���
                            }
                        }
                        i--;
                        token = "";
                    }
                    // ʶ�������
                    else if (isOp(ch)) {
                        token += ch;
                        // ���������һ��"="
                        if (isPlusEqu(ch)) {
                            i++;
                            if (i >= strline.length)
                                break;
                            ch = strline[i];
                            if (ch == '=')
                                token += ch;
                            else
                            // �жϸ÷��ź����ǲ��ǿ������ź��Լ�һ����
                            {
                                if (isPlusSame(strline[i - 1]) && ch == strline[i - 1]) {
                                    token += ch;
                                } else
                                    --i;
                            }
                        }
                        // �������ֵ�����ĵ���
                        if (token.equals("=")) {
                            int pos = isMatchbound(token);
                            out.write(token + "\t<" + pos + ", " + " _ >\tline" + (j + 1) + "\n");
                            // System.out.println(token+"\t<"+pos+", "+token+" >");
                        }
                        // �����
                        else {
                            int pos = isMatchOP(token);
                            out.write(token + "\t<" + pos + ", " + " _ >\tline" + (j + 1) + "\n");
                            // System.out.println(token+"\t<"+pos+", "+token+" >");
                        }
                        token = "";
                    }
                    // ʶ����
                    else if (isBound(ch)) {
                        token += ch;
                        // if(stack1<0) {
                        // stack1=0;
                        // out2.write(token + "\tû����֮ƥ���������\n");
                        // System.out.println(token + "\tû����֮ƥ���������");
                        // }
                        // if(stack2<0) {
                        // stack2=0;
                        // out2.write(token + "\tû����֮ƥ���������\n");
                        // System.out.println(token + "\tû����֮ƥ���������");
                        // }
                        // if(stack3<0) {
                        // stack3=0;
                        // out2.write(token + "\tû����֮ƥ���������\n");
                        // System.out.println(token + "\tû����֮ƥ���������");
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
                    // ʶ��ע��
                    else if (ch == '/') {
                        token += ch;
                        i++;
                        if (i >= strline.length)
                            break;
                        ch = strline[i];
                        if (ch != '*') {
                            // ��ʱʶ�𵽵�/����������еĳ���������
                            if (ch == '=') {
                                token += ch; // /=
                            } else {
                                --i; // / �м�����1
                            }
                            int pos = isMatchbound(token);
                            // out.write(token+"\t<"+pos+", "+" _ >\tline"+(j+1)+"\n");
                            System.out.println(token + "\t<" + pos + ", " + token + " >");
                        }
                        // ����ʶ��/*
                        else {
                            Boolean haveMistake = false;
                            // ��ʼ������1״̬
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
                            // ���������ʱ��ע�͵��Զ�������1��2״̬�����д�˵��û�м�⵽*/
                            if (state == 1 || state == 2) {
                                haveMistake = true;
                            }
                            if (haveMistake) {
                                out2.write("��" + (j + 1) + "��\t" + token + "\tע��/*�����\n");
                                // System.out.println("��"+(j+1)+"��\t"+token + "\tע��/*�����");
                            } else {
                                // out.write(token+"\t<"+55+", "+" _ >\n");//ע�͵��ֱ����Ϊ55
                                // System.out.println("ע�ͣ� "+token);
                            }
                        }
                        token = "";
                    }
                    // �ж��Ƿ����ַ�������
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
                            out2.write("��" + (j + 1) + "��\t" + token + "\t�ַ���\"�����\n");
                            // System.out.println("��"+(j+1)+"��\t"+token + "\t�ַ���\"�����");
                        } else {
                            out.write(token + "\t<" + 5 + ", " + " _ >\tline" + (j + 1) + "\n");
                            value_pos++;
                            out3.write(token + "     String    " + value_pos + "\n");
                            // System.out.println(token+"\t<"+5+", "+" _ >");
                        }
                        token = "";
                    }
                    // �ж��Ƿ����ַ�������
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
                            out2.write("��" + (j + 1) + "��\t" + token + "\t�ַ�\'�����\n");
                            // System.out.println("��"+(j+1)+"��\t"+token + "\t�ַ�\'�����");
                        } else if (haveMistake == 2) {

                            out2.write("��" + (j + 1) + "��\t" + token + "\t�ַ��ĳ���ֻ��Ϊ1\n");
                            // System.out.println("��"+(j+1)+"��\t"+token + "\t�ַ��ĳ���ֻ��Ϊ1");
                        } else {
                            out.write(token + "\t<" + 4 + ", " + " _ >\tline" + (j + 1) + "\n");
                            value_pos++;
                            out3.write(token + "     char    " + value_pos + "\n");
                            // System.out.println(token+"\t<"+5+", "+" _ >");
                        }
                        token = "";
                    }
                    // ���Ϸ��ַ�
                    else {
                        if (ch != ' ' && ch != '\t' && ch != '\0' && ch != '\n' && ch != '\r') {
                            out2.write("��" + (j + 1) + "��\t" + ch + "\t���ڲ��Ϸ��ַ�\n");
                            // System.out.println("��"+(j+1)+"��\t"+token + "\t���ڲ��Ϸ��ַ�");
                        }
                    }
                }
            }
            // if(stack1!=0) {
            // out2.write("(\t(�����\n");
            // System.out.println("(\t(�����\n");
            // }
            // if(stack2!=0) {
            // out2.write("[\t[�����\n");
            // System.out.println("[\t[�����\n");
            // }
            // if(stack3!=0) {
            // out2.write("[\t{�����\n");
            // System.out.println("[\t{�����\n");
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
