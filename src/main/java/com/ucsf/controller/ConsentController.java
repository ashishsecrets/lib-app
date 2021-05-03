package com.ucsf.controller;

import java.io.File;
import java.nio.file.Files;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ucsf.payload.request.ConsentRequest;
import com.ucsf.service.ConsentService;

@RestController
@RequestMapping("/api/consent")
public class ConsentController {

	@Autowired
	ConsentService consentService;

	@GetMapping(value = "/getConsentForm", produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String getConsentForm(HttpServletResponse response) {
		try {

			File resource = new ClassPathResource("consentForms/SkinTrackerAssentForm.html").getFile();
			String consent = new String(Files.readAllBytes(resource.toPath()));
			return consent;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	@PostMapping(value = "/saveConsentForm")
	@ResponseBody
	public ResponseEntity<?> saveConsentForm(ConsentRequest consent) {
		consentService.save(consent);
		return null;
	}
	
	@GetMapping(value = "/getConsent")
	@ResponseBody
	public ResponseEntity<?> getConsent() {
		JSONObject obj = new JSONObject();
		consentService.getConsent();
		obj.put("data", consentService.getConsent());
		return new ResponseEntity(obj.toMap(), HttpStatus.OK);
		//return consentService.getConsent();
	}
}
