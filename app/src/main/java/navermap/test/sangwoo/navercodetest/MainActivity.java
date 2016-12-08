package navermap.test.sangwoo.navercodetest;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;

import static navermap.test.sangwoo.navercodetest.Global.PREFERENCE_NAME;

public class MainActivity extends NMapActivity {

    //액티비티에서 선언.
    private MainService mService; //서비스 클래스



    boolean isPause = false;

    FragmentMain fragmentMain ;
    FragmentSQLlite fragmentSQLite;

    FragmentManager manager;  //Fragment를 관리하는 클래스의 참조변수
    FragmentTransaction tran;  //실제로 Fragment를 추가/삭제/재배치 하는 클래스의 참조변수

    //서비스 커넥션 선언.
    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            MainService.MainServiceBinder binder = (MainService.MainServiceBinder) service;
            mService = binder.getService(); //서비스 받아옴
            mService.registerCallback(mCallback); //콜백 등록

        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };

    //서비스에서 아래의 콜백 함수를 호출하며, 콜백 함수에서는 액티비티에서 처리할 내용 입력
    private MainService.ICallback mCallback = new MainService.ICallback() {
        @Override
        public void count(int count, double distance, double longitude, double latitude) {
            if(fragmentMain != null){
                fragmentMain.setCount(count);
                if(distance >=0)
                   fragmentMain.setDistance(distance);
            }





            Log.d("d"," mCallback longitude " + longitude + " latitude  " + latitude);
            try{
                findPlacemarkAtLocation(longitude,latitude);
            }catch (Exception e){

            }
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
        isPause = false;
        if(mService != null)
            mService.onActivityInfo(isPause);

        intiFragmantMain();

    }

    @Override
    protected void onPause() {
        isPause =true;
        if(mService != null)
            mService.onActivityInfo(isPause);
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    /* NMapDataProvider Listener */
    private final OnDataProviderListener onDataProviderListener = new OnDataProviderListener() {

        @Override
        public void onReverseGeocoderResponse(NMapPlacemark placeMark, NMapError errInfo) {
           if(placeMark != null && placeMark.toString() != null){
               if(fragmentMain != null){
                   fragmentMain.setLocation(placeMark.toString());
               }
           }
        }

    };

    private void intiFragmantMain(){

        if(fragmentMain != null){
            tran= manager.beginTransaction();

            tran.replace(R.id.container, fragmentMain);
            tran.commitAllowingStateLoss();


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SharedPreferences sharedPreferences
                            = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);;//나혼자만
                    fragmentMain.setCount(sharedPreferences.getInt(DBManager.SQL_LITE_DATABASE_MAN_BO_GI_COUNT,DBManager.DEFAULT_VALUE_MAN_BO_GI_COUNT));
                    fragmentMain.setDistance(sharedPreferences.getFloat(DBManager.SQL_LITE_DATABASE_DISTENCE,DBManager.DEFAULT_VALUE_DISTENCE));

                }
            });

            fragmentMain.setServiceClickLietener(new FragmentMain.ServiceClickLietener() {
                @Override
                public void onClick(String mode) {
                    if (getString(R.string.start).equals(mode)) {
                        Intent service = new Intent(MainActivity.this, MainService.class);
                        bindService(service, mConnection, Context.BIND_AUTO_CREATE);
                    }
                    else{
                        unbindService(mConnection);
                    }

                }
            });
        }
    }

    private void intiFragmentSqlLite(){
        if(fragmentSQLite != null){
            tran= manager.beginTransaction();

            tran.replace(R.id.container, fragmentSQLite);
            tran.commit();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1234);
            }
        }

        manager= (FragmentManager)getFragmentManager();

        fragmentMain = new FragmentMain();
        fragmentSQLite = new FragmentSQLlite();


        findViewById(R.id.btn_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intiFragmantMain();
            }
        });

        findViewById(R.id.btn_sqllite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    unbindService(mConnection);
                }catch (Exception e){

                }


                intiFragmentSqlLite();
            }
        });


        DBManager dbManager = new DBManager(this , DBManager.SQL_LITE_DATABASE_TABLE_NAME, null ,DBManager.DATABASE_VERSION);

        intiFragmantMain();

        NMapView mMapView = new NMapView(this);
        mMapView.setClientId("p_KrYE68n6BxcZLxdJXu");
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();

        setMapDataProviderListener(onDataProviderListener );


    }
}
