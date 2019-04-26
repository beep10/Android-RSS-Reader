package others;

public class RssItem {

    private boolean read=false;

    private String title;
    private String link;
    private String pubdate;

    public static final String TITLE = "title";
    public static final String PUBDATE = "pubdate";

    public boolean getRead(){return read;}
    public void setRead(boolean r){this.read=r;}

    public String getTitle() {
        if (title.length() > 20) {
            return title.substring(0, 19) + "...";
        }
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    @Override
    public String toString() {
        return "RssItem [title=" + title + ", link=" + link +  ", pubdate="
                + pubdate + "]";
    }
}