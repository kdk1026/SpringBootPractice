package kr.co.test.app.rest.valid.vo;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

import kr.co.test.app.common.spring.valid.constraint.MaxByte;
import lombok.Data;

@Data
public class MemberVo {

	@NotBlank(message = "ID를 입력해 주세요.")
	private String id;
	
	@NotBlank(message = "이름을 입력해 주세요.")
	@MaxByte(max = 12, message = "최대 12 byte를 초과할 수 없습니다.")
	private String name;
	
	@NotBlank(message = "이름2를 입력해 주세요.")
	@MaxByte(max = 8, message = "최대 8 byte를 초과할 수 없습니다. (euc-kr)", charsetName = "MS949")
	private String name2;
	
	@NotBlank(message = "휴대폰 번호를 입력해 주세요.")
	@Pattern(regexp = "^(01[016789])-?(\\d{3,4})-?(\\d{4})+$", message = "휴대폰 번호 형식에 맞게 입력해 주세요.")
	private String m_phone;
	
}
