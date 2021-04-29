package com.ucsf.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

@Service
public class EmailService {

	@Autowired
	JavaMailSender javaMailSender;

	@Value("${web.site.url}")
	String webSiteUrl;

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
}
