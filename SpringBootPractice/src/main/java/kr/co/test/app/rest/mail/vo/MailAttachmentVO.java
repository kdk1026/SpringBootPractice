package kr.co.test.app.rest.mail.vo;

import java.io.File;

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
public class MailAttachmentVO extends MailVO {

	private static final long serialVersionUID = 1L;
	
	private File attachFile;
	private boolean isFileDelete;
	
	public File getAttachFile() {
		return attachFile;
	}
	public void setAttachFile(File attachFile) {
		this.attachFile = attachFile;
	}
	public boolean isFileDelete() {
		return isFileDelete;
	}
	public void setFileDelete(boolean isFileDelete) {
		this.isFileDelete = isFileDelete;
	}
	
}
