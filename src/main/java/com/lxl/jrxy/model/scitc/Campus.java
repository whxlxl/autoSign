package com.lxl.jrxy.model.scitc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.lxl.jrxy.mapper.UserMapper;
import com.lxl.jrxy.model.MyLog;
import com.lxl.jrxy.service.MailService;
import com.lxl.jrxy.util.ScitcUtil;
import okhttp3.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 提交时需要的一些属性
 */

public class Campus {

    public Map<String,String> ques;//数据库中的问题集合
    private UserMapper userMapper;
    private Integer wid;
    private Integer formWid;
    private Integer schoolTaskWid;
    private String token;
    private ScitcUser scitcUser;
    private ScitcUrls scitcUrls;
    private WebClient webClient;
    private OkHttpClient okHttpClient;

    private MailService mailService;

    private MediaType mediaType_json;


    Map<String,JSONObject> forms;//请求下来的表单集合

    String desc;//描述

    public Campus(ScitcUser scitcUser, ScitcUrls scitcUrls, WebClient webClient, OkHttpClient okHttpClient, MailService mailService, UserMapper userMapper,Map<String,String> map){
        this.scitcUser = scitcUser;
        this.scitcUrls = scitcUrls;
        this.webClient = webClient;
        this.okHttpClient = okHttpClient;
        this.mailService = mailService;
        this.userMapper = userMapper;
        this.ques = map;
        desc="";
        mediaType_json = MediaType.parse("application/json; charset=utf-8");
        forms = new HashMap<>();
        init();//查询其他数据
    }

    private void init() {
        //判断今天的数据是否在数据库中
        if (scitcUser.myLog == null){
            //可能是今天没有填写，给他加一条数据进去
            MyLog myLog = new MyLog();
            myLog.clock_six = 0;
            myLog.clock_twelve = 0;
            myLog.day = ScitcUtil.getTime();
            myLog.six_describe = "";
            myLog.twelve_describe = "";
            myLog.username = scitcUser.username;
            Integer integer = userMapper.insertLog(myLog);
            if (integer > 0) System.out.println("成功注入今日初始化的信息");
            scitcUser.myLog = myLog;
        }
    }


    //这是个测试方法
    public boolean testSign(){
        getToken();
        //this.token= " acw_tc=76b20f6915960759184005158e371668150b726865d9ee5f13fa0f0bf8a703;MOD_AUTH_CAS=ST-35088-0Ng1m6POTMKrPuRql7ve1596075920976-BTff-cas";
        getFromIdAndWid();
        getSchoolTaskWid();
        getForm();
        submit();
        return true;
    }


    //获取自定义数据答案
    public String userdate(String title){
        String abs = null;
        ObjectMapper objectMapper = new ObjectMapper();
        JSONObject jsonObject = null;
        try {
            String string = objectMapper.writeValueAsString(scitcUser);
            jsonObject = (JSONObject) new JSONParser().parse(string);
            abs = (String) jsonObject.get(title);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("自定义数据转换出错1！！！");
        }
        return abs;
    }

    //构建表单
    public JSONArray buildForm(){
        try {
            JSONArray array = new JSONArray();

            for (Map.Entry<String,JSONObject> temp:forms.entrySet()){
                //将得到的表单与数据库进行匹配
                String title = temp.getKey();//请求下来的表单的title
                JSONObject form = temp.getValue();//表单数据

                String value = null;//表单的答案

                //遍历数据库中的问题集合
                for (Map.Entry<String,String> questions:ques.entrySet()){
                    String key = questions.getKey();
                    //将表单的title与当前得到进行包含匹配
                    if (title.contains(key)){
                        value = questions.getValue();//获取答案
                        break;
                    }
                }
                if (value == null){
                    //表示没有找到答案，此时停止对表单的提交
                    String msg = "数据库没有记录当前问题："+title+"\n问题详情："+temp.getValue().toJSONString();
                    System.out.println(msg);
                    desc = msg;
                    return null;
                }

                //正则匹配看看是不是自定义需要的数据
                boolean matches = value.matches("^%\\w+%$");
                if (matches){
                    //这个问题需要获取当前的用户自定义数据
                    value = userdate(value.substring(1,value.length()-1));
                    System.out.println("当前是自定义的数据");
                }

                //如果数据库操作失误，这里对其进行拦截不提交
                if (value == null) {
                    desc = "数据库中可能操作失误，请详细检查字段";
                    System.out.println(desc);
                    return null;
                }

                form.replace("value",value);
                //构建数组的选项
                JSONArray fieldItems = proccessfieldItems((JSONArray)form.get("fieldItems"),value);
                form.replace("fieldItems",fieldItems);
                array.add(form);
            }
            return array;
        }catch (Exception e){
            e.printStackTrace();
            desc = e.getMessage();
            System.out.println("表单构建出现问题");
        }
        return null;
    }

    //提交
    public boolean sign(){

        //提交之前判断当前时间段是否能提交，并且看看当前用户是否成功提交过

        if (!(ScitcUtil.getHourse() == 6 || ScitcUtil.getHourse() == 12 ||ScitcUtil.getHourse() == 7)){
            System.out.println("没到提交数据的时间段！！！");
            return false;
        }
        int ys = ScitcUtil.getHourse() < 12 ? 6 : 12;
        if (ys == 6 && scitcUser.myLog.clock_six != 0){
            System.out.println(scitcUser.nick+"6点的信息已经填报，无需再次执行");
            return false;
        }
        if (ys == 12 && scitcUser.myLog.clock_twelve != 0){
            System.out.println(scitcUser.nick+"12点的信息已经填报，无需再次执行");
            return false;
        }


        getToken();
        if (this.token == null) getToken();//最后再一次获取token
        if (this.token == null) {
            if (ys == 6)
                scitcUser.myLog.six_describe = desc;
            if (ys == 12)
                scitcUser.myLog.twelve_describe = desc;

            userMapper.updateLog(scitcUser.myLog);
            System.out.println("token获取失败！！");
            return false;
        }


        if (!getFromIdAndWid()){
            System.out.println("getFromIdAndWid 停止运行！！！");
            return false;
        }

        if (!getSchoolTaskWid()|| !getForm() || !submit()){
            System.out.println(scitcUser.nick+"签到失败");

            if (ys == 6){
                scitcUser.myLog.clock_six = 0;
                scitcUser.myLog.six_describe = desc;
            }
            if (ys == 12){
                scitcUser.myLog.clock_twelve = 0;
                scitcUser.myLog.twelve_describe = desc;
            }

            userMapper.updateLog(scitcUser.myLog);
            //mailEntity.mail.put(scitcUser.email,scitcUser.nick+"签到结果：签到失败！！！原因："+desc+"<p>10分钟后将会再次重试</p>");
            mailService.sendHtmlMsg(scitcUser.email,scitcUser.nick+"签到结果：签到失败！！！原因："+desc+"<p>10分钟后将会再次重试</p>");
            return false;
        }else{
            if (ys == 6){
                scitcUser.myLog.clock_six = 1;
                scitcUser.myLog.six_describe = desc;
            }
            if (ys == 12){
                scitcUser.myLog.clock_twelve = 1;
                scitcUser.myLog.twelve_describe = desc;
            }

            userMapper.updateLog(scitcUser.myLog);
            //mailEntity.mail.put(scitcUser.email,scitcUser.nick+"签到结果："+desc+"！！！");
            mailService.sendHtmlMsg(scitcUser.email,scitcUser.nick+"签到结果："+desc+"！！！");
            System.out.println(scitcUser.nick+"自动签到成功");
        }
        return true;
    }

    //获取统一的头部
    private Request.Builder getRequestBuilder(){
        try {
            //如果出问题就改这里
            String encode = URLEncoder.encode("今日校园/1 CFNetwork/1126 Darwin/19.5.1", "utf-8");

            Request.Builder builder = new Request.Builder()
                    .addHeader("accept","application/json, text/plain, */*")
                    .addHeader("Origin","https://scitc.cpdaily.com")
                    .addHeader("x-requested-with","XMLHttpRequest")
                    //.addHeader("User-Agent","今日校园/1 CFNetwork/1126 Darwin/19.5.1")
                    //.addHeader("User-Agent","\\u4eca\\u65e5\\u6821\\u56ed/1 CFNetwork/1126 Darwin/19.5.1")
                    .addHeader("User-Agent","Mozilla/5.0 (Linux; Android 5.1.1; PCRT00 Build/LMY48Z; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/52.0.2743.100 Safari/537.36 okhttp/3.12.4")
                    .addHeader("Accept-Encoding","gzip, deflate")
                    .addHeader("Accept-Language","zh-CN,en-US;q=0.8")
                    .addHeader("Cookie",token);//cookie只需要acw_tc和MOD_AUTH_CAS就行了
            return builder;
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("getRequestBuilder 中出现转码错误！！！！");
        }
        return null;
    }

    /**
     * 提交当前json数据
     */
    private boolean submit(){
        Request.Builder requestBuilder = getRequestBuilder();
        requestBuilder.addHeader("Cpdaily-Extension","1wAXD2TvR72sQ8u+0Dw8Dr1Qo1jhbem8Nr+LOE6xdiqxKKuj5sXbDTrOWcaf v1X35UtZdUfxokyuIKD4mPPw5LwwsQXbVZ0Q+sXnuKEpPOtk2KDzQoQ89KVs gslxPICKmyfvEpl58eloAZSZpaLc3ifgciGw+PIdB6vOsm2H6KSbwD8FpjY3 3Tprn2s5jeHOp/3GcSdmiFLYwYXjBt7pwgd/ERR3HiBfCgGGTclquQz+tgjJ PdnDjA==")
                .url(scitcUrls.submitUrl);
        //构造请求体了
        JSONObject jsonObject = new JSONObject();
        //固定信息
        jsonObject.put("formWid",this.formWid+"");
        jsonObject.put("address",this.scitcUser.address+"");
        jsonObject.put("collectWid",this.wid+"");
        jsonObject.put("schoolTaskWid",this.schoolTaskWid+"");
        JSONArray array = buildForm();//构建j'son表单
        System.out.println(array);
        if (array != null)
            jsonObject.put("form",array);
        else
            return false;
        System.out.println("当前构建的数据：\n"+jsonObject.toJSONString());

        requestBuilder.post(RequestBody.create(jsonObject.toJSONString(),mediaType_json));

        for (int i = 0; i < 3; i++){
            try {
                Response execute = okHttpClient.newCall(requestBuilder.build()).execute();
                if (execute.isSuccessful()){
                    String string = execute.body().string();
                    System.out.println("提交返回的数据："+string);
                    JSONObject jsons = (JSONObject) new JSONParser().parse(string);
                    String code = (String) jsons.get("code");
                    String message = (String)jsons.get("message");

                    if (code.equals("0") || message.equals("SUCCESS")){
                        System.out.println("提交成功");
                        desc = "签到成功";
                        return true;
                    }else if (message.contains("该收集已填写无需再次填写")){

                        System.out.println("用户已经填写(签到)。");
                        desc = "用户已经填写(签到)。";
                        return true;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                String message = e.getMessage();
                desc = "提交过程出现问题，尝试再次提交+e:"+message;
                System.out.println("提交过程出现问题，尝试再次提交");
            }
        }
        return false;
    }

    /**
     * 获取表单的实际信息
     */
    private boolean getForm(){
        Request.Builder Builder = getRequestBuilder();
        JSONObject submit_json = new JSONObject();
        submit_json.put("pageSize",10);
        submit_json.put("pageNumber",1);
        submit_json.put("formWid",this.formWid);
        submit_json.put("collectorWid",this.wid);
        Builder.url(scitcUrls.moreWidsUrl)
                .post(RequestBody.create(submit_json.toJSONString(),mediaType_json));
        try {
            String string = calls(Builder.build());
            JSONObject parse = (JSONObject)new JSONParser().parse(string);
            JSONObject datas = (JSONObject)parse.get("datas");
            JSONArray rows = (JSONArray)datas.get("rows");

            for (int i = 0; i < rows.size();i++){
                JSONObject o = (JSONObject)rows.get(i);
                String question = (String)o.get("title");
                forms.put(question,o);
            }
            return true;
        }catch (ParseException e) {
            System.out.println("getSchoolTaskWid 中json转换错误！！");
            String message = e.getMessage();
            desc = "getSchoolTaskWid 中json转换错误！！+e:"+message;
        }catch (Exception e){
            e.printStackTrace();
            String message = e.getMessage();
            desc = "getForm 发生其他错误+e:"+message;
            System.out.println("getForm 发生其他错误");
        }
        return false;
    }
    private boolean getSchoolTaskWid(){
        JSONObject json = new JSONObject();
        json.put("collectorWid",wid+"");
        Request.Builder Builder = getRequestBuilder();
        Builder.url(scitcUrls.schoolTaskWidUrl)
                .post(RequestBody.create(json.toJSONString(),mediaType_json));
        try {
            String string = calls(Builder.build());
            JSONObject parse = (JSONObject)new JSONParser().parse(string);
            JSONObject datas = (JSONObject)parse.get("datas");
            JSONObject collector = (JSONObject)datas.get("collector");
            String schoolTaskWid = (String)collector.get("schoolTaskWid");
            this.schoolTaskWid = Integer.valueOf(schoolTaskWid);
            System.out.println(String.format("schoolTaskWid:%s获取成功",schoolTaskWid+""));
            return true;
        }catch (ParseException e) {

            String message = e.getMessage();
            desc = "getSchoolTaskWid 中json转换错误！！+e:"+message;

            System.out.println("getSchoolTaskWid 中json转换错误！！");
        }catch (Exception e){
            e.printStackTrace();
            String message = e.getMessage();
            desc = "getSchoolTaskWid 中发生其他错误！！+e:"+message;
            System.out.println("getSchoolTaskWid 中发生其他错误！！");
        }
        return false;
    }
    private boolean getFromIdAndWid(){
        Request.Builder requestBuilder = getRequestBuilder();
        JSONObject json = new JSONObject();
        json.put("pageSize",6);
        json.put("pageNumber",1);
        RequestBody requestBody = RequestBody.create(json.toJSONString(), mediaType_json);
        requestBuilder.url(scitcUrls.listUrl)
        .post(requestBody);
        try {
            String string = calls(requestBuilder.build());
            System.out.println(string);
            JSONObject parse = (JSONObject)new JSONParser().parse(string);
            JSONObject datas = (JSONObject)parse.get("datas");
            JSONArray rows = (JSONArray)datas.get("rows");
            if (rows == null || rows.size() <= 0) {
                System.out.println(scitcUser.nick+"当前没有填报信息");
                desc = scitcUser.nick+"当前没有填报信息";
                return false;
            }
            for (int k = 0; k < rows.size();k++){
                JSONObject o = (JSONObject)rows.get(k);
                //查看是否被提交过
                long isHandled = (long)o.get("isHandled");
                if (isHandled == 0){
                    this.wid = Integer.valueOf((String)o.get("wid"));
                    this.formWid = Integer.valueOf((String)o.get("formWid"));
                    System.out.println(String.format("wid:%d和formWid:%d获取成功",wid,formWid));
                    return true;
                }
            }
            int ys = ScitcUtil.getHourse() < 12 ? 6 : 12;
            desc = scitcUser.nick+"没有新的填报信息";
            System.out.println(scitcUser.nick+"没有新的填报信息");
            if (ys == 6)
                scitcUser.myLog.six_describe = desc;
            if (ys == 12)
                scitcUser.myLog.twelve_describe = desc;

            userMapper.updateLog(scitcUser.myLog);
            //说明需要提交的已经提交过
            return false;
        }catch (ParseException e) {
            String message = e.getMessage();
            desc = "getFromIdAndWid 中 json转换错误！！！+e:"+message;
            System.out.println("getFromIdAndWid 中 json转换错误！！！");
        }catch (Exception e){
            e.printStackTrace();
            String message = e.getMessage();
            desc = "getFromIdAndWid 中 发生其他错误！！！+e:"+message;

            System.out.println("getFromIdAndWid 中 发生其他错误！！！");
        }
        return false;
    }
    private boolean getToken(){
        //一直尝试获取，如果出现一些小问题
        try {
            int re = 0;
            while (true){
                re++;
                try {
                    if (re > 10) break;
                    HtmlPage page = webClient.getPage(scitcUrls.tokenUrl);
                    System.out.println(page);
                    HtmlTextInput username = (HtmlTextInput) page.getElementById("username");
                    HtmlPasswordInput password = (HtmlPasswordInput) page.getElementById("password");
                    username.setText(scitcUser.username);
                    password.setText(scitcUser.password);
                    List<HtmlButton> byXPath = page.getByXPath("//button[@type='submit']");
                    byXPath.get(0).click();//模拟点击 登录提示
                    Page clickPage = byXPath.get(0).click();
                    //检测是否登录成功
                    String pageString = clickPage.getWebResponse().getContentAsString();
                    if (pageString.contains("登录提示")){
                        String mtemp ="登录失败！！！！ 当前"+scitcUser.nick+"用户出现验证码或者是密码错误";
                        desc = mtemp;
                        System.out.println(mtemp);
                        return false;
                    }
                    if (re > 4) {
                        System.out.println(scitcUser.nick+"4次重试都失败！！");
                        return false;
                    }
                    //登录成功拿取token
                    List<String> tokens = new ArrayList<>();

                    webClient.getCookieManager().getCookies().forEach(e->{
                        if (e.getName().equals("acw_tc") || e.getName().equals("MOD_AUTH_CAS")){
                            tokens.add(e.getName()+"="+e.getValue());
                        }
                    });
                    this.token = tokens.get(0)+";"+tokens.get(1);
                    webClient.getCookieManager().clearCookies();//清除所有的cookie
                    System.out.println("token获取成功！！！"+this.token);
                    return true;
                }catch (FailingHttpStatusCodeException e){
                    System.out.println("请求服务器超时，重试中");
                    desc = "请求服务器超时";
                }
                catch (Exception e){
                    e.printStackTrace();
                    desc = "重试第"+re+"次过程出现异常+e:"+e.getMessage();
                    System.out.println("重试第"+re+"次过程出现异常");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            desc = "webclient请求出现错误！！！！！"+e.getMessage();
            System.out.println("webclient请求出现错误！！！！！");
        }
        return false;
    }
    private String calls(Request request) {
        int i = 0;
        while (true) {
            if (i >= 5) break;
            i++;
            try {
                String string = null;
                Response execute = okHttpClient.newCall(request).execute();
                if (execute.isSuccessful()) {
                    string = execute.body().string();
                    execute.close();
                    return string;
                }
                return string;
            } catch (Exception e) {
                e.printStackTrace();
                desc = "请求出现问题+"+e.getMessage();
                System.out.println("请求出现问题正在重试中" + i);
            }
        }
        return null;
    }

    private JSONArray proccessfieldItems(JSONArray jsonArray,String content){
        for (int i = 0; i < jsonArray.size();i++){
            JSONObject temp = (JSONObject)jsonArray.get(i);
            String contents = (String) temp.get("content");
            if (contents.contains(content)){
                jsonArray.clear();
                jsonArray.add(temp);//把自身加进去
                return jsonArray;
            }
        }
        return jsonArray;
    }
}
