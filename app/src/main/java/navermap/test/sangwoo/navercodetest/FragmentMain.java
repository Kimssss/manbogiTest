package navermap.test.sangwoo.navercodetest;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by sangwoo-pc on 2016. 12. 7..
 */

public class FragmentMain extends Fragment {

    TextView txvCount;
    TextView txvDistence;
    TextView txvLocation;
    Button btnStart;

    public interface ServiceClickLietener{
        void onClick(String mode);
    }

    ServiceClickLietener serviceClickLietener;

    public ServiceClickLietener getServiceClickLietener() {
        return serviceClickLietener;
    }

    public void setServiceClickLietener(ServiceClickLietener serviceClickLietener) {
        this.serviceClickLietener = serviceClickLietener;
    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=null;//Fragment가 보여줄 View 객체를 참조할 참조변수

        view= inflater.inflate(R.layout.fragment_main, null);

        txvCount = (TextView) view.findViewById(R.id.txv_count);
        txvDistence = (TextView) view.findViewById(R.id.txv_distence);
        txvLocation = (TextView) view.findViewById(R.id.txv_location);
        btnStart = (Button) view.findViewById(R.id.btn_start);


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(serviceClickLietener != null)
                    serviceClickLietener.onClick(btnStart.getText().toString());

                if(btnStart.getText().toString().equals(getString(R.string.start))){
                    btnStart.setBackgroundColor(getResources().getColor(R.color.colorBlue));
                    btnStart.setText(getString(R.string.stop));
                }
                else{
                    btnStart.setBackgroundColor(getResources().getColor(R.color.colorRed));
                    btnStart.setText(getString(R.string.start));
                }
            }
        });
        return view;
    }
//    int count, double distance, double longitude, double latitude
    public void setCount(int count){
        if(getActivity() == null)
            return;

        if(txvCount != null)
            txvCount.setText(String.valueOf(count));
    }

    public void setDistance(double distance){
        if(getActivity() == null)
            return;

        if(txvDistence != null){

            if(1000> distance){
                txvDistence.setText(Global.meter(distance) + getString(R.string.meter));

            }else {
                txvDistence.setText(Global.meterToKillmeter(distance) + getString(R.string.kilometer));

            }
        }
    }

    public  void setLocation(String location){
        if(getActivity() == null)
            return;

        if(txvLocation != null)
            txvLocation.setText(location);
    }
}
