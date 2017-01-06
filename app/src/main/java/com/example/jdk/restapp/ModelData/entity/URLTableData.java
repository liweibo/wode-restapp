package com.example.jdk.restapp.ModelData.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by JDK on 2016/8/12.
 */
public class URLTableData extends Base implements Serializable {
    public int id;
    public String url;//链接地址
    public String who;//作者
    public String desc;//干货内容的描述
    public Date publishedAt;
    public String type;
    public boolean isCollected;
    public URLTableData(){

    }
    public URLTableData(String url,String who,String desc,Date createdAt){
        this.url=url;
        this.who=who;
        this.desc=desc;
        this.publishedAt=createdAt;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int get_id() {
        return id;
    }

    public void set_id(int _id) {
        this.id = _id;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setIsCollected(boolean isCollected) {
        this.isCollected = isCollected;
    }
}
