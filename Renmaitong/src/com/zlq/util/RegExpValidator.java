package com.zlq.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegExpValidator {
	
	public static void main(String[] args) {
		String myMail = "asdfad@asdfa.com";
		String myPhone = "027-65463811";
		String myPost = "123123";
		String myIp = "192.168.1.1";
		String myMobile = "15927417800";
		String myUrl = "http://lovephone.cn";
		
		System.out.println(isEmail(myMail));
		System.out.println(IsTelephone(myPhone));
		System.out.println(IsPostalcode(myPost));
		System.out.println(isIP(myIp));
		System.out.println(isMobileNO(myMobile));
		System.out.println(IsUrl(myUrl));
	}
	
    public static boolean isMobileNO(String mobiles){       
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");       
        Matcher m = p.matcher(mobiles);       
        return m.matches();       
    }   
 /**
  * ��֤����
  * 
  * @param ����֤���ַ���
  * @return ����Ƿ��ϵ��ַ���,���� <b>true </b>,����Ϊ <b>false </b>
  */
 public static boolean isEmail(String str) {
  String regex = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
  return match(regex, str);
 }

 /**
  * ��֤IP��ַ
  * 
  * @param ����֤���ַ���
  * @return ����Ƿ��ϸ�ʽ���ַ���,���� <b>true </b>,����Ϊ <b>false </b>
  */
 public static boolean isIP(String str) {
  String num = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";
  String regex = "^" + num + "\\." + num + "\\." + num + "\\." + num + "$";
  return match(regex, str);
 }

 /**
  * ��֤��ַUrl
  * 
  * @param ����֤���ַ���
  * @return ����Ƿ��ϸ�ʽ���ַ���,���� <b>true </b>,����Ϊ <b>false </b>
  */
 public static boolean IsUrl(String str) {
  String regex = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
  return match(regex, str);
 }

 /**
  * ��֤�绰����
  * 
  * @param ����֤���ַ���
  * @return ����Ƿ��ϸ�ʽ���ַ���,���� <b>true </b>,����Ϊ <b>false </b>
  */
 public static boolean IsTelephone(String str) {
  String regex = "^(\\d{3,4}-)?\\d{6,8}$";
  return match(regex, str);
 }

 /**
  * ��֤������������(�ַ�������ͬʱ����)
  * 
  * @param ����֤���ַ���
  * @return ����Ƿ��ϸ�ʽ���ַ���,���� <b>true </b>,����Ϊ <b>false </b>
  */
 public static boolean IsPassword(String str) {
  String regex = "[A-Za-z]+[0-9]";
  return match(regex, str);
 }

 /**
  * ��֤�������볤�� (6-18λ)
  * 
  * @param ����֤���ַ���
  * @return ����Ƿ��ϸ�ʽ���ַ���,���� <b>true </b>,����Ϊ <b>false </b>
  */
 public static boolean IsPasswLength(String str) {
  String regex = "^\\d{6,18}$";
  return match(regex, str);
 }

 /**
  * ��֤�����������
  * 
  * @param ����֤���ַ���
  * @return ����Ƿ��ϸ�ʽ���ַ���,���� <b>true </b>,����Ϊ <b>false </b>
  */
 public static boolean IsPostalcode(String str) {
  String regex = "^\\d{6}$";
  return match(regex, str);
 }

 /**
  * ��֤�����ֻ�����
  * 
  * @param ����֤���ַ���
  * @return ����Ƿ��ϸ�ʽ���ַ���,���� <b>true </b>,����Ϊ <b>false </b>
  */
 public static boolean IsHandset(String str) {
  String regex = "^[1]+[3,5]+\\d{9}$";
  return match(regex, str);
 }

 /**
  * ��֤�������֤��
  * 
  * @param ����֤���ַ���
  * @return ����Ƿ��ϸ�ʽ���ַ���,���� <b>true </b>,����Ϊ <b>false </b>
  */
 public static boolean IsIDcard(String str) {
  String regex = "(^\\d{18}$)|(^\\d{15}$)";
  return match(regex, str);
 }

 /**
  * ��֤������λС��
  * 
  * @param ����֤���ַ���
  * @return ����Ƿ��ϸ�ʽ���ַ���,���� <b>true </b>,����Ϊ <b>false </b>
  */
 public static boolean IsDecimal(String str) {
  String regex = "^[0-9]+(.[0-9]{2})?$";
  return match(regex, str);
 }

 /**
  * ��֤����һ���12����
  * 
  * @param ����֤���ַ���
  * @return ����Ƿ��ϸ�ʽ���ַ���,���� <b>true </b>,����Ϊ <b>false </b>
  */
 public static boolean IsMonth(String str) {
  String regex = "^(0?[[1-9]|1[0-2])$";
  return match(regex, str);
 }

 /**
  * ��֤����һ���µ�31��
  * 
  * @param ����֤���ַ���
  * @return ����Ƿ��ϸ�ʽ���ַ���,���� <b>true </b>,����Ϊ <b>false </b>
  */
 public static boolean IsDay(String str) {
  String regex = "^((0?[1-9])|((1|2)[0-9])|30|31)$";
  return match(regex, str);
 }

 /**
  * ��֤����ʱ��
  * 
  * @param ����֤���ַ���
  * @return ����Ƿ�����ַ��ʽ���ַ���,���� <b>true </b>,����Ϊ <b>false </b>
  */
 public static boolean isDate(String str) {
  // �ϸ���֤ʱ���ʽ��(ƥ��[2002-01-31], [1997-04-30],
  // [2004-01-01])��ƥ��([2002-01-32], [2003-02-29], [04-01-01])
  // String regex =
  // "^((((19|20)(([02468][048])|([13579][26]))-02-29))|((20[0-9][0-9])|(19[0-9][0-9]))-((((0[1-9])|(1[0-2]))-((0[1-9])|(1\\d)|(2[0-8])))|((((0[13578])|(1[02]))-31)|(((01,3-9])|(1[0-2]))-(29|30)))))$";
  // û��ʱ����֤��YYYY-MM-DD
  // String regex =
  // "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))$";
  // ����ʱ����֤��YYYY-MM-DD 00:00:00
  String regex = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$";
  return match(regex, str);
 }

 /**
  * ��֤��������
  * 
  * @param ����֤���ַ���
  * @return ����Ƿ��ϸ�ʽ���ַ���,���� <b>true </b>,����Ϊ <b>false </b>
  */
 public static boolean IsNumber(String str) {
  String regex = "^[0-9]*$";
  return match(regex, str);
 }

 /**
  * ��֤�����������
  * 
  * @param ����֤���ַ���
  * @return ����Ƿ��ϸ�ʽ���ַ���,���� <b>true </b>,����Ϊ <b>false </b>
  */
 public static boolean IsIntNumber(String str) {
  String regex = "^\\+?[1-9][0-9]*$";
  return match(regex, str);
 }

 /**
  * ��֤��д��ĸ
  * 
  * @param ����֤���ַ���
  * @return ����Ƿ��ϸ�ʽ���ַ���,���� <b>true </b>,����Ϊ <b>false </b>
  */
 public static boolean IsUpChar(String str) {
  String regex = "^[A-Z]+$";
  return match(regex, str);
 }

 /**
  * ��֤Сд��ĸ
  * 
  * @param ����֤���ַ���
  * @return ����Ƿ��ϸ�ʽ���ַ���,���� <b>true </b>,����Ϊ <b>false </b>
  */
 public static boolean IsLowChar(String str) {
  String regex = "^[a-z]+$";
  return match(regex, str);
 }

 /**
  * ��֤��֤������ĸ
  * 
  * @param ����֤���ַ���
  * @return ����Ƿ��ϸ�ʽ���ַ���,���� <b>true </b>,����Ϊ <b>false </b>
  */
 public static boolean IsLetter(String str) {
  String regex = "^[A-Za-z]+$";
  return match(regex, str);
 }


 /**
  * ��֤��֤�����ַ���
  * 
  * @param ����֤���ַ���
  * @return ����Ƿ��ϸ�ʽ���ַ���,���� <b>true </b>,����Ϊ <b>false </b>
  */
 public static boolean IsLength(String str) {
  String regex = "^.{8,}$";
  return match(regex, str);
 }

 /**
  * @param regex
  *            ������ʽ�ַ���
  * @param str
  *            Ҫƥ����ַ���
  * @return ���str ���� regex��������ʽ��ʽ,����true, ���򷵻� false;
  */
 private static boolean match(String regex, String str) {
  Pattern pattern = Pattern.compile(regex);
  Matcher matcher = pattern.matcher(str);
  return matcher.matches();
 }

 // 3. ����ַ����ظ����ֵĴ�
 //  
 // private void btnWord_Click(object sender, EventArgs e)
 // {
 // System.Text.RegularExpressions.MatchCollection matches =
 // System.Text.RegularExpressions.Regex.Matches(label1.Text,
 //  
 // @"\b(?<word>\w+)\s+(\k<word>)\b",
 // System.Text.RegularExpressions.RegexOptions.Compiled |
 // System.Text.RegularExpressions.RegexOptions.IgnoreCase);
 // if (matches.Count != 0)
 // {
 // foreach (System.Text.RegularExpressions.Match match in matches)
 // {
 // string word = match.Groups["word"].Value;
 // MessageBox.Show(word.ToString(),"Ӣ�ĵ���");
 // }
 // }
 // else { MessageBox.Show("û���ظ��ĵ���"); }
 //  
 //  
 // }
 //  
 // 4. �滻�ַ���
 //  
 // private void button1_Click(object sender, EventArgs e)
 // {
 //  
 // string strResult =
 // System.Text.RegularExpressions.Regex.Replace(textBox1.Text,
 // @"[A-Za-z]\*?", textBox2.Text);
 // MessageBox.Show("�滻ǰ�ַ�:" + "\n" + textBox1.Text + "\n" + "�滻���ַ�:" + "\n"
 // + textBox2.Text + "\n" +
 //  
 // "�滻����ַ�:" + "\n" + strResult,"�滻");
 //  
 // }
 //  
 // 5. ����ַ���
 //  
 // private void button1_Click(object sender, EventArgs e)
 // {
 // //ʵ��: ��025-8343243��0755-2228382��029-32983298389289328932893289��
 // foreach (string s in
 // System.Text.RegularExpressions.Regex.Split(textBox1.Text,@"\d{3,4}-\d*"))
 // {
 // textBox2.Text+=s; //������� "���ұ���"
 // }
 //  
 // }

}