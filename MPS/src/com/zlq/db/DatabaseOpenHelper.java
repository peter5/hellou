package com.zlq.db;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "aaa.db";
	private static final int DATABASE_VERSION = 1;
	private static String CREATETABLE_SQL_FILE_PATH = "sfa.sql";
	private Context context;
	private String tag = "DatabaseOpenHelper";
	private static boolean loaded = false;

	public DatabaseOpenHelper(Context context) {

		// context.deleteDatabase(DATABASE_NAME);
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		super.onOpen(db);
		if (loaded) { // 只在第一次使用时执行建表操作
			return;
		}
		loaded = true;
		
		Log.i(tag, "execute create table sql script");
//		try {
//			BufferedReader reader = new BufferedReader(
//					new InputStreamReader(context.getResources().getAssets().open(CREATETABLE_SQL_FILE_PATH)));
//			String line = null;
//			while ((line = reader.readLine()) != null) {
//				db.execSQL(line);
//			}
////			db.execSQL("CREATE TABLE test3(test char(8))");
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			// e.printStackTrace();
//			Log.w(tag, "onOpen- exception  - " + e.getMessage());
//		}
	}
	
//	@Override
//	public synchronized SQLiteDatabase getWritableDatabase() {
//		File file = context.getDatabasePath(DATABASE_NAME);
//		File dir = file.getParentFile();
//		if (!dir.exists()) {
//			dir.mkdir();
//		}
//		if (!file.exists()) {
//			onCopyDatabase(file);
//		}
//		SQLiteDatabase sqlite = null;
//		while (true) {
//			try {
//				sqlite = super.getWritableDatabase();
//				break;
//			} catch (Exception e) {
//				e.printStackTrace();
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//			}
//		}
//		return sqlite;
//	}

//	public void onCopyDatabase(File toFile) {
//		InputStream stream;
//		try {
//			stream = context.getResources().getAssets().open(DATABASE_NAME);
//
//			if (toFile.exists()) {
//				System.out.println("here is runned.");
//				toFile.delete();
//			}
//			OutputStream out = new FileOutputStream(toFile);
//			try {
//				byte[] buffer = new byte[4096];
//				int bytesRead;
//				while ((bytesRead = stream.read(buffer)) >= 0) {
//					out.write(buffer, 0, bytesRead);
//				}
//			} finally {
//				out.close();
//				stream.close();
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.i(tag, "onCreate");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.i(tag, "onUpgrade");
	}

}
