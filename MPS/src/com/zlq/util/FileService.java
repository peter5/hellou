package com.zlq.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
 
import org.apache.http.util.EncodingUtils;
 
import android.content.Context;
import android.os.Environment;
 
/**
 * class name：FileService<BR>
 * class description：android文件的一些读取操�?BR>
 * PS�?<BR>
 *
 */
public class FileService {
    private Context context;
 
    public FileService(Context c) {
        this.context = c;
    }
    
    public boolean IsExist(String fileName){
    	File file = new File(fileName);
    	return file.exists()? true: false;
    }
 
    // 读取sd中的文件
    public String readSDCardFile(String path) throws IOException {
        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        String result = streamRead(fis);
        return result;
    }
 
    // 在res目录下建立一个raw资源文件夹，这里的文件只能读不能写入。�?�?
    public String readRawFile(int fileId) throws IOException {
        // 取得输入�?
        InputStream is = context.getResources().openRawResource(fileId);
        String result = streamRead(is);// 返回�?��字符�?
        return result;
    }
 
    private String streamRead(InputStream is) throws IOException {
        int buffersize = is.available();// 取得输入流的字节长度
        byte buffer[] = new byte[buffersize];
        is.read(buffer);// 将数据读入数�?
        is.close();// 读取完毕后要关闭流�?
        String result = EncodingUtils.getString(buffer, "UTF-8");// 防止乱码
        return result;
    }
 
    // 在assets文件夹下的文件，同样是只能读取不能写�?
    public String readAssetsFile(String filename) throws IOException {
        // 取得输入�?
        InputStream is = context.getResources().getAssets().open(filename);
        String result = streamRead(is);// 返回�?��字符�?
        return result;
    }
 
    // �?d卡中写入文件
    public void writeSDCardFile(String path, byte[] buffer) throws IOException {
        File file = new File(path);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(buffer);// 也可String.getBytes()写入�?��字符�?
        fos.close();
    }
 
    // 将文件写入应用的data/data的files目录�?
    public static void writeDateFile(Context context, String fileName, byte[] buffer) throws Exception {
        byte[] buf = fileName.getBytes("iso8859-1");
        fileName = new String(buf, "utf-8");
        FileOutputStream fos = context.openFileOutput(fileName,
                Context.MODE_APPEND);// 添加在文件后�?
        fos.write(buffer);
        fos.close();
    }
 
    // 读取应用的data/data的files目录下文件数�?
    public String readDateFile(String fileName) throws Exception {
        FileInputStream fis = context.openFileInput(fileName);
        String result = streamRead(fis);
        return result;
    }
    
    //复制assets�?data/data
    
    public static void copyAssetsToData(Context context, String fileName){
        final String addstr="/data/data/io.vov.vitamio/files/";//里面的包名换成你自己�?
        InputStream assetsDB;
		try {
			assetsDB = context.getAssets().open(fileName);
			 OutputStream dbOut = new FileOutputStream(addstr + fileName);//strout是你要保存的文件�?

	        	byte[] buffer = new byte[1024];
	        		int length;
	        		while ((length = assetsDB.read(buffer)) > 0) {
	        			dbOut.write(buffer, 0, length);
	        		}

	        		dbOut.flush();
	        		dbOut.close();
	        		assetsDB.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//strln是assets文件夹下的文件名
    }
    
    public static void copyAssetsDatabaseToData(Context context, String fileName){
        final String addstr="/data/data/io.vov.vitamio/databases/";//里面的包名换成你自己�?
        File file = new File(addstr);
        if(!file.exists()){
        	file.mkdir();
        }
        File dbFile = new File(addstr+fileName);
        if(dbFile.exists()){
        	System.out.println("数据库已经存");
        }else{
        	System.out.println("数据库不存在");
        	InputStream assetsDB;
    		try {
    			assetsDB = context.getAssets().open(fileName);
    			 OutputStream dbOut = new FileOutputStream(dbFile);//strout是你要保存的文件�?

    	        	byte[] buffer = new byte[1024];
    	        		int length;
    	        		while ((length = assetsDB.read(buffer)) > 0) {
    	        			dbOut.write(buffer, 0, length);
    	        		}

    	        		dbOut.flush();
    	        		dbOut.close();
    	        		assetsDB.close();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}//strln是assets文件夹下的文件名
        }
        
    }
    
    public String getSDPath(){
    	File sdDir = null;
    	boolean sdCardExist = Environment.getExternalStorageState()
    	.equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
    	if (sdCardExist)
    	{
    	sdDir = Environment.getExternalStorageDirectory();//获取跟目录
    	}
    	return sdDir.toString();
    	}

}