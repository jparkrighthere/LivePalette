package com.example.demo.user.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.Duration;


@Service
@RequiredArgsConstructor
public class EmailUtil {
    private int authNumber;

    private final JavaMailSender javaMailSender;
    private final StringRedisTemplate redisTemplate;

    public String getData(String key){
        ValueOperations<String,String> valueOperations=redisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    //지정된 키(key)에 값을 저장하고, 지정된 시간(duration) 후에 데이터가 만료되도록 설정하는 메서드
    public void setDataExpire(String key,String value,long duration){
        ValueOperations<String,String> valueOperations=redisTemplate.opsForValue();
        Duration expireDuration= Duration.ofSeconds(duration);
        valueOperations.set(key,value,expireDuration);
    }


    public boolean CheckAuthNum(String email, String authNum){
        return getData(authNum) != null && getData(authNum).equals(email);
    }

    public void makeRandomNumber() {
        authNumber = (int)(Math.random() * (900000)) + 100000;
    }

    //Create Email
    public MimeMessage createEmail(String receiverEmail) {
        makeRandomNumber();
        MimeMessage message = javaMailSender.createMimeMessage();
        try{
            MimeMessageHelper helper = new MimeMessageHelper(message,true,"utf-8");
            helper.setFrom("livepaletteemailserver@gmail.com");
            helper.setTo(receiverEmail);
            helper.setSubject("회원 가입 인증 이메일 입니다.");
            helper.setText("",
                    "Live Palette를 방문해주셔서 감사합니다." +
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
        setDataExpire(Integer.toString(authNumber),receiverEmail,60*5L);

        return Integer.toString(authNumber);
    }
}
