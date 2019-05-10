package kr.co.test.app.rest.mail.vo;

import java.util.List;

import common.BaseObject;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2019. 5. 10. 김대광	최초작성
 * </pre>
 * 
 *
 * @author 김대광
 */
public class MailVO extends BaseObject {

	private static final long serialVersionUID = 1L;
	
	private String subject;
	private String contents;
	
	private String toMail;
	private List<String> toMails;
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public String getToMail() {
		return toMail;
	}
	public void setToMail(String toMail) {
		this.toMail = toMail;
	}
	public List<String> getToMails() {
		return toMails;
	}
	public void setToMails(List<String> toMails) {
		this.toMails = toMails;
	}
	
}
