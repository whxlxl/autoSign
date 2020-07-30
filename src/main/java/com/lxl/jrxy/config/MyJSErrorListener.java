package com.lxl.jrxy.config;

import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.DefaultJavaScriptErrorListener;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@Component
public class MyJSErrorListener extends DefaultJavaScriptErrorListener{
    public MyJSErrorListener() {

    }

    @Override
    public void scriptException(HtmlPage page, ScriptException scriptException) {
        System.out.println("脚本异常");
    }

    @Override
    public void timeoutError(HtmlPage page, long allowedTime, long executionTime) {

    }

    @Override
    public void malformedScriptURL(HtmlPage page, String url, MalformedURLException malformedURLException) {

    }

    @Override
    public void loadScriptError(HtmlPage page, URL scriptUrl, Exception exception) {

    }

    @Override
    public void warn(String message, String sourceName, int line, String lineSource, int lineOffset) {

    }
}
