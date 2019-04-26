package activities;

import main.MainApplication;
import others.*;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.os.SystemClock;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.widget.SimpleAdapter;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import com.example.houly.andriodTest.R;

import java.util.Random;

import static android.graphics.Color.BLACK;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,OnItemClickListener,OnRefreshListener,AbsListView.OnScrollListener{

    private MainApplication app;

    private android.support.v7.widget.Toolbar toolbar;

    private RssFeed feed=null;
    private SwipeRefreshLayout refresh;

    private ListView lv;
    private boolean More=false;
    private HorizontalListView hlv;
    private Button btn;

    private SearchView.SearchAutoComplete sac_key;

    private long exitTime=0;

    private int lastFirstItem;
    private boolean stop;
    private  int lastTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        app=MainApplication.getApp();
        refresh=findViewById(R.id.refresh);
        refresh.setOnRefreshListener(this);
        refresh.setColorSchemeResources(R.color.red,R.color.orange,R.color.green,R.color.blue);

        lv = this.findViewById(R.id.list);
        lv.setOnItemClickListener(this);
        lv.setOnScrollListener(this);
        lv.setEmptyView(findViewById(R.id.empty));

        btn=this.findViewById(R.id.btn);
        btn.setOnClickListener(this);

        hlv=this.findViewById(R.id.hlv);
        hlv.setOnItemClickListener(this);

        if(!app.loaded2)
            new Thread(new LoadData2()).start();

        new Thread(new GetRss(app.choose)).start();
        new Thread(new GetRss2()).start();

        refreshHlv();
    }

    @Override
    protected void onStop(){
        app.storeData3();
        super.onStop();
    }

    private class GetRss implements Runnable{
        private int id;
        public GetRss(int i){
            id=i;
        }
        public void run(){
            try {
                RssFeed get = new RssFeed_SAXParser().getFeed(app.urls.get(id));
                if(get==null){
                    mHandler.sendEmptyMessage(1);//加载失败
                    return;
                }
                int toAll=app.tagIndex.get(app.tags.get(id));
                app.allFeeds[toAll]=get;
                mHandler.sendEmptyMessage(0);//加载成功
                    return;
            } catch (Exception e) {
                mHandler.sendEmptyMessage(1);
                    return;
            }
        }
    }

    private class LoadData2 implements Runnable{
        public void run(){
            app.loadData2();
        }
    }

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0:
                    refresh.setRefreshing(false);
                    showListView();
                    break;
                case 2:
                    if(app.choose==0)
                        showTui();
            }
        }
    };

    private void refreshHlv(){
        ArrayAdapter<String> ad=new ArrayAdapter<>(this,R.layout.text,app.tags);
        hlv.setAdapter(ad);
        hlv.setSelection(0);
    }

    @Override
    public void onRefresh(){
        if(app.choose==0)
            showTui();
        else
            new Thread(new GetRss(app.choose)).start();
    }

    private void showListView() {
        int toAll=app.tagIndex.get(app.tags.get(app.choose));
        feed=app.allFeeds[toAll];
        if(feed==null)
            feed=new RssFeed();
        SimpleAdapter simpleAdapter = new SimpleAdapter(this,
                feed.getAllItems(),android.R.layout.simple_list_item_2,
                new String[]{RssItem.TITLE, RssItem.PUBDATE}, new int[]{
                android.R.id.text1, android.R.id.text2});
        lv.setAdapter(simpleAdapter);
        lastFirstItem=0;
        lastTop=0;
        stop=true;
        lv.setSelection(0);
    }

    private void showTui(){
        feed=new RssFeed();
        int total=0;
        for(int i=0;i<app.channel.length;++i)
            total+=app.history[i];
        for(int i=0;i<app.channel.length;++i){
            if(app.allFeeds[i]==null) {
                continue;
            }
            int get=(int)(20*app.history[i]/(double)total);

            if(get>app.allFeeds[i].getItemCount())
                get=app.allFeeds[i].getItemCount();
            for(int j=0;j<get;++j){
                feed.addItem(app.allFeeds[i].getItem(app.allFeeds[i].getItemCount()-j-1));
            }
        }

        int size=feed.getItemCount();
        Random random=new Random();
        for(int i=0;i<size;++i){
            int randomPos = random.nextInt(size);
            RssItem temp=feed.getItem(i);
            feed.getRssItems().set(i,feed.getItem(randomPos));
            feed.getRssItems().set(randomPos,temp);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this,
                feed.getAllItems(),android.R.layout.simple_list_item_2,
                new String[]{RssItem.TITLE, RssItem.PUBDATE}, new int[]{
                android.R.id.text1, android.R.id.text2});
        lv.setAdapter(simpleAdapter);
        lastFirstItem=0;
        lastTop=0;
        stop=true;
        lv.setSelection(0);
        refresh.setRefreshing(false);
    }
    private  class GetRss2 implements  Runnable{
        public void run(){
            for(int i=0;i<app.channel.length;++i) {
                if (app.allFeeds[i] == null) {
                    try {
                        app.allFeeds[i] = new RssFeed_SAXParser().getFeed(app.URLS[i]);
                    } catch (Exception e) {}
                }
            }
            mHandler.sendEmptyMessage(2);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        switch (adapterView.getId()){
            case R.id.list:
                if(app.choose!=0)
                    ++app.history[app.tagIndex.get(app.tags.get(app.choose))];
                if(!feed.getItem(position).getRead()) {
                    TextView text = lv.getChildAt(position - lv.getFirstVisiblePosition()).findViewById(android.R.id.text1);
                    text.setTextColor(this.getResources().getColor(R.color.gray));
                    text = lv.getChildAt(position - lv.getFirstVisiblePosition()).findViewById(android.R.id.text2);
                    text.setTextColor(this.getResources().getColor(R.color.gray));
                    feed.getItem(position).setRead(true);
                }
                Intent intent = new Intent();
                intent.setClass(this, ArticleActivity.class);
                intent.putExtra("link", feed.getItem(position).getLink());
                intent.putExtra("title",feed.getItem(position).getTitle());
                intent.putExtra("date",feed.getItem(position).getPubdate());
                startActivity(intent);
                break;
            case R.id.hlv:
                app.choose = position;
                if(position!=0) {
                    if(app.allFeeds[app.tagIndex.get(app.tags.get(position))]==null)
                        new Thread(new GetRss(app.choose)).start();
                    else
                        showListView();
                }
                else{
                    showTui();
                }
                break;
        }
    }

    @Override
    public void onClick(View v){
        if(v.getId()==R.id.btn){
            Intent intent=new Intent(this,ChannelActivity.class);
            startActivityForResult(intent,app.CHANNELACT);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK&& event.getAction() == KeyEvent.ACTION_DOWN){
            if(System.currentTimeMillis()-exitTime>2000||exitTime==0){
                Toast.makeText(this, "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime=System.currentTimeMillis();
            }else{
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==app.CHANNELACT){
            app.choose=0;
            refreshHlv();
            showTui();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                More = (firstVisibleItem + visibleItemCount < totalItemCount);

                boolean down=false;

                View v = view.getChildAt(0);
                int top = (v == null) ? 0 : v.getTop();
                if (firstVisibleItem < lastFirstItem) {
                    down=true;
                } else {
                    if(top > lastTop) {
                        down=true;
                    }
                }
                lastFirstItem = firstVisibleItem;
                lastTop = top;



        if(lv.getLastVisiblePosition()%10==0&&stop&&down) {
                    lv.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_CANCEL, 0, 0, 0));
                    stop = false;
                }
                if(lv.getLastVisiblePosition()%10!=0)
                    stop=true;

                int f = lv.getFirstVisiblePosition();
                int l = lv.getLastVisiblePosition();
                for (int i = f; i <= l; ++i) {
                    if (feed.getItem(i).getRead()) {
                        TextView text = lv.getChildAt(i - f).findViewById(android.R.id.text1);
                        text.setTextColor(this.getResources().getColor(R.color.gray));
                        text = lv.getChildAt(i - f).findViewById(android.R.id.text2);
                        text.setTextColor(this.getResources().getColor(R.color.gray));
                    } else {
                        TextView text = lv.getChildAt(i - f).findViewById(android.R.id.text1);
                        text.setTextColor(BLACK);
                        text = lv.getChildAt(i - f).findViewById(android.R.id.text2);
                        text.setTextColor(BLACK);
                    }
                }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState){
        if(scrollState== AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
            if(!More){
                Toast.makeText(this, "没有更多内容",
                        Toast.LENGTH_SHORT).show();
            }
            if(lv.getFirstVisiblePosition()==0){
                Toast.makeText(this, "下拉刷新",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        initSearchView(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.user:
                break;
            case R.id.collect:
                Intent intent=new Intent(this,CollectionActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initSearchView(Menu menu){
        MenuItem menuItem=menu.findItem(R.id.search);
        SearchView searchView=(SearchView) MenuItemCompat.getActionView(menuItem);
        if(searchView==null){
            Log.e("debug","Fail to get SearchView");
        }
        else{
            if(getIntent()!=null){
                searchView.setIconifiedByDefault(getIntent().getBooleanExtra("collapse",true));
            }else{
                searchView.setIconifiedByDefault(true);
            }
            searchView.setSubmitButtonEnabled(true);
            SearchManager searchManager=(SearchManager)getSystemService(Context.SEARCH_SERVICE);
            ComponentName cn=new ComponentName(this,SearchActivity.class);
            SearchableInfo info=searchManager.getSearchableInfo(cn);
            searchView.setSearchableInfo(info);
            sac_key=searchView.findViewById(R.id.search_src_text);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
                @Override
                public boolean onQueryTextSubmit(String query){
                    return false;
                }
                @Override
                public boolean onQueryTextChange(String newText){
                    return true;
                }
            });
        }
    }

}
