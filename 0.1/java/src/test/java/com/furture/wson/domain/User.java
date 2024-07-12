package com.furture.wson.domain;

import java.io.Serializable;

/**
 * Created by 剑白(jianbai.gbj) on 2017/9/3.
 */
public class User extends  Base  implements Serializable{


    private static final long serialVersionUID = 4015482440089425674L;

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }


    public User next;
    public String name;

    public String country;

    public boolean type;

    public int age;

    private int num;

    protected String getHelp(){
        return "help";
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }
}
