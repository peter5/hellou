package com.zlq.db;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseHelper {

	private DatabaseOpenHelper dbHelper = null;
	public SQLiteDatabase db = null;

	private String tag = "DatabaseHelper";

	public DatabaseHelper(Context ctx) {
		try {
			dbHelper = new DatabaseOpenHelper(ctx);
			db = dbHelper.getWritableDatabase();
		} catch (Exception e) {
			Log.w(tag, "exception - DatabaseHelper - " + e.getMessage());
		}
	}

	public SQLiteDatabase getSQLiteDatabase() {
		return db;
	}

	public boolean exists(String tableName, ContentValues configuration) {
		Cursor cursor = this.query(tableName, configuration);
		boolean b = cursor.getCount() > 0;
		cursor.close();
		return b;
	}

	public boolean execSQL(String sql) {
		try {
			db.execSQL(sql);
			return true;
		} catch (Exception e) {
			Log.w(tag, "exception - execSQL - " + e.getMessage());
		}
		return false;
	}

	public Cursor rawQuery(String sql, String[] selectionArgs) {
		try {
			return db.rawQuery(sql, selectionArgs);
		} catch (Exception e) {
			Log.w(tag, "exception - execSQL - " + e.getMessage());
		}
		return null;
	}

	public boolean close() {
		try {
			dbHelper.close();
			return true;
		} catch (Exception e) {
			Log.w(tag, "exception - close - " + e.getMessage());
		}
		return false;
	}

	public boolean execSQL(List<String> sqls) {
		boolean result = false;
		db.beginTransaction();// 手动设置�?��事务
		try {
			for (String sql : sqls) {
				execSQL(sql);
			}
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			Log.w(tag, "exception - deleteForJSON - " + e.getMessage());
		}
		db.endTransaction();// 处理完成
		return result;
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * insert
	 */

	public boolean insert(String tableName, ContentValues contentValues) {
		try {
			return db.insertOrThrow(tableName, null, contentValues) > 0;
		} catch (Exception e) {
			Log.w(tag, "exception - insert - " + e.getMessage());
		}
		return false;
	}

	



	public boolean insertForJSON(String tableName, JSONArray array) {

		boolean result = false;
		db.beginTransaction();// 手动设置�?��事务
		try { // 捕捉JSON异常

			for (int i = 0; i < array.length(); i++) {

				JSONObject data = array.getJSONObject(i);
				ContentValues values = new ContentValues();
				Iterator<?> iterator = data.keys();
				while (iterator.hasNext()) {
					String key = (String) iterator.next();
					values.put(key, data.getString(key));
				}
				db.insertOrThrow(tableName, null, values);

			}
			db.setTransactionSuccessful();// 设置事务处理成功，不设置会自动回滚不提交
			result = true;

		} catch (Exception e) {
			Log.w(tag, "exception - json - " + e.getMessage());
		}
		db.endTransaction();// 处理完成
		return result;
	}

	/*
	 * delete
	 * 
	 * 删除指定行号的记�?删除指定内容的记�?删除全部的记�?
	 */

	public boolean delete(String tableName, ContentValues condition) {
		try {
			String whereClause = "";
			String[] whereArgs = new String[condition.size()];
			int pos = 0;
			for (Entry<String, Object> o : condition.valueSet()) {
				whereClause += o.getKey()
						+ ((pos < condition.size() - 1) ? "=?," : "=?");
				whereArgs[pos++] = (String) o.getValue();
			}
			// Log.e("whereClause:" + whereClause + ",whereArgs[0]:" +
			// whereArgs[0]);
			return db.delete(tableName, whereClause, whereArgs) > 0;
		} catch (Exception e) {
			Log.w(tag, "exception - delete - " + e.getMessage());
		}
		return false;
	}

	public boolean delete(String tableName, String key, String value) {
		try {
			return db.delete(tableName, key + "=?", new String[] { value }) > 0;
		} catch (Exception e) {
			Log.w(tag, "exception - delete - " + e.getMessage());
		}
		return false;
	}

	public boolean delete(String tableName, int rowid) {
		try {
			return db.delete(tableName, "rowid=?", new String[] { rowid + "" }) > 0;
		} catch (Exception e) {
			Log.w(tag, "exception - delete - " + e.getMessage());
		}
		return false;
	}

	public boolean delete(String tableName) {
		try {
			return db.delete(tableName, null, null) > 0;
		} catch (Exception e) {
			Log.w(tag, "exception - delete - " + e.getMessage());
		}
		return false;
	}

	/*
	 * update 根据行号或条件更�?
	 */

	public boolean update(String tableName, ContentValues configuration, ContentValues condition){
		try {
			String whereClause = "";
			String[] whereArgs = new String[condition.size()];
			int pos = 0;
			for (Entry<String, Object> o : condition.valueSet()) {
				whereClause += o.getKey()
						+ ((pos < condition.size() - 1) ? "=?," : "=?");
				whereArgs[pos++] = (String) o.getValue();
			}
			// Log.e("whereClause:" + whereClause + ",whereArgs[0]:" +
			// whereArgs[0]);
			return db.update(tableName, configuration, whereClause, whereArgs) > 0;
		} catch (Exception e) {
			Log.w(tag, "exception - update - " + e.getMessage());
		}
		return false;
	}

	public boolean update(String tableName, ContentValues config, int rowid) {
		try {
			return db.update(tableName, config, "rowid=?", new String[] { rowid
					+ "" }) > 0;
		} catch (Exception e) {
			Log.w(tag, "exception - update - " + e.getMessage());
		}
		return false;
	}

	// - 注意：更新项中不包含主键
	// - 不�?合在DAO中使用，仅提供底层操作接�?
	// - DAO中的操作是基于主键的，无论删除�?更新，�?插入必须包含主键，查询是实体无关的操�?
	public boolean update(String tableName, ContentValues configuration) {
		try {
			return db.update(tableName, configuration, null, null) > 0;
		} catch (Exception e) {
			Log.w(tag, "exception - update - " + e.getMessage());
		}
		return false;
	}

	/*
	 * query
	 */

	public Cursor query(String tableName, ContentValues configuration) {
		try {
			String whereClause = "";
			String[] whereArgs = new String[configuration.size()];
			int pos = 0;
			for (Entry<String, Object> o : configuration.valueSet()) {
				whereClause += o.getKey()
						+ ((pos < configuration.size() - 1) ? "=?," : "=?");
				whereArgs[pos++] = (String) o.getValue();
			}
			return db.rawQuery("select rowid,* from " + tableName + " where "
					+ whereClause, whereArgs);
		} catch (Exception e) {
			Log.w(tag, "exception - query - " + e.getMessage());
		}
		return null;
	}

	/*
	 * public Cursor query(String tableName,int rowid){ try{ return
	 * db.rawQuery("select rowid,* from " + tableName + " where rowid=?", new
	 * String[]{rowid+""}); }catch(Exception e){
	 * Log.w(tag,"exception - query - " + e.getMessage()); } return null; }
	 */

	public Cursor query(String tableName) {
		try {
			return db.rawQuery("select rowid,* from " + tableName, null);
		} catch (Exception e) {
			Log.w(tag, "exception - query - " + e.getMessage());
		}
		return null;
	}

	// selectionArgs表示结果字段集合,返回二位数组存储结果数据
	// 用例：query("select ? from OrderHead",new String[]{"order_id"})
	public String[][] query(String sql, String[] selectionArgs) {

		String data[][] = null;

		try {
			for (int i = 0; i < selectionArgs.length; i++) {
				sql = sql.replaceFirst("\\?", selectionArgs[i]);
			}
			System.out.println(sql);

			Cursor cursor = db.rawQuery(sql, null);
			if (cursor != null && cursor.moveToFirst()) {
				data = new String[cursor.getCount()][selectionArgs.length];
				for (int i = 0; i < cursor.getCount(); i++) {
					for (int j = 0; j < selectionArgs.length; j++) {
						data[i][j] = cursor.getString(j);
					}
					cursor.moveToNext();
				}
				cursor.close();
			}
		} catch (Exception e) {
			Log.w(tag, "exception - query - " + e.getMessage());
		}
		return data;
	}

	/*
	 * 新增 BY 向雨�?
	 */

	public String[][] query(String sql, String[] selectionArgs, int N) {
		String data[][] = null;
		try {
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor != null && cursor.moveToFirst()) {
				data = new String[cursor.getCount()][selectionArgs.length];
				for (int i = 0; i < cursor.getCount(); i++) {
					for (int j = 0; j < selectionArgs.length; j++) {
						if (j >= selectionArgs.length - N) {
							data[i][j] = "0";
						} else
							data[i][j] = cursor.getString(j);
					}
					cursor.moveToNext();
				}
				cursor.close();
			}
		} catch (Exception e) {
			Log.w(tag, "exception - query - " + e.getMessage());
		}
		return data;
	}

	/*
	 * 新增 - 与�?String[][] query(String sql, String[] selectionArgs)”比�?
	 * 
	 * 1、执行SQL语句中不�?��嵌入问号，�?使用columnCount参数指定返回列数 返回结果列的内容与SQL语句中指定字段顺序一�?
	 * 2、支持返回结果中添加额外的行和列，指定extraColumnCount参数,额外列内填充数据�?"�?
	 * 额外的行和列分为前导行和前导列，即开始的行和�?
	 * 
	 * 例：String[] query("select code from Customer",1) 例：String[][]
	 * query("select code,name from Customer",2,1)//后缀额外�?�?
	 */

	public String[] query(String sql, int extraRowCount) {
		String data[] = null;
		try {
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor != null && cursor.moveToFirst()) {

				int rows = extraRowCount + cursor.getCount();
				data = new String[rows];

				int i = 0;
				for (; i < extraRowCount; i++) { // 前导空白�?
					data[i] = "";
				}
				for (; i < rows; i++) {
					data[i] = cursor.getString(0);
					cursor.moveToNext();
				}
				cursor.close();
			}
		} catch (Exception e) {
			Log.w(tag, "exception - query - " + e.getMessage());
		}
		return data;
	}

	public String[][] query(String sql, int columnCount, int extraRowCount,
			int extraColumnCount) {

		String data[][] = null;
		try {
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor != null && cursor.moveToFirst()) {

				int rows = extraRowCount + cursor.getCount();
				int cols = columnCount + extraColumnCount;
				data = new String[rows][cols];

				int i = 0;
				for (; i < extraRowCount; i++) { // 前导空白�?
					for (int j = 0; j < cols; j++) {
						data[i][j] = "";
					}
				}
				for (; i < rows; i++) {
					int j = 0;
					for (; j < extraColumnCount; j++) { // 前导空白�?
						data[i][j] = "";
					}
					for (; j < cols; j++) {
						data[i][j] = cursor.getString(j - extraColumnCount);
					}
					cursor.moveToNext();
				}
				cursor.close();
			}
		} catch (Exception e) {
			Log.w(tag, "exception - query - " + e.getMessage());
		}
		return data;
	}

	/*
	 * others
	 */

	
	public String queryForJSON(String sql) {

		StringBuffer json = new StringBuffer("");
		json.append("{");
		try {

			Cursor cursor = db.rawQuery(sql, null);
			if (cursor != null && cursor.moveToFirst()) {

				json.append("\"rows\":" + cursor.getCount() + ",");
				json.append("\"data\":");
				json.append("[");

				int row = cursor.getCount();
				int col = cursor.getColumnCount();
				for (int i = 0; i < row; i++) { // 行数

					json.append("{\"value\":\"(");
					for (int j = 0; j < col; j++) { // 列数

						String s = cursor.getString(j);
						if (s == null) {
							s = "";
						}
						if (j == col - 1) {
							json.append("'" + s + "'");
						} else {
							json.append("'" + s + "',");
						}
					}
					if (i == row - 1) {
						json.append(")\"}");
					} else {
						json.append(")\"},");
					}

					cursor.moveToNext();
				}
				json.append("]}");
				cursor.close();
			} else {

				json.append("\"rows\":0,");
				json.append("\"data\":[]}");
			}

		} catch (Exception e1) {

			Log.w(tag, "exception - query - " + e1.getMessage());
			return "error";
		}
		return json.toString();
	}
	
}
