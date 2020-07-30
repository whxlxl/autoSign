package com.lxl.jrxy.model.scitc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class ScitcUrls {
    @Value("${scitc.getTokenUrl}")
    public String tokenUrl;
    @Value("${scitc.getlistUrl}")
    public String listUrl;
    @Value("${scitc.getschoolTaskWidUrl}")
    public String schoolTaskWidUrl;
    @Value("${scitc.getMoreWidsUrl}")
    public String moreWidsUrl;
    @Value("${scitc.submitUrl}")
    public String submitUrl;
}
