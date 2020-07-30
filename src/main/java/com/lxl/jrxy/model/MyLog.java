package com.lxl.jrxy.model;

import java.io.Serializable;
import java.sql.Date;

public class MyLog implements Serializable {
    public Integer id;
    public String username;
    public Date day;
    public Integer clock_six;
    public Integer clock_twelve;
    public String six_describe;
    public String twelve_describe;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public Integer getClock_six() {
        return clock_six;
    }

    public void setClock_six(Integer clock_six) {
        this.clock_six = clock_six;
    }

    public Integer getClock_twelve() {
        return clock_twelve;
    }

    public void setClock_twelve(Integer clock_twelve) {
        this.clock_twelve = clock_twelve;
    }

    public String getSix_describe() {
        return six_describe;
    }

    public void setSix_describe(String six_describe) {
        this.six_describe = six_describe;
    }

    public String getTwelve_describe() {
        return twelve_describe;
    }

    public void setTwelve_describe(String twelve_describe) {
        this.twelve_describe = twelve_describe;
    }
}
