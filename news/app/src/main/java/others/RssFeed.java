package others;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RssFeed {

    private String title; // 标题
    private String pubdate; // 发布日期
    private int itemCount=0; // 用于计算列表的数目
    private List<RssItem> rssItems; // 用于描述列表item

    public RssFeed() {
        rssItems = new ArrayList<RssItem>();
    }

    // 添加RssItem条目,返回列表长度
    public int addItem(RssItem rssItem) {
        rssItems.add(rssItem);
        itemCount++;
        return itemCount;
    }

    // 根据下标获取RssItem
    public RssItem getItem(int position) {
        return rssItems.get(position);
    }

    public List<HashMap<String, Object>> getAllItems() {
        List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < rssItems.size(); i++) {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put(RssItem.TITLE, rssItems.get(i).getTitle());
            item.put(RssItem.PUBDATE, rssItems.get(i).getPubdate());
            data.add(item);
        }
        return data;
    }

    public List<RssItem> getRssItems(){ return rssItems; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    public int getItemCount() {
        return itemCount;
    }
}

