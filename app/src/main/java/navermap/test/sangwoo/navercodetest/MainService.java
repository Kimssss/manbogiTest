package navermap.test.sangwoo.navercodetest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.maplib.NGeoPoint;

import static navermap.test.sangwoo.navercodetest.Global.FAIL_GET_GPS_INFOMATION;
import static navermap.test.sangwoo.navercodetest.Global.PREFERENCES_LATITUDE_NAME;
import static navermap.test.sangwoo.navercodetest.Global.PREFERENCES_LONGITUDE_NAME;
import static navermap.test.sangwoo.navercodetest.Global.PREFERENCE_NAME;
import static navermap.test.sangwoo.navercodetest.Global.PREFERNCES_MANBO_COUNT_DEFAULT_VALUE;

/**
 * Created by sangwoo-pc on 2016. 12. 6..
 */

public class MainService extends Service implements SensorEventListener {

    private long lastTime;
    private float speed;
    private float lastX;
    private float lastY;
    private float lastZ;
    private float x, y, z;

    private static final int SHAKE_THRESHOLD = 1000;
    private static final int DATA_X = SensorManager.DATA_X;
    private static final int DATA_Y = SensorManager.DATA_Y;
    private static final int DATA_Z = SensorManager.DATA_Z;

    @Override
    public boolean onUnbind(Intent intent) {
        sensorManager.unregisterListener(this);

        return false;
    }

    private SensorManager sensorManager;
    private Sensor accelerormeterSensor;

    NMapLocationManager mMapLocationManager;
    NMapCompassManager mMapCompassManager;
//    NMapView mMapView;





    SharedPreferences sharedPreferences;


    int manboCount = 0;
    double longitude = 0;
    double latitude = 0;
    double distance = 0;

    private WindowManager.LayoutParams mParams;  //layout params 객체. 뷰의 위치 및 크기
    private WindowManager mWindowManager;          //윈도우 매니저


    private float START_X, START_Y;
    private int PREV_X, PREV_Y;
    private int MAX_X = -1, MAX_Y = -1;

    LinearLayout linearLayout;
    TextView txvBackgroundDistence;
    TextView txvBackgroundCount;
    boolean isPause = false;

    public void onCreate() {
        super.onCreate();
        sharedPreferences
                = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);;//나혼자만



        manboCount = sharedPreferences.getInt(DBManager.SQL_LITE_DATABASE_MAN_BO_GI_COUNT, PREFERNCES_MANBO_COUNT_DEFAULT_VALUE);
        longitude = sharedPreferences.getFloat(PREFERENCES_LONGITUDE_NAME, 0);
        latitude = sharedPreferences.getFloat(PREFERENCES_LATITUDE_NAME , 0);
        distance = sharedPreferences.getFloat(DBManager.SQL_LITE_DATABASE_DISTENCE , 0);

        mMapLocationManager = new NMapLocationManager(this);
        mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);



        startMyLocation();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerormeterSensor != null)
            sensorManager.registerListener(this, accelerormeterSensor,
                    SensorManager.SENSOR_DELAY_GAME);




    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorManager == null)
            return;
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);
            if (gabOfTime > 100) {
                lastTime = currentTime;
                x = sensorEvent.values[SensorManager.DATA_X];
                y = sensorEvent.values[SensorManager.DATA_Y];
                z = sensorEvent.values[SensorManager.DATA_Z];

                speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    // 이벤트발생!!

                    manboCount ++;
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putInt(DBManager.SQL_LITE_DATABASE_MAN_BO_GI_COUNT , manboCount);
                    edit.commit();



                    if(latitude == 0  || longitude == 0){
                        if(!isPause)
                            mCallback.count(manboCount ,FAIL_GET_GPS_INFOMATION,longitude,latitude);
                        else{
                            if(txvBackgroundCount != null)
                                txvBackgroundCount.setText(String.valueOf(manboCount));

                            if(txvBackgroundDistence != null){
                                if(1000>distance){
                                    txvBackgroundDistence.setText(String.valueOf( Global.meter(distance)) +  getString(R.string.meter));

                                }else
                                {
                                    txvBackgroundDistence.setText(String.valueOf( Global.meterToKillmeter(distance)) +  getString(R.string.kilometer));

                                }

                            }
                        }

                        edit.putFloat(PREFERENCES_LATITUDE_NAME, (float)latitude );
                        edit.commit();

                        edit.putFloat(PREFERENCES_LONGITUDE_NAME, (float)longitude );
                        edit.commit();

                    }else{
                        double tempLongitude = sharedPreferences.getFloat(PREFERENCES_LONGITUDE_NAME,0);
                        double tempLatitude = sharedPreferences.getFloat(PREFERENCES_LATITUDE_NAME,0);

                        if (tempLatitude == 0 && tempLongitude ==0){
                            tempLatitude = latitude;
                            tempLongitude = longitude;
                        }
                        double addDistance= 0 ;

                        addDistance = NGeoPoint.getDistance(new NGeoPoint(longitude,latitude) , new NGeoPoint(tempLongitude, tempLatitude));
                        if(Global.MAX_ONE_SETP_MOVE_DISTANCE > addDistance){
                            distance = distance +  addDistance;
                            Log.d("d", "distance " + distance);

                        }
                        if(!isPause)
                            mCallback.count(manboCount , distance  ,(float)longitude,(float)latitude);
                        else{
                            if(txvBackgroundDistence != null){
                                if(1000>distance){
                                    txvBackgroundDistence.setText(String.valueOf( Global.meter(distance)) +  getString(R.string.meter));

                                }else{

                                    txvBackgroundDistence.setText(String.valueOf( Global.meterToKillmeter(distance)) +  getString(R.string.kilometer));
                                }
                            }

                            if(txvBackgroundCount != null)
                                txvBackgroundCount.setText(String.valueOf(manboCount));

                        }

                        edit.putFloat(DBManager.SQL_LITE_DATABASE_DISTENCE, (float)distance);
                        edit.commit();

                        edit.putFloat(PREFERENCES_LATITUDE_NAME, (float)latitude );
                        edit.commit();

                        edit.putFloat(PREFERENCES_LONGITUDE_NAME, (float)longitude );
                        edit.commit();

                    }



                }

                lastX = sensorEvent.values[DATA_X];
                lastY = sensorEvent.values[DATA_Y];
                lastZ = sensorEvent.values[DATA_Z];
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    //서비스 바인더 내부 클래스 선언
    public class MainServiceBinder extends Binder {
        MainService getService() {
            return MainService.this; //현재 서비스를 반환.
        }
    }

    private final IBinder mBinder = new MainServiceBinder();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }

    //콜백 인터페이스 선언
    public interface ICallback {
        void count(int count , double distance ,double longitude, double latitude); //액티비티에서 선언한 콜백 함수.
    }

    public void onActivityInfo(boolean isPause){
        this.isPause = isPause;
        if(isPause){
            LayoutInflater inflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );

            linearLayout = (LinearLayout) inflater.inflate( R.layout.background_main, null );

            txvBackgroundCount = (TextView) linearLayout.findViewById(R.id.txv_background_count);
            txvBackgroundDistence = (TextView) linearLayout.findViewById(R.id.txv_background_distence);

            txvBackgroundCount.setText(String.valueOf(sharedPreferences.getInt(DBManager.SQL_LITE_DATABASE_MAN_BO_GI_COUNT,DBManager.DEFAULT_VALUE_MAN_BO_GI_COUNT)));
            double distence = sharedPreferences.getFloat(DBManager.SQL_LITE_DATABASE_DISTENCE,DBManager.DEFAULT_VALUE_DISTENCE);

            if(1000>distence){
                txvBackgroundDistence.setText(Global.meter(distence) + getString(R.string.meter));

            }else {
                txvBackgroundDistence.setText(Global.meterToKillmeter(distence) + getString(R.string.kilometer));

            }

            //최상위 윈도우에 넣기 위한 설정
            mParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,//항상 최 상위. 터치 이벤트 받을 수 있음.
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,  //포커스를 가지지 않음
                    PixelFormat.TRANSLUCENT);                                        //투명
            mParams.gravity = Gravity.LEFT | Gravity.TOP;                   //왼쪽 상단에 위치하게 함.

           mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);  //윈도우 매니저
            mWindowManager.addView(linearLayout, mParams);      //윈도우에 뷰 넣기. permission 필요.
            linearLayout.setOnTouchListener(mViewTouchListener);              //팝업뷰에 터치 리스너 등록

        }else{
            if(mWindowManager != null) {        //서비스 종료시 뷰 제거. *중요 : 뷰를 꼭 제거 해야함.
                if(linearLayout != null) mWindowManager.removeView(linearLayout);
            }
        }

    }

    private View.OnTouchListener mViewTouchListener = new View.OnTouchListener() {
        @Override public boolean onTouch(View v, MotionEvent event) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:                //사용자 터치 다운이면
                    if(MAX_X == -1)
                        setMaxPosition();
                    START_X = event.getRawX();                    //터치 시작 점
                    START_Y = event.getRawY();                    //터치 시작 점
                    PREV_X = mParams.x;                            //뷰의 시작 점
                    PREV_Y = mParams.y;                            //뷰의 시작 점
                    break;
                case MotionEvent.ACTION_MOVE:
                    int x = (int)(event.getRawX() - START_X);    //이동한 거리
                    int y = (int)(event.getRawY() - START_Y);    //이동한 거리

                    //터치해서 이동한 만큼 이동 시킨다
                    mParams.x = PREV_X + x;
                    mParams.y = PREV_Y + y;

                    optimizePosition();        //뷰의 위치 최적화
                    mWindowManager.updateViewLayout(linearLayout, mParams);    //뷰 업데이트
                    break;
            }

            return true;
        }
    };

    private void setMaxPosition() {
        DisplayMetrics matrix = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(matrix);		//ȭ�� ������ �����ͼ�

        MAX_X = matrix.widthPixels - linearLayout.getWidth();			//x �ִ밪 ����
        MAX_Y = matrix.heightPixels - linearLayout.getHeight();			//y �ִ밪 ����
    }

    private void optimizePosition() {
        if(mParams.x > MAX_X) mParams.x = MAX_X;
        if(mParams.y > MAX_Y) mParams.y = MAX_Y;
        if(mParams.x < 0) mParams.x = 0;
        if(mParams.y < 0) mParams.y = 0;
    }

    private ICallback mCallback;

    //액티비티에서 콜백 함수를 등록하기 위함.
    public void registerCallback(ICallback cb) {
        mCallback = cb;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        return super.onStartCommand(intent, flags, startId);
    }


    /* MyLocation Listener */
    private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {

        @Override
        public boolean onLocationChanged(NMapLocationManager locationManager, NGeoPoint myLocation) {

            latitude = myLocation.getLatitude();
            longitude = myLocation.getLongitude();
            Log.d("d", "nMap location lat " + myLocation.getLatitude() +" lng " + myLocation.getLongitude());
            return true;
        }

        @Override
        public void onLocationUpdateTimeout(NMapLocationManager locationManager) {


            Toast.makeText(MainService.this, "Your current location is temporarily unavailable.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {

            Toast.makeText(MainService.this, "Your current location is unavailable area.", Toast.LENGTH_LONG).show();

        }

    };

    private void startMyLocation() {


        if (mMapLocationManager.isMyLocationEnabled()) {


                mMapCompassManager.enableCompass();






        } else {
            boolean isMyLocationEnabled = mMapLocationManager.enableMyLocation(true);
            if (!isMyLocationEnabled) {
                Toast.makeText(this, "Please enable a My Location source in system settings",
                        Toast.LENGTH_LONG).show();

                Intent goToSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(goToSettings);

                return;
            }
        }
//        }
    }
}
