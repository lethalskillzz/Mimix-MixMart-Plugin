package com.lethalsys.mimix.mixmart_addon;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


class MixMartDatabase extends SQLiteOpenHelper {
private static final String DATABASE_NAME="mixmart.db";
private static final int SCHEMA_VERSION=1;
public MixMartDatabase(Context context) {
super(context, DATABASE_NAME, null, SCHEMA_VERSION);
}
@Override
public void onCreate(SQLiteDatabase db) {
	db.execSQL("CREATE TABLE comm (_id INTEGER PRIMARY KEY AUTOINCREMENT,ad_id TEXT, user TEXT, title TEXT, description TEXT, price TEXT, ad_img BLOB, date TEXT, IMG TEXT, email TEXT, phone TEXT);");
	db.execSQL("CREATE TABLE commpostloc (_id INTEGER PRIMARY KEY AUTOINCREMENT, commpostloc INTEGER);");
}



@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	db.execSQL("DROP TABLE IF EXISTS comm");
	db.execSQL("DROP TABLE IF EXISTS commpostloc");
	onCreate(db);
}


public void ClearCommPostLoc() {
	getWritableDatabase().execSQL("DELETE FROM "+"commpostloc"+";");
}

public void insertCommPostLoc(int postloc) {
ContentValues cv=new ContentValues();
cv.put("commpostloc", postloc);
getWritableDatabase().insert("commpostloc", "commpostloc", cv);
}

public int getCommPostLoc() {
	
Cursor c = getReadableDatabase().rawQuery("SELECT _id, commpostloc FROM commpostloc ORDER BY _id",null);
int rturn=0;
if (c != null)
{
if(c.moveToFirst())
{
	rturn=(c.getInt(1));
}
}

return rturn;

}



public void ClearComm() {
	getWritableDatabase().execSQL("DELETE FROM "+"comm"+";");
}





public void insertComm(String ad_id, String user, String title, String description,
String price, byte[] ad_img, String date, String IMG , String email, String phone) {
ContentValues cv=new ContentValues();
cv.put("ad_id", ad_id);
cv.put("user", user);
cv.put("title", title);
cv.put("description", description);
cv.put("price", price);
cv.put("ad_img", ad_img);
cv.put("date", date);
cv.put("IMG", IMG);
cv.put("email", email);
cv.put("phone", phone);
getWritableDatabase().insert("comm", "ad_id", cv);
}


public Cursor getAllComm() {
return(getReadableDatabase()
.rawQuery("SELECT _id, ad_id, user, title, description, price, ad_img, date, IMG FROM comm ORDER BY _id ASC",
null));
}

public String getCommId(Cursor c) {
return(c.getString(1));
}

public String getCommUser(Cursor c) {
return(c.getString(2));
}

public String getCommTitle(Cursor c) {
return(c.getString(3));
}

public String getCommDesc (Cursor c) {
return(c.getString(4));
}

public String getCommPrice (Cursor c) {
return(c.getString(5));
}

public  byte[] getCommAd_Img(Cursor c) {
return(c.getBlob(6));
}

public String getCommDate (Cursor c) {
return(c.getString(7));
}

public String getCommIMG (Cursor c) {
return(c.getString(8));
}


public String getCommMail(String user) {

Cursor c = getReadableDatabase().rawQuery("SELECT _id, email FROM comm WHERE user =? ",new String[]{user});
String rturn=null;
if (c != null)  
{
if(c.moveToFirst())
{
	rturn=(c.getString(1));
}
}

return rturn;

}


public String getCommPhn(String user) {

Cursor c = getReadableDatabase().rawQuery("SELECT _id, phone FROM comm WHERE user =? ",new String[]{user});
String rturn=null;
if (c != null)  
{
if(c.moveToFirst())
{
	rturn=(c.getString(1));
}
}

return rturn;

}  


}