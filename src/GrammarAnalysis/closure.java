package GrammarAnalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class closure {

    static String[] ps = readG();
    static String[][] psforfirst = transformGforFirst();
    static DefaultTableModel model = new DefaultTableModel();
    public String LRtext;
    public char expected;

    public String getLRtext() {
        return LRtext;
    }

    public void setLRtext(String lRtext) {
        LRtext = lRtext;
    }

    public char getExpected() {
        return expected;
    }

    public void setExpected(char expected) {
        this.expected = expected;
    }

    public closure() {

    }

    public closure(String str, char end) {
        this.LRtext = str;
        this.expected = end;
    }

    @Override
    public String toString() {
        return LRtext + ", " + expected;
    }

    static List<List<closure>> Ilist = new ArrayList<List<closure>>();

    static List<itemsGo> GoList = new ArrayList<itemsGo>();

    @SuppressWarnings("resource")
    public static String[] readG() {
        String inputG = "src/GrammarAnalysis/myG2.txt";
        try {
            FileInputStream inG = new FileInputStream(inputG);
            BufferedReader strG = new BufferedReader(new InputStreamReader(inG));
            String line = "";
            int linecount = 0;
            while ((line = strG.readLine()) != null) {
                linecount++;
            }
            String[] Gline = new String[linecount];
            linecount = 0;
            inG = new FileInputStream(inputG);
            strG = new BufferedReader(new InputStreamReader(inG));
            while ((line = strG.readLine()) != null) {
                Gline[linecount] = line;
                linecount++;
            }
            return Gline;
        } catch (Exception e) {
            return null;
        }
    }

    public static String[][] transformGforFirst() {
        String[] ls = readG();
        String[][] fs = new String[ls.length][2];
        int i = 0;
        for (String lsi : ls) {
            fs[i][0] = lsi.substring(0, lsi.indexOf("->"));
            fs[i][1] = lsi.substring(lsi.indexOf("->") + 2, lsi.length());
            i++;

        }
        return fs;
    }

    public static String removerepeatedchar(String s) {
        if (s == null)
            return s;
        StringBuilder sb = new StringBuilder();
        int i = 0, len = s.length();
        while (i < len) {
            char c = s.charAt(i);
            sb.append(c);
            i++;
            while (i < len && s.charAt(i) == c) {
                i++;
            }
        }
        return sb.toString();
    }

    public static List<closure> closureset(List<closure> initlist, closure clo) {

        if (initlist.size() == 0) {
            initlist.add(clo);
        }

        String A = "", a = "", B = "", b = "", e = "";

        String Exp = "^(\\w){1}->([^.]*).([^.]{0,1})([^.]*)$";

        Pattern p = Pattern.compile(Exp);
        Matcher m = p.matcher(clo.LRtext);
        try {
            while (m.find()) {
                A = m.group(1);
                a = m.group(2);
                B = m.group(3);
                b = m.group(4);
                if (a == null) {
                    a = "";
                }
                if (B == null) {
                    B = "";
                }
                if (b == null) {
                    b = "";
                }
            }

            List<String> alist = findn(B.charAt(0));

            if (b.equals("B") || b.equals("C")) {
                e = String.valueOf(clo.expected);
            }
            e = e + getFirsts(b + clo.expected);
            e = removerepeatedchar(e);

            for (int k = 0; k < e.length(); k++) {
                for (int i = 0; i < alist.size(); i++) {
                    closure clouse2;
                    if (alist.get(i).equals("q")) {
                        if (e.charAt(k) != 'q') {
                            clouse2 = new closure(B + "->" + alist.get(i) + ".", e.charAt(k));
                            if (!isExist(initlist, clouse2)) {
                                initlist.add(clouse2);
                                closureset(initlist, clouse2);
                            }
                            System.out.println(e.charAt(k));
                        }
                    } else {
                        if (e.charAt(k) != 'q') {
                            clouse2 = new closure(B + "->." + alist.get(i), e.charAt(k));
                            if (!isExist(initlist, clouse2)) {
                                initlist.add(clouse2);
                                closureset(initlist, clouse2);
                            }
                        }

                    }
                }
            }
            return initlist;
        } catch (Exception e2) {
            return initlist;
        }
    }

    public static List<String> findn(char head) {
        List<String> nl = new ArrayList<String>();
        for (int i = 1; i < ps.length; i++) {
            if (ps[i].charAt(0) == head) {
                nl.add(ps[i].substring(3, ps[i].length()));
            }
        }
        return nl;
    }

    /*
     * the Following is GO function
     */

    public static List<closure> GO(List<closure> li, char ch) {
        List<closure> sonList = new ArrayList<closure>();
        for (int i = 0; i < li.size(); i++) {
            if (li.get(i).LRtext.indexOf('.') + 1 != li.get(i).LRtext.length()) {
                if (li.get(i).LRtext.charAt(li.get(i).LRtext.indexOf('.') + 1) == ch) {
                    StringBuffer buffer = new StringBuffer(li.get(i).LRtext);
                    buffer.setCharAt(li.get(i).LRtext.indexOf('.'),
                            li.get(i).LRtext.charAt(li.get(i).LRtext.indexOf('.') + 1));
                    buffer.setCharAt(li.get(i).LRtext.indexOf('.') + 1, '.');
                    String bufferS = buffer.toString();
                    closure clo = new closure(bufferS, li.get(i).expected);
                    if (!sonList.contains(clo))
                        sonList.add(clo);
                }
            }
        }

        int len = sonList.size();
        for (int k = 0; k < len; k++) {
            sonList = closureset(sonList, sonList.get(k));
        }
        return sonList;
    }

    public static boolean isExist(List<closure> list, closure clo) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).LRtext.equals(clo.LRtext) && list.get(i).expected == clo.expected) {
                return true;
            }
        }
        return false;
    }

    /*
     * end of GO
     */
    // 语法分析的主函数
    public String YFmain(String testsh) throws IOException {
        String firstStr = "";

        String action_result = "src/GrammarAnalysis/action_result.txt";
        String goto_result = "src/GrammarAnalysis/goto_result.txt";
        String item_result = "src/GrammarAnalysis/item_result.txt";

        BufferedWriter action_output = new BufferedWriter(new FileWriter(action_result));
        BufferedWriter goto_output = new BufferedWriter(new FileWriter(goto_result));
        BufferedWriter item_output = new BufferedWriter(new FileWriter(item_result));

        firstStr = ps[0].substring(0, ps[0].indexOf("->") + 2) + '.'
                + ps[0].substring(ps[0].indexOf("->") + 2, ps[0].length());
        List<closure> list1 = new ArrayList<closure>();

        list1 = closureset(list1, new closure(firstStr, '#'));

        Ilist.add(list1);
        GoList.add(new itemsGo(list1, 0, -1, ' '));

        getAlllist(new itemsGo(list1, 0, -1, ' '));

        for (int i = 0; i < GoList.size(); i++) {
            item_output.write(GoList.get(i).toString());
        }

        List<action_table> lis = createtable(ps, GoList);
        for (int wa = 0; wa < lis.size(); wa++) {
            action_output.write(String.format(GoList.get(wa).name + ": "));
            action_output.write(String.format(lis.get(wa).ch + "") + System.getProperty("line.separator"));
            action_output.write(String.format(lis.get(wa).value + "") + System.getProperty("line.separator"));
        }
        List<goto_table> lis2 = createtable_goto(ps, GoList);
        for (int wao = 0; wao < lis2.size(); wao++) {
            goto_output.write(String.format(GoList.get(wao).name + ": "));
            goto_output.write(String.format(lis2.get(wao).ch + "") + System.getProperty("line.separator"));
            goto_output.write(String.format(lis2.get(wao).value + "") + System.getProperty("line.separator"));
        }

        action_output.flush();
        action_output.close();
        goto_output.flush();
        goto_output.close();
        item_output.flush();
        item_output.close();
        String s = LRanalyze(testsh, lis, lis2);
        JTable table = new JTable(model);
        JFrame jFrame = new JFrame("分析表");
        jFrame.add(new JScrollPane(table));
        jFrame.setSize(1000, 700);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrame.setVisible(true);
        return s;
    }

    static int count = 0;

    public static void getAlllist(itemsGo firstlist) {
        Stack<Character> stack = new Stack<Character>();
        for (int i = 0; i < firstlist.Iitem.size(); i++) {
            if (firstlist.Iitem.get(i).LRtext.indexOf('.') + 1 != firstlist.Iitem.get(i).LRtext.length()) {
                if (!stack.contains(
                        firstlist.Iitem.get(i).LRtext.charAt(firstlist.Iitem.get(i).LRtext.indexOf('.') + 1))) {
                    stack.push(firstlist.Iitem.get(i).LRtext.charAt(firstlist.Iitem.get(i).LRtext.indexOf('.') + 1));
                }
            }
        }
        int len = stack.size();
        for (int j = 0; j < len; j++) {
            char tmp = stack.pop();
            List<closure> list = new ArrayList<closure>();
            list = GO(firstlist.Iitem, tmp);
            if (!ifinIlist(list)) {
                count++;
                int name = count;
                Ilist.add(list);
                GoList.add(new itemsGo(list, name, firstlist.name, tmp));
                getAlllist(new itemsGo(list, name, firstlist.name, tmp));
            }
        }
    }

    public static boolean ifinIlist(List<closure> testlist) {
        for (int i = 0; i < Ilist.size(); i++) {
            if (equals(Ilist.get(i), testlist)) {
                return true;
            }
        }
        return false;
    }

    public static int ifinIlist_findname(List<closure> testlist) {
        for (int i = 0; i < GoList.size(); i++) {
            if (equals(GoList.get(i).Iitem, testlist)) {
                return GoList.get(i).name;
            }
        }
        return -1;
    }

    public static boolean equals(List<closure> listone, List<closure> listtwo) {
        if (listone.size() != listtwo.size())
            return false;
        else {
            for (int i = 0; i < listtwo.size(); i++) {
                if (!isExist(listone, listtwo.get(i))) {
                    return false;
                }
            }
            return true;
        }
    }

    /*
     * Construct the action table
     */
    public static List<action_table> createtable(String ps[], List<itemsGo> GoList) {

        List<action_table> actionlist = new ArrayList<action_table>();

        String str = "";
        for (String item : ps) {
            item = item.substring(0, 1) + item.substring(3, item.length());
            str += item;
        }
        str = str.substring(1, str.length());

        Stack<Character> stack = new Stack<Character>();

        for (int i = 0; i < str.length(); i++) {
            if (!stack.contains(str.charAt(i)) && str.charAt(i)!='q')
                stack.push(str.charAt(i));
        }
        stack.push('#');

        for (int j = 0; j < GoList.size(); j++) {
            // every row:
            action_table action = new action_table();
            for (char stackitem : stack) {
                if (!(stackitem <= 'Z' && stackitem >= 'A')) {
                    int key = ifinIlist_findname(GO(GoList.get(j).Iitem, stackitem));
                    if (key >= 0) {
                        action.ch.add(stackitem);
                        action.value.add("S" + Integer.toString(key));
                    } else {
                        int hit = condition2(GoList.get(j).Iitem, stackitem);
                        if (hit > -1) {
                            if (hit == 1) {
                                action.ch.add(stackitem);
                                action.value.add("acc");
                            } else {
                                action.ch.add(stackitem);
                                action.value.add("r" + Integer.valueOf(hit));
                            }
                        } else {
                            action.ch.add(stackitem);
                            action.value.add("error");
                        }
                    }
                }
            }
            actionlist.add(action);
        }
        return actionlist;
    }

    /*
     * Construct the action table
     */
    public static List<goto_table> createtable_goto(String ps[], List<itemsGo> GoList) {
        List<goto_table> gotolist = new ArrayList<goto_table>();

        String str = "";
        for (String item : ps) {
            item = item.substring(0, 1) + item.substring(3, item.length());
            str += item;
        }
        str = str.substring(1, str.length());

        Stack<Character> stack = new Stack<Character>();

        for (int i = 0; i < str.length(); i++) {
            if (!stack.contains(str.charAt(i)))
                stack.push(str.charAt(i));
        }

        for (int j = 0; j < GoList.size(); j++) {
            // every row:
            goto_table gotos = new goto_table();
            for (char stackitem : stack) {
                if (stackitem <= 'Z' && stackitem >= 'A') {
                    int key = ifinIlist_findname(GO(GoList.get(j).Iitem, stackitem));
                    if (key >= 0) {
                        gotos.ch.add(stackitem);
                        gotos.value.add(Integer.toString(key));
                    } else {
                        gotos.ch.add(stackitem);
                        gotos.value.add("error");
                    }
                }
            }
            gotolist.add(gotos);
        }
        return gotolist;
    }

    public static int condition2(List<closure> list, char ch) {

        for (closure item : list) {
            if (item.expected == ch) {
                if (item.LRtext.indexOf('.') == item.LRtext.length() - 1) {
                    String used = item.LRtext.substring(0, item.LRtext.indexOf('.'))
                            + item.LRtext.substring(item.LRtext.indexOf('.') + 1, item.LRtext.length());
                    int fran = ifinG(readG(), used);
                    if (fran > -1) {
                        return fran;
                    }
                }
            }
        }
        return -1;
    }

    public static int ifinG(String[] ps, String str) {
        for (int i = 0; i < ps.length; i++) {
            if (ps[i].equals(str)) {
                return i + 1;
            }
        }
        return -1;
    }

    public static boolean exits(char ch, String str) {
        for (int i = 0; i < str.length(); i++) {
            if (ch == str.charAt(i)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isV(char ch) {
        if (ch >= 'A' && ch <= 'Z')
            return true;
        return false;
    }

    public static String getFirst(char ch) { // get First Set (ch)
        String result = "";
        String itsfirstch = "";
        if (isV(ch)) {
            itsfirstch = itsFirst(ch);
            for (int i = 0; i < itsfirstch.length(); i++) {
                if (!exits(itsfirstch.charAt(i), result))
                    result += getFirst(itsfirstch.charAt(i));
            }
        } else {/* it is T */
            if (!exits(ch, result))
                result += String.valueOf(ch);
            // System.out.println("2");
        }
        return result;
    }

    public static String getFirsts(String set) { // get First Set (String)
        String result = "";

        result += getFirst(set.charAt(0));
        return result;
    }

    public static String itsFirst(char ch) {

        String res = "";
        for (int i = 0; i < psforfirst.length; i++) {
            if (psforfirst[i][0].charAt(0) == ch) {
                if (psforfirst[i][1].equals("")) {
                    res += '#';
                } else {
                    if (psforfirst[i][1].charAt(0) != ch) {
                        res += psforfirst[i][1].charAt(0);
                    }
                }
            }
        }
        return res;
    }

    /// end of first thing
    static translate tran = new translate();

    @SuppressWarnings("resource")
    public static String LRanalyze(String test, List<action_table> actiontab, List<goto_table> gototab)
            throws IOException {
        // DefaultTableModel model=new DefaultTableModel();
        model.setColumnIdentifiers(new Object[] { "状态栈", "字符栈", "待分析句子", "动作" });
        System.out.println("-------------------正在进行语法分析-----------------------");

        String output_an = "src/GrammarAnalysis/out_analyze.txt";
        String outsr = "src/GrammarAnalysis/usewhat.txt";
        String outsr1 = "src/GrammarAnalysis/test.txt";
        BufferedWriter output = new BufferedWriter(new FileWriter(output_an));
        BufferedWriter output2 = new BufferedWriter(new FileWriter(outsr));
        BufferedWriter output1 = new BufferedWriter(new FileWriter(outsr1));

        Stack<Character> chStack = new Stack<Character>();
        Stack<Integer> statusStack = new Stack<Integer>();
        chStack.push('#');
        statusStack.push(0);
        String tempStr = test + "#";

        int ip = 0;
        String tmpuse;
        while (true) {
            output.write(String.format("Status Stack now have : " + putStack(statusStack))
                    + System.getProperty("line.separator"));
            output.write(String.format("Char Stack now have : " + putsStack(chStack))
                    + System.getProperty("line.separator"));
            int topstatus = statusStack.peek();
            if ((tmpuse = find_in_action(actiontab, topstatus, tempStr.charAt(ip))) != null) {
                if (tmpuse.charAt(0) == 'S') {
                    int stmt = 0;
                    if (tmpuse.length() == 2) {
                        stmt = tmpuse.charAt(1) - 48;
                    } else if (tmpuse.length() > 2) {
                        stmt = Integer.valueOf(tmpuse.substring(1, tmpuse.length()));
                    }
                    model.addRow(new Object[] { putStack(statusStack), putsStack(chStack),
                            tempStr.substring(ip, tempStr.length()), tmpuse });
                    output.write(String.format("Use Action->" + tmpuse) + System.getProperty("line.separator"));
                    statusStack.push(stmt);
                    chStack.push(tempStr.charAt(ip));
                    output.write(String.format("Status Stack push :" + stmt + System.getProperty("line.separator")));
                    output.write(String.format("Char Stack push : " + tempStr.charAt(ip))
                            + System.getProperty("line.separator"));

                    ip++;
                } else if (tmpuse.charAt(0) == 'r') {
                    int linenum = 0;
                    if (tmpuse.length() == 2) {
                        linenum = tmpuse.charAt(1) - 48;
                    } else if (tmpuse.length() > 2) {
                        linenum = Integer.valueOf(tmpuse.substring(1, tmpuse.length()));
                    }
                    // newt.getUse(linenum);
                    output2.write(
                            String.format("" + linenum + ":" + ps[linenum - 1]) + System.getProperty("line.separator"));
                    int length = ps[linenum - 1].length() - 3;
                    model.addRow(new Object[] { putStack(statusStack), putsStack(chStack),
                            tempStr.substring(ip, tempStr.length()), tmpuse });
                    if (ps[linenum - 1].charAt(3) == 'q') {
                       // chStack.push(ps[linenum - 1].charAt(0));
                    } else {
                        for (int k = 0; k < length; k++) {
                            chStack.pop();
                            statusStack.pop();
                        }
                    }

                    output.write(String.format("Status and Char Stack pop " + length + " element(s)")
                            + System.getProperty("line.separator"));
                    int topstatus2 = statusStack.peek();
                    chStack.push(ps[linenum - 1].charAt(0));

                    String tmpStr = find_in_goto(gototab, topstatus2, ps[linenum - 1].charAt(0));

                    if (tmpStr != null) {
                        model.addRow(new Object[] { putStack(statusStack), putsStack(chStack),
                                tempStr.substring(ip, tempStr.length()),
                                "GOTO(" + statusStack.peek() + "," + chStack.peek() + ")=" + tmpStr });
                        statusStack.push(Integer.parseInt(tmpStr));
                        output.write(String.format("Status Stack push " + Integer.parseInt(tmpStr))
                                + System.getProperty("line.separator"));
                    }
                    output.write(String.format("Use Goto->" + tmpuse) + System.getProperty("line.separator"));

                } else if (tmpuse.equals("acc")) {
                    // System.out.println("Have been accepted!");
                    output.write(String.format("Now Accepted!") + System.getProperty("line.separator"));
                    output2.write(String.format("1:" + ps[0]) + System.getProperty("line.separator"));
                    output.flush();
                    output.close();
                    output2.flush();
                    output2.close();
                    model.addRow(new Object[] { putStack(statusStack), putsStack(chStack),
                            tempStr.substring(ip, tempStr.length()), tmpuse });
                    // newt.showAllUse();
                    return "Have been accepted!";
                } else if (tmpuse.equals("error")) {
                    System.out.println(tran.find_error(ip));
                    model.addRow(new Object[] { putStack(statusStack), putsStack(chStack),
                            tempStr.substring(ip, tempStr.length()), tmpuse });
                    // while(tempStr.charAt(ip)!=';') {
                    // ip++;
                    // }
                    System.out.println("Error in character:" + tempStr.charAt(ip));
                    if (tempStr.charAt(ip) != '#')
                        ip++;
                    // while (!isV(chStack.peek())) {
                    // chStack.pop();
                    // statusStack.pop();
                    // model.addRow(new
                    // Object[]{putStack(statusStack),putsStack(chStack),tempStr.substring(ip,tempStr.length()),"错误处理"});
                    // }

                    output.write(String.format("Error!") + System.getProperty("line.separator"));

                    // output.flush();
                    // output.close();
                    //
                    if (tempStr.charAt(ip) == '#')
                        return "Error in the end!";
                    // return tran.find_error(ip);
                }
            }
        }

    }

//    public static String LRanalyze1(String test, List<action_table> actiontab, List<goto_table> gototab)
//            throws IOException {
//        // DefaultTableModel model=new DefaultTableModel();
//        model.setColumnIdentifiers(new Object[] { "状态栈", "字符栈", "待分析句子", "动作" });
//        System.out.println("-------------------正在进行语法分析-----------------------");
//
//        String output_an = "src/GrammarAnalysis/out_analyze.txt";
//        String outsr = "src/GrammarAnalysis/usewhat.txt";
//        BufferedWriter output = new BufferedWriter(new FileWriter(output_an));
//        BufferedWriter output2 = new BufferedWriter(new FileWriter(outsr));
//
//        Stack<Character> chStack = new Stack<Character>();
//        Stack<Integer> statusStack = new Stack<Integer>();
//        chStack.push('#');
//        statusStack.push(0);
//        String tempStr = test + "#";
//        int ip = 0;
//        String tmpuse;
//        while (true) {
//            output.write(String.format("Status Stack now have : " + putStack(statusStack))
//                    + System.getProperty("line.separator"));
//            output.write(String.format("Char Stack now have : " + putsStack(chStack))
//                    + System.getProperty("line.separator"));
//            int topstatus = statusStack.peek();
//            if ((tmpuse = find_in_action(actiontab, topstatus, tempStr.charAt(ip))) != null) {
//                if (tmpuse.charAt(0) == 'S') {
//                    int stmt = 0;
//                    if (tmpuse.length() == 2) {
//                        stmt = tmpuse.charAt(1) - 48;
//                    } else if (tmpuse.length() > 2) {
//                        stmt = Integer.valueOf(tmpuse.substring(1, tmpuse.length()));
//                    }
//                    model.addRow(new Object[] { putStack(statusStack), putsStack(chStack),
//                            tempStr.substring(ip, tempStr.length()), tmpuse });
//                    output.write(String.format("Use Action->" + tmpuse) + System.getProperty("line.separator"));
//                    statusStack.push(stmt);
//                    chStack.push(tempStr.charAt(ip));
//                    output.write(String.format("Status Stack push :" + stmt + System.getProperty("line.separator")));
//                    output.write(String.format("Char Stack push : " + tempStr.charAt(ip))
//                            + System.getProperty("line.separator"));
//
//                    ip++;
//                } else if (tmpuse.charAt(0) == 'r') {
//                    int linenum = 0;
//                    if (tmpuse.length() == 2) {
//                        linenum = tmpuse.charAt(1) - 48;
//                    } else if (tmpuse.length() > 2) {
//                        linenum = Integer.valueOf(tmpuse.substring(1, tmpuse.length()));
//                    }
//                    // newt.getUse(linenum);
//                    output2.write(
//                            String.format("" + linenum + ":" + ps[linenum - 1]) + System.getProperty("line.separator"));
//                    int length = ps[linenum - 1].length() - 3;
//                    model.addRow(new Object[] { putStack(statusStack), putsStack(chStack),
//                            tempStr.substring(ip, tempStr.length()), tmpuse });
//                    for (int k = 0; k < length; k++) {
//                        chStack.pop();
//                        statusStack.pop();
//                    }
//                    output.write(String.format("Status and Char Stack pop " + length + " element(s)")
//                            + System.getProperty("line.separator"));
//                    int topstatus2 = statusStack.peek();
//                    chStack.push(ps[linenum - 1].charAt(0));
//
//                    String tmpStr = find_in_goto(gototab, topstatus2, ps[linenum - 1].charAt(0));
//
//                    if (tmpStr != null) {
//                        model.addRow(new Object[] { putStack(statusStack), putsStack(chStack),
//                                tempStr.substring(ip, tempStr.length()),
//                                "GOTO(" + statusStack.peek() + "," + chStack.peek() + ")=" + tmpStr });
//                        statusStack.push(Integer.parseInt(tmpStr));
//                        output.write(String.format("Status Stack push " + Integer.parseInt(tmpStr))
//                                + System.getProperty("line.separator"));
//                    }
//                    output.write(String.format("Use Goto->" + tmpuse) + System.getProperty("line.separator"));
//
//                } else if (tmpuse.equals("acc")) {
//                    // System.out.println("Have been accepted!");
//                    output.write(String.format("Now Accepted!") + System.getProperty("line.separator"));
//                    output2.write(String.format("1:" + ps[0]) + System.getProperty("line.separator"));
//                    output.flush();
//                    output.close();
//                    output2.flush();
//                    output2.close();
//                    model.addRow(new Object[] { putStack(statusStack), putsStack(chStack),
//                            tempStr.substring(ip, tempStr.length()), tmpuse });
//                    // newt.showAllUse();
//                    return "Have been accepted!";
//                } else if (tmpuse.equals("error")) {
//                    System.out.println(tran.find_error(ip));
//                    model.addRow(new Object[] { putStack(statusStack), putsStack(chStack),
//                            tempStr.substring(ip, tempStr.length()), tmpuse });
//                    // while(tempStr.charAt(ip)!=';') {
//                    // ip++;
//                    // }
//                    System.out.println("Error in character:" + tempStr.charAt(ip));
//                    if (tempStr.charAt(ip) != '#')
//                        ip++;
//                    // while (!isV(chStack.peek())) {
//                    // chStack.pop();
//                    // statusStack.pop();
//                    // model.addRow(new
//                    // Object[]{putStack(statusStack),putsStack(chStack),tempStr.substring(ip,tempStr.length()),"错误处理"});
//                    // }
//
//                    output.write(String.format("Error!") + System.getProperty("line.separator"));
//
//                    // output.flush();
//                    // output.close();
//                    //
//                    if (tempStr.charAt(ip) == '#')
//                        return "Error in the end!";
//                    // return tran.find_error(ip);
//                }
//            }
//        }
//
//    }

    public static String find_in_action(List<action_table> actiontab, int status, char ch) {
        // actiontab.get(0).value
        List<Character> templist = actiontab.get(status).ch;
        int ret = 0;
        String res = "";
        for (Character item : templist) {
            if (item == ch) {
                res = actiontab.get(status).value.get(ret);
                return res;
            }
            ret++;
        }
        return null;
    }

    public static String find_in_goto(List<goto_table> gototab, int status, char ch) {
        // actiontab.get(0).value
        List<Character> templist = gototab.get(status).ch;
        int ret = 0;
        for (Character item : templist) {
            if (item == ch) {
                return gototab.get(status).value.get(ret);
            }
            ret++;
        }
        // System.out.println(res+"~~~~~~~~~~~~~~~~~~~~");
        return null;
    }

    public static void printstack(Stack<?> stack) {
        int len = stack.size();
        for (int i = 0; i < len; i++) {
            // System.out.println(stack.peek());
            stack.pop();
        }
    }

    public static String putsStack(Stack<?> stack) {
        String things = "";
        for (Object item : stack) {
            things += item.toString();
        }
        return things;
    }

    public static String putStack(Stack<?> stack) {
        String things = "";
        for (Object item : stack) {
            things = things + "|" + item.toString();
        }
        return things;
    }
}
