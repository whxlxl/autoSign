package com.lxl.jrxy.config;

import com.gargoylesoftware.htmlunit.WebClient;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Autowired
    MyJSErrorListener myJSErrorListener;
    @Bean
    public WebClient getWebClient(){
        WebClient webClient = new WebClient();
        webClient.getOptions().setJavaScriptEnabled(true);//开启js
        webClient.getOptions().setCssEnabled(true);//开启css
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.setJavaScriptErrorListener(myJSErrorListener);
        webClient.setJavaScriptTimeout(5000);
        webClient.getCookieManager().setCookiesEnabled(true);
        return webClient;
    }

    @Bean
    public OkHttpClient getOkHttpClient(){
        return new OkHttpClient();
    }
}
