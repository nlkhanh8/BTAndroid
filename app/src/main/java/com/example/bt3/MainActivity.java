package com.example.bt3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.*;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView lvFood;
    private GetData a;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControl();
        loadEvent();
    }

    private void addControl() {
        lvFood = findViewById(R.id.lvFood);

    }

    private void loadEvent() {
        a = new GetData(); a.execute();
        lvFood.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, "Vi tri " + i, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), SubActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("link", a.catalogListLinks.get(i).toString());
                bundle.putString("catalogName", a.catalogListItem.get(i).toString());
                intent.putExtra("data", bundle);
                startActivity(intent);
            }
        });
    }

    class GetData extends AsyncTask<String,Void,String>{
        protected ArrayList<String> catalogListItem = new ArrayList<>();
        protected ArrayList<String> catalogListLinks = new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String url = "https://www.petfoodindustry.com/rss";
                Document doc = Jsoup.connect(url).ignoreContentType(true).get();
                Elements links = doc.select(".topic-feed-item__headline");
                for (Element e: links) {
                    catalogListLinks.add(e.getElementsByTag("a").attr("href").toString());
                    catalogListItem.add(e.getElementsByTag("a").attr("title").toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.print("Error");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ArrayAdapter adapter = new ArrayAdapter(MainActivity.this,
                    android.R.layout.simple_list_item_1, catalogListItem);
            lvFood.setAdapter(adapter);
        }
    }
}

