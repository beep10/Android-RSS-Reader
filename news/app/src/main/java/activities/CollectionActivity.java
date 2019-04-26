package activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.houly.andriodTest.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.MainApplication;

public class CollectionActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private MainApplication app;

    private android.support.v7.widget.Toolbar toolbar;

    private ListView lv;
    public List<HashMap<String, Object>> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        app=MainApplication.getApp();
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lv=findViewById(R.id.lv);
        lv.setOnItemClickListener(this);
        lv.setSelection(0);
        lv.setEmptyView(findViewById(R.id.empty));

        showList();

        lv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 0, 0, "删除");
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int id=(int)info.id;
        app.collectionLink.remove(id);
        app.collectionTitle.remove(id);
        app.collectionDate.remove(id);
        showList();
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onResume(){
        showList();
        super.onResume();
    }
    @Override
    protected void onStop(){
        app.storeData2();
        super.onStop();
    }
    private void showList(){
        makeList();
        SimpleAdapter ad= new SimpleAdapter(this,
                list,android.R.layout.simple_list_item_2,
                new String[]{"TITLE", "DATE"}, new int[]{
                android.R.id.text1, android.R.id.text2});
        lv.setAdapter(ad);
        lv.setSelection(0);
    }

    private void makeList(){
        list = new ArrayList<>();
        for (int i = 0; i < app.collectionLink.size(); ++i) {
            HashMap<String, Object> item = new HashMap<>();
            item.put("TITLE",app.collectionTitle.get(i));
            item.put("DATE",app.collectionDate.get(i));
            list.add(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent=new Intent(this,ArticleActivity.class);
        intent.putExtra("link",app.collectionLink.get(position));
        intent.putExtra("title",app.collectionTitle.get(position));
        intent.putExtra("date",app.collectionDate.get(position));
        startActivity(intent);
    }
}
