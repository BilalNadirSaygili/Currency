package bns237.webservicesapp;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.ListView;


public class MainActivity extends AppCompatActivity {
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        WebServiceAsynTask task = new WebServiceAsynTask(this);
        task.execute("http://www.tcmb.gov.tr/kurlar/today.xml");
    }


}

