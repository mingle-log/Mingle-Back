package com.greedy.mingle.message.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greedy.mingle.common.ResponseDTO;
import com.greedy.mingle.employee.dto.EmployeeDTO;
import com.greedy.mingle.message.dto.MessageDTO;
import com.greedy.mingle.message.service.MessageService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/message")
public class MessageController {
	
	private final MessageService messageService;
	
	public MessageController(MessageService messageService) {
		this.messageService = messageService;
	}
	
	/* 1. 받은 쪽지함 조회 (최근 20개) */
	@GetMapping("/received")
	public ResponseEntity<ResponseDTO> selectReceivedMessage(@AuthenticationPrincipal EmployeeDTO receiver) {
		
		return ResponseEntity
				.ok()
				.body(new ResponseDTO(HttpStatus.OK, "받은 쪽지함 조회 성공", messageService.selectReceivedMessage(receiver.getEmpCode())));
		
	}
	
	/* 2. 받은 쪽지 클릭 시, 쪽지 읽음 표시 */
	@PatchMapping("/read/{msgCode}")
	public ResponseEntity<ResponseDTO> readMessage(@PathVariable Long msgCode, @AuthenticationPrincipal EmployeeDTO receiver) {
		
		messageService.readMessage(msgCode, receiver.getEmpCode());
		
		return ResponseEntity
				.ok()
				.body(new ResponseDTO(HttpStatus.OK, "쪽지 읽음 표시 성공"));
	}
	
	/* 3. 교직원명/내용으로 쪽지 검색 후 조회 (받은 쪽지함) */
	@GetMapping("/received/search")
	public ResponseEntity<ResponseDTO> searchReceivedMessage(@AuthenticationPrincipal EmployeeDTO receiver, 
															 @RequestParam(name="condition")String condition, 
															 @RequestParam(name="word")String word) {
		return ResponseEntity
				.ok()
				.body(new ResponseDTO(HttpStatus.OK, "받은 쪽지함 검색 후 조회 성공", messageService.searchReceivedMessage(receiver.getEmpCode(), condition, word)));
		
	}
	
	/* 4. 보낸 쪽지함 조회 (최근 20개) */
	@GetMapping("/sent")
	public ResponseEntity<ResponseDTO> selectSentMessage(@AuthenticationPrincipal EmployeeDTO sender) {
		
		return ResponseEntity
				.ok()
				.body(new ResponseDTO(HttpStatus.OK, "보낸 쪽지함 조회 성공", messageService.selectSentMessage(sender.getEmpCode())));
		
	}
	
	/* 5. 교직원명/내용으로 쪽지 검색 후 조회 (보낸 쪽지함) */
	@GetMapping("/sent/search")
	public ResponseEntity<ResponseDTO> searchSentMessage(@AuthenticationPrincipal EmployeeDTO sender, 
													     @RequestParam(name="condition")String condition, 
														 @RequestParam(name="word")String word) {
		return ResponseEntity
				.ok()
				.body(new ResponseDTO(HttpStatus.OK, "보낸 쪽지함 검색 후 조회 성공", messageService.searchSentMessage(sender.getEmpCode(), condition, word)));
		
	}
	
	/* 6. 중요 쪽지함 조회 (전체) */
	@GetMapping("/liked")
	public ResponseEntity<ResponseDTO> selectlikedMessage(@AuthenticationPrincipal EmployeeDTO employee) {
		
		return ResponseEntity
				.ok()
				.body(new ResponseDTO(HttpStatus.OK, "중요 쪽지함 조회 성공", messageService.selectLikedMessage(employee.getEmpCode())));
		
	}
	
	/* 7. 교직원명/내용으로 쪽지 검색 후 조회 (중요 쪽지함) */
	@GetMapping("/liked/search")
	public ResponseEntity<ResponseDTO> searchLikedMessage(@AuthenticationPrincipal EmployeeDTO employee, 
														  @RequestParam(name="condition")String condition, 
														  @RequestParam(name="word")String word) {
		return ResponseEntity
				.ok()
				.body(new ResponseDTO(HttpStatus.OK, "중요 쪽지함 검색 후 조회 성공", messageService.searchLikedMessage(employee.getEmpCode(), condition, word)));
		
	}
	
	/* 8. 하트 클릭 시, 중요 쪽지함으로 이동 및 취소 */
	@PatchMapping("/like/{msgCode}")
	public ResponseEntity<ResponseDTO> likeToggleMessage(@PathVariable Long msgCode, @AuthenticationPrincipal EmployeeDTO employee) {
		
		messageService.likeToggleMessage(msgCode, employee.getEmpCode());
		
		return ResponseEntity
				.ok()
				.body(new ResponseDTO(HttpStatus.OK, "중요 쪽지함으로 이동/취소 성공"));
	}
	
	/* 9. 상위 카테고리가 존재하는 소속 전체 조회 */
	@GetMapping("/find/department")
	public ResponseEntity<ResponseDTO> selectAllDepartment() {
		
		return ResponseEntity
				.ok()
				.body(new ResponseDTO(HttpStatus.OK, "현재 존재하는 소속 전체 조회 성공", messageService.selectAllDepartment()));
		
	}
	
	/* 10. 소속 선택 시, 해당 소속 교직원 조회 */
	@GetMapping("/find/employee/{deptCode}")
	public ResponseEntity<ResponseDTO> selectReceiverByDeptCode(@PathVariable Long deptCode) {
		
		return ResponseEntity
				.ok()
				.body(new ResponseDTO(HttpStatus.OK, "소속 선택 시, 해당 소속 교직원 조회 성공", messageService.selectReceiverByDeptCode(deptCode)));
		
	}
	
	/* 11. 쪽지 전송 */
	@PostMapping("/send")
	public ResponseEntity<ResponseDTO> sendMessage(@ModelAttribute MessageDTO messageDTO, @AuthenticationPrincipal EmployeeDTO sender) {
		
		messageDTO.setSender(sender);
		messageService.sendMessage(messageDTO);
		
		return ResponseEntity
				.ok()
				.body(new ResponseDTO(HttpStatus.OK, "쪽지 전송 성공"));
		
	}
	
	/* 12. 선택한 쪽지 삭제 */
	@PatchMapping("/remove")
	public ResponseEntity<ResponseDTO> removeMessage(@RequestBody MessageDTO messageDTO, @AuthenticationPrincipal EmployeeDTO employee) {
		
		Long [] selectedMsgs = messageDTO.getSelectedMsgs();
		
		messageService.removeMessage(selectedMsgs, employee.getEmpCode());
		
		return ResponseEntity
				.ok()
				.body(new ResponseDTO(HttpStatus.OK, "선택한 쪽지 삭제 성공"));
		
	}
	

}
