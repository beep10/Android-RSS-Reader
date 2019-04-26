package activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.houly.andriodTest.R;

import main.MainApplication;

public class ChannelActivity extends AppCompatActivity implements OnItemClickListener{

    private MainApplication app;
    private GridView grid1,grid2;

    private android.support.v7.widget.Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        app=MainApplication.getApp();
        grid1=findViewById(R.id.grid1);
        grid2=findViewById(R.id.grid2);
        ArrayAdapter<String> ad=new ArrayAdapter<>(this,R.layout.text_channel,app.tags);
        grid1.setAdapter(ad);
        ad=new ArrayAdapter<>(this,R.layout.text_channel,app.otherTags);
        grid2.setAdapter(ad);
        grid1.setOnItemClickListener(this);
        grid2.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id){
        switch(adapterView.getId()){
            case R.id.grid1:
                if(position!=0&&app.urls.size()>2) {
                    String url = app.urls.remove(position);
                    String tag=app.tags.remove(position);
                    ArrayAdapter<String> ad = new ArrayAdapter<>(this,R.layout.text_channel, app.tags);
                    grid1.setAdapter(ad);
                    app.otherUrls.add(url);
                    app.otherTags.add(tag);
                    ad=new ArrayAdapter<>(this,R.layout.text_channel,app.otherTags);
                    grid2.setAdapter(ad);
                }
                break;
            case R.id.grid2:
                if(app.otherUrls.size()>0){
                    String otherUrl=app.otherUrls.remove(position);
                    String otherTag=app.otherTags.remove(position);
                    ArrayAdapter<String> ad=new ArrayAdapter<>(this,R.layout.text_channel,app.otherTags);
                    grid2.setAdapter(ad);
                    app.urls.add(otherUrl);
                    app.tags.add(otherTag);
                    ad=new ArrayAdapter<>(this,R.layout.text_channel,app.tags);
                    grid1.setAdapter(ad);
                }
                break;
        }
    }

    @Override
    protected void onStop(){
        app.storeData1();
        super.onStop();
    }

    public void onClick(int keyCode,KeyEvent e){
        if(keyCode==KeyEvent.KEYCODE_BACK) {
            setResult(1);
            this.finish();
        }
    }

}
