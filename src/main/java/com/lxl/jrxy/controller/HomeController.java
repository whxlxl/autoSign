package com.lxl.jrxy.controller;

import com.gargoylesoftware.htmlunit.WebClient;
import com.lxl.jrxy.mapper.UserMapper;
import com.lxl.jrxy.model.MyLog;
import com.lxl.jrxy.model.Tips;
import com.lxl.jrxy.model.scitc.ScitcUser;
import com.lxl.jrxy.service.MailService;
import com.lxl.jrxy.util.ScitcUtil;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    UserMapper userMapper;
    @Autowired
    WebClient webClient;
    @Autowired
    OkHttpClient okHttpClient;
    @Autowired
    MailService mailService;

    @GetMapping("/")
    public String home(Model model){
        Tips tips = userMapper.getTips();
        model.addAttribute("tips",tips.getTips());
        return "index";
    }


    @PostMapping("/scitcLogin")
    public String scitcLogin(String username, String password, HttpServletRequest request, HttpServletResponse response, Model model){
        //本地数据库查询看看，如果有就对了
        ScitcUser user = userMapper.findByUsernameAndPassword(username, password);

        if (user != null){
            //成功跳转其他界面
            request.getSession(true).setAttribute("user",user);
            return "redirect:/info";
        }else{
            if (ScitcUtil.login(webClient,username,password,request,okHttpClient,userMapper,model)){
                //登陆成功
                ScitcUser user1 = (ScitcUser)request.getSession().getAttribute("user");
                //发送通知邮件
                if (user1 != null){
                    mailService.sendMail_with_newUser(user1.nick,user1.userdepartment);
                }

                return "redirect:/info";
            }
            model.addAttribute("msg","验证失败了,建议重试一下，可能学校服务器抽风了但也有可能是学号或者密码错误哦！！");
        }
        Tips tips = userMapper.getTips();
        model.addAttribute("tips",tips.getTips());
        return "index";
    }

    @GetMapping("/info")
    public String info(HttpServletRequest request,Model model){
        ScitcUser user = (ScitcUser)request.getSession().getAttribute("user");
        if (user == null){
            model.addAttribute("msg","验证失败了唉！");
            model.addAttribute("url","/");
            return "tips";
        }
        userMapper.findLog();
        model.addAttribute("user",user);
        return "info";
    }

    @PostMapping("/getLog")
    @ResponseBody
    public List<MyLog> getLogs(HttpServletRequest request,HttpServletResponse response){
        ScitcUser user = (ScitcUser)request.getSession().getAttribute("user");
        if (user == null){
            try {
                response.sendRedirect("/");//进行跳转
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        List<MyLog> byUsernameAll = userMapper.findByUsernameAll(user.username);
        return byUsernameAll;
    }

    /**
     * 更新用户信息
     * @return
     */
    @PostMapping("/updateUser")
    @ResponseBody
    public Object updateUser(String password,String address,String temperature,String email,String dormitory,String apartment,String auto,HttpServletRequest request,HttpServletResponse response){
        ScitcUser user = (ScitcUser)request.getSession().getAttribute("user");
        if (user == null){
            try {
                response.sendRedirect("/");//进行跳转
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        password.replaceAll(" ","");
        address.replaceAll(" ","");
        temperature.replaceAll(" ","");
        email.replaceAll(" ","");
        dormitory.replaceAll(" ","");
        apartment.replaceAll(" ","");
        auto.replaceAll(" ","");

        String patter = "^\\w+@\\w{1,4}\\.\\w{1,4}";
        boolean matches = email.matches(patter);
        if (!matches){
            return "邮箱验证失败，请重新填写";
        }

        if (password == null || password == ""|| address == null || address == ""
                ||temperature == null || temperature == ""
                || email == null|| email == ""
                || dormitory == null || dormitory == ""
                || apartment == null || apartment == ""
                ||auto == null){
            return "这里的所有字段都不能为空，请重新检查填写";
        }

        user.password = password;
        user.address = address;
        user.temperature = temperature;
        user.email = email;
        user.dormitory = dormitory;
        user.apartment = apartment;
        user.auto = Integer.valueOf(auto);

        Integer integer = userMapper.updateUser(user);
        if (integer > 0){
            return "数据更新成功";
        }

        return "数据更新失败！！！";
    }


    @RequestMapping("/testMail")
    public String testMail(HttpServletRequest request){
        ScitcUser user = (ScitcUser)request.getSession().getAttribute("user");
        if (user != null){
            if (user.username.equals("18201060"))
            mailService.sendMailTest("913413756@qq.com");
            return "success";
        }

        return "index";
    }


}
