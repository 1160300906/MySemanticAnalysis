package SemanticAnalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Entity.AddrNum;
import Entity.CharTable;
import Entity.FourAddr;
import Entity.RreeSemanticRecord;
import Entity.LRTree;
import Entity.grammerSemanticLoca;

public class Main {

    static String[] ps = readG();// ���еĲ���ʽ
    static ArrayList<LRTree> slrTreeArray = getUse2();// ��������õ����﷨��
    static HashMap<String, Integer> map=new HashMap<String, Integer>();
    static ArrayList<String> error=new ArrayList<String>();
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
            strG.close();
            return Gline;
        } catch (Exception e) {
            return null;
        }
    }

    public static int findGnum(String g) {
        for (int i = 0; i < ps.length; i++) {
            if (ps[i].equals(g)) {
                return i + 1;
            }
        }
        return -1;
    }

    public static boolean isV(char ch) {
        if (ch >= 'A' && ch <= 'Z')
            return true;
        return false;
    }

    public static grammerSemanticLoca[] GrammerSemanticLoca() throws Exception {
        String inputG1 = "src/SemanticAnalysis/SemanticLoca.txt";
        grammerSemanticLoca[] w = new grammerSemanticLoca[46];
        FileInputStream inG1 = new FileInputStream(inputG1);
        BufferedReader strG1 = new BufferedReader(new InputStreamReader(inG1));
        String line2 = "";
        int i = 0;
        while ((line2 = strG1.readLine()) != null) {
            Pattern p = Pattern.compile("([^\\s]+)(\\s+)([^\\s]+)(\\s+)(\\d+)$");
            Matcher m = p.matcher(line2);
            if (m.find()) {
                w[i] = new grammerSemanticLoca();
                w[i].setGrammerNum(Integer.parseInt(m.group(1)));
                w[i].setRulelLoc(Integer.parseInt(m.group(3)));
                w[i].setRuleNum(Integer.parseInt(m.group(5)));
                i++;
            }
        }
        strG1.close();
        // ������������ʽ��Ӧ����
        System.out.println("������������ʽ��Ӧ����==========");
        // for(int i1=0;i1<w.length;i1++)
        // {
        // System.out.println(w[i1].getGrammerNum()+" "+w[i1].getRulelLoc()+"
        // "+w[i1].getRuleNum());
        // }
        System.out.println("������������ʽ��Ӧ���н���==========");
        return w;
    }

    // TODO
    public static ArrayList<LRTree> getUse2() {
        List<String> huseG = new ArrayList<String>(); // ��Լ�ķ�
        ArrayList<LRTree> slrTreeArray = new ArrayList<LRTree>();
        String use = "src/GrammarAnalysis/usewhat.txt";
        List<String> idName = new ArrayList<String>();
        List<Integer> idpos = new ArrayList<Integer>();
        String inputG = "src/lexicalAnalysis/symbol.txt";
        List<String> valuelist = new ArrayList<String>();
        List<Integer> valuepos = new ArrayList<Integer>();
        String inputG1 = "src/lexicalAnalysis/value.txt";
        try {
            FileInputStream inU = new FileInputStream(use);
            BufferedReader strU = new BufferedReader(new InputStreamReader(inU));
            String line = "";
            while ((line = strU.readLine()) != null) {
                String pattern = "(\\d+):(.*)";
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(line);
                if (m.find()) {
                    huseG.add(m.group(2));
                    // System.out.println(m.group(2));
                }
            }
            FileInputStream inG = new FileInputStream(inputG);
            BufferedReader strG = new BufferedReader(new InputStreamReader(inG));
            String line1 = "";
            while ((line1 = strG.readLine()) != null) {
                Pattern p = Pattern.compile("([^\\s]+)(\\s+)(\\d+)");
                Matcher m = p.matcher(line1);
                if (m.find()) {
                    idName.add(m.group(1));
                    idpos.add(Integer.valueOf(m.group(3)));
                    // System.out.println(m.group(1));
                }
            }
            FileInputStream inG1 = new FileInputStream(inputG1);
            BufferedReader strG1 = new BufferedReader(new InputStreamReader(inG1));
            String line2 = "";
            while ((line2 = strG1.readLine()) != null) {
                Pattern p = Pattern.compile("([^\\s]+)(\\s+)([^\\s]+)(\\s+)(\\d+)$");
                Matcher m = p.matcher(line2);
                if (m.find()) {
                    valuelist.add(m.group(1));
                    valuepos.add(Integer.valueOf(5));
                    // System.out.println(m.group(1));
                }
            }
            Stack<Character> charStack = new Stack<Character>();
            Stack<LRTree> treeStack = new Stack<LRTree>();
            charStack.push('#');
            int syspos = 0;
            int valpos = 0;
            for (int i = 0; i < huseG.size() - 1; i++) {
                // System.out.println(huseG.get(i));
                LRTree slrTree;
                if (charStack.peek() == huseG.get(i).charAt(huseG.get(i).length() - 1)) {
                    // slrTree = treeStack.pop();
                    // charStack.pop();
                    slrTree = new LRTree();
                    slrTree.setName(String.valueOf(huseG.get(i).charAt(0)));

                    String right = huseG.get(i).substring(3, huseG.get(i).length());
                    String[] b = new String[right.length()];
                    // System.out.println(right);
                    // for(int k=right.length()-1;k>-1;k--)
//                    if(right.equals("y[E]")) {
//                    	for (int k =0; k <right.length(); k++) {
//                    	if (right.charAt(k) == 'y') {                            
//                            
//                            LRTree slrTree1 = new LRTree();
//                            slrTree1.setName(String.valueOf(right.charAt(k)));
//                           
//                            slrTree1.setChildId(slrTreeArray.get(Integer.valueOf(treeStack.peek().getChildId()[0])).getChildId());
//                            slrTree1.setPos(slrTreeArray.get(Integer.valueOf(slrTree1.getChildId()[0])).getPos());
//                            slrTree1.setId(slrTreeArray.size());
//                            slrTreeArray.add(slrTree1);
//                            b[0] = String.valueOf(slrTree1.getId());
//                        }   else if (charStack.peek() == right.charAt(k)) {
//                            // System.out.println(charStack.peek());
//                            LRTree slrTree1 = treeStack.pop();
//                            charStack.pop();
//                            b[k] = String.valueOf(slrTree1.getId());
//                            // System.out.println("b" +k+": "+ b[k]);
//                            LRTree slrTree2 = new LRTree();
//                            slrTree2.setName(idName.get(syspos));
//                            
//                            slrTree2.setPos(idpos.get(syspos));
//                            
//                            syspos++;
//                            String a = "-1";
//                            String[] bb = a.split(" ");
//                            slrTree2.setChildId(bb);
//                            slrTree2.setId(slrTreeArray.size());
//                            slrTreeArray.add(slrTree2); 
//                            String aa = String.valueOf(slrTree2.getId());
//                            String[] bbb = aa.split(" ");
//                            LRTree slrTree3=slrTreeArray.get(Integer.valueOf(slrTree1.getChildId()[0]));
//                            slrTree3.setChildId(bbb);
//                            slrTree3.setPos(slrTreeArray.get(Integer.valueOf(slrTree3.getChildId()[0])).getPos());
//                         }else {
//                            LRTree slrTree1 = new LRTree();
//                            slrTree1.setName(String.valueOf(right.charAt(k)));
//                            String a = "-1";
//                            String[] bb = a.split(" ");
//                            slrTree1.setChildId(bb);
//                            slrTree1.setPos(-1);
//                            slrTree1.setId(slrTreeArray.size());
//                            slrTreeArray.add(slrTree1);
//                            // if (isV(right.charAt(k))) {
//                            // charStack.push(right.charAt(k));
//                            // treeStack.push(slrTree1);
//                            // }
//                            b[k] = String.valueOf(slrTree1.getId());
//                        } 
//                    	}
//                    	
//                    	}
//                    else {
                    for (int k = right.length() - 1; k > -1; k--) {
                    	
                        if (right.charAt(k) == 'y') {
                            LRTree slrTree2 = new LRTree();
                            slrTree2.setName(idName.get(syspos));
                            
                            slrTree2.setPos(idpos.get(syspos));
                            
                            syspos++;
                            String a = "-1";
                            String[] bb = a.split(" ");
                            slrTree2.setChildId(bb);
                            slrTree2.setId(slrTreeArray.size());
                            slrTreeArray.add(slrTree2);
                                                       
                            LRTree slrTree1 = new LRTree();
                            slrTree1.setName(String.valueOf(right.charAt(k)));
                            String aa = String.valueOf(slrTree2.getId());
                            String[] bbb = aa.split(" ");
                            slrTree1.setChildId(bbb);
                            slrTree1.setPos(slrTreeArray.get(Integer.valueOf(slrTree1.getChildId()[0])).getPos());
                            slrTree1.setId(slrTreeArray.size());
                            slrTreeArray.add(slrTree1);
                            b[k] = String.valueOf(slrTree1.getId());
                        } else if (right.charAt(k) == 'z' || right.charAt(k) == 'x' || right.charAt(k) == '@'
                                || right.charAt(k) == '$') {
                            LRTree slrTree2 = new LRTree();
                            slrTree2.setName(valuelist.get(valpos));
                            
                            slrTree2.setPos(valuepos.get(valpos));
                            
                            valpos++;
                            String a = "-1";
                            String[] bb = a.split(" ");
                            slrTree2.setChildId(bb);
                            slrTree2.setId(slrTreeArray.size());
                            slrTreeArray.add(slrTree2);
                            LRTree slrTree1 = new LRTree();
                            slrTree1.setName(String.valueOf(right.charAt(k)));
                            String aa = String.valueOf(slrTree2.getId());
                            String[] bbb = aa.split(" ");
                            slrTree1.setChildId(bbb);
                            slrTree1.setPos(slrTreeArray.get(Integer.valueOf(slrTree1.getChildId()[0])).getPos());
                            slrTree1.setId(slrTreeArray.size());
                            slrTreeArray.add(slrTree1);
                            b[k] = String.valueOf(slrTree1.getId());
                        }
                        // else if (right.charAt(k) == 'q') {
                        // String a = "-1";
                        // b = a.split(" ");
                        // }
                        else if (charStack.peek() == right.charAt(k)) {
                            // System.out.println(charStack.peek());
                            LRTree slrTree1 = treeStack.pop();
                            charStack.pop();
                            b[k] = String.valueOf(slrTree1.getId());
                            // System.out.println("b" +k+": "+ b[k]);

                        } else {
                            LRTree slrTree1 = new LRTree();
                            slrTree1.setName(String.valueOf(right.charAt(k)));
                            String a = "-1";
                            String[] bb = a.split(" ");
                            slrTree1.setChildId(bb);
                            slrTree1.setPos(-1);
                            slrTree1.setId(slrTreeArray.size());
                            slrTreeArray.add(slrTree1);
                            // if (isV(right.charAt(k))) {
                            // charStack.push(right.charAt(k));
                            // treeStack.push(slrTree1);
                            // }
                            b[k] = String.valueOf(slrTree1.getId());
                        }
                    }
                    //}
                    // System.out.println("char��ǰջ��" + charStack.peek());
                    // System.out.println("****");
                    // for (int ii = 0; ii < b.length; ii++) {
                    // System.out.println("���ӽڵ�: " + b[ii]);
                    // }
                    slrTree.setChildId(b);

                    treeStack.push(slrTree);
                    // System.out.println("ѹջ�Ľڵ�Ϊ��" +slrTree.getName());
                    charStack.push(huseG.get(i).charAt(0));
                    slrTree.setId(slrTreeArray.size());
                    slrTreeArray.add(slrTree);

                } else {
                    slrTree = new LRTree();
                    slrTree.setName(String.valueOf(huseG.get(i).charAt(0)));
                    String right = huseG.get(i).substring(3, huseG.get(i).length());
                    String[] b = new String[right.length()];
                    // System.out.println(right);
//                    if(right.equals("y[E]")) {
//                    	for (int k =0; k <right.length(); k++) {
//                    	if (right.charAt(k) == 'y') {                            
//                            
//                            LRTree slrTree1 = new LRTree();
//                            slrTree1.setName(String.valueOf(right.charAt(k)));
//                           
//                            slrTree1.setChildId(slrTreeArray.get(Integer.valueOf(treeStack.peek().getChildId()[0])).getChildId());
//                            slrTree1.setId(slrTreeArray.size());
//                            slrTree1.setPos(slrTreeArray.get(Integer.valueOf(slrTree1.getChildId()[0])).getPos());
//                            slrTreeArray.add(slrTree1);
//                            b[0] = String.valueOf(slrTree1.getId());
//                        }   else if (charStack.peek() == right.charAt(k)) {
//                            // System.out.println(charStack.peek());
//                            LRTree slrTree1 = treeStack.pop();
//                            charStack.pop();
//                            b[k] = String.valueOf(slrTree1.getId());
//                            // System.out.println("b" +k+": "+ b[k]);
//                            LRTree slrTree2 = new LRTree();
//                            slrTree2.setName(idName.get(syspos));
//                            
//                            slrTree2.setPos(idpos.get(syspos));
//                            
//                            syspos++;
//                            String a = "-1";
//                            String[] bb = a.split(" ");
//                            slrTree2.setChildId(bb);
//                            slrTree2.setId(slrTreeArray.size());
//                            slrTreeArray.add(slrTree2); 
//                            String aa = String.valueOf(slrTree2.getId());
//                            String[] bbb = aa.split(" ");
//                            LRTree slrTree3=slrTreeArray.get(Integer.valueOf(slrTree1.getChildId()[0]));
//                            slrTree3.setChildId(bbb);
//                            slrTree3.setPos(slrTreeArray.get(Integer.valueOf(slrTree3.getChildId()[0])).getPos());
//                         //   
//                        }else {
//                            LRTree slrTree1 = new LRTree();
//                            slrTree1.setName(String.valueOf(right.charAt(k)));
//                            String a = "-1";
//                            String[] bb = a.split(" ");
//                            slrTree1.setChildId(bb);
//                            slrTree1.setPos(-1);
//                            slrTree1.setId(slrTreeArray.size());
//                            slrTreeArray.add(slrTree1);
//                            // if (isV(right.charAt(k))) {
//                            // charStack.push(right.charAt(k));
//                            // treeStack.push(slrTree1);
//                            // }
//                            b[k] = String.valueOf(slrTree1.getId());
//                        } 
//                    	}
//                    	
//                    	}
//                    else {
                    for (int k = right.length() - 1; k > -1; k--) {
                        if (right.charAt(k) == 'y') {
                            LRTree slrTree2 = new LRTree();
                            slrTree2.setName(idName.get(syspos));
                            
                            slrTree2.setPos(idpos.get(syspos));
                            
                            syspos++;
                            String a = "-1";
                            String[] bb = a.split(" ");
                            slrTree2.setChildId(bb);
                            slrTree2.setId(slrTreeArray.size());
                            slrTreeArray.add(slrTree2);
                            
                         //   posMap.put(slrTreeArray.size()-1, idpos.get(syspos-1));

                            LRTree slrTree1 = new LRTree();
                            slrTree1.setName(String.valueOf(right.charAt(k)));
                            String aa = String.valueOf(slrTree2.getId());
                            String[] bbb = aa.split(" ");
                            slrTree1.setChildId(bbb);
                            slrTree1.setId(slrTreeArray.size());
                            slrTree1.setPos(slrTreeArray.get(Integer.valueOf(slrTree1.getChildId()[0])).getPos());
                            slrTreeArray.add(slrTree1);
                            b[k] = String.valueOf(slrTree1.getId());
                        } else if (right.charAt(k) == 'z' || right.charAt(k) == 'x' || right.charAt(k) == '@'
                                || right.charAt(k) == '$') {
                            LRTree slrTree2 = new LRTree();
                            slrTree2.setName(valuelist.get(valpos));
                            
                            slrTree2.setPos(valuepos.get(valpos));
                            
                            valpos++;
                            String a = "-1";
                            String[] bb = a.split(" ");
                            slrTree2.setChildId(bb);
                            slrTree2.setId(slrTreeArray.size());
                            slrTreeArray.add(slrTree2);

                            LRTree slrTree1 = new LRTree();
                            slrTree1.setName(String.valueOf(right.charAt(k)));
                            String aa = String.valueOf(slrTree2.getId());
                            String[] bbb = aa.split(" ");
                            slrTree1.setChildId(bbb);
                            slrTree1.setPos(slrTreeArray.get(Integer.valueOf(slrTree1.getChildId()[0])).getPos());
                            slrTree1.setId(slrTreeArray.size());
                            slrTreeArray.add(slrTree1);
                            b[k] = String.valueOf(slrTree1.getId());
                        }
                        // else if (right.charAt(k) == 'q') {
                        // String a = "-1";
                        // b = a.split(" ");
                        // }
                        else if (charStack.peek() == right.charAt(k)) {
                            // System.out.println(charStack.peek());
                            LRTree slrTree1 = treeStack.pop();
                            charStack.pop();
                            b[k] = String.valueOf(slrTree1.getId());
                            // System.out.println("b" +k+": "+ b[k]);

                        } else {
                            LRTree slrTree1 = new LRTree();
                            slrTree1.setName(String.valueOf(right.charAt(k)));
                            String a = "-1";
                            String[] bb = a.split(" ");
                            slrTree1.setChildId(bb);
                            slrTree1.setPos(-1);
                            slrTree1.setId(slrTreeArray.size());
                            slrTreeArray.add(slrTree1);
                            // if (isV(right.charAt(k))) {
                            // charStack.push(right.charAt(k));
                            // treeStack.push(slrTree1);
                            // }
                            b[k] = String.valueOf(slrTree1.getId());
                        }
                    }
                    //}
                    // System.err.println("char��ǰջ��" + charStack.peek());
                    slrTree.setChildId(b);
                    if(right.length()>1 && slrTreeArray.get(Integer.valueOf(slrTree.getChildId()[0])).getPos()==-1)
                    slrTree.setPos(slrTreeArray.get(Integer.valueOf(slrTree.getChildId()[1])).getPos());
                    else
                    	slrTree.setPos(slrTreeArray.get(Integer.valueOf(slrTree.getChildId()[0])).getPos());
                    slrTree.setId(slrTreeArray.size());
                    slrTreeArray.add(slrTree);

                    treeStack.push(slrTree);
                    // System.out.println("ѹջ�Ľڵ�Ϊ��" +slrTree.getName());
                    charStack.push(huseG.get(i).charAt(0));
                }

            }
            strU.close();
            strG.close();
            strG1.close();
        } catch (Exception e) {
        }

        for (int i = 0; i < slrTreeArray.size(); i++) {
            System.out.print("��� " + i + " " + slrTreeArray.get(i).getId());
            System.out.print(" Ԫ��" + slrTreeArray.get(i).getName());
            System.out.print("  ���ӣ�");
            for (int j = 0; j <slrTreeArray.get(i).getChildId().length;j++) {
                if (slrTreeArray.get(i).getChildId()[j].equals("-1")) {
                    System.out.print("��");
                    break;
                }
                System.out.print(" " + slrTreeArray.get(Integer.parseInt(slrTreeArray.get(i).getChildId()[j])).getId());
            }
            System.out.println();
        }
        for (int i = 0; i < slrTreeArray.size(); i++) {
            System.out.print("��� " + i);
            System.out.print(" Ԫ��" + slrTreeArray.get(i).getName());
            System.out.print("  ���ӣ�");
            for (int j = slrTreeArray.get(i).getChildId().length - 1; j >= 0; j--) {
                if (slrTreeArray.get(i).getChildId()[j].equals("-1")) {
                    System.out.print("��");
                    break;
                }
                System.out
                        .print(" " + slrTreeArray.get(Integer.parseInt(slrTreeArray.get(i).getChildId()[j])).getName());
            }
            System.out.println();
        }
        return slrTreeArray;
    }

   
    public static void main(String[] args) {
        // getUse();
        try {
        	 BufferedWriter out = new BufferedWriter(new FileWriter("src/SemanticAnalysis/fourAddr.txt"));
             BufferedWriter out1 = new BufferedWriter(new FileWriter("src/SemanticAnalysis/symbolTable.txt"));
             BufferedWriter out2 = new BufferedWriter(new FileWriter("src/SemanticAnalysis/error.txt"));
            grammerSemanticLoca[] grammersemanticLoca = GrammerSemanticLoca();
            // ������������ʽ��Ӧ����
            // System.out.println("������������ʽ��Ӧ����==========");
            // for(int i1=0;i1<grammersemanticLoca.length;i1++)
            // {
            // System.out.println(grammersemanticLoca[i1].getGrammerNum()+"
            // "+grammersemanticLoca[i1].getRulelLoc()+"
            // "+grammersemanticLoca[i1].getRuleNum());
            // }
            // System.out.println("������������ʽ��Ӧ���н���==========");
            ArrayList<RreeSemanticRecord> treeSemanticRecord = new ArrayList<RreeSemanticRecord>();
            ArrayList<String> addrList = new ArrayList<String>();// ��ַ��L1 L2 L3
            ArrayList<String> addrResult = new ArrayList<String>();// ������if a>b goto l1
            ArrayList<String> addrResult1=new ArrayList<String>();
            ArrayList<AddrNum> addrNum = new ArrayList<AddrNum>();// ��ַ����ŵĶ�Ӧ��
            ArrayList<CharTable> charTable = new ArrayList<CharTable>();// ���ű�
            ArrayList<FourAddr> fourAddr = new ArrayList<FourAddr>();// �ĵ�ַ��
            ArrayList<String> param = new ArrayList<String>();// ������
            initRecord(treeSemanticRecord);
            semanticTest(treeSemanticRecord, grammersemanticLoca, addrList, addrResult, addrNum, charTable, fourAddr,
                    param, slrTreeArray.size() - 1);
            ArrayList<String> b = change(addrResult, addrNum);
            System.out.println("********��ַ��*************");
            for (int i = 0; i < addrList.size(); i++) {
                System.out.println(addrList.get(i));
                // addrListTbModel.addRow(new Object[] { i+1,addrResult.get(i)});
            }
           System.out.println(b.size() + " " + fourAddr.size());
//            for (int i = 0; i < b.size(); i++) {
//            //	if(fourAddr.get(i).getToaddr())
//                String s = "< " + fourAddr.get(i).getOp() + " , " + fourAddr.get(i).getParam1() + " , "
//                        + fourAddr.get(i).getParam2() + " , " + fourAddr.get(i).getToaddr() + " >";
//                // addrListTbModel.addRow(new Object[] { i+1,b.get(i),s});
//            }
            System.out.println("********��ַ��Ӧ��*************");
            System.out.println("size=" + addrNum.size());
            for (int i = 0; i < addrNum.size(); i++) {
            	map.put(addrNum.get(i).getAddr(), addrNum.get(i).getNum());
                System.out.println(addrNum.get(i).getAddr() + " " + addrNum.get(i).getNum());

            }
            System.out.println("********����ַ��*************");
            for (int i = 0; i < addrResult.size(); i++) {
            	String[] result=addrResult.get(i).split(" ");
            	String tString=result[result.length-1];
            	// System.out.print(result[result.length-1]);
            	if(map.containsKey(tString)) {
            		result[result.length-1]=String.valueOf(map.get(tString));
            	}
            	String s="";
                System.out.print((i+1)+": ");
                for(int j=0;j<result.length;j++) {
                	s=s+result[j]+" ";
                	System.out.print(result[j]+" ");
                }
                addrResult1.add(s);
                System.out.print("\n");
                // addrListTbModel.addRow(new Object[] { i+1,addrResult.get(i)});
            }
            out.write("********�ĵ�ַ��*********************����ַ��*************\n");
            System.out.println("********�ĵ�ַ��*************");
            for (int i = 0; i < fourAddr.size(); i++) {
            	
            		if(map.containsKey(fourAddr.get(i).getToaddr())) {
            			out.write((i+1)+":< " + fourAddr.get(i).getOp() + " , " + fourAddr.get(i).getParam1() + " , "
                        + fourAddr.get(i).getParam2() + " , " +map.get(fourAddr.get(i).getToaddr()) + " >           "+
                        addrResult1.get(i)+"\n");
            			 System.out.println((i+1)+":< " + fourAddr.get(i).getOp() + " , " + fourAddr.get(i).getParam1() + " , "
                        + fourAddr.get(i).getParam2() + " , " +map.get(fourAddr.get(i).getToaddr()) + " >");
            			 
            		}else {
            			out.write((i+1)+":< " + fourAddr.get(i).getOp() + " , " + fourAddr.get(i).getParam1() + " , "
                                + fourAddr.get(i).getParam2() + " , " +fourAddr.get(i).getToaddr() + " >            "+
            					addrResult1.get(i)+"\n");
            			System.out.println((i+1)+":< " + fourAddr.get(i).getOp() + " , " + fourAddr.get(i).getParam1() + " , "
                                + fourAddr.get(i).getParam2() + " , " +fourAddr.get(i).getToaddr() + " >");
            		}
            	
            	
            }
            System.out.println("********���ű�*************");
            out1.write("����" + " " + "����" + " "
                    + "ƫ��"+"\n");
            for (int i = 0; i < charTable.size(); i++) {
            	out1.write(charTable.get(i).getChara() + " " + charTable.get(i).getType() + " "
                        + charTable.get(i).getOffset()+"\n");
                System.out.println(charTable.get(i).getChara() + " " + charTable.get(i).getType() + " "
                        + charTable.get(i).getOffset());
                // charListTbModel.addRow(new Object[] {
                // charTable.get(i).getChara(),charTable.get(i).getType(),charTable.get(i).getOffset()});
            }
            System.out.println("********������Ϣ��*************");
            for (int i = 0; i < error.size(); i++) {
            	out2.write(error.get(i)+"\n");
                System.out.println(error.get(i));
            }
//            System.out.println("********��¼����Ϣ��*************");
//            testRecord(treeSemanticRecord);
            System.out.println("********�����������*************");
            out.close();
            out1.close();
            out2.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static int getFatherId(ArrayList<LRTree> slrTreeArray, int childId) {
        for (int i = 0; i < slrTreeArray.size(); i++) {
            for (int j = 0; j < slrTreeArray.get(i).getChildId().length; j++) {
                // System.out.println("slrTreeArray.get(i).getChildId()[j]="+slrTreeArray.get(i).getChildId()[j]);
                if (slrTreeArray.get(i).getChildId()[j].equals(String.valueOf(childId))) {
                    return i;
                }
            }
        }
        return childId;
    }

    /**
     * ���������ʼ
     * 
     * @throws Exception
     *             ****************************
     */

    public static void initRecord(ArrayList<RreeSemanticRecord> treeSemanticRecord) {
        // for (int i = slrTreeArray.size() - 1; i > -1; i--)
        for (int i = 0; i < slrTreeArray.size(); i++) {
            int firstchildID = Integer.parseInt(slrTreeArray.get(i).getChildId()[0]);
            if (firstchildID == -1) {
                int fatherId = getFatherId(slrTreeArray, i);
                String fatherName = slrTreeArray.get(fatherId).getName();
                if (fatherName.equals("y") || fatherName.equals("z") || fatherName.equals("x") || fatherName.equals("@")
                        || fatherName.equals("$")) {
                    RreeSemanticRecord r = new RreeSemanticRecord();
                    r.setTreeNodeNum(fatherId);
                    r.setTreeNodeName(fatherName);
//                    System.err.println(fatherName);
                    r.setProperty("lexeme");
                    r.setValue(slrTreeArray.get(i).getName());
                    treeSemanticRecord.add(r);
                }
            }
        }
        RreeSemanticRecord r = new RreeSemanticRecord();
        r.setTreeNodeNum(slrTreeArray.size() + 1);
        r.setTreeNodeName("t");
        r.setProperty("t");
        r.setValue("ceshi");
        treeSemanticRecord.add(r);
    }

    /**
     * 
     * @param treeSemanticRecord��¼�����������Ϣ�ļ���
     * @param grammersemanticLoca���ĵ��ı��¼ÿ������ʽ������������Ƭ��
     * @param num��ǰ�����
     * @throws Exception
     */
    public static void semanticTest(ArrayList<RreeSemanticRecord> treeSemanticRecord,
            grammerSemanticLoca[] grammersemanticLoca, ArrayList<String> addrList, ArrayList<String> addrResult,
            ArrayList<AddrNum> addrNum, ArrayList<CharTable> charTable, ArrayList<FourAddr> fourAddr,
            ArrayList<String> param, int num) throws Exception {

        // �ҵ���Ӧ�Ĳ���ʽ
        System.out.println(" " + slrTreeArray.get(num).getName());
        System.out.println("num:" + num);

        String formula = slrTreeArray.get(num).getName() + "->";

        int length = slrTreeArray.get(num).getChildId().length;// ���ӽ��ĸ���
        System.out.println("���Ӹ���length:" + length);
        // String beforString = slrTreeArray.get(num).getName();//��������
        String nextString = "";
        for (int j = 0; j < length; j++) {
            if (slrTreeArray.get(num).getChildId()[j].equals("-1")) {
                nextString = "no";
                break;
            }
            nextString += slrTreeArray.get(Integer.parseInt(slrTreeArray.get(num).getChildId()[j])).getName() + " ";
            formula = formula + slrTreeArray.get(Integer.parseInt(slrTreeArray.get(num).getChildId()[j])).getName();
        }
        System.out.println(formula);
        System.out.println("�����ַ�����" + nextString);
        // SLRFormula formula=new SLRFormula();
        // formula.setBeforeString(beforString);
        // formula.setFlag(0);
        // formula.setNextString(nextString.split(" "));
        int grammerNum = findGnum(formula);// �ķ���Ĳ���ʽ���
        System.out.println("����ʽ�ı�ţ�" + grammerNum);
        // �﷨������ʼ

        // �Բ���ʽ��ÿһ����
        for (int i = 0; i < length; i++) {
            // ��ǰλ���г���Ƭ��
            // num�ڵ��ţ�grammarNum����ʽ��ţ�i����ʽ��λ��
            record(grammersemanticLoca, treeSemanticRecord, addrList, addrResult, addrNum, charTable, fourAddr, param,
                    num, grammerNum, i);

            int firstchildID = Integer.parseInt(slrTreeArray.get(num).getChildId()[0]);
//            int firstchildID = Integer.parseInt(slrTreeArray.get(num).getChildId()[slrTreeArray.get(num).getChildId().length-1]);
            // System.out.println(slrTreeArray.get(num).getChildId()[0]);
            if (firstchildID != -1)// �Լ���Ҷ�ڵ㣬����
            {
                // System.out.println("����Ҷ�ڵ�");
                // �Ե�ǰ�ӽڵ㣬����к��ӽ�㣬�ݹ飬�޺��ӽ�㣬����
                int childID = Integer.parseInt(slrTreeArray.get(num).getChildId()[i]);
                // System.out.println("====���ӽ��:"+childID);
                semanticTest(treeSemanticRecord, grammersemanticLoca, addrList, addrResult, addrNum, charTable,
                        fourAddr, param, childID);
            }
        }
        // ����Ƿ�Ҳ������Ƭ��
        // System.out.println("����ʽ���");
        record(grammersemanticLoca, treeSemanticRecord, addrList, addrResult, addrNum, charTable, fourAddr, param, num,
                grammerNum, length);

    }

    /**
     * ��õ�ǰ����ʽ��ǰλ���Ƿ����������Ƭ�Σ����򷵻�Ƭ�κţ����򷵻�-1
     * 
     * @param grammerNum
     * @param loc
     * @param grammersemanticLoca
     * @return
     */
    public static int isExitSemanticRule(int grammerNum, int loc, grammerSemanticLoca[] grammersemanticLoca) {
        for (int i = 0; i < grammersemanticLoca.length; i++) {
            if (grammersemanticLoca[i].getGrammerNum() == grammerNum && grammersemanticLoca[i].getRulelLoc() == loc) {
                return grammersemanticLoca[i].getRuleNum();
            }
        }
        return -1;
    }

    public static void record(grammerSemanticLoca[] grammersemanticLoca,
            ArrayList<RreeSemanticRecord> treeSemanticRecord, ArrayList<String> addrList, ArrayList<String> addrResult,
            ArrayList<AddrNum> addrNum, ArrayList<CharTable> charTable, ArrayList<FourAddr> fourAddr,
            ArrayList<String> param, int treeNodeNum, int grammerNum, int loc) {       
    	int ruleNum = isExitSemanticRule(grammerNum, loc, grammersemanticLoca);
        if (ruleNum != -1) {
            System.out.println("����ʽ" + grammerNum + " λ��" + loc + " ������Ƭ����ţ�" + ruleNum);
        }
        switch (ruleNum) {
        case 1:
            System.out.println("��" + ruleNum + "��");
            new semanticRule().semanticRule_1(treeSemanticRecord, slrTreeArray, treeNodeNum);
//            testRecord(treeSemanticRecord);
            break;
        case 2:
            System.out.println("��" + ruleNum + "��");
            new semanticRule().semanticRule_2(treeSemanticRecord, slrTreeArray, treeNodeNum);
//            testRecord(treeSemanticRecord);
            break;
        case 3:
            System.out.println("��" + ruleNum + "��");
            String s3 = new semanticRule().semanticRule_3(treeSemanticRecord, slrTreeArray, charTable, fourAddr,
                    treeNodeNum);
//            testRecord(treeSemanticRecord);
            String[] e3 = s3.split(" ");
            if (e3[0].equals("error")) {
            	if(e3[1].equals("3"))
            	error.add("Error at Line["+e3[2]+"]:������ƥ��"+e3[3]+" "+e3[4]);
            	if(e3[1].equals("1")) {
            		error.add("Error at Line["+e3[2]+"]:����δ����"+e3[3]);
            	}
                // semerrorListTbModel.addRow((new Object[]{3,"������ƥ��",e3[2]+" "+e3[3]}));
                
                break;
            }
            addrResult.add(s3);
            break;
        case 4:
            System.out.println("��" + ruleNum + "��");
            String s4 = new semanticRule().semanticRule_4(treeSemanticRecord, slrTreeArray, charTable, fourAddr,
                    treeNodeNum);
//            testRecord(treeSemanticRecord);
            String[] e4 = s4.split(" ");
            if (e4[0].equals("error")) {
            	error.add("������ƥ��"+e4[2]+" "+e4[3]);
                // semerrorListTbModel.addRow((new Object[]{3,"������ƥ��",e4[2]+" "+e4[3]}));
                break;
            }
            addrResult.add(s4);
            break;
        case 5:
            System.out.println("��" + ruleNum + "��");
            String s5 = new semanticRule().semanticRule_5(treeSemanticRecord, slrTreeArray, fourAddr, treeNodeNum);
//            testRecord(treeSemanticRecord);

            addrResult.add(s5);
            break;
        case 6:
            System.out.println("��" + ruleNum + "��");
            new semanticRule().semanticRule_6(treeSemanticRecord, slrTreeArray, treeNodeNum);
//            testRecord(treeSemanticRecord);
            break;
        case 7:
            System.out.println("��" + ruleNum + "��");
            String e7 = new semanticRule().semanticRule_7(treeSemanticRecord, slrTreeArray, charTable, treeNodeNum);
 //           testRecord(treeSemanticRecord);
            String[] e77 = e7.split(" ");
            if (e77[0].equals("error")) {
            	error.add("Error at Line["+e77[2]+"]:����δ����"+e77[3]);
                // semerrorListTbModel.addRow((new Object[]{1,"����δ����",e77[2]}));
            }
            break;
        case 8:
            System.out.println("��" + ruleNum + "��");
            new semanticRule().semanticRule_8(treeSemanticRecord, slrTreeArray, treeNodeNum);
//            testRecord(treeSemanticRecord);
            break;
        case 9:
            System.out.println("��" + ruleNum + "��");
            String s9 = new semanticRule().semanticRule_9(treeSemanticRecord, slrTreeArray, fourAddr, treeNodeNum);
//            testRecord(treeSemanticRecord);
            
            addrResult.add(s9);
            break;
        case 10:
            System.out.println("��" + ruleNum + "��");
            new semanticRule().semanticRule_10(treeSemanticRecord, slrTreeArray, treeNodeNum);
//           testRecord(treeSemanticRecord);
            break;
        case 11:
            System.out.println("��" + ruleNum + "��");
            new semanticRule().semanticRule_11(treeSemanticRecord, slrTreeArray, treeNodeNum, addrList);
 //           testRecord(treeSemanticRecord);
            break;
        case 12:
            System.out.println("��" + ruleNum + "��");
            new semanticRule().semanticRule_12(treeSemanticRecord, slrTreeArray, treeNodeNum, addrResult, addrNum);
//            testRecord(treeSemanticRecord);
//            testAddr(addrNum);
            break;
        case 13:
            System.out.println("��" + ruleNum + "��");
            new semanticRule().semanticRule_13(treeSemanticRecord, slrTreeArray, addrList, treeNodeNum);
//            testRecord(treeSemanticRecord);
            break;
        case 14:
            System.out.println("��" + ruleNum + "��");
            new semanticRule().semanticRule_14(treeSemanticRecord, slrTreeArray, addrResult, addrNum, treeNodeNum);
 //            testRecord(treeSemanticRecord);
//            testAddr(addrNum);
            break;
        case 15:
            System.out.println("��" + ruleNum + "��");
            new semanticRule().semanticRule_15(treeSemanticRecord, slrTreeArray, addrList, treeNodeNum);
//             testRecord(treeSemanticRecord);
            break;
        case 16:
            System.out.println("��" + ruleNum + "��");
            new semanticRule().semanticRule_16(treeSemanticRecord, slrTreeArray, addrResult, addrNum, treeNodeNum);
 //            testRecord(treeSemanticRecord);
            testAddr(addrNum);
            break;
        case 17:
            System.out.println("��" + ruleNum + "��");
            new semanticRule().semanticRule_17(treeSemanticRecord, slrTreeArray, treeNodeNum);
 //            testRecord(treeSemanticRecord);
            break;
        case 18:
            System.out.println("��" + ruleNum + "��");
            new semanticRule().semanticRule_18(treeSemanticRecord, slrTreeArray, treeNodeNum);
//             testRecord(treeSemanticRecord);
            break;
        case 19:
            System.out.println("��" + ruleNum + "��");
            String[] s19 = new semanticRule().semanticRule_19(treeSemanticRecord, slrTreeArray, fourAddr, treeNodeNum);
//             testRecord(treeSemanticRecord);
            String s191 = s19[0];
            String s192 = s19[1];

            addrResult.add(s191);
            addrResult.add(s192);
            break;
        case 20:
            System.out.println("��" + ruleNum + "��");
            String s20 = new semanticRule().semanticRule_20(treeSemanticRecord, slrTreeArray, fourAddr, treeNodeNum);
  //          testRecord(treeSemanticRecord);

            addrResult.add(s20);
            break;
        case 21:
            System.out.println("��" + ruleNum + "��");
            String s21 = new semanticRule().semanticRule_21(treeSemanticRecord, slrTreeArray, fourAddr, treeNodeNum);
 //            testRecord(treeSemanticRecord);

            addrResult.add(s21);
            break;
        case 22:
            System.out.println("��" + ruleNum + "��");
            String s22 = new semanticRule().semanticRule_22(treeSemanticRecord, slrTreeArray, charTable, fourAddr,
                    treeNodeNum);
   //          testRecord(treeSemanticRecord);
            System.out.println(s22 + "=s22");
            String[] e22 = s22.split(" ");
            if (e22[0].equals("error")) {
                if (e22[1].equals("1")) {
                	error.add("Error at Line["+e22[2]+"]:����δ����"+e22[3]);
                    // semerrorListTbModel.addRow((new Object[]{1,"����δ����",e22[2]}));
                }
                if (e22[1].equals("3")) {
                	error.add("Error at Line["+e22[2]+"]:������ƥ��"+e22[3]+" "+e22[4]);
                    // semerrorListTbModel.addRow((new Object[]{3,"������ƥ��",e22[2]+" "+e22[3]}));
                }
                break;
            }

            addrResult.add(s22);
            break;
        case 23:
            System.out.println("��" + ruleNum + "��");
            String s23 = new semanticRule().semanticRule_23(treeSemanticRecord, slrTreeArray, fourAddr, treeNodeNum);
   //          testRecord(treeSemanticRecord);

            addrResult.add(s23);
            break;
        case 24:
            System.out.println("��" + ruleNum + "��");
            new semanticRule().semanticRule_24(treeSemanticRecord, slrTreeArray, treeNodeNum, addrList);
//             testRecord(treeSemanticRecord);
            break;
        case 25:
            System.out.println("��" + ruleNum + "��");
            new semanticRule().semanticRule_25(treeSemanticRecord, slrTreeArray, treeNodeNum, addrResult, addrNum);
//             testRecord(treeSemanticRecord);
//            testAddr(addrNum);
            break;
        case 26:
            System.out.println("��" + ruleNum + "��");
            new semanticRule().semanticRule_26(treeSemanticRecord, slrTreeArray, treeNodeNum, addrList);
 //            testRecord(treeSemanticRecord);
            break;
        case 27:
            System.out.println("��" + ruleNum + "��");
            new semanticRule().semanticRule_27(treeSemanticRecord, slrTreeArray, treeNodeNum, addrResult, addrNum);
//             testRecord(treeSemanticRecord);
//            testAddr(addrNum);
            break;
        case 28:
            System.out.println("��" + ruleNum + "��");
            new semanticRule().semanticRule_28(treeSemanticRecord, slrTreeArray, treeNodeNum, addrResult, addrNum);
             testRecord(treeSemanticRecord);
 //           testAddr(addrNum);
            break;
        case 29:
            System.out.println("��" + ruleNum + "��");
            new semanticRule().semanticRule_29(treeSemanticRecord, slrTreeArray, treeNodeNum, addrList, addrResult,
                    addrNum);
//             testRecord(treeSemanticRecord);
//            testAddr(addrNum);
            break;
        case 30:
            System.out.println("��" + ruleNum + "��");
            new semanticRule().semanticRule_30(treeSemanticRecord, slrTreeArray, treeNodeNum, addrResult, addrNum);
//             testRecord(treeSemanticRecord);
//            testAddr(addrNum);
            break;
        case 31:
            System.out.println("��" + ruleNum + "��");
            new semanticRule().semanticRule_31(treeSemanticRecord, slrTreeArray, treeNodeNum, addrList);
//             testRecord(treeSemanticRecord);
            break;
        case 32:
            System.out.println("��" + ruleNum + "��");
            new semanticRule().semanticRule_32(treeSemanticRecord, slrTreeArray, treeNodeNum, addrList, addrResult,
                    addrNum);
//             testRecord(treeSemanticRecord);
//            testAddr(addrNum);
            break;
        case 33:
            System.out.println("��" + ruleNum + "��");
            new semanticRule().semanticRule_33(treeSemanticRecord, slrTreeArray, treeNodeNum);
  //           testRecord(treeSemanticRecord);
            break;
        case 34:
            System.out.println("��" + ruleNum + "��");
            new semanticRule().semanticRule_34(treeSemanticRecord, slrTreeArray, treeNodeNum);
 //           testRecord(treeSemanticRecord);
            break;
        case 35:
            System.out.println("��" + ruleNum + "��");
            String s35 = new semanticRule().semanticRule_35(treeSemanticRecord, slrTreeArray, charTable, fourAddr,
                    treeNodeNum);
//             testRecord(treeSemanticRecord);
            String[] e35 = s35.split(" ");
            if (e35[0].equals("error")) {
            	error.add("Error at Line["+e35[2]+"]:����δ����"+e35[2]);
                // semerrorListTbModel.addRow((new Object[]{1,"����δ����",e35[2]}));
                break;
            }
            addrResult.add(s35);
            break;
        case 36:
            System.out.println("��" + ruleNum + "��");
            String[] s36 = new semanticRule().semanticRule_36(treeSemanticRecord, slrTreeArray, fourAddr, treeNodeNum);
   //          testRecord(treeSemanticRecord);
            String s361 = s36[0];
            String s362 = s36[1];

            addrResult.add(s361);
            addrResult.add(s362);
            break;
        case 37:
            System.out.println("��" + ruleNum + "��");
            new semanticRule().semanticRule_37(treeSemanticRecord, slrTreeArray, treeNodeNum);
  //           testRecord(treeSemanticRecord);
            break;
        case 38:
            System.out.println("��" + ruleNum + "��");
            new semanticRule().semanticRule_38(treeSemanticRecord, slrTreeArray, treeNodeNum);
 //            testRecord(treeSemanticRecord);
            break;
        case 39:
            System.out.println("��" + ruleNum + "��");
            String s39 = new semanticRule().semanticRule_39(treeSemanticRecord, slrTreeArray, fourAddr, treeNodeNum);
  //           testRecord(treeSemanticRecord);

            addrResult.add(s39);
            break;
        case 40:
            System.out.println("��" + ruleNum + "��");
            String s40 = new semanticRule().semanticRule_40(treeSemanticRecord, slrTreeArray, fourAddr, treeNodeNum);
  //           testRecord(treeSemanticRecord);

            addrResult.add(s40);
            break;
        case 41:
            System.out.println("��" + ruleNum + "��");
            String e41 = new semanticRule().semanticRule_41(treeSemanticRecord, slrTreeArray, charTable, treeNodeNum);
  //           testRecord(treeSemanticRecord);
            String[] e411 = e41.split(" ");
            if (e411[0].equals("error")) {
            	error.add("Error at Line["+e411[2]+"]:�����ظ�����"+e411[3]);
                // semerrorListTbModel.addRow((new Object[]{2,"�����ظ�����",e411[2]}));
            }
            break;
        case 42:
            System.out.println("��" + ruleNum + "��");
            new semanticRule().semanticRule_42(treeSemanticRecord, slrTreeArray, param, treeNodeNum);
            break;
        case 43:
            System.out.println("��" + ruleNum + "��");
            new semanticRule().semanticRule_43(treeSemanticRecord, slrTreeArray, param, treeNodeNum);
            break;
        case 44:
            System.out.println("��" + ruleNum + "��");
            ArrayList<String> s44 = new semanticRule().semanticRule_44(treeSemanticRecord, slrTreeArray, param,
                    fourAddr, charTable, treeNodeNum);
            String e44 = s44.get(0);
            String[] e441 = e44.split(" ");
            if (e441[0].equals("error")) {
            	error.add("������δ����"+e441[2]+"\n");
                // semerrorListTbModel.addRow((new Object[]{5,"������δ����",e441[2]}));
            }
            for (int i = 0; i < s44.size(); i++) {
                addrResult.add(s44.get(i));
            }
            break;
        case 45:
            System.out.println("��" + ruleNum + "��==============");
            String e45 = new semanticRule().semanticRule_45(treeSemanticRecord, slrTreeArray, charTable, treeNodeNum);
       //      testRecord(treeSemanticRecord);
            String[] e451 = e45.split(" ");
            if (e451[0].equals("error")) {
            	error.add("�����������ͻ"+e451[2]+"\n");
                // semerrorListTbModel.addRow((new Object[]{4,"�����������ͻ",e451[2]}));
            }
            break;
        default:
             System.out.println("û��"+ruleNum);
            break;

        }

    }

    public static void testRecord(ArrayList<RreeSemanticRecord> treeSemanticRecord) {
        for (int i = 0; i < treeSemanticRecord.size(); i++) {
            System.out.print("���� " + treeSemanticRecord.get(i).getTreeNodeNum());
            System.out.print(" ������ " + treeSemanticRecord.get(i).getTreeNodeName());
            System.out.print(" ����" + treeSemanticRecord.get(i).getProperty());
            System.out.println(" ֵ " + treeSemanticRecord.get(i).getValue());
        }
    }

    public static void testAddr(ArrayList<AddrNum> addrNum) {
        for (int i = 0; i < addrNum.size(); i++) {
            System.out.println(addrNum.get(i).getAddr() + "  " + addrNum.get(i).getNum());
        }
    }

    public String findValue(ArrayList<RreeSemanticRecord> treeSemanticRecord, int treeNodeNum, String property) {
        for (int i = 0; i < treeSemanticRecord.size(); i++) {
            if (treeSemanticRecord.get(i).getTreeNodeNum() == treeNodeNum
                    && treeSemanticRecord.get(i).getProperty().equals(property)) {
                return treeSemanticRecord.get(i).getValue();
            }
        }
        return "null";
    }

    private static ArrayList<String> change(ArrayList<String> addrResult, ArrayList<AddrNum> addrNum) {
        ArrayList<String> a = new ArrayList<String>();
        for (int i = 0; i < addrResult.size(); i++) {
            String[] b = addrResult.get(i).split(" ");
            StringBuffer sb2 = new StringBuffer();
            for (int j = 0; j < b.length; j++) {
                boolean is = false;
                for (int z = 0; z < addrNum.size(); z++) {
                    if (b[j].equals(addrNum.get(z).getAddr())) {
                        sb2.append(addrNum.get(z).getNum() + " ");
                        is = true;
                        continue;
                    }
                }
                if (!is) {
                    sb2.append(b[j] + " ");
                }

            }
            a.add(sb2.toString());
        }
        return a;
    }

}
