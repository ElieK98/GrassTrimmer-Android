package com.example.grasstrimmer.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.grasstrimmer.Model.Helpers;
import com.example.grasstrimmer.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class StatisticsFragment extends Fragment {

    BarChart barChart;
    ArrayList<BarEntry> barEntryArrayList;
    ArrayList<String> dates;

    ArrayList<TrimmingData> TrimmingDataArrayList = new ArrayList<>(); //sessionLength
    ArrayList<TrimmingData> DistanceDataArrayList = new ArrayList<>();//distance
    ArrayList<TrimmingData> SpeedDataArrayList = new ArrayList<>();
    ArrayList<TrimmingData> TrimTimeArrayList = new ArrayList<>();//Trimming

    DatabaseReference reference;
    //create new object of bare entries arraylist and labels arraylist

    LinearLayout averageDistanceLayout;
    LinearLayout sessionLengthLayout;
    LinearLayout averageSpeedLayout;
    LinearLayout trimTimeLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        averageDistanceLayout = view.findViewById(R.id.averageDistance);
        sessionLengthLayout =view.findViewById(R.id.sessionLength);
        averageSpeedLayout=view.findViewById(R.id.averageSpeed);
        trimTimeLayout=view.findViewById(R.id.trimTime);

        barChart = view.findViewById(R.id.barChart);
        barEntryArrayList = new ArrayList<>();
        dates = new ArrayList<String>();
//        fillMonthSales();

        String userID = Helpers.getFromPreferences("userID", getContext());
        reference = FirebaseDatabase.getInstance().getReference("ESP1").child(userID);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> snapshotValue = (HashMap<String, Object>) dataSnapshot.getValue();
                Iterator iterator = snapshotValue.entrySet().iterator();
                HashMap<Date, Object> days = new HashMap<>();
                while (iterator.hasNext()) {
                    Map.Entry pair = (Map.Entry) iterator.next();
                    try {
                        days.put(new SimpleDateFormat("dd-MM-yyyy").parse((String) pair.getKey()), pair.getValue());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                ArrayList<Map<String, Long>> sessionLengths = new ArrayList<>();
                ArrayList<Map<String,Long>> distances=new ArrayList<>();
                ArrayList<Map<String, Long>> Speeds=new ArrayList<>();
                ArrayList<Map<String,Long>> trimTimes=new ArrayList<>();

                Iterator daysIterator = days.entrySet().iterator();
                while (daysIterator.hasNext()) {
                    Map.Entry pair = (Map.Entry) daysIterator.next();
                    Date date = (Date) pair.getKey();
                    HashMap<String, Object> sessions = (HashMap<String, Object>) pair.getValue();

                    //SORT SESSIONS
                    Set<Map.Entry<String, Object>> entries = sessions.entrySet();
                    TreeMap<String, Object> sorted = new TreeMap<>(sessions);
                    Set<Map.Entry<String, Object>> mappings = sorted.entrySet();


                    Iterator sessionIterator = mappings.iterator();
                    while (sessionIterator.hasNext()) {
                        Map.Entry sessionIteratorPair = (Map.Entry) sessionIterator.next();
                        HashMap<String, Object> session = (HashMap<String, Object>) sessionIteratorPair.getValue();

                        Map<String, Long> map = new HashMap<>();
                        Map<String, Long> distanceMap=new HashMap<>();
                        Map<String, Long> SpeedMap=new HashMap<>();
                        Map<String, Long> TrimMap=new HashMap<>();

                        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd / MM");
                        final String dayString = df.format(date);

                        map.put(dayString, (Long) session.get("sessionLength"));
                        distanceMap.put(dayString,(Long) session.get("averageDistance"));
                        SpeedMap.put(dayString,(Long) session.get("averageSpeed"));
                        TrimMap.put(dayString,(Long) session.get("trimTimeTotal"));

                        sessionLengths.add(map);
                        distances.add(distanceMap);
                        Speeds.add(SpeedMap);
                        trimTimes.add(TrimMap);


                    }
                }
                //loading sessionLength
                TrimmingDataArrayList.clear();
                for(int i = 0; i < sessionLengths.size(); i++) {
                    Map.Entry<String, Long> entry = sessionLengths.get(i).entrySet().iterator().next();
                    String key = entry.getKey();
                    Long value = entry.getValue();
                    TrimmingDataArrayList.add(new TrimmingData(key,value));
                }
                //loading distance trimmed
                DistanceDataArrayList.clear();
                for(int i=0;i<distances.size();i++){
                    Map.Entry<String,Long> entry=distances.get(i).entrySet().iterator().next();
                    String key=entry.getKey();
                    Long value=entry.getValue();
                    DistanceDataArrayList.add(new TrimmingData(key,value));
                }

                //loading speed of robot
                SpeedDataArrayList.clear();
                for(int i=0;i<Speeds.size();i++){
                    Map.Entry<String,Long> entry=Speeds.get(i).entrySet().iterator().next();
                    String key=entry.getKey();
                    Long value=entry.getValue();
                    SpeedDataArrayList.add(new TrimmingData(key,value));
                }
                //loading total trim time in one day
                TrimTimeArrayList.clear();
                for(int i=0;i<Speeds.size();i++){
                    Map.Entry<String,Long> entry=trimTimes.get(i).entrySet().iterator().next();
                    String key=entry.getKey();
                    Long value=entry.getValue();
                    TrimTimeArrayList.add(new TrimmingData(key,value));
                }
                //SHOW DATA IN CHART
                //Show SessionLength

/*
                for (int i = 0; i<TrimmingDataArrayList.size(); i++){
                    Long sessionLength = TrimmingDataArrayList.get(i).getSessionLength();
                    String date = TrimmingDataArrayList.get(i).getDate();
                    barEntryArrayList.add(new BarEntry(i, Float.parseFloat(String.valueOf(sessionLength))));
                    dates.add(date);
                }
*/


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reference.addValueEventListener(listener);


        sessionLengthLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barEntryArrayList.clear();
                for (int i = 0; i<TrimmingDataArrayList.size(); i++){
                    Long sessionLength = TrimmingDataArrayList.get(i).getSessionLength();
                    String date = TrimmingDataArrayList.get(i).getDate();
                    barEntryArrayList.add(new BarEntry(i, Float.parseFloat(String.valueOf(sessionLength))));
                    dates.add(date);
                }
                fillGraph();
            }
        });

        averageDistanceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barEntryArrayList.clear();
                for (int i = 0; i<DistanceDataArrayList.size(); i++){
                    Long distance = DistanceDataArrayList.get(i).getSessionLength();
                    String date = DistanceDataArrayList.get(i).getDate();
                    dates.add(date);
                    if (distance != null) {
                        barEntryArrayList.add(new BarEntry(i, Float.parseFloat(String.valueOf(distance))));
                    } else {
                        barEntryArrayList.add(new BarEntry(i, 0));
                    }
                }
                fillGraph();
            }
        });

        averageSpeedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barEntryArrayList.clear();
                for (int i = 0; i<SpeedDataArrayList.size(); i++){
                    Long speed = SpeedDataArrayList.get(i).getSessionLength();
                    String date = SpeedDataArrayList.get(i).getDate();
                    if (speed != null) {
                        barEntryArrayList.add(new BarEntry(i, Float.parseFloat(String.valueOf(speed))));
                    } else {
                        barEntryArrayList.add(new BarEntry(i, 0));
                    }
                    dates.add(date);
                }
                fillGraph();
            }
        });

        trimTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barEntryArrayList.clear();
                for (int i = 0; i<TrimTimeArrayList.size(); i++){
                    Long trimTime = TrimTimeArrayList.get(i).getSessionLength();
                    String date = TrimTimeArrayList.get(i).getDate();
                    if (trimTime != null) {
                        barEntryArrayList.add(new BarEntry(i, Float.parseFloat(String.valueOf(trimTime))));
                    } else {
                        barEntryArrayList.add(new BarEntry(i, 0));
                    }
                    dates.add(date);
                }
                fillGraph();
            }
        });

        return view;
    }

    private void fillGraph() {
        BarDataSet barDataSet = new BarDataSet(barEntryArrayList,"Session Lengths");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextColor(R.color.colorPrimaryGreen);
        Description description = new Description();
        description.setText("Date");
        barChart.setDescription(description);
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);


        //we need to set XAsis value formater
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));

        //set position of labels (months names)
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(dates.size());
        xAxis.setLabelRotationAngle(270);
        Legend l = barChart.getLegend();
        l.setTextColor(R.color.colorWhite);
        barChart.animateY(2000);
        barChart.invalidate();
    }

    /*private void fillMonthSales(){
        TrimmingDataArrayList.clear();
        TrimmingDataArrayList.add(new TrimmingData("04/05/2020","252000","1"));
        TrimmingDataArrayList.add(new TrimmingData("10/05/2020","200000","2"));
        TrimmingDataArrayList.add(new TrimmingData("15/05/2020","300000","3"));
        TrimmingDataArrayList.add(new TrimmingData("16/05/2020","190000","4"));
        TrimmingDataArrayList.add(new TrimmingData("24/05/2020","200000","5"));
        TrimmingDataArrayList.add(new TrimmingData("31/05/2020","30000","6"));
        TrimmingDataArrayList.add(new TrimmingData("05/06/2020","270000","7"));
        TrimmingDataArrayList.add(new TrimmingData("20/06/2020","200000","8"));
    }*/



}

