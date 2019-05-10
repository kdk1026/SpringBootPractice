package kr.co.test.mail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import kr.co.test.app.rest.mail.EmailComponent;
import kr.co.test.app.rest.mail.vo.MailVO;

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
@ActiveProfiles("local")
@RunWith(SpringRunner.class)
@SpringBootTest
public class EmailTest {
	
	@Autowired
	private EmailComponent emailComponent;

	@Test
	public void test() {
		MailVO vo = new MailVO();
		vo.setToMail("kdk1026@naver.com");
		vo.setSubject("테스트");
		vo.setContents("<b>테스트임</b>");
		
		emailComponent.sendMail(vo);	}
	
}
