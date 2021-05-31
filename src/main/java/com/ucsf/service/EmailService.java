package com.ucsf.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.itextpdf.html2pdf.HtmlConverter;
import com.ucsf.auth.model.User;
import com.ucsf.model.UserConsent;

@Service
public class EmailService {

	@Autowired
	JavaMailSender javaMailSender;

	@Value("${web.site.url}")
	String webSiteUrl;
	
	@Value("${spring.mail.from}")
	String fromEmail;
	
	@Autowired ConsentService consentService;

	public void sendResetPasswordEmail(String from, String to, String subject, String name, String url, String fileName)
			throws Exception {
		MimeMessage msg = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(msg, true);
		helper.setTo(to);
		helper.setFrom(from);
		helper.setSubject(subject);
		File file = ResourceUtils.getFile(fileName);
		String body = readFromInputStream(new FileInputStream(file));
		body = body.replaceAll("\\{\\{name\\}\\}", name);
		body = body.replaceAll("\\{\\{url\\}\\}", url);
		String redirectUrl = webSiteUrl + "?token=" + url;
		body = body.replaceAll("\\{\\{webSiteUrl\\}\\}", redirectUrl);
		helper.setText(body, true);
		javaMailSender.send(msg);
	}
	
	public void informStudyTeam(String from, String[] to, String subject, String name,String email)
			throws Exception {
		MimeMessage msg = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(msg, true);
		helper.setTo(to);
		helper.setFrom(from);
		helper.setSubject(subject);
		File file = ResourceUtils.getFile("classpath:template/informStudyTeamAboutNewPatient.html");
		String body = readFromInputStream(new FileInputStream(file));
		body = body.replaceAll("\\{\\{name\\}\\}", name);
		body = body.replaceAll("\\{\\{email\\}\\}", email);
		helper.setText(body, true);
		javaMailSender.send(msg);
	}

	public void sendStudyApprovalEmail(String from, String to, String subject, String name) throws Exception {
		MimeMessage msg = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(msg, true);
		helper.setTo(to);
		helper.setFrom(from);
		helper.setSubject(subject);
		File file = ResourceUtils.getFile("classpath:template/studyApprovalEmail.html");
		String body = readFromInputStream(new FileInputStream(file));
		body = body.replaceAll("\\{\\{name\\}\\}", name);
		helper.setText(body, true);
		javaMailSender.send(msg);
	}

	public void sendStudyDisApprovalEmail(String from, String to, String subject, String name) throws Exception {
		MimeMessage msg = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(msg, true);
		helper.setTo(to);
		helper.setFrom(from);
		helper.setSubject(subject);
		File file = ResourceUtils.getFile("classpath:template/studyDisApprovalEmail.html");
		String body = readFromInputStream(new FileInputStream(file));
		body = body.replaceAll("\\{\\{name\\}\\}", name);
		helper.setText(body, true);
		javaMailSender.send(msg);
	}

	public void sendOtpEmail(String from, String to, String subject, String name, String otp) throws Exception {
		MimeMessage msg = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(msg, true);
		helper.setTo(to);
		helper.setFrom(from);
		helper.setSubject(subject);
		File file = ResourceUtils.getFile("classpath:template/otpEmail.html");
		String body = readFromInputStream(new FileInputStream(file));
		body = body.replaceAll("\\{\\{name\\}\\}", name);
		body = body.replaceAll("\\{\\{otp\\}\\}", otp);
		helper.setText(body, true);
		javaMailSender.send(msg);
	}

	public void sendCredsToUsersAddedByAdmin(String from, String to, String subject, String name, String password) throws Exception {
		MimeMessage msg = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(msg, true);
		helper.setTo(to);
		helper.setFrom(from);
		helper.setSubject(subject);
		File file = ResourceUtils.getFile("classpath:template/addUser.html");
		String body = readFromInputStream(new FileInputStream(file));
		body = body.replaceAll("\\{\\{name\\}\\}", name);
		body = body.replaceAll("\\{\\{email\\}\\}", to);
		body = body.replaceAll("\\{\\{password\\}\\}", password);
		helper.setText(body, true);
		javaMailSender.send(msg);
	}

	private String readFromInputStream(InputStream inputStream) throws IOException {
		StringBuilder resultStringBuilder = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			while ((line = br.readLine()) != null) {
				resultStringBuilder.append(line).append("\n");
			}
		}
		return resultStringBuilder.toString();
	}
	
	public UserConsent sendUserConsentEmail(User user, String subject, String formContent, UserConsent userConsent, String fileName, File patientSignatureFile, File parentSignatureFile, String age,String type) throws Exception {
		MimeMessage msg = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(msg, true);
		
		helper.setTo(user.getEmail());
		helper.setFrom(fromEmail);
		helper.setSubject(subject);
		File file = ResourceUtils.getFile("classpath:template/userConsentEmail.html");
		String body = readFromInputStream(new FileInputStream(file));
		body = body.replaceAll("\\{\\{name\\}\\}", user.getFirstName()+" "+user.getLastName());
		body = body.replaceAll("\\{\\{type\\}\\}", type.toLowerCase());

        formContent = formContent.replaceAll("\\{\\{date\\}\\}", userConsent.getDate())
								 .replaceAll("\\{\\{patientName\\}\\}", userConsent.getPatientName())
								// .replaceAll("\\{\\{patientSignature\\}\\}", patientSignatureFile.getPath())
								 .replaceAll("\\{\\{parentName\\}\\}", userConsent.getParentName())
								// .replaceAll("\\{\\{type\\}\\}", type.toLowerCase())
								 .replaceAll("\\{\\{age\\}\\}", age);
        if(parentSignatureFile != null) {
        	formContent = formContent.replaceAll("\\{\\{parentSignature\\}\\}", parentSignatureFile.getPath());
        }
        if(patientSignatureFile != null) {
        	formContent = formContent.replaceAll("\\{\\{patientSignature\\}\\}", patientSignatureFile.getPath());
        }
        
        File pdfFile = new File(fileName+".pdf");
        HtmlConverter.convertToPdf(formContent, new FileOutputStream(pdfFile));
        
        userConsent.setPdfFile(consentService.saveFile(pdfFile, user.getId().toString()));
        
        FileSystemResource attachmentFile = new FileSystemResource(pdfFile);
        helper.addAttachment(type.toLowerCase()+"_"+user.getFirstName()+user.getLastName().replace(" ", "")+".pdf", attachmentFile);
        helper.setText(body, true);
     
		javaMailSender.send(msg);
		pdfFile.delete();
		
		return userConsent;
	}
	
}
