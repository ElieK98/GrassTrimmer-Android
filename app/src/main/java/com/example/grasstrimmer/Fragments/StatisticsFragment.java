package com.example.grasstrimmer.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.grasstrimmer.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class StatisticsFragment extends Fragment {

    BarChart barChart;
    ArrayList<BarEntry> barEntryArrayList;
    ArrayList<String> labelsNames;

    ArrayList<TrimmingData> TrimmingDataArrayList = new ArrayList<>();
    //create new object of bare entries arraylist and labels arraylist

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        barChart = view.findViewById(R.id.barChart);
        barEntryArrayList = new ArrayList<>();
        labelsNames = new ArrayList<>();
        //fillMonthSales();

        for (int i = 0; i<TrimmingDataArrayList.size(); i++){
            String date = TrimmingDataArrayList.get(i).getDate();
            String Minutes = TrimmingDataArrayList.get(i).getMinutes();
            barEntryArrayList.add(new BarEntry(i, Float.parseFloat(Minutes)));
            labelsNames.add(date);

        }

        BarDataSet barDataSet = new BarDataSet(barEntryArrayList,"Minutes of Trimming");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        Description description = new Description();
        description.setText("Date");
        barChart.setDescription(description);
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);


        //we need to set XAsis value formater
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labelsNames));

        //set position of labels (months names)
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labelsNames.size());
        xAxis.setLabelRotationAngle(270);

        barChart.animateY(2000);
        barChart.invalidate();
        return view;
    }
/*
    private void fillMonthSales(){
        TrimmingDataArrayList.clear();
        TrimmingDataArrayList.add(new TrimmingData("04/05/2020","252000","1"));
        TrimmingDataArrayList.add(new TrimmingData("10/05/2020","200000","2"));
        TrimmingDataArrayList.add(new TrimmingData("15/05/2020","300000","3"));
        TrimmingDataArrayList.add(new TrimmingData("16/05/2020","190000","4"));
        TrimmingDataArrayList.add(new TrimmingData("24/05/2020","200000","5"));
        TrimmingDataArrayList.add(new TrimmingData("31/05/2020","30000","6"));
        TrimmingDataArrayList.add(new TrimmingData("05/06/2020","270000","7"));
        TrimmingDataArrayList.add(new TrimmingData("20/06/2020","200000","8"));
    }
*/


}

