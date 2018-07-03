package kr.co.test.app.rest.login.service;

import common.spring.resolver.ParamCollector;
import common.util.map.ResultSetMap;
import kr.co.test.app.page.login.model.AuthenticatedUser;

public interface RestLoginService {
	
	public ResultSetMap processAuthByUsername(ParamCollector paramCollector);
	
	public AuthenticatedUser processAuthByToken(ParamCollector paramCollector);
	
	/**
	 * <pre>
	 * 토큰 갱신
	 *  - 카카오 API '사용자 토큰 갱신' 참조
	 * </pre>
	 * @param paramCollector
	 * @return
	 */
	public ResultSetMap processRefresh(ParamCollector paramCollector);
}
