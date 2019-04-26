package activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.houly.andriodTest.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.MainApplication;

public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private MainApplication app;

    private android.support.v7.widget.Toolbar toolbar;

    private ListView lv;

    private String keyWord;

    private List<HashMap<String, Object>> list;
    private List<String> links;

    @Override
    protected void onCreate(Bundle savedInstanceStat){
        super.onCreate(savedInstanceStat);
        setContentView(R.layout.activity_search);

        app=MainApplication.getApp();

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lv=findViewById(R.id.lv);
        lv.setOnItemClickListener(this);
        lv.setSelection(0);
        lv.setEmptyView(findViewById(R.id.empty));

        list=new ArrayList<>();
        links=new ArrayList<>();

        Intent intent=getIntent();
        doSearchQuery(intent);
        showList();
    }

    private void doSearchQuery(Intent intent){
        if(intent==null)
            return;
        else{
            if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
                keyWord = intent.getStringExtra(SearchManager.QUERY);
            }
        }
    }
    private void searchNews(){
        String title;
        for(int i=0;i<app.channel.length;++i){
            if(app.allFeeds[i]==null)
                continue;
            for(int j=0;j<app.allFeeds[i].getItemCount();++j){
                title=app.allFeeds[i].getItem(j).getTitle();
                if(title.contains(keyWord)){
                    HashMap<String, Object> item = new HashMap<>();
                    item.put("TITLE",title);
                    item.put("DATE",app.allFeeds[i].getItem(j).getPubdate());
                    list.add(item);
                    links.add(app.allFeeds[i].getItem(j).getLink());
                }
            }
        }
    }

    private void showList(){
        searchNews();
        SimpleAdapter ad= new SimpleAdapter(this,
                list,android.R.layout.simple_list_item_2,
                new String[]{"TITLE", "DATE"}, new int[]{
                android.R.id.text1, android.R.id.text2});
        lv.setAdapter(ad);
        lv.setSelection(0);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent=new Intent(this,ArticleActivity.class);
        intent.putExtra("link",links.get(position));
        intent.putExtra("title",(String)list.get(position).get("TITLE"));
        intent.putExtra("date",(String)list.get(position).get("DATE"));
        startActivity(intent);
    }
}
