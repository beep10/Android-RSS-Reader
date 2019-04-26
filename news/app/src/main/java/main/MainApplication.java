package main;

import android.app.Application;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.io.*;
import others.RssFeed;

public class MainApplication extends Application {
    private static MainApplication mApp;
//手工维护的信息源
    public final String[] URLS={"http://www.people.com.cn/rss/politics.xml",
            "http://www.people.com.cn/rss/world.xml",
            "http://www.people.com.cn/rss/finance.xml",
            "http://www.people.com.cn/rss/sports.xml",
            "http://www.people.com.cn/rss/haixia.xml",
            "http://www.people.com.cn/rss/edu.xml",
            "http://www.people.com.cn/rss/game.xml"};
    public final String[] TAGS={"国内","国际","经济","体育","台湾","教育","游戏"};

    public boolean loaded2=false;
//文件目录
    public boolean[] channel;
    private final String channelPath="channel.txt";//订阅频道
    private final String linkPath="link.txt";//收藏夹
    private final String titlePath="title.txt";
    private final String datePath="date.txt";
    private final String histPath="history.txt";
    //各个页面标识
    public final int CHANNELACT=2;
//收藏夹
    public ArrayList<String> collectionTitle;
    public ArrayList<String> collectionDate;
    public ArrayList<String> collectionLink;
//tag到index的映射
    public HashMap<String,Integer> tagIndex;
//程序运行过程中维护的网站列表
    public int choose=1;//当前选中的界面在列表中的位置
    public ArrayList<String> urls;
    public ArrayList<String> tags;
    public ArrayList<String> otherUrls;
    public ArrayList<String> otherTags;
//运行时各个页面下新闻的网址
    public RssFeed[] allFeeds;
//推荐
    public int[] history;

    public static MainApplication getApp(){
        return mApp;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        mApp=this;

        urls=new ArrayList<>();
        urls.add(URLS[0]);
        tags=new ArrayList<>();
        tags.add("推荐");
        otherUrls=new ArrayList<>();
        otherTags=new ArrayList<>();

        channel=new boolean[TAGS.length];
        collectionDate=new ArrayList<>();
        collectionLink=new ArrayList<>();
        collectionTitle=new ArrayList<>();
        tagIndex=new HashMap<>();

        allFeeds=new RssFeed[TAGS.length];

        history=new int[TAGS.length];
        for (int i = 0; i < TAGS.length; ++i) {
            tagIndex.put(TAGS[i], i);
        }
        loadData1();
        loadData3();
    }

    public void loadData1() {
        try {
            FileInputStream bookStr = getApplicationContext().openFileInput(channelPath);
            Scanner cin = new Scanner(bookStr);
            for (int i = 0; i < TAGS.length; ++i) {
                channel[i] = cin.nextBoolean();
            }
            cin.close();
        } catch (Exception e) {
            for (int i = 0; i < TAGS.length; ++i)
                channel[i] = true;
        }
        for(int i=0;i<TAGS.length;++i){
            if(channel[i]) {
                urls.add(URLS[i]);
                tags.add(TAGS[i]);
            }
            else {
                otherUrls.add(URLS[i]);
                otherTags.add(TAGS[i]);
            }
        }
    }

    public void loadData2(){
        try{
            FileInputStream linkStr=getApplicationContext().openFileInput(linkPath);
            FileInputStream titleStr=getApplicationContext().openFileInput(titlePath);
            FileInputStream dateStr=getApplicationContext().openFileInput(datePath);
            Scanner cin1=new Scanner(linkStr);
            Scanner cin2=new Scanner(titleStr);
            Scanner cin3=new Scanner(dateStr);
            while(cin1.hasNext()){
                collectionLink.add(cin1.nextLine());
                collectionTitle.add(cin2.nextLine());
                collectionDate.add(cin3.nextLine());
            }
            cin1.close();
            cin2.close();
            cin3.close();
        }catch(Exception e){}
        loaded2=true;
    }

    public void loadData3(){
        try{
            FileInputStream histStr=getApplicationContext().openFileInput(histPath);
            Scanner cin=new Scanner(histStr);
            for(int i=0;i<TAGS.length;++i){
                history[i]=cin.nextInt();
            }
            cin.close();
        }catch(Exception e){
            for(int i=0;i<TAGS.length;++i)
                history[i]=1;
        }
    }

    public void storeData1(){
        for(int i=0;i<channel.length;++i)
            channel[i]=false;
        for(int i=1;i<tags.size();++i){
            channel[tagIndex.get(tags.get(i))]=true;
        }
        try{
            FileOutputStream channelStr=openFileOutput(channelPath,MODE_PRIVATE);
            for(int i=0;i<channel.length;++i){
                channelStr.write((channel[i]+"\n").getBytes());
            }
            channelStr.close();
        }catch(IOException e){}
    }
    public void storeData2(){
        try{
            FileOutputStream linkStr=openFileOutput(linkPath,MODE_PRIVATE);
            FileOutputStream titleStr=openFileOutput(titlePath,MODE_PRIVATE);
            FileOutputStream dateStr=openFileOutput(datePath,MODE_PRIVATE);
            for(int i=0;i<collectionLink.size();++i){
                linkStr.write((collectionLink.get(i)+"\n").getBytes());
                titleStr.write((collectionTitle.get(i)+"\n").getBytes());
                dateStr.write((collectionDate.get(i)+"\n").getBytes());
            }
            linkStr.close();
            titleStr.close();
            dateStr.close();
        }catch(IOException e){}
    }
    public void storeData3(){
        try{
            FileOutputStream histStr=openFileOutput(histPath,MODE_PRIVATE);
            for(int i=0;i<TAGS.length;++i){
                histStr.write((history[i]+"\n").getBytes());
            }
        }catch(Exception e){}
    }
    public void storeData(){
        storeData1();
        storeData2();
        storeData3();
    }
}