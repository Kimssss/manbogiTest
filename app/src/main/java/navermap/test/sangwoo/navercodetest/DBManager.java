package navermap.test.sangwoo.navercodetest;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static navermap.test.sangwoo.navercodetest.Global.PREFERENCE_NAME;

public class DBManager extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String SQL_LITE_DATABASE_TABLE_NAME = "MAN_BO_GI";

    public static final String SQL_LITE_DATABASE_DATE = "date";
    public static final String SQL_LITE_DATABASE_MAN_BO_GI_COUNT = "man_bo_gi_count";
    public static final String SQL_LITE_DATABASE_DISTENCE = "distence";

    public static final int DEFAULT_VALUE_MAN_BO_GI_COUNT = 0;
    public static final float DEFAULT_VALUE_DISTENCE = 0;


    Context context;
    public DBManager(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);

        this.context =context;

        SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd", Locale.KOREA );
        Date currentTime = new Date( );
        String dTime = formatter.format ( currentTime );
        System.out.println ( dTime );



        String query = "select * from " + SQL_LITE_DATABASE_TABLE_NAME + " ORDER BY " + SQL_LITE_DATABASE_DATE + " desc limit 1";
        Cursor cursor = printData(query);

        SharedPreferences sharedPreferences
                = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);;//나혼자만

        String preferencesTime = sharedPreferences.getString(SQL_LITE_DATABASE_DATE , dTime);


        if(cursor.getCount() == 0){

            insert("insert into " + SQL_LITE_DATABASE_TABLE_NAME + "  "+ insertValue(preferencesTime, String.valueOf(DEFAULT_VALUE_MAN_BO_GI_COUNT), String.valueOf(DEFAULT_VALUE_DISTENCE)) +" ;" );

        }
            while (cursor.moveToNext()){
                String dateTime = cursor.getString(1);

                if(!dateTime.contains(dTime)){
                    String manBoGiCount = String.valueOf(sharedPreferences.getInt(SQL_LITE_DATABASE_MAN_BO_GI_COUNT, DEFAULT_VALUE_MAN_BO_GI_COUNT));
                    String distence = String.valueOf(sharedPreferences.getFloat(SQL_LITE_DATABASE_DISTENCE, DEFAULT_VALUE_DISTENCE));
                    insert("insert into " + SQL_LITE_DATABASE_TABLE_NAME + insertValue(preferencesTime, manBoGiCount, distence) +" ;" );

                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.clear();
                    edit.commit();

                    edit.putString(SQL_LITE_DATABASE_DATE, dTime);
                    edit.commit();
                }
                else{
                    String manBoGiCount = String.valueOf(sharedPreferences.getInt(SQL_LITE_DATABASE_MAN_BO_GI_COUNT, DEFAULT_VALUE_MAN_BO_GI_COUNT));
                    String distence = String.valueOf(sharedPreferences.getFloat(SQL_LITE_DATABASE_DISTENCE, DEFAULT_VALUE_DISTENCE));
                    update("update " + SQL_LITE_DATABASE_TABLE_NAME + updateValue(dateTime ,manBoGiCount,distence, dateTime) + " ;" );

                }

            }




    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블을 생성한다.
        // create table 테이블명 (컬럼명 타입 옵션);
        db.execSQL("CREATE TABLE "+SQL_LITE_DATABASE_TABLE_NAME+
                "( _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SQL_LITE_DATABASE_DATE + " DATE , " +
                SQL_LITE_DATABASE_MAN_BO_GI_COUNT + " INTEGER, " +
                SQL_LITE_DATABASE_DISTENCE +
                " DOUBLE );");



    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public String insertValue(String value1 , String value2 , String value3)  {
        try{
            return " values (null, DATE('"+ value1  +"') , "+value2 + " , " + value3 + ")" ;

        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }


    public void insert(String _query) {
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL(_query);
            db.close();

    }

    public String updateValue(String value1 , String value2 , String value3 , String date){
        try{
            return " set " + SQL_LITE_DATABASE_DATE + " = DATE('"+ value1  +"') , " +SQL_LITE_DATABASE_MAN_BO_GI_COUNT + " = " +value2 + " , " + SQL_LITE_DATABASE_DISTENCE + " = "+ value3 +" where " + SQL_LITE_DATABASE_DATE + " = " +  "DATE('"+ value1  +"')" ;

        }catch (Exception e){
            return "";
        }
    }

    public void update(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();     
    }
     
    public void delete(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();     
    }
     
    public Cursor printData(String query) {
        SQLiteDatabase db = getReadableDatabase();
        String str = "";
//        "select * from " + SQL_LITE_DATABASE_TABLE_NAME + " ORDER BY " + SQL_LITE_DATABASE_DATE + "DESC"
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }
}