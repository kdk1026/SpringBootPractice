package kr.co.test.app.common.spring.valid.constraint;

import java.io.UnsupportedEncodingException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.util.StringUtils;

public class MaxByteValidator implements ConstraintValidator<MaxByte, String> {

	private int max;
	private String charsetName;
	
	@Override
	public void initialize(MaxByte constraintAnnotation) {
		this.max = constraintAnnotation.max();
		this.charsetName = constraintAnnotation.charsetName();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if ( StringUtils.isEmpty(value) ) {
			return true;
		}
		
		if ( StringUtils.isEmpty(charsetName) ) {
			if ( value.getBytes().length > max ) {
				return false;
			}
			
		} else {
			try {
				if ( value.getBytes(charsetName).length > max ) {
					return false;
				}
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		return true;
	}
	
}
