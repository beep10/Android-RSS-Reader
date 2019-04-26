package activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.example.houly.andriodTest.R;

import main.MainApplication;

public class ArticleActivity extends AppCompatActivity {

    private MainApplication app;

    private android.support.v7.widget.Toolbar toolbar;

    private String link;
    private String title;
    private String date;

    private WebView web;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        app=MainApplication.getApp();

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        web = this.findViewById(R.id.web);
        WebSettings settings=web.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        Intent intent = getIntent();
        link = intent.getStringExtra("link");
        title=intent.getStringExtra("title");
        date=intent.getStringExtra("date");

        web.loadUrl(link);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_article,menu);
        if(app.collectionLink.contains(link))
            menu.findItem(R.id.collect).setTitle("取消收藏");
        else
            menu.findItem(R.id.collect).setTitle("收藏");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.share:
                Intent share_intent=new Intent();
                share_intent.setAction(Intent.ACTION_SEND);
                share_intent.setType("text/plain");
                share_intent.putExtra(Intent.EXTRA_TEXT,title+"\n"+link);
                startActivity(Intent.createChooser(share_intent,"分享"));
                break;
            case R.id.collect:
                if(item.getTitle()=="收藏") {
                    item.setTitle("取消收藏");
                    app.collectionLink.add(0,link);
                    app.collectionTitle.add(0,title);
                    app.collectionDate.add(0,date);
                }else{
                    item.setTitle("收藏");
                    app.collectionLink.remove(link);
                    app.collectionTitle.remove(title);
                    app.collectionDate.remove(date);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop(){
        app.storeData2();
        super.onStop();
    }
}
