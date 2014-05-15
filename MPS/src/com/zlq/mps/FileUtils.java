package com.zlq.mps;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;
  
public class FileUtils {  
	
	
	public  static  int get_photoFile(final List<photoFileInfo> photoList,File file){//获得文件
    	
    	file.listFiles(new FileFilter(){

			@Override
			public boolean accept(File file) {
				String name = file.getName();
//				file.lastModified();
				
				System.out.println("name----accept--------------------"+name);
				if(name.startsWith("photo_"))
				{
				int i = name.indexOf('.');
				if(i != -1){
					name = name.substring(i);
					if(name.equalsIgnoreCase(".jpg")){
						photoFileInfo mi = new photoFileInfo();
						mi.displayName = file.getName();
						mi.path = file.getAbsolutePath();
						photoList.add(mi);
						return true;
					}
				}
				}
				return false;
			}
    	});
    	return photoList.size();
    }
	
	
	
	
	 static class photoFileInfo{
		String displayName;//名称  
		String path;//路径
	}
	
	
	
	
	public static  long getSDAvailaleSize(){

		File path = Environment.getExternalStorageDirectory(); //取得sdcard文件路径

		StatFs stat = new StatFs(path.getPath()); 

		long blockSize = stat.getBlockSize(); 

		long availableBlocks = stat.getAvailableBlocks();

//		return availableBlocks * blockSize; 

		//(availableBlocks * blockSize)/1024      KIB 单位

		return (availableBlocks * blockSize)/1024 /1024 ;// MB单位

		}
		 

		public static  long getSDAllSize(){

		File path = Environment.getExternalStorageDirectory(); 

		StatFs stat = new StatFs(path.getPath()); 

		long blockSize = stat.getBlockSize(); 

		long availableBlocks = stat.getBlockCount();


		return (availableBlocks * blockSize)/1024 /1024; // MB单位

		}
	
	
	
	public static String ReadTxtFile()
    {
		
//		DownloadManager dm = new DownloadManager();
		
//		String strPath = Environment.getExternalStorageDirectory().getPath()+"/mps/failedUpload.txt";
        String path = Environment.getExternalStorageDirectory().getPath()+"/mps/failedUpload.txt";
        String content = ""; //文件内容字符串
            //打开文件
            File file = new File(path);
            //如果path是传递过来的参数，可以做一个非目录的判断
            if (file.isDirectory())
            {
//                Log.d("TestFile", "The File doesn't not exist.");
            }
            else
            {
                try {
                    InputStream instream = new FileInputStream(file); 
                   if (instream != null) 
                   {
                        InputStreamReader inputreader = new InputStreamReader(instream);
                        BufferedReader buffreader = new BufferedReader(inputreader);
                        String line;
                        //分行读取
                        while (( line = buffreader.readLine()) != null) {
                            content += line + "\n";
                        }                
                        instream.close();
                    }
                }
                catch (java.io.FileNotFoundException e) 
               {
//                    Log.d("TestFile", "The File doesn't not exist.");
                } 
               catch (IOException e) 
               {
//                     Log.d("TestFile", e.getMessage());
                }
            }
            return content;
    }
	
	public static boolean deleteUPFailedTxt() {
		String strPath = Environment.getExternalStorageDirectory().getPath()+"/mps/failedUpload.txt";
		File file = new File(strPath);
		return file.delete();
	}
	
	
	//将字符串写入到文本文件中
    public static void write2Txt(String strcontent)
    {
    	
    	String strPath = Environment.getExternalStorageDirectory().getPath()+"/mps/failedUpload.txt";
    	
      //每次写入时，都换行写
      String strContent=strcontent+"\n";
      try {
           File file = new File(strPath);
           if (!file.exists()) {
//            Log.d("TestFile", "Create the file:" + strFilePath);
            file.createNewFile();
           }
           RandomAccessFile raf = new RandomAccessFile(file, "rw");
           raf.seek(file.length());
           raf.write(strContent.getBytes());
           raf.close();
      } catch (Exception e) {
//           Log.e("TestFile", "Error on write File.");
    	  e.printStackTrace();
          }
    }
	
	
	
	public static void saveMyBitmap(String fileName, Bitmap mBitmap) throws IOException {
        File f = new File(Environment.getExternalStorageDirectory().getPath()+"/mps/"+fileName);
        if(!f.getParentFile().exists()){
        	f.getParentFile().mkdirs();
        }
        f.createNewFile();
        FileOutputStream fOut = null;
        try {
                fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
                fOut.flush();
        } catch (IOException e) {
                e.printStackTrace();
        }
        try {
                fOut.close();
        } catch (IOException e) {
                e.printStackTrace();
        }
    }
	 /**
     *  backup 备份到 "konruns-backup" 文件夹下
     * @param srcFileAbspath 源文件路径
     */
	public static void doBackup(String srcFileAbspath) {
		
		File file = new File(srcFileAbspath);
		File file_backup = new File(new StringBuilder(Environment
				.getExternalStorageDirectory().getAbsolutePath())
				.append(File.separator).append("konruns-backup").toString()
				+ "/" + file.getName());
		if (file.exists()) {
			try {
				copyFile(file, file_backup);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	// 复制文件
	   public static void copyFile(File sourceFile, File targetFile) throws IOException {
	       BufferedInputStream inBuff = null;
	       BufferedOutputStream outBuff = null;
	       try {
	           // 新建文件输入流并对它进行缓冲
	           inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
	           // 新建文件输出流并对它进行缓冲
	           outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
	           // 缓冲数组
	           byte[] b = new byte[1024 * 5];
	           int len;
	           while ((len = inBuff.read(b)) != -1) {
	               outBuff.write(b, 0, len);
	           }
	           // 刷新此缓冲的输出流
	           outBuff.flush();
	       } finally {
	           // 关闭流
	           if (inBuff != null)
	               inBuff.close();
	           if (outBuff != null)
	               outBuff.close();
	       }
	   }
    private String SDPATH;  
      
    private int FILESIZE = 4 * 1024;   
      
    public String getSDPATH(){  
        return SDPATH;  
    }  
      
    public FileUtils(){  
        //得到当前外部存储设备的目录( /SDCARD )  
        SDPATH = Environment.getExternalStorageDirectory() + "/";  
    }  
      
    /**  
     * 在SD卡上创建文件  
     * @param fileName  
     * @return  
     * @throws IOException  
     */  
    public File createSDFile(String fileName) throws IOException{  
        File file = new File(SDPATH + fileName);  
        file.createNewFile();  
        return file;  
    }  
      
    /**  
     * 在SD卡上创建目录  
     * @param dirName  
     * @return  
     */  
    public File createSDDir(String dirName){  
        File dir = new File(SDPATH + dirName);  
        dir.mkdir();  
        return dir;  
    }  
      
    /**  
     * 判断SD卡上的文件夹是否存在  
     * @param fileName  
     * @return  
     */  
    public boolean isFileExist(String fileName){  
        File file = new File(SDPATH + fileName);  
        return file.exists();  
    }  
      
    /**  
     * 将一个InputStream里面的数据写入到SD卡中  
     * @param path  
     * @param fileName  
     * @param input  
     * @return  
     */  
    public File write2SDFromInput(String path,String fileName,InputStream input){  
        File file = null;  
        OutputStream output = null;  
        try {  
            createSDDir(path);  
            file = createSDFile(path + fileName);  
            output = new FileOutputStream(file);  
            byte[] buffer = new byte[FILESIZE];  
            while((input.read(buffer)) != -1){  
                output.write(buffer);  
            }  
            output.flush();  
        }   
        catch (Exception e) {  
            e.printStackTrace();  
        }  
        finally{  
            try {  
                output.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return file;  
    }  
  
}  