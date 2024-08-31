package com.furture.wson.domain;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return type == user.type && age == user.age && num == user.num && Objects.equals(next, user.next) && Objects.equals(name, user.name) && Objects.equals(country, user.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(next, name, country, type, age, num);
    }

    @Override
    public String toString() {
        return "User{" +
                "next=" + next +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", type=" + type +
                ", age=" + age +
                ", num=" + num +
                '}';
    }
}
