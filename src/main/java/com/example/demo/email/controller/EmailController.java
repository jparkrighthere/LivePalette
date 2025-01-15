package com.example.demo.email.controller;

import com.example.demo.email.dto.EmailCheckRequest;
import com.example.demo.email.dto.EmailRequest;
import com.example.demo.email.dto.EmailResponse;
import com.example.demo.email.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {
//    private final EmailService emailService;
//    @PostMapping("/sendEmail")
//    public ResponseEntity<?> mailSend(@RequestBody @Valid EmailRequest emailRequest) {
//        String authNum = emailService.sendEmail(emailRequest.getEmail());
//        if (authNum == null) {
//            return ResponseEntity.status(403).body("Something went wrong");
//        }
//        EmailResponse emailResponse = new EmailResponse();
//        emailResponse.setAuthNum(authNum);
//        return ResponseEntity.status(200).body(emailResponse);
//    }
//
//    @PostMapping("/mailAuthCheck")
//    public ResponseEntity<?> AuthCheck(@RequestBody @Valid EmailCheckRequest emailCheckRequest){
//        Boolean Checked=emailService.CheckAuthNum(emailCheckRequest.getEmail(),emailCheckRequest.getAuthNum());
//        if(Checked){
//            return ResponseEntity.status(200).body("Authorization Success");
//        }
//        else{
//            return ResponseEntity.status(403).body("Wrong Number");
//        }
//    }
}
