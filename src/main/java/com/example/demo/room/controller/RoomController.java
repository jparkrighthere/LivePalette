package com.example.demo.room.controller;

import com.example.demo.room.dto.RoomGetRoomIdLIstResponse;
import com.example.demo.user.dto.UserGetProfileResponse;
import com.example.demo.user.model.CustomUserDetail;
import com.example.demo.user.model.User;
import com.example.demo.user.service.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
@RequestMapping("/user/room")
@RequiredArgsConstructor
public class RoomController {
    private final CustomUserDetailService customUserDetailService;

    @GetMapping("/{base64Email}")
    public ResponseEntity<?> getRoomIdList(@PathVariable String base64Email){
        String decodedEmail = new String(Base64.getUrlDecoder().decode(base64Email));
        CustomUserDetail customUserDetail = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String tokenEmail = customUserDetail.getUsername();
        if (!decodedEmail.equals(tokenEmail)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        UserDetails userDetails = customUserDetailService.loadUserByUsername(decodedEmail);
        User user = ((CustomUserDetail) userDetails).getUser();
        String[] roomIdList = {"success", "success2"};
        user.setRoomIdList(roomIdList);
        RoomGetRoomIdLIstResponse roomGetRoomIdLIstResponse = new RoomGetRoomIdLIstResponse();
        roomGetRoomIdLIstResponse.setRoomIdList(user.getRoomIdList());

        return ResponseEntity.status(200).body(roomGetRoomIdLIstResponse);
    }

}
