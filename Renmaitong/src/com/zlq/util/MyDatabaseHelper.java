package com.zlq.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.zlq.db.DatabaseHelper;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;



public class MyDatabaseHelper {

	private static final char TAB = '\t'; 
	private static Context context;
	static DatabaseHelper myDBHelper = new DatabaseHelper(context);
	
	public boolean test(){
		return myDBHelper.execSQL("select * from User");
	}
	
	public static void deleteContacts(Context context){
		DatabaseHelper db = new DatabaseHelper(context);
		String sql = "DELETE FROM company";
		db.execSQL(sql);
		sql = "DELETE FROM member";
		db.execSQL(sql);
		db.close();
	}
	
	public static void insertContacts(Context context, ArrayList<HashMap<String, String>> companyList, 
			ArrayList<HashMap<String, String>> staffList){
		String sql = "";
		DatabaseHelper db = new DatabaseHelper(context);
		if(!companyList.isEmpty()){
			for(int i=0; i<companyList.size(); i++){
				sql = "INSERT INTO company VALUES('"+companyList.get(i).get("cityName")+"', '"+
						companyList.get(i).get("compName")+"', '"+
						companyList.get(i).get("compId")+"', '"+
						PinYin4JCn.converterToFirstSpell(companyList.get(i).get("compName"))+"')";
				db.execSQL(sql);
			}
		}
		
		if(!staffList.isEmpty()){
			for(int i=0; i<staffList.size(); i++){
				sql = "INSERT INTO member VALUES('"+staffList.get(i).get("compName")+"', '"+
						staffList.get(i).get("memberName")+"', '"+
						staffList.get(i).get("post")+"', '"+
						staffList.get(i).get("memberId")+"', '"+
						PinYin4JCn.converterToFirstSpell(staffList.get(i).get("memberName")) +"')";
				db.execSQL(sql);
			}
		}
		db.close();
	}
	
	public static ArrayList<ArrayList<String>> selectContacts(Context context, String content){
		DatabaseHelper db = new DatabaseHelper(context);
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		String[][] Data = null;
		Data = db.query(
				"SELECT * FROM company WHERE comp_name LIKE '%"+content+"%' or comp_index LIKE '%"+content+"%'",
				new String[] {"city_name", "comp_name", "comp_id"});
		if(Data != null){
			for(int i=0; i<Data.length; i++){
				ArrayList<String> temp = new ArrayList<String>();
				temp.add("0");
				temp.add(Data[i][0]);
				temp.add(Data[i][1]);
				temp.add(Data[i][2]);
				temp.add("0");
				result.add(temp);
			}
		}
		Data = db.query(
				"SELECT * FROM member WHERE  member_name LIKE '%"+content+"%' or member_index LIKE '%"+content+"%'",
				new String[] {"comp_name", "member_name", "post", "member_id"});
		if(Data != null){
			for(int i=0; i<Data.length; i++){
				ArrayList<String> temp = new ArrayList<String>();
				temp.add("1");
				temp.add(Data[i][0]);
				temp.add(Data[i][1]);
				temp.add(Data[i][2]);
				temp.add(Data[i][3]);
				result.add(temp);
			}
		}
		db.close();
		return result;
	}
	
	public static void deleteAmmeter(Context context, String userId){
		DatabaseHelper db = new DatabaseHelper(context);
		String sql = "DELETE FROM Ammeter WHERE user_id = '"+userId+"'";
		db.execSQL(sql);
		db.close();
	}
	
	public static void deleteCopyAmmeter(Context context, String userId){
		DatabaseHelper db = new DatabaseHelper(context);
		String sql = "DELETE FROM NotUploadAmmeter WHERE user_id = '"+userId+"'";
		db.execSQL(sql);
		db.close();
	}
	
	public static void deleteSection(Context context, String userId){
		DatabaseHelper db = new DatabaseHelper(context);
		String sql = "DELETE FROM Section WHERE user_id = '"+userId+"'";
		db.execSQL(sql);
		db.close();
	}
	
	public static void insertSectionData(Context context, ArrayList<List<String>> Section, String userId){
		DatabaseHelper myHelper = new DatabaseHelper(context);
		String sql;
//		ArrayList<ArrayList<String>> Section;
//		Section = ReadFile.getSectionList2(ReadFile.getDataList(context.getFilesDir()+"/Section.dat"));
		for(int i=0; i<Section.size(); i++){
			if(Section.get(i) != null){
				sql = "INSERT INTO Section VALUES('"+userId+"', '"+Section.get(i).get(0)+"', '"+Section.get(i).get(1)+"', '"+
						Section.get(i).get(2)+"', '"+Section.get(i).get(3)+"', '"+Section.get(i).get(4)+"','"+
						Section.get(i).get(5)+"', '"+Section.get(i).get(6)+"', '"+Section.get(i).get(7)+"', '"+
						Section.get(i).get(8)+"', '"+Section.get(i).get(9)+"', '"+Section.get(i).get(10)+"')";
				myHelper.execSQL(sql);
				System.out.println(Section.get(i).get(2)+"===================================");
			}
		}
		myHelper.close();
	}
	
	
	public static String[][] getSection(Context context, String userId){
		DatabaseHelper db = new DatabaseHelper(context);
		String[][] sectionData = null;
		sectionData = db.query(
				"SELECT * FROM Section WHERE user_id = '"+userId+"'", new String[] {
						"user_id", "section_id", "section_name", "section_worker", 
						"section_phone", "section_mobile" ,"section_count", "section_longitude",
						"section_latitude", "section_recorded", "section_sold", "section_supply"});
		db.close();
		return sectionData;
	}
	
	public static void SectionRecordedAdd(Context context, String userId, String sectionId, String recorded){
		DatabaseHelper db = new DatabaseHelper(context);
		db.execSQL("UPDATE Section SET section_recorded = '"+recorded+"' WHERE user_id = '"+userId+
				"' and section_id = '"+sectionId+"'");
		db.close();
	}
	
	//插入已抄�?
	public static void insertCopyAmmeterData(Context context, ArrayList<String> Ammeter){
		DatabaseHelper db = new DatabaseHelper(context);
		String sql;
		sql = "INSERT INTO NotUploadAmmeter VALUES('"+Ammeter.get(0)+"', '"+Ammeter.get(1)+
				"', '"+Ammeter.get(2)+"', '"+Ammeter.get(3)+"', '"+Ammeter.get(4)+"', '"+
				Ammeter.get(5)+"', '"+Ammeter.get(6)+"', '"+Ammeter.get(7)+"', '"+
				Ammeter.get(8)+"', '"+Ammeter.get(9)+"', '"+Ammeter.get(10)+"','"+
				Ammeter.get(11)+"', '"+Ammeter.get(12)+"', '"+Ammeter.get(13)+"', '"+
				Ammeter.get(14)+"', '"+Ammeter.get(15)+"','"+Ammeter.get(16)+"', '"+
				Ammeter.get(17)+"', '"+Ammeter.get(18)+"', '"+Ammeter.get(19)+"','"+
				Ammeter.get(20)+"', '"+Ammeter.get(21)+"', '"+1+"', '"+Ammeter.get(23)+"')";
			db.execSQL(sql);
		db.close();
	}
	

	
	
	public static String[][] getAmmeterData(Context context, String userId, String sectionId, String flag){
		DatabaseHelper db = new DatabaseHelper(context);
		String[][] AmmeterData;
		AmmeterData = db.query(
				"SELECT * FROM Ammeter WHERE user_id = '"+userId+"' AND ammeter_recorded = '"+flag+
						"' AND ammeter_section_id = '"+sectionId+"'",
				new String[] {"user_id", "ammeter_section_id", "ammeter_id", "ammeter_device_id", 
						"ammeter_name", "ammeter_number", "ammeter_category", "ammeter_special",
						"ammeter_type", "ammeter_old_rate", "ammeter_rate", "ammeter_old_record",
						"ammeter_old_base", "ammeter_base", "ammeter_old_longitude", "ammeter_old_latitude",
						"ammeter_quantity", "ammeter_longitude", "ammeter_latitude", "ammeter_time",
						"ammeter_height", "ammeter_record", "ammeter_recorded", "ammeter_box"});
		db.close();
		return AmmeterData;
	}
	
	public static String[][] getAmmeterAllData(Context context, String userId, String sectionId){
		DatabaseHelper db = new DatabaseHelper(context);
		String[][] AmmeterData;
		AmmeterData = db.query(
				"SELECT * FROM Ammeter WHERE user_id = '"+userId+"' AND ammeter_section_id = '"+sectionId+"'",
				new String[] {"user_id", "ammeter_section_id", "ammeter_id", "ammeter_device_id", 
						"ammeter_name", "ammeter_number", "ammeter_category", "ammeter_special",
						"ammeter_type", "ammeter_old_rate", "ammeter_rate", "ammeter_old_record",
						"ammeter_old_base", "ammeter_base", "ammeter_old_longitude", "ammeter_old_latitude",
						"ammeter_quantity", "ammeter_longitude", "ammeter_latitude", "ammeter_time",
						"ammeter_height", "ammeter_record", "ammeter_recorded", "ammeter_box"});
		db.close();
		return AmmeterData;
	}
	
	public static String[][] getAmmeterAutoCopy(Context context, String userId, String flag){
		DatabaseHelper db = new DatabaseHelper(context);
		String[][] AmmeterData;
		AmmeterData = db.query(
				"SELECT * FROM Ammeter WHERE user_id = '"+userId+"' AND ammeter_recorded = '"+flag+"'",
				new String[] {"user_id", "ammeter_section_id", "ammeter_id", "ammeter_device_id", 
						"ammeter_name", "ammeter_number", "ammeter_category", "ammeter_special",
						"ammeter_type", "ammeter_old_rate", "ammeter_rate", "ammeter_old_record",
						"ammeter_old_base", "ammeter_base", "ammeter_old_longitude", "ammeter_old_latitude",
						"ammeter_quantity", "ammeter_longitude", "ammeter_latitude", "ammeter_time",
						"ammeter_height", "ammeter_record", "ammeter_recorded", "ammeter_box"});
		db.close();
		return AmmeterData;
	}
	
	
	public static void updateAmmeter(Context context, String userId, String ammeterId){
		DatabaseHelper db = new DatabaseHelper(context);
		String sql = "UPDATE Ammeter SET ammeter_recorded = '1' WHERE ammeter_id = '"+ammeterId+
					"' AND user_id = '"+userId+"'";
		db.execSQL(sql);
		db.close();
	}
	
	public static void updateCopyAmmeterData(Context context, String userId, String ammeterId, 
				String ammeterNumber, ArrayList<String> location){
		DatabaseHelper db = new DatabaseHelper(context);
		String sql = "UPDATE Ammeter SET ammeter_recorded = '1' WHERE ammeter_id = '"+ammeterId+
					"' AND user_id = '"+userId+"'";
		sql = "UPDATE Ammeter SET ammeter_record = '"+ammeterNumber+"', ammeter_longitude='"+location.get(1)+
				"', ammeter_latitude='"+location.get(0)+"', ammeter_time='"+location.get(3)+
			"', ammeter_height='"+location.get(2)+"'  WHERE ammeter_id = '"+ammeterId+"' AND user_id = '"+userId+"'";
		db.execSQL(sql);
		db.close();
	}
	
	public static void myUpdateCopyAmmeterData(Context context, String userId, String ammeterId, 
			String ammeterNumber, ArrayList<String> location){
	DatabaseHelper db = new DatabaseHelper(context);
	String sql = "";
	sql = "UPDATE NotUploadAmmeter SET ammeter_record = '"+ammeterNumber+"', ammeter_longitude='"+location.get(1)+
			"', ammeter_latitude='"+location.get(0)+"', ammeter_time='"+location.get(3)+
		"', ammeter_height='"+location.get(2)+"'  WHERE ammeter_id = '"+ammeterId+"' AND user_id = '"+userId+"'";
	db.execSQL(sql);
	db.close();
}
	
	public static String[][] getUploadAmmeter(Context context, String userId, String ammeterid){
		DatabaseHelper db = new DatabaseHelper(context);
		String[][] AmmeterUploadData;
		AmmeterUploadData = db.query(
				"SELECT ammeter_device_id, ammeter_longitude, ammeter_latitude, ammeter_time, ammeter_height," +
				" ammeter_record FROM Ammeter WHERE user_id='"+userId+"' AND ammeter_id = '"+ammeterid+"'", new String[] {
						"ammeter_device_id", "ammeter_longitude", "ammeter_latitude", "ammeter_time", 
						"ammeter_height", "ammeter_record" });
		db.close();
		return AmmeterUploadData;
	}
//	public static ArrayList<String[]> getNetSectionData(){
//		ArrayList<String[]> list = new ArrayList<String[]>();
//		String[] array;
//		SocketClient client = new SocketClient(Setting.serverUrl, Setting.dataPort);
//		if(client.token() == 1){
//			array = getNetData(byte[] buf);
//			list.add(array);
//			while(array[1].equals(1) || array[1] == String.valueOf(1) ){
//				array = getNetData(byte[] buf);
//				list.add(array);
//			}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
//			
//		}
//		return list;
//	}
	
	public static void getNetAmmeterData(){
		
	}
	

	
	public static void uploadRecord(){
		
	}
	
	public static void getPostMessage(){
		
	}
	

	
	//轨迹fileList
	public static String[][] getRecordFileList(Context context, String userId, String logType, String flag){
		DatabaseHelper db = new DatabaseHelper(context);
		String[][] data= null;
		data = db.query("SELECT DISTINCT log_name FROM Record WHERE user_id= '"+userId+"' AND flag='"+flag+
				"' AND log_type='"+logType+"'", new String[] {"log_name"});
		db.close();
		return data;
	}
	
	//轨迹全记�?
	public static String[][] getRecord(Context context, String userId, String fileName){
		DatabaseHelper db = new DatabaseHelper(context);
		String[][] data;
		data = db.query(
				"SELECT * FROM Record WHERE user_id = '"+userId+"' AND log_name= '"+fileName+"'", 
				new String[] {"user_id", "log_type", "log_namelog_name", "flag", 
						"cp_longitude", "cp_latitude", "cp_height", "cp_time" });
		db.close();
		return data;
	}
	
	//删除轨迹文件
	public static void deleteRecord(Context context, String userId, String fileName){
		DatabaseHelper db = new DatabaseHelper(context);
		String sql = "DELETE FROM Record WHERE user_id = '"+userId+"' AND log_name= '"+fileName+"'";
		db.execSQL(sql);
		db.close();
	}
	
	//上传轨迹记录
	public static void updateRecord(Context context, String userId, String fileName, String logType){
		DatabaseHelper db = new DatabaseHelper(context);
		String sql = "UPDATE  Record SET flag = '1' WHERE user_id = '"+userId+"' AND log_name= '"+fileName+
				"' AND log_type='"+logType+"'";
		db.execSQL(sql);
		db.close();
	}
	
	//插入轨迹记录
	
	//获取用户
	public static String[][] getUserList(Context context){
		DatabaseHelper db = new DatabaseHelper(context);
		String[][] data;
		data = db.query(
				"SELECT * FROM User ORDER BY login_time DESC  Limit 5;", new String[] {
						"user_id", "user_name", "password", "remember_password", "login_time" ,
						"update_user", "update_time"});
		db.close();
		return data;
	}
	
	//插入用户
	public static void insertUser(Context context, String userName, String password, String rememberPassword){
		DatabaseHelper db = new DatabaseHelper(context);
		String sql = "INSERT INTO User VALUES(strftime('%Y%m%d%H%M%S','now','localtime'), '"+userName+"', '"
				+password+"', '"+rememberPassword+"', datetime('now','+8 hour'), 'user', datetime('now','+8 hour'))";
		db.execSQL(sql);
		db.close();
	}
	
	//更新插入时间
	public static void updatetUserTime(Context context, String userName,String password, String remember_password){
		DatabaseHelper db = new DatabaseHelper(context);
		String sql = "UPDATE User SET login_time = datetime('now','+8 hour'), remember_password = '"+
				remember_password+"', password='"+password+"' WHERE user_name = '"+userName+"'";
		db.execSQL(sql);
		db.close();
	}
	
	//插入设置信息
	//INSERT INTO Setting VALUES('10.10.10.1', '10.10.10.1', '1023', '1024', '1', '1', '0', '5', '50', '1', '1', '');
	public static void insertSetting(Context context, String[] array){
		DatabaseHelper db = new DatabaseHelper(context);
		String sql = "INSERT INTO Setting VALUES('"+array[0]+"', '"+array[1]+"', '"+
				array[2]+"', '"+array[3]+"', '"+array[4]+"', '"+array[5]+"', '"+array[6]+"', '"+array[7]+
				"', '"+array[8]+"', '"+array[9]+"', '"+array[10]+"', '"+array[11]+"', '"+array[12]+"', '"+array[13]+"')";
		db.execSQL(sql);
		db.close();
	}
	
	//删除设置信息
	public static void deleteSetting(Context context){
		DatabaseHelper db = new DatabaseHelper(context);
		String sql = "DELETE  from Setting";
		db.execSQL(sql);
		db.close();
	}
	
	//设置信息
	public static String[][] getSetting(Context context){
		DatabaseHelper db = new DatabaseHelper(context);
		String[][] data;
		data = db.query("SELECT * FROM Setting ", new String[] {"serverUrl", "serverIP", "dataPort", 
				"messagePort", "immediatelyUpdate", "centerReported", "alarmThreshold", "Interval",
				"searchArea", "userName", "password", "DeviceId", "userId", "installCode"});
		db.close();
		return data;
	}
	
	//更改设置信息
	public static void updateSetting(Context context, String installCode){
		DatabaseHelper db = new DatabaseHelper(context);
		String sql = "UPDATE Setting SET installCode = '"+installCode+"'";
		db.execSQL(sql);
		db.close();
	}
	

	
	public static void insertDefaultRecord(Context context, ArrayList<String> recordList, String userId, String logType){
		DatabaseHelper db = new DatabaseHelper(context);
		String sql = "INSERT INTO Record VALUES('"+userId+"', '"+logType+"', "+"strftime('%Y%m%d','now','localtime')||'.log'"+", '"+"0"+"', '"+
					recordList.get(0)+"', '"+recordList.get(1)+"', '"+recordList.get(2)+"','"+recordList.get(3)+"')";
		db.execSQL(sql);
		db.close();
	}
	
	public static void insertRecord(Context context, ArrayList<String> recordList, String userId, String logType, String fileName){
		DatabaseHelper db = new DatabaseHelper(context);
		String sql = "INSERT INTO Record VALUES('"+userId+"', '"+logType+"', '"+fileName+"', '"+"0"+"', '"+
					recordList.get(0)+"', '"+recordList.get(1)+"', '"+recordList.get(2)+"','"+recordList.get(3)+"')";
		db.execSQL(sql);
		db.close();
	}
	
	public static String stringArrayToString(String[] array){
		StringBuffer result = new StringBuffer();
		for(int i=0; i<array.length-1; i++){
			result.append(array[i]).append(TAB);
		}
		result.append(array.length-1);
		return result.toString();
	}
	
	public static ArrayList<ArrayList<String>> arrayToList(String[][] data){
		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		if(data != null){
			for(int i=0; i<data.length; i++){
				ArrayList<String> tem = new ArrayList<String>();
				for(int j=0; j<data[i].length; j++){
					tem.add(data[i][j]);
				}
				list.add(tem);
			}
		}
		return list;
	}
	
	public static ArrayList<String> getLastLocation(Context context){
		ArrayList<String> list = new ArrayList<String>();
		Date rightNow = new Date();    
	    DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");  
	    String date = format1.format(rightNow);
	    
		String serviceName = Context.LOCATION_SERVICE;
		LocationManager locationManager = (LocationManager) context.getSystemService(serviceName);
		String provider = LocationManager.GPS_PROVIDER;

		Location location = locationManager.getLastKnownLocation(provider);

		if (location == null){
			location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		System.out.println(location.getLongitude()+"  "+location.getLatitude());
		list.add(String.valueOf(location.getLatitude()));
		list.add(String.valueOf(location.getLongitude()));
		list.add(String.valueOf(location.getAltitude()));
		list.add(date);
		
        return list;
	}
	
	
	public static Location getCurrentLocation(Context context){
		String serviceName = Context.LOCATION_SERVICE;
		LocationManager locationManager = (LocationManager) context.getSystemService(serviceName);
		String provider = LocationManager.GPS_PROVIDER;

		Location location = locationManager.getLastKnownLocation(provider);

		if (location == null){
			location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		return location;
	}
}