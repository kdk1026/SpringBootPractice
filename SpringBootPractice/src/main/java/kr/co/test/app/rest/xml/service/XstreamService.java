package kr.co.test.app.rest.xml.service;

import common.spring.resolver.ParamCollector;
import kr.co.test.app.rest.xml.vo.XsFood;
import kr.co.test.app.rest.xml.vo.XsFoods;

public interface XstreamService {

	public XsFood food(ParamCollector paramCollector);
	
	public XsFoods foods(ParamCollector paramCollector);
	
}
