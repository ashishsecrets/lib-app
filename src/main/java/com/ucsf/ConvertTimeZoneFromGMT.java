package com.ucsf;

import java.io.File;
import java.io.FileInputStream;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.ResourceUtils;

class testemailattachemnt {
	
	@Autowired
	JavaMailSender javaMailSender;
	
	public static void main(String[] args) {
		
		}

}


