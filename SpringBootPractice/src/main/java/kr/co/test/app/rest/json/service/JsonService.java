package kr.co.test.app.rest.json.service;

import common.spring.resolver.ParamCollector;
import common.util.map.ResultSetMap;

public interface JsonService {

	public ResultSetMap data(ParamCollector paramCollector);
	
}
