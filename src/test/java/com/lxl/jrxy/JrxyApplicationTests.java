package com.lxl.jrxy;

import com.gargoylesoftware.htmlunit.WebClient;
import com.lxl.jrxy.mapper.UserMapper;
import com.lxl.jrxy.model.scitc.Campus;
import com.lxl.jrxy.model.scitc.ScitcUrls;
import com.lxl.jrxy.model.scitc.ScitcUser;
import com.lxl.jrxy.service.AutoSign;
import com.lxl.jrxy.service.MailService;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;
import java.util.Map;


@SpringBootTest
@ComponentScan("com.lxl")
class JrxyApplicationTests {

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

    @Autowired
    AutoSign autoSign;


}
