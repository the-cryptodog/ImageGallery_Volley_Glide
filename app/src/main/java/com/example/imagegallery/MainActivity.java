package com.example.imagegallery;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.Random;

import static com.example.imagegallery.R.string.Load_Success;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ImageView imageView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String url1;
    private String url2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //核心做法:設定監聽器來設定下拉行為
        swipeRefreshLayout = findViewById(R.id.swiperefreshlayout);
        imageView = findViewById(R.id.imageView);
        url1 = "https://attach2.mobile01.com/images/touch/apple-touch-icon-180x180.png";
        url2 = "https://attach.mobile01.com/image/news/df34d40d00fc25f8edfc90cb883ba85b.jpg";


        loadImageWithGlide(); //預載一張圖


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                /*畫面下拉便啟動(須額外設定關閉refresh狀態)*/
                loadImageWithGlide();
            }
        });

        /*此處為Volley自帶載入圖片之框架，然而通常使用第三方Glide功能也非常齊全*/

//       //Volley往往要封裝成單例
//       RequestQueue queue = Volley.newRequestQueue(this);
//        ImageLoader imageLoader = new ImageLoader(queue, new ImageLoader.ImageCache() {
//
//            private LruCache<String,Bitmap> cache = new LruCache<>(50); // least recently used
//
//            @Override
//            public Bitmap getBitmap(String url) {
//                return cache.get(url);
//            }
//
//            @Override
//            public void putBitmap(String url, Bitmap bitmap) {
//                cache.put(url,bitmap);
//            }
//        });
//
//        imageLoader.get(url, new ImageLoader.ImageListener() {
//            @Override
//            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
//                imageView.setImageBitmap(response.getBitmap());
//            }
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG,"error",error);
//            }
//        });


    }

    //設定Glide載入圖片框架
    void loadImageWithGlide(){
        //設定一個隨機的布林(50/50機率)來讀兩種圖
        Random random = new Random();
        String url = random.nextBoolean()? url1:url2;

        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.ic_launcher_background)
                .listener(new RequestListener<Drawable>() { //加載監聽器
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Toast.makeText(MainActivity.this,"載入失敗",Toast.LENGTH_SHORT).show();
                        if(swipeRefreshLayout.isRefreshing()){
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Toast.makeText(MainActivity.this,"載入成功",Toast.LENGTH_SHORT).show();

                        /*載入成功後回調檢測，關閉swipeRefreshLayout 的 refreshing 狀態*/
                        if(swipeRefreshLayout.isRefreshing()){
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        return false;
                    }
                })
                .into(imageView);
    }
}