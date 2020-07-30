package com.lxl.jrxy.util;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.lxl.jrxy.mapper.UserMapper;
import com.lxl.jrxy.model.scitc.ScitcUser;
import okhttp3.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScitcUtil {
    public static Date getTime(){
        return new Date(new java.util.Date().getTime());
    }


    public static Integer getHourse(){
            Calendar instance = Calendar.getInstance();
        int i = instance.get(Calendar.HOUR_OF_DAY);//24小时制
        return i;
    }

    public static HtmlPage Login(WebClient webClient,String username,String password){
        try {
            HtmlPage page = webClient.getPage("http://authserver.scitc.com.cn/authserver/login?service=https%3A%2F%2Fscitc.cpdaily.com%2Fportal%2Flogin");
            HtmlTextInput username_txt = (HtmlTextInput) page.getElementById("username");
            HtmlPasswordInput password_txt = (HtmlPasswordInput) page.getElementById("password");
            username_txt.setText(username);
            password_txt.setText(password);
            List<HtmlButton> byXPath = page.getByXPath("//button[@type='submit']");
            byXPath.get(0).click();//模拟点击 登录提示
            HtmlPage clickPage = byXPath.get(0).click();
            return clickPage;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println("MalformedURLException错误");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("io流出现错误");
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public static boolean login(WebClient webClient, String username, String password, HttpServletRequest request, OkHttpClient okHttpClient,UserMapper userMapper, Model model){
        HtmlPage login = Login(webClient, username, password);
        if (login == null) return false;//验证失败
        String pageString = login.getWebResponse().getContentAsString();
        if (pageString.contains("登录提示")) {
            System.out.println("登陆失败");
            return false;
        }

        RequestBody body = new FormBody.Builder().add("type","").build();
        Request.Builder builder = new Request.Builder()
                .post(body)
                .url("https://scitc.cpdaily.com/portal/desktop/userDesktopInfo")
                .addHeader("Accept","application/json, text/javascript, */*; q=0.01")
                .addHeader("Accept-Encoding","gzip, deflate, br")
                .addHeader("Accept-Language","zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
                .addHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("X-Requested-With","XMLHttpRequest")
                .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/537.36");
        //登陆成功时,将cookie拿出来
        List<String> strings = new ArrayList<>();
        webClient.getCookieManager().getCookies().forEach(e->{
            strings.add(e.getName()+"="+e.getValue()+";");
        });
        String ck = "";
        for (String temp:strings){
            ck += temp;
        }
        builder.addHeader("Cookie",ck.substring(0,ck.length()-1));

        try {
            Response execute = okHttpClient.newCall(builder.build()).execute();
            String string = execute.body().string();
            JSONObject jsonObject = (JSONObject)new JSONParser().parse(string);
            JSONObject datas = (JSONObject)jsonObject.get("datas");
            String nick = (String)datas.get("userName");
            String userDepartment = (String) datas.get("userDepartment");

            //走到这里说明成功了
            ScitcUser scitcUser = new ScitcUser();
            scitcUser.username = username;
            scitcUser.password = password;
            scitcUser.nick = nick;
            scitcUser.userdepartment = userDepartment;
            scitcUser.temperature = "36.5";
            scitcUser.school = "scitc";
            scitcUser.auto = 0;
            userMapper.addScitcUser(scitcUser);
            request.getSession(true).setAttribute("user",scitcUser);
            webClient.getCookieManager().clearCookies();
            System.out.println("登陆成功");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("出现io流错误");
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("json转换失败");
        }
        return false;
    }
}
