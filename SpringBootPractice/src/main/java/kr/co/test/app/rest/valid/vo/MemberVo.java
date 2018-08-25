package kr.co.test.app.rest.valid.vo;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;

@Data
public class MemberVo {

	@NotBlank(message = "ID를 입력해 주세요.")
	private String id;
	
	@NotBlank(message = "이름을 입력해 주세요.")
	private String name;
	
	@NotBlank(message = "휴대폰 번호를 입력해 주세요.")
	@Pattern(regexp = "^(01[016789])-?(\\d{3,4})-?(\\d{4})+$", message = "휴대폰 번호 형식에 맞게 입력해 주세요.")
	private String m_phone;
	
}
