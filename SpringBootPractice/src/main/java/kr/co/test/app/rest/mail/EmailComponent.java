package kr.co.test.app.rest.mail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import kr.co.test.app.rest.mail.vo.MailAttachmentVO;
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
@Async
@Component
public class EmailComponent {

	@Autowired
	private JavaMailSender javaMailSender; 
	
	@Value("${spring.mail.username}")
	private String springMailUsername; 
	
	public void sendMail(MailVO vo) {
		final MimeMessagePreparator preparator = new MimeMessagePreparator() {
			
			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				final MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
				
				messageHelper.setFrom(springMailUsername);
				messageHelper.setTo(vo.getToMail());
				messageHelper.setSubject(vo.getSubject());
				messageHelper.setText(vo.getContents(), true);
			}
			
		};
		
		javaMailSender.send(preparator);
	}
	
	public void sendMailWithAttachment(MailAttachmentVO vo) throws IOException {
		File attachFile = vo.getAttachFile();
		
		final MimeMessagePreparator preparator = new MimeMessagePreparator() {
			
			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				final MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
				
				messageHelper.setFrom(springMailUsername);
				messageHelper.setTo(vo.getToMail());
				messageHelper.setSubject(vo.getSubject());
				messageHelper.setText(vo.getContents(), true);
				
				FileSystemResource fileResource = new FileSystemResource(attachFile);
				messageHelper.addAttachment(attachFile.getName(), fileResource);
			}
			
		};
		
		javaMailSender.send(preparator);
		
		if ( vo.isFileDelete() ) {
			Files.delete(attachFile.toPath());
		}
	}
	
	public void sendMailMultiple(MailVO vo) {
		List<String> toMailAddrs = vo.getToMails();
		MimeMessagePreparator[] preparators = new MimeMessagePreparator[toMailAddrs.size()];
		
		int i=0;
		for (final String toMail : toMailAddrs) {
			preparators[i++] = new MimeMessagePreparator() {
				
				@Override
				public void prepare(MimeMessage mimeMessage) throws Exception {
					final MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
					
					messageHelper.setFrom(springMailUsername);
					messageHelper.setTo(toMail);
					messageHelper.setSubject(vo.getSubject());
					messageHelper.setText(vo.getContents(), true);
				}
				
			};
		}
		
		javaMailSender.send(preparators);
	}
	
	public void sendMailWithAttachmentMultiple(MailAttachmentVO vo) throws IOException {
		File attachFile = vo.getAttachFile();
		
		List<String> toMailAddrs = vo.getToMails();
		MimeMessagePreparator[] preparators = new MimeMessagePreparator[toMailAddrs.size()];
		
		int i=0;
		for (final String toMail : toMailAddrs) {
			preparators[i++] = new MimeMessagePreparator() {
				
				@Override
				public void prepare(MimeMessage mimeMessage) throws Exception {
					final MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
					
					messageHelper.setFrom(springMailUsername);
					messageHelper.setTo(toMail);
					messageHelper.setSubject(vo.getSubject());
					messageHelper.setText(vo.getContents(), true);
				}
				
			};
		}
		
		javaMailSender.send(preparators);
		
		if ( vo.isFileDelete() ) {
			Files.delete(attachFile.toPath());
		}
	}
	
}
