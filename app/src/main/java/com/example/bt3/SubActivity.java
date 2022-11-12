package com.example.bt3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SubActivity extends AppCompatActivity {
    private String catalogListLinks;
    private String catalogNameItem;
    private ListView lvFood1;
    private GetData1 a;
    private WebView wvBrowser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        addControls();
        addEvents();
    }

    private void addControls() {
        Intent intent = getIntent();
        try {
            Bundle bundle = intent.getBundleExtra("data");
            catalogNameItem = bundle.getString("catalogName");
            catalogListLinks = bundle.getString("link");
        } catch (Exception ee) {

        }
        lvFood1 = findViewById(R.id.lvFood1);
    }
    private void addEvents() {
        a = new GetData1(); a.execute(); a.onPostExecute("");
        lvFood1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(SubActivity.this, "Vị trí " + i, Toast.LENGTH_SHORT).show();
                openCustomDialog(i, Gravity.CENTER);
            }
        });
    }

    private void openCustomDialog(int i, int gravity) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dlg);
        Window window = dialog.getWindow();
        if(window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
        if(Gravity.BOTTOM == gravity) {
            dialog.setCancelable(true);
        } else {
            dialog.setCancelable(true);
        }
        TableRow tbVis;
        ImageView imgTitle, imgContent;
        TextView tvContent, tvDecription, tvTitle, tvDialog;
        Button btMore, btClose;
        tvDecription = dialog.findViewById(R.id.tvDecription);
        tvTitle = dialog.findViewById(R.id.tvTitle);
        imgTitle = dialog.findViewById(R.id.imgTitle);

        btMore = dialog.findViewById(R.id.btMore);
        btClose = dialog.findViewById(R.id.btClose);
        tvDialog = dialog.findViewById(R.id.tvDialog);
        tvContent = dialog.findViewById(R.id.tvContent);
        tvDialog.setText(catalogNameItem);
        tvContent.setText(a.foodArrayListItem.get(i).getDescription());
        tvTitle.setText(a.foodArrayListItem.get(i).getName());
        tvDecription.setText(a.foodArrayListItem.get(i).getTextDescription());
        imgContent = dialog.findViewById(R.id.imgContent);
        System.out.println(a.foodArrayListItem.get(i).getImage());
        try {
            (new DownloadImageFromInternet(imgContent, a.foodArrayListItem.get(i).getImage())).execute();
        } catch (Exception evv) {
            (new DownloadImageFromInternet(imgContent, a.foodArrayListItem.get(i).getImage())).execute();
        }
        dialog.show();
        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Myweb activity
//                Intent intent = new Intent(getApplicationContext(), ViewWebrowser.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("linkMore", a.foodArrayListItem.get(i).getHref());
//                intent.putExtra("das", bundle);
//                startActivity(intent);
                //Open with browser
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(a.foodArrayListItem.get(i).getHref()));
                startActivity(browserIntent);
            }
        });
    }
    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        String url;
        public DownloadImageFromInternet(ImageView imageView, String url) {
            this.imageView=imageView;
            this.url = url;
            Toast.makeText(getApplicationContext(), "Please wait, it may take a few minute...",Toast.LENGTH_SHORT).show();
        }
        protected Bitmap doInBackground(String... urls) {
            String imageURL=url;
            Bitmap bimage=null;
            System.out.println(url);
            try {
                InputStream in=new java.net.URL(imageURL).openStream();
                bimage=BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }
        protected void onPostExecute(Bitmap result) {
            if (result == null) {
            } else {
                result = getResizedBitmap(result, 40, 40);
                imageView.setImageBitmap(result);
            }
        }
        public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
            int width = bm.getWidth();
            int height = bm.getHeight();
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();
            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight);

            // "RECREATE" THE NEW BITMAP
            Bitmap resizedBitmap = Bitmap.createBitmap(
                    bm, 0, 0, width, height, matrix, false);
            bm.recycle();
            return resizedBitmap;
        }
    }
    class GetData1 extends AsyncTask<String,Void,String> {
        protected ArrayList<String> foodListItem = new ArrayList<>();
        protected ArrayList<Food> foodArrayListItem = new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String url = catalogListLinks;
                Document doc = Jsoup.connect(url).ignoreContentType(true).get();
                doc = Jsoup.parse(doc.toString(), "", Parser.xmlParser());
//                doc = Jsoup.parseBodyFragment (doc.toString());
                Elements item = doc.getElementsByTag("item");
                for (Element e: item) {
                    Food food = new Food();
                    String href = "";
                    String name = "";
                    String img = "";
                    String textDescription = "";
                    String decription = "";
                    img = e.getElementsByTag("media:content").attr("url").toString();
                    decription = e.select("description").text().replaceAll("</p>", "").replaceAll("<p>","");
                    textDescription = "Create: " + e.getElementsByTag("pubDate").text().substring(0, e.getElementsByTag("pubDate").text().length()-5);
                    href = e.getElementsByTag("guid").text();
                    name = e.getElementsByTag("title").text();
                    food.setName(name);
                    food.setHref(href);
                    food.setImage(img);
                    food.setTextDescription(textDescription);
                    food.setDescription(decription);
                    foodArrayListItem.add(food);
                    foodListItem.add(name);
                }
                System.out.println(foodListItem.size());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.print("Error");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ArrayAdapter adapter = new ArrayAdapter(SubActivity.this,
                    android.R.layout.simple_list_item_1, foodListItem);
            lvFood1.setAdapter(adapter);
        }
    }
}