package elink.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import demo.demo.R;
import com.coolkit.common.HLog;

import elink.utils.SpHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yaoyi on 10/12/15.
 */
public class CountryCodeActivity extends BasicActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = CountryCodeActivity.class.getSimpleName();

    ListView mlistCountryCode;
    String CountryName = "";
    String CountryCode = "";
    List<String> citylist = new ArrayList<String>();
    List<String> citycode = new ArrayList<String>();
    public SpHelper sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countrycode);
        sp = new SpHelper(this);
        initView();
        initData();
        setView();
    }

    public void setView() {
        if (null != this.getActionBar()) {
            getActionBar().setLogo(new BitmapDrawable());
            this.getActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(getString(R.string.choice_part));
        }
        MyAdapter adapter = new MyAdapter();
        mlistCountryCode.setAdapter(adapter);
    }

    public class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return citylist==null?0:citylist.size();
        }

        @Override
        public Object getItem(int position) {
            return citylist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if(convertView == null){
                viewHolder = new ViewHolder();

                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_country_list,null);
                viewHolder.mCountryName = (TextView) convertView.findViewById(R.id.tv_countryitem);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.mCountryName.setText(citylist.get(position));
            return convertView;
        }
    }

    public static class ViewHolder{
        public TextView mCountryName;
    }

    private void initData() {
        String[] rl = getResources().getStringArray(R.array.country_code1);
        for (int i = 0; i<rl.length; i++){
            String[] g = rl[i].split("\\+");
            CountryName = g[0];
            CountryCode = g[1];
            citylist.add(CountryName);
            citycode.add(CountryCode);
        }

    }

    private void initView() {
        mlistCountryCode = (ListView) findViewById(R.id.lv_countrycode);
        mlistCountryCode.setOnItemClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    String item;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        String str = getIntent().getStringExtra("key");
        Intent intent = getIntent();
        String str = intent.getStringExtra("key");
//        if(str.equals("ShareDeviceActvity")){
//            item = citycode.get(position);
//            intent.putExtra("result", "+"+item);
//            intent.setAction("com.android.share");
//            sp.saveCountryCode("+" + item);
//            sendBroadcast(intent);
//        }else{
//            item = citycode.get(position);
//            Intent intent = new Intent();
//            intent.putExtra("result", "+"+item);
//            app.mCountry="+"+item;
//            intent.setAction("com.android.data");
//            sp.saveCountryCode("+" + item);
//            HLog.i(TAG, item + "countrycode");
//            sendBroadcast(intent);
//        }
//        finish();
        item = citycode.get(position);
        if(str.equals("login")){
            intent = new Intent();
            intent.putExtra("result", "+"+item);
            app.mCountry="+"+item;
            intent.setAction("com.android.login");
            sp.saveCountryCode("+" + item);
            HLog.i(TAG, item + "countrycode");
            sendBroadcast(intent);
        }else if(str.equals("share")){
            intent = new Intent();
            intent.putExtra("share","+"+item);
            intent.setAction("com.android.share");
            sendBroadcast(intent);
        }else if(str.equals("register")){
            intent = new Intent();
            intent.putExtra("register","+"+item);
            intent.setAction("com.android.register");
            sendBroadcast(intent);
        }
        finish();
    }
}
