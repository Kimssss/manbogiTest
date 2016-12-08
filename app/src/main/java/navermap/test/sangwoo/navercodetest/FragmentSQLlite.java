package navermap.test.sangwoo.navercodetest;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import static navermap.test.sangwoo.navercodetest.DBManager.SQL_LITE_DATABASE_TABLE_NAME;

/**
 * Created by sangwoo-pc on 2016. 12. 7..
 */

public class FragmentSQLlite extends Fragment {

    ArrayList<DBDateInfo> dbDateInfosList = new ArrayList();
    ListView listView ;
    DBlistAdapter baseAdapter;

    public static int MAX_LOW_DATA_COUNT =5;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        View view=null;//Fragment가 보여줄 View 객체를 참조할 참조변수

        view= inflater.inflate(R.layout.fragment_sqllite, null);
        listView = (ListView)view.findViewById(R.id.listview);

        final DBManager dbManager =  new DBManager(getActivity() , SQL_LITE_DATABASE_TABLE_NAME, null ,DBManager.DATABASE_VERSION);


        baseAdapter = new DBlistAdapter(getActivity(),dbDateInfosList);
        listView.setAdapter(baseAdapter);


        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int row = 0;

                Cursor cursor =dbManager.printData("select * from " + SQL_LITE_DATABASE_TABLE_NAME  +" LIMIT " +row+ ", " + MAX_LOW_DATA_COUNT ) ;

                dbDateInfosList = new ArrayList();

                while (cursor.getCount() >0){
                    while (cursor.moveToNext()) {
                        DBDateInfo dbDateInfo = new DBDateInfo();
                        dbDateInfo.setDate(cursor.getString(1));
                        dbDateInfo.setManBoGiCount(cursor.getInt(2));
                        dbDateInfo.setDistence(cursor.getDouble(3));
                        dbDateInfosList.add(dbDateInfo);
                        baseAdapter.setData(dbDateInfosList);

                        Log.d("d","row " +cursor.getInt(0));


                    }
                    row = row + MAX_LOW_DATA_COUNT;
                    cursor =dbManager.printData("select * from " + SQL_LITE_DATABASE_TABLE_NAME  +" LIMIT " +row+ ", " + MAX_LOW_DATA_COUNT ) ;
                }

            }
        });
        return view;
    }
}
