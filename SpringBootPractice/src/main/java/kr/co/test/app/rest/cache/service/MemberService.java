package kr.co.test.app.rest.cache.service;

import java.util.List;

import kr.co.test.app.rest.cache.vo.Member;

public interface MemberService {

	public List<Member> getMembersCache();
	
	public void processRemove();
	
	public void processRefresh();
	
	public Member getFindByNameNoCache(String name);
	
	public Member getFindByNameCache(String name);
	
	public void processRemove(String name);
	
	public void processRefresh(String name);
	
}
