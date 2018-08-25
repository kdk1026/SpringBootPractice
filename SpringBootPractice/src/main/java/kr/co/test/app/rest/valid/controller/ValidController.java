package kr.co.test.app.rest.valid.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import common.LogDeclare;
import common.ResponseCodeEnum;
import common.spring.resolver.ParamCollector;
import common.util.map.MapUtil;
import common.util.map.ResultSetMap;
import kr.co.test.app.rest.valid.vo.MemberVo;

@RestController
@RequestMapping("valid")
public class ValidController extends LogDeclare {
	
	@Autowired
	private LocalValidatorFactoryBean validator;
	
	@RequestMapping("add")
	public ResponseEntity<?> add(@Valid MemberVo member, BindingResult bindingResult) {
		if ( bindingResult.hasErrors() ) {
			String sErrMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
			return new ResponseEntity<>(sErrMsg, HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<>(member, HttpStatus.OK);
	}
	
	@RequestMapping("add2")
	public ResponseEntity<?> add2(ParamCollector paramCollector) {
		MemberVo member = new MemberVo();
		MapUtil.convertMapToObject(paramCollector.getMap(), member);

		DataBinder binder = new DataBinder(member);
		BindingResult bindingResult = binder.getBindingResult();
		
		validator.validate(member, bindingResult);
		
		if ( bindingResult.hasErrors() ) {
			String sErrMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
			return new ResponseEntity<>(sErrMsg, HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<>(member, HttpStatus.OK);
	}

	@RequestMapping(value = "add3", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResultSetMap add3(ParamCollector paramCollector) {
		MemberVo member = new MemberVo();
		MapUtil.convertMapToObject(paramCollector.getMap(), member);

		DataBinder binder = new DataBinder(member);
		BindingResult bindingResult = binder.getBindingResult();
		
		validator.validate(member, bindingResult);
		
		ResultSetMap resMap = new ResultSetMap();
		
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
			
		} else {
			resMap.put("resp_code", ResponseCodeEnum.SUCCESS.getCode());
			resMap.put("resp_msg", ResponseCodeEnum.SUCCESS.getMessage());
		}
		
		return resMap;
	}
}
