package navermap.test.sangwoo.navercodetest;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sangwoo-pc on 2016. 12. 7..
 */

public class DBlistAdapter extends BaseAdapter {


    Context context;
    ArrayList<DBDateInfo> list;

    public DBlistAdapter(Context context, ArrayList<DBDateInfo> list) {
        this.context = context;
        this.list = list;
    }

    public void setData(ArrayList<DBDateInfo> list){
        this.list= list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 0;

        } else {
            return list.size();
        }
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();

        if (convertView == null) {
            // view가 null일 경우 커스텀 레이아웃을 얻어 옴
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.dblist_row, viewGroup, false);

            TextView date = (TextView) convertView.findViewById(R.id.txv_date);
            date.setText(list.get(i).getDate());

            TextView count = (TextView) convertView.findViewById(R.id.txv_count);
            count.setText(Global.getManBOcount(context,list.get(i).getManBoGiCount()));

            TextView txvDistence = (TextView) convertView.findViewById(R.id.txv_distence);
            txvDistence.setText(Global.getMeterOrKillmeter(context, list.get(i).getDistence()));


        }
        return convertView;

    }
}