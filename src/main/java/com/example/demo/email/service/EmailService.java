package com.example.demo.email.service;

import com.example.demo.global.util.RedisUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private RedisUtil redisUtil;
    private int authNumber;
    @Autowired
    private JavaMailSender javaMailSender;

    public boolean CheckAuthNum(String email,String authNum){
        if(redisUtil.getData(authNum)==null || !redisUtil.getData(authNum).equals(email)){
            return false;
        }
        return true;
    }

    public void makeRandomNumber() {
        Random r = new Random();
        authNumber = (int)(Math.random() * (900000)) + 100000;
    }

    //Create Email
    public MimeMessage createEmail(String receiverEmail) {
        makeRandomNumber();
        MimeMessage message = mailSender.createMimeMessage();
        try{
            MimeMessageHelper helper = new MimeMessageHelper(message,true,"utf-8");
            helper.setFrom("livepaletteemailserver@gmail.com");
            helper.setTo(receiverEmail);
            helper.setSubject("회원 가입 인증 이메일 입니다.");
            helper.setText(
                    "나의 APP을 방문해주셔서 감사합니다." + 	//html 형식으로 작성 !
                            "<br><br>" +
                            "인증 번호는 " + authNumber + "입니다." +
                            "<br>" +
                            "6자리 인증번호를 바르게 입력해주세요"
            );
        } catch(MessagingException e){
            e.printStackTrace();
            message = null;
        }

        return message;
    }

    public String sendEmail(String receiverEmail) {
        MimeMessage message = createEmail(receiverEmail);
        if(message==null){
            return null;
        }
        javaMailSender.send(message);
        redisUtil.setDataExpire(Integer.toString(authNumber),receiverEmail,60*5L);

        return Integer.toString(authNumber);
    }
}
