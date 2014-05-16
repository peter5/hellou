package com.zlq.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * ����ת��ƴ��������
 * 
 * @Project UserCenter
 * @Author shiming.xin
 * @Version 1.0
 * @Date Mar 30, 2011 10:36:07 AM
 */
public class PinYin4JCn {
	
	public static void main(String[] args) {
		  // �ص��β� �����j ����,������
		  String str = "�ص���12adfa";
		  System.out.println(converterToFirstSpell(str));
		  System.out.println(converterToSpell(str));
		 }
	

 /**
  * ����ת��λ����ƴ������ĸ��Ӣ���ַ����䣬�����ַ���ʧ
  * ֧�ֶ����֣����ɷ�ʽ�磨�ص���:cdc,zds,cds,zdc��
  * 
  * @param chines
  *            ����
  * @return ƴ��
  */
 public static String converterToFirstSpell(String chines) {
  StringBuffer pinyinName = new StringBuffer();
  char[] nameChar = chines.toCharArray();
  HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
  defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
  defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
  for (int i = 0; i < nameChar.length; i++) {
   if (nameChar[i] > 128) {
    try {
     // ȡ�õ�ǰ���ֵ�����ȫƴ
     String[] strs = PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat);
     if (strs != null) {
      for (int j = 0; j < strs.length; j++) {
       // ȡ����ĸ
       pinyinName.append(strs[j].charAt(0));
       if (j != strs.length - 1) {
        pinyinName.append(",");
       }
      }
     }
     // else {
     // pinyinName.append(nameChar[i]);
     // }
    } catch (BadHanyuPinyinOutputFormatCombination e) {
     e.printStackTrace();
    }
   } else {
    pinyinName.append(nameChar[i]);
   }
   pinyinName.append(" ");
  }
  // return pinyinName.toString();
  return parseTheChineseByObject(discountTheChinese(pinyinName.toString()));
 }

 /**
  * ����ת��λ����ȫƴ��Ӣ���ַ����䣬�����ַ���ʧ
  * ֧�ֶ����֣����ɷ�ʽ�磨�ص���:zhongdangcen,zhongdangcan,chongdangcen,chongdangshen,zhongdangshen,chongdangcan��
  * 
  * @param chines
  *            ����
  * @return ƴ��
  */
 public static String converterToSpell(String chines) {
  StringBuffer pinyinName = new StringBuffer();
  char[] nameChar = chines.toCharArray();
  HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
  defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
  defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
  for (int i = 0; i < nameChar.length; i++) {
   if (nameChar[i] > 128) {
    try {
     // ȡ�õ�ǰ���ֵ�����ȫƴ
     String[] strs = PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat);
     if (strs != null) {
      for (int j = 0; j < strs.length; j++) {
       pinyinName.append(strs[j]);
       if (j != strs.length - 1) {
        pinyinName.append(",");
       }
      }
     }
    } catch (BadHanyuPinyinOutputFormatCombination e) {
     e.printStackTrace();
    }
   } else {
    pinyinName.append(nameChar[i]);
   }
   pinyinName.append(" ");
  }
  // return pinyinName.toString();
  return parseTheChineseByObject(discountTheChinese(pinyinName.toString()));
 }

 /**
  * ȥ���������ظ�����
  * 
  * @param theStr
  * @return
  */
 private static List<Map<String, Integer>> discountTheChinese(String theStr) {
  // ȥ���ظ�ƴ�����ƴ���б�
  List<Map<String, Integer>> mapList = new ArrayList<Map<String, Integer>>();
  // ���ڴ���ÿ���ֵĶ����֣�ȥ���ظ�
  Map<String, Integer> onlyOne = null;

  String[] firsts = theStr.split(" ");
  // ����ÿ�����ֵ�ƴ��
  for (String str : firsts) {
   onlyOne = new Hashtable<String, Integer>();
   String[] china = str.split(",");
   // �����ִ���
   for (String s : china) {
    Integer count = onlyOne.get(s);
    if (count == null) {
     onlyOne.put(s, new Integer(1));
    } else {
     onlyOne.remove(s);
     count++;
     onlyOne.put(s, count);
    }
   }
   mapList.add(onlyOne);
  }
  return mapList;
 }

 /**
  * ���������ƴ��������ϲ�����(�Ƽ�ʹ��)
  * 
  * @return
  */
 private static String parseTheChineseByObject(List<Map<String, Integer>> list) {
  Map<String, Integer> first = null; // ����ͳ��ÿһ��,�����������
  // ����ÿһ�鼯��
  for (int i = 0; i < list.size(); i++) {
   // ÿһ�鼯������һ����ϵ�Map
   Map<String, Integer> temp = new Hashtable<String, Integer>();
   // ��һ��ѭ����firstΪ��
   if (first != null) {
    // ȡ���ϴ������˴μ��ϵ��ַ���������
    for (String s : first.keySet()) {
     for (String s1 : list.get(i).keySet()) {
      String str = s + s1;
      temp.put(str, 1);
     }
    }
    // ������һ���������
    if(temp != null && temp.size() > 0){
     first.clear();
    }
   } else {
    for (String s : list.get(i).keySet()) {
     String str = s;
     temp.put(str, 1);
    }
   }
   // ������������Ա��´�ѭ��ʹ��
   if(temp != null && temp.size() > 0){
    first = temp;
   }
  }
  String returnStr = "";
  if(first != null){
   // ����ȡ������ַ���
   for (String str : first.keySet()) {
    returnStr += (str + ",");
   }
  }
  if(returnStr.length() > 0){
   returnStr = returnStr.substring(0, returnStr.length() - 1);
  }
  return returnStr;
 }

 /**
  * ���������ƴ����ѭ����ȡ�������������Ƽ�ʹ�ã�
  * 
  * ���������¼�������: {1,2,3} {4,5} {7,8,9} {5,2,8}
  * Ҫ��д���㷨��������������������,��:1475,1472,1478,1485,1482....������ƣ��õ�����ϸպ������������������ϣ����಻�٣�.
  * ע��Ҫ��������ϣ�������ȫ�����㷨���ǰ�õ������������ģ�����Ϲ����У���һ�������ŵ�һλ���ڶ����ŵڶ�λ���������ŵ���λ....
  * 
  * @param list
  * @return
  */
 private static String parseTheChineseByFor(List<Map<String, Integer>> list) {
  StringBuffer sbf = new StringBuffer();
  int size = list.size();
  switch (size) {
  case 1:
   for (String s : list.get(0).keySet()) {
    String str = s;
    sbf.append(str + ",");
   }
   break;
  case 2:
   for (String s : list.get(0).keySet()) {
    for (String s1 : list.get(1).keySet()) {
     String str = s + s1;
     sbf.append(str + ",");
    }
   }
   break;
  case 3:
   for (String s : list.get(0).keySet()) {
    for (String s1 : list.get(1).keySet()) {
     for (String s2 : list.get(2).keySet()) {
      String str = s + s1 + s2;
      sbf.append(str + ",");
     }
    }
   }
   break;
  // �˴�ʡ����������װ���̣���װ������ݽṹ���¡�
  // ע:List<Map<String, Integer>> list��List��ľ����ж����������������4��
  // Map���Ǿ����ĳһ�����飨�˴���Map��Ҫ�Ƿ���������������ظ�Ԫ��������
  // StringBuffer sbf = new StringBuffer();--���ڼ�¼����ַ��Ļ�����
  case 4:
   for (String s : list.get(0).keySet()) {
    for (String s1 : list.get(1).keySet()) {
     for (String s2 : list.get(2).keySet()) {
      for (String s3 : list.get(3).keySet()) {
       String str = s + s1 + s2 + s3;
       // �˴���sbfΪStringBuffer
       sbf.append(str + ",");
      }
     }
    }
   }
   break;
  case 5:
   for (String s : list.get(0).keySet()) {
    for (String s1 : list.get(1).keySet()) {
     for (String s2 : list.get(2).keySet()) {
      for (String s3 : list.get(3).keySet()) {
       for (String s4 : list.get(4).keySet()) {
        String str = s + s1 + s2 + s3 + s4;
        sbf.append(str + ",");
       }
      }
     }
    }
   }
   break;
  case 6:
   for (String s : list.get(0).keySet()) {
    for (String s1 : list.get(1).keySet()) {
     for (String s2 : list.get(2).keySet()) {
      for (String s3 : list.get(3).keySet()) {
       for (String s4 : list.get(4).keySet()) {
        for (String s5 : list.get(5).keySet()) {
         String str = s + s1 + s2 + s3 + s4 + s5;
         sbf.append(str + ",");
        }
       }
      }
     }
    }
   }
   break;
  case 7:
   for (String s : list.get(0).keySet()) {
    for (String s1 : list.get(1).keySet()) {
     for (String s2 : list.get(2).keySet()) {
      for (String s3 : list.get(3).keySet()) {
       for (String s4 : list.get(4).keySet()) {
        for (String s5 : list.get(5).keySet()) {
         for (String s6 : list.get(6).keySet()) {
          String str = s + s1 + s2 + s3 + s4 + s5 + s6;
          sbf.append(str + ",");
         }
        }
       }
      }
     }
    }
   }
   break;
  }
  String str = sbf.toString();
  return str.substring(0, str.length() - 1);
 }

}

