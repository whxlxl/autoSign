package com.lxl.jrxy.model.scitc;

import com.lxl.jrxy.model.MyLog;

import java.io.Serializable;

public class ScitcUser implements Serializable {
    public String username;//学号或者用户名
    public String password;//密码
    public String nick;//昵称
    public String address;//地址
    public String temperature;//温度
    public String email;//邮箱
    public String dormitory;//寝室
    public String apartment;//公寓
    public String school;//学校
    public String userdepartment;//系别
    public Integer auto;
    public MyLog myLog;//包含当前用户的所有信息,包括是否签到这些

    public String getUserdepartment() {
        return userdepartment;
    }

    public void setUserdepartment(String userdepartment) {
        this.userdepartment = userdepartment;
    }


    public Integer getAuto() {
        return auto;
    }

    public void setAuto(Integer auto) {
        this.auto = auto;
    }

    public MyLog getMyLog() {
        return myLog;
    }

    public void setMyLog(MyLog myLog) {
        this.myLog = myLog;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDormitory() {
        return dormitory;
    }

    public void setDormitory(String dormitory) {
        this.dormitory = dormitory;
    }

    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }
}
