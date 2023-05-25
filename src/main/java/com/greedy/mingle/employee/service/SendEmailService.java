package com.greedy.mingle.employee.service;




import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.greedy.mingle.employee.dto.MailDTO;
import com.greedy.mingle.employee.entity.Employee;
import com.greedy.mingle.employee.repository.EmployeeRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SendEmailService {

   private final EmployeeRepository employeeRepository;
   private final JavaMailSender mailSender;
   private final PasswordEncoder passwordEncoder;
   private static final String FROM_ADDRESS = "qwe030223@naver.com";

   public SendEmailService(EmployeeRepository employeeRepository, JavaMailSender mailSender, PasswordEncoder passwordEncoder) {
      this.employeeRepository = employeeRepository;
      this.mailSender = mailSender;
      this.passwordEncoder = passwordEncoder;
   }
   
   @Transactional
   public MailDTO createMailAndTwoPassword(String empId, String empEmail, String empName) {
      
      log.info("[SendEmailService] empName : {}", empName);
      
      String password = getTwoPassword();
      MailDTO dto = new MailDTO();
      dto.setAddress(empEmail);
      dto.setTitle(empName + "님의 morethanus 2차 비밀번호 이메일 입니다.");
      dto.setMessage("안녕하세요. Mingle 2차 비밀번호 안내 관련 이메일 입니다." + "[" + empName + "]" + "님의 임시 비밀번호는 " + password + " 입니다.");
  
      
      return dto;
   }
   
   
   
   @Transactional
   public MailDTO createMailAndChangePassword(String empId, String empEmail, String empName) {
      
      log.info("[SendEmailService] empName : {}", empName);
      
      String str = getTempPassword();
      MailDTO dto = new MailDTO();
      dto.setAddress(empEmail);
      dto.setTitle(empName + "님의 morethanus 임시비밀번호 안내 이메일 입니다.");
      dto.setMessage("안녕하세요. Mingle 임시비밀번호 안내 관련 이메일 입니다." + "[" + empName + "]" + "님의 임시 비밀번호는 " + str + " 입니다.");
      String pwd = passwordEncoder.encode(str);
      Employee id = employeeRepository.findByEmpId(empId).orElseThrow(IllegalArgumentException::new);
      id.setEmpPwd(pwd);
      return dto;
   }

   public String getTempPassword() {
       char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
               'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

   
       
       String str = "";

       int idx = 0;
       for (int i = 0; i < 10; i++) {
           idx = (int) (charSet.length * Math.random());
           str += charSet[idx];
       }
       return str;
   }
   
   public String getTwoPassword() {
	   char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
               'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

	   String password = "";
	   int idx = 0;
	   
	    for (int i = 0; i < 6; i++) {
	    	 idx = (int) (charSet.length * Math.random());
	    	 password += charSet[idx];
	    }

	    return password; // StringBuilder를 String으로 변환하여 반환
	}


   public void mailSend(MailDTO mailDTO) {
      log.info("이메일 전송 완료");
      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(mailDTO.getAddress());
      message.setFrom(SendEmailService.FROM_ADDRESS);
      message.setSubject(mailDTO.getTitle());
      message.setText(mailDTO.getMessage());

		 mailSender.send(message); 
   }




}