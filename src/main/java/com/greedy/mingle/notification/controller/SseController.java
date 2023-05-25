package com.greedy.mingle.notification.controller;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.greedy.mingle.employee.jwt.TokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
public class SseController {
	
	public static Map<String, SseEmitter> sseEmitters = new ConcurrentHashMap<>();
	private final TokenProvider tokenProvider;
    
    @CrossOrigin
    @GetMapping(value = "/noti/{token}", produces= MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable(value="token") String token) {
			
        // 전달받은 token에서 user의 pk값 파싱 => 사용자별로 SseEmitter를 식별하여 이벤트 전송 가능
    	String empCode = tokenProvider.getUserIdFromToken(token);
//    	Long empCode = employee.getEmpCode();
    	log.info("[SseController] empCode : {}", empCode);
		
        // 현재 클라이언트를 위한 SseEmitter 생성
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        
        // 503 Service Unavailable 오류가 발생하지 않도록 첫 데이터 보내기
        try {
            // 연결
            sseEmitter.send(SseEmitter.event().name("connected").data("연결 성공!🥳"));
        } catch (IOException e) {
            e.printStackTrace();
        } 
        
        // user의 pk값을 key값으로 하여 SseEmitter 저장
        sseEmitters.put(empCode, sseEmitter);

        sseEmitter.onCompletion(() -> sseEmitters.remove(empCode));
        sseEmitter.onTimeout(() -> sseEmitters.remove(empCode));
        sseEmitter.onError((e) -> sseEmitters.remove(empCode));

        return sseEmitter;
    }

}