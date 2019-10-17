package com.nowcoder.wenda.util;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.util.Map;
import java.util.Properties;

/**
 * @author LuoJiaQi
 * @Date 2019/10/16
 * @Time 17:35
 */

@Service
public class MailSender implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailSender.class);
    private JavaMailSenderImpl mailSender;

    @Autowired
    private VelocityEngine velocityEngine;

    /** 邮件发送服务
     * @param to       发给谁
     * @param subject  标题
     * @param template 模板
     * @param model    模板里变量的替换
     * @return
     */
    public boolean sendWithHTMLTemplate(String to, String subject,
                                        String template, Map<String, Object> model) {
        try {
            //邮件里显示的发件人昵称
            String nick = MimeUtility.encodeText("牛客网高级课");
            //邮件里显示的发件人地址
            InternetAddress from = new InternetAddress(nick + "<313993678@qq.com>");
            //创建邮件正文
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            //创建Helper，用于设置邮件
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            //用Velocity的引擎渲染模板，model里包含传进来的各种变量
            String result = VelocityEngineUtils
                    .mergeTemplateIntoString(velocityEngine, template, "UTF-8", model);
            //设置收件人
            mimeMessageHelper.setTo(to);
            //设置发件人
            mimeMessageHelper.setFrom(from);
            //设置邮件标题
            mimeMessageHelper.setSubject(subject);
            //设置邮件正文内容，result是从Velocity的模板直接获取的
            mimeMessageHelper.setText(result, true);
            mailSender.send(mimeMessage);
            return true;
        } catch (Exception e) {
            LOGGER.error("发送邮件失败" + e.getMessage());
            return false;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        mailSender = new JavaMailSenderImpl();
        mailSender.setUsername("313993678@qq.com");
        mailSender.setPassword("uhutullrdktocahh");
        mailSender.setHost("smtp.qq.com");
        mailSender.setPort(465);
        mailSender.setProtocol("smtps");
        mailSender.setDefaultEncoding("utf8");
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.ssl.enable", true);

        mailSender.setJavaMailProperties(javaMailProperties);
    }
}