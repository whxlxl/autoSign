package com.lxl.jrxy.service;

import com.gargoylesoftware.htmlunit.WebClient;
import com.lxl.jrxy.mapper.UserMapper;
import com.lxl.jrxy.model.scitc.Campus;
import com.lxl.jrxy.model.scitc.ScitcUrls;
import com.lxl.jrxy.model.scitc.ScitcUser;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AutoSign {

    @Autowired
    ScitcUrls scitcUrls;

    @Autowired
    WebClient webClient;

    @Autowired
    OkHttpClient okHttpClient;


    @Autowired
    UserMapper userMapper;

    @Autowired
    MailService mailService;


    public Map<String,String> convertQuestion(List<Map<String,String>> allList){
        Map<String,String> map = new HashMap<>();
        for (Map<String,String> temp:allList){
            map.put(temp.get("question"),temp.get("answer"));
        }
        return map;
    }



    @Scheduled(cron = "0 33 06 * * *")
    @Scheduled(cron = "0 48 06 * * *")
    @Scheduled(cron = "0 01 07 * * *")
    @Scheduled(cron = "0 21 07 * * *")
    @Scheduled(cron = "0 40 07 * * *")
    @Scheduled(cron = "0 03 12 * * *")
    @Scheduled(cron = "0 15 12 * * *")
    @Scheduled(cron = "0 26 12 * * *")
    public void autoSign(){
        Map<String,String> question = convertQuestion(userMapper.getAllQuestion());//问题以及答案的集合，key代表问题题目，value代表答案
        while (true){
            try {
                System.out.println("开始扫描所有用户");
                List<ScitcUser> all = userMapper.findAll();
                for (ScitcUser scitcUser1:all){
                    Campus campus1 = new Campus(scitcUser1, scitcUrls, webClient, okHttpClient, mailService,userMapper,question);
                    campus1.sign();
                }
                break;
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("异常了一次");
            }
        }
        System.out.println("所有用户执行完成");
    }

}
