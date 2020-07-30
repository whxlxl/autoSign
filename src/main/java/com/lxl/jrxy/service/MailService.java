package com.lxl.jrxy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
public class MailService {

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private JavaMailSender mailSender;

    /**
     * 新用户入驻通知
     */
    @Async
    public void sendMail_with_newUser(String name,String xb){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = null;
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(from,"今日校园-新用户通知");
            helper.setTo(from);
            helper.setSubject("新用户通知");
            helper.setText("用户："+name+"\t  系别："+xb, true);
            mailSender.send(message);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("通知失败");
        }
    }

    @Async
    public void sendHtmlMsg(String to,String msg){
        System.out.println("正在发送邮件！！");
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = null;
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(from,"蓝小狼-今日校园自动签到");
            helper.setTo(to);
            helper.setSubject("今日校园签到结果");
            helper.setText("<html><body><h1 color='red'>"+msg+"</h1>" +
                    "<small>以上结果仅参考-建议去今日校园查看是否签到成功</small>" +
                    "</body></html>", true);
            mailSender.send(message);
            System.out.println("发送成功！！");
            Thread.sleep(15000);//停留15秒防止频繁
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("邮件发送失败！！");
        }
    }

    @Async
    public void sendMailTest(String to){
        try {
            System.out.println("开始发送邮件");
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from,"这是一条测试邮件");
            helper.setTo(to);
            helper.setSubject("邮箱测试");
            helper.setText("当前信息只是用于测试，请不要把当前发信人加入垃圾箱，每天的签到通知邮箱就是这个", true);
            mailSender.send(message);
            System.out.println("发送成功");

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("邮箱发送异常");
        }
    }

}
