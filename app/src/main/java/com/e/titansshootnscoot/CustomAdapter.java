package com.e.titansshootnscoot;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Admin on 3/3/2018.
 */

public class CustomAdapter  extends BaseAdapter {

    private Context context;


    public CustomAdapter(Context context) {

        this.context = context;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }
    @Override
    public int getItemViewType(int position) { return position; }

    @Override
    public int getCount() {
        return MainActivity.modelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return MainActivity.modelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


//    @Override
//    public boolean isEnabled(int position) {
//        Button bt = new Button();
//        ListView listView = (ListView) getItem(position);
//        listView.getAdapter().
//        holder.btn_plus.setEnabled(false);
//        holder.btn_minus.setEnabled(false);
//        return true;
//    }

//    @Override
//    public boolean isEnabled(int position) {
//        if(YOUR CONDITION){
//            return false;
//        }
//        return true;
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        boolean red = true;

        if (convertView == null) {
            holder = new ViewHolder(); LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lv_item, null, true);

            holder.tvFruit = convertView.findViewById(R.id.animal);
            holder.tvnumber = convertView.findViewById(R.id.number);
            holder.tvtime = convertView.findViewById(R.id.time);

            holder.btn_plus = convertView.findViewById(R.id.plus);
            holder.btn_minus = convertView.findViewById(R.id.minus);

            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

        holder.tvFruit.setText(MainActivity.modelArrayList.get(position).getHole());
        holder.tvnumber.setText(String.valueOf(MainActivity.modelArrayList.get(position).getNumber()));

        holder.tvtime.setText(MainActivity.modelArrayList.get(position).getElapsedTime());
        if (!MainActivity.modelArrayList.get(position).getElapsedTime().equals("-1")) {
            red = false;
        }
        if (!red) {
            holder.tvtime.setTextColor(Color.GREEN);
        }

        holder.btn_plus.setTag(R.integer.btn_plus_view, convertView);
        holder.btn_plus.setTag(R.integer.btn_plus_pos, position);

        holder.btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View tempview = (View) holder.btn_plus.getTag(R.integer.btn_plus_view);
                TextView tv_num = tempview.findViewById(R.id.number);
                Integer pos = (Integer) holder.btn_plus.getTag(R.integer.btn_plus_pos);

                int number = Integer.parseInt(tv_num.getText().toString()) + 1;
                tv_num.setText(String.valueOf(number));

                TextView tv_time = tempview.findViewById(R.id.time);
                android.util.Log.i("time1: ", tv_time.getText().toString());

                if (tv_time.getText().toString().equals("-1")) {
                    android.util.Log.i("time2: ", MainActivity.timerValue.getText().toString());
                    //TextView tv_curTime = tempview.findViewById(R.id.timerValue);
                    String curTime = MainActivity.timerValue.getText().toString();
                    android.util.Log.i("curTime: ", curTime);

                    tv_time.setText(curTime);
                    tv_time.setTextColor(Color.GREEN);
                    MainActivity.modelArrayList.get(pos).setElapsedTime(curTime);
                }

                MainActivity.modelArrayList.get(pos).setNumber(number);
            }
        });

        holder.btn_minus.setTag(R.integer.btn_minus_view, convertView);
        holder.btn_minus.setTag(R.integer.btn_minus_pos, position);

        holder.btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View tempview = (View) holder.btn_minus.getTag(R.integer.btn_minus_view);
                TextView tv = tempview.findViewById(R.id.number);
                Integer pos = (Integer) holder.btn_minus.getTag(R.integer.btn_minus_pos);

                int number = Integer.parseInt(tv.getText().toString()) - 1;
                if (number <= 0) number = 0;        // don't allow negative number
                tv.setText(String.valueOf(number));

                MainActivity.modelArrayList.get(pos).setNumber(number);

            }
        });

        // see: https://stackoverflow.com/questions/36418939/how-to-disable-recyclerview-items-in-android
        // and  http://android.amberfog.com/?p=296
        holder.btn_plus.setEnabled(false);
        holder.btn_minus.setEnabled(false);

        return convertView;
    }

    private class ViewHolder {
        protected Button btn_plus, btn_minus;
        private TextView tvFruit, tvnumber, tvtime;
    }

}

