package com.example.jakek.ubicomp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        this.showGraph();



    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar, menu);
        return true;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ReceiptHistory:
                startActivity(new Intent(this, ReceiptHistory.class));
                return true;
            case R.id.ShoppingList:
                startActivity(new Intent(this, ShoppingList.class));
                return true;
            case R.id.StatsPage:
                startActivity(new Intent(this, StatsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    protected void onStart() {
        super.onStart();
    }


    public void showGraph(){
        GraphView graph = (GraphView) findViewById(R.id.graph);

        DBHandler db = new DBHandler(this);

        String[] names = {"Jun", " Jul", "Aug", "Sep", "Oct", "Nov",""};
        String year = new SimpleDateFormat("yyyy").format(new Date());
        int month = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()))-5;

        DataPoint[] allPoints = new DataPoint[6];

        for (int i = 0; i< 6; i++) {
            int x = db.spendByMonth(month + "/" + year);
            allPoints[i] = new DataPoint(i, x);
            month = month+1;

        }
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(allPoints);
        graph.addSeries(series);
        series.setSpacing(2);
        series.setDrawValuesOnTop(true);

        series.setAnimated(true);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(names);

        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
//        graph.getGridLabelRenderer().setNumHorizontalLabels(12);
        graph.getGridLabelRenderer().setNumVerticalLabels(6);
        graph.getGridLabelRenderer().setVerticalAxisTitle("Spend in €");
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Last 6 months");


        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setScrollable(true);
    }
}
