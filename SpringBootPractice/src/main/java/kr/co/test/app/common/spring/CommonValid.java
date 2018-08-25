package kr.co.test.app.common.spring;

import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import common.ResponseCodeEnum;
import common.util.map.ResultSetMap;

public class CommonValid {

	public static ResultSetMap checkRestValid(LocalValidatorFactoryBean validator, Object obj) {
		ResultSetMap resMap = new ResultSetMap();
		
		DataBinder binder = new DataBinder(obj);
		BindingResult bindingResult = binder.getBindingResult();
		
		validator.validate(obj, bindingResult);
		
		if ( bindingResult.hasErrors() ) {
			ObjectError error = bindingResult.getAllErrors().get(0);
			
			switch (error.getCode()) {
			case "NotBlank":
				resMap.put("resp_code", ResponseCodeEnum.NO_INPUT.getCode());
				break;
				
			case "Pattern":
				resMap.put("resp_code", ResponseCodeEnum.NO_INPUT_FORMAT.getCode());
				break;

			default:
				break;
			}
			
			resMap.put("resp_msg", error.getDefaultMessage());
		}
		
		return resMap;
	}
	
}
