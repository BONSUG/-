package com.example.record;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;




import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity {

    private static String IP_ADDRESS = "";
    private static String TAG = "record";
    public String[] dlist = new String[7];
    private LineChart lineChart;
    public List<Entry> entries = new ArrayList<>();
    private TextView mTextViewResult;
    private ArrayList<PersonalData> mArrayList;
    private UsersAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private String mJsonString;


    public String getdatenow() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        Date date = new Date();
        String currentDate = formatter.format(date);
        SimpleDateFormat formatter2 = new SimpleDateFormat("MM/dd", Locale.KOREA);
        String d = formatter2.format(date);
        dlist[0]=d;
        return currentDate;
    }

    public String getdatebefore(int num){
        Date newDate = new Date();
        newDate = new Date(newDate.getTime()+(1000*60*60*24*-num));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        String date = formatter.format(newDate);
        SimpleDateFormat formatter2 = new SimpleDateFormat("MM/dd", Locale.KOREA);
        String d = formatter2.format(newDate);
        dlist[num]=d;
        return date;
    }

    public String[] getlist(){
        String[] d = new String[7];
        d[0]=getdatenow();
        d[1]=getdatebefore(1);
        d[2]=getdatebefore(2);
        d[3]=getdatebefore(3);
        d[4]=getdatebefore(4);
        d[5]=getdatebefore(5);
        d[6]=getdatebefore(6);
        return d;
    }


    public PersonalData GetPD(String date) {

        int index1 = 0;
        PersonalData pd = null;
        for (PersonalData p : mArrayList) {
            if (p.getDate().equals(date) && p.getName().equals("friend")) {
                pd=p;
                break;
            }
        }
        return pd;
    }
    public PersonalData GetPD2(String date) {

        int index1 = 0;
        PersonalData pd = null;
        for (PersonalData p : mArrayList) {
            if (p.getDate().equals(date) && p.getName().equals("me")) {
                pd=p;
                break;
            }
        }
        return pd;
    }

    private void DrawChart(ArrayList<PersonalData> m){
        lineChart = (LineChart)findViewById(R.id.chart);

        String[] d =getlist(); //검색용
        PersonalData p1=GetPD(d[6]);
        PersonalData p2=GetPD(d[5]);
        PersonalData p3=GetPD(d[4]);
        PersonalData p4=GetPD(d[3]);
        PersonalData p5=GetPD(d[2]);
        PersonalData p6=GetPD(d[1]);
        PersonalData p7=GetPD(d[0]);

        String[] d1 =getlist(); //검색용
        PersonalData tp1=GetPD2(d1[6]);
        PersonalData tp2=GetPD2(d1[5]);
        PersonalData tp3=GetPD2(d1[4]);
        PersonalData tp4=GetPD2(d1[3]);
        PersonalData tp5=GetPD2(d1[2]);
        PersonalData tp6=GetPD2(d1[1]);
        PersonalData tp7=GetPD2(d1[0]);

        LineData chartData = new LineData(); //

        List<Entry> entry2 = new ArrayList<>();
        entry2.add(new Entry(0f, Integer.parseInt(tp1.getReps())));
        entry2.add(new Entry(1f, Integer.parseInt(tp2.getReps())));
        entry2.add(new Entry(2f, Integer.parseInt(tp3.getReps())));
        entry2.add(new Entry(3f, Integer.parseInt(tp4.getReps())));
        entry2.add(new Entry(4f, Integer.parseInt(tp5.getReps())));
        entry2.add(new Entry(5f, Integer.parseInt(tp6.getReps())));
        entry2.add(new Entry(6f, Integer.parseInt(tp7.getReps())));

        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0f, Integer.parseInt(p1.getReps())));
        entries.add(new Entry(1f, Integer.parseInt(p2.getReps())));
        entries.add(new Entry(2f, Integer.parseInt(p3.getReps())));
        entries.add(new Entry(3f, Integer.parseInt(p4.getReps())));
        entries.add(new Entry(4f, Integer.parseInt(p5.getReps())));
        entries.add(new Entry(5f, Integer.parseInt(p6.getReps())));
        entries.add(new Entry(6f, Integer.parseInt(p7.getReps())));

        List<Entry> entry3 = new ArrayList<>();
        entry3.add(new Entry(0f, 3));
        entry3.add(new Entry(1f, 3));
        entry3.add(new Entry(2f, 3));
        entry3.add(new Entry(3f, 3));
        entry3.add(new Entry(4f, 3));
        entry3.add(new Entry(5f, 3));
        entry3.add(new Entry(6f, 3));


        LineDataSet lineDataSet2 = new LineDataSet(entries, "사용자 평균");

        lineDataSet2.setLineWidth(2);
        lineDataSet2.setCircleRadius(6);
        lineDataSet2.setCircleColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet2.setCircleColorHole(Color.BLUE);
        lineDataSet2.setColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet2.setDrawCircleHole(true);
        lineDataSet2.setDrawCircles(true);
        lineDataSet2.setDrawHorizontalHighlightIndicator(false);
        lineDataSet2.setDrawHighlightIndicators(false);
        lineDataSet2.setDrawValues(false);

        LineDataSet lineDataSet = new LineDataSet(entry2, "나의 기록");

        lineDataSet.setLineWidth(3);
        lineDataSet.setCircleRadius(6);
        lineDataSet.setCircleColor(Color.parseColor("#FA1100"));
        lineDataSet.setCircleColorHole(Color.RED);
        lineDataSet.setColor(Color.parseColor("#FA1100"));
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setDrawValues(false);

        LineDataSet lineDataSet3 = new LineDataSet(entry3, "권장 횟수");

        lineDataSet3.setLineWidth(2);
        lineDataSet3.setCircleRadius(6);
        lineDataSet3.setCircleColor(Color.parseColor("#6143F436"));
        lineDataSet3.setCircleColorHole(Color.GREEN);
        lineDataSet3.setColor(Color.parseColor("#6143F436"));
        lineDataSet3.setDrawCircleHole(true);
        lineDataSet3.setDrawCircles(true);
        lineDataSet3.setDrawHorizontalHighlightIndicator(false);
        lineDataSet3.setDrawHighlightIndicators(false);
        lineDataSet3.setDrawValues(false);
        
        LineData lineData = new LineData();
        lineData.addDataSet(lineDataSet);
        lineData.addDataSet(lineDataSet2);
        lineData.addDataSet(lineDataSet3);
        lineChart.setData(lineData);


        final String[] quarters = new String[] { dlist[6],dlist[5],dlist[4],dlist[3],dlist[2],dlist[1],dlist[0] };

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return quarters[(int) value];
            }


            public int getDecimalDigits() {  return 0; }
        };

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);


        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);

        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        Description description = new Description();
        description.setText("");

        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setDescription(description);
        lineChart.animateY(2000, Easing.EasingOption.EaseInCubic);
        lineChart.invalidate();
        int cal = 18*Integer.parseInt(tp7.getReps());
        //mTextViewResult.setText("오늘의 소모 칼로리 : "+cal+" Cal");
        TextView temp = (TextView)findViewById(R.id.textView3);
        temp.setText("오늘의 소모 칼로리 : "+cal+" Cal");
    }

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewResult = (TextView)findViewById(R.id.textView_main_result);
        mRecyclerView = (RecyclerView) findViewById(R.id.listView_main_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mTextViewResult.setMovementMethod(new ScrollingMovementMethod());



        mArrayList = new ArrayList<>();
//        LineChart lineChart = (LineChart) findViewById(R.id.chart);

        mAdapter = new UsersAdapter(this, mArrayList);
        mRecyclerView.setAdapter(mAdapter);


        Button button_all = (Button) findViewById(R.id.button_main_all);
        button_all.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                mArrayList.clear();
                mAdapter.notifyDataSetChanged();

                GetData task = new GetData();
                task.execute( "http://" + IP_ADDRESS + "/getjson.php", "");
            }
        });



    }



    private class GetData extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG, "response - " + result);

            if (result == null){

                mTextViewResult.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult();
                DrawChart(mArrayList);
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String postParameters = params[1];


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }


    private void showResult(){

        String TAG_JSON="webnautes";
        String TAG_DATE = "date";
        String TAG_TIME = "time";
        String TAG_REPS = "reps";
        String TAG_NAME = "name";



        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String date = item.getString(TAG_DATE);
                String time = item.getString(TAG_TIME);
                String reps = item.getString(TAG_REPS);
                String name = item.getString(TAG_NAME);


                PersonalData personalData = new PersonalData();

                personalData.setDate(date);
                personalData.setTime(time);
                personalData.setReps(reps);
                personalData.setName(name);


                mArrayList.add(personalData);
                mAdapter.notifyDataSetChanged();
            }



        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }
}