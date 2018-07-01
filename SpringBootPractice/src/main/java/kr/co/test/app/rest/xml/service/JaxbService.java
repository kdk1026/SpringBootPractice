package kr.co.test.app.rest.xml.service;

import common.spring.resolver.ParamCollector;
import kr.co.test.app.rest.xml.vo.JaxFood;
import kr.co.test.app.rest.xml.vo.JaxFoods;

public interface JaxbService {

	public JaxFood food(ParamCollector paramCollector);
	
	public JaxFoods foods(ParamCollector paramCollector);
}
