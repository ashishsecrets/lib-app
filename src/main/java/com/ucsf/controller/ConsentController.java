package com.ucsf.controller;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ucsf.auth.model.User;
import com.ucsf.common.Constants;
import com.ucsf.common.ErrorCodes;
import com.ucsf.model.ConsentForms;
import com.ucsf.model.ConsentForms.ConsentType;
import com.ucsf.model.UserConsent;
import com.ucsf.model.UserConsent.FormType;
import com.ucsf.model.UserMetadata;
import com.ucsf.payload.request.ConsentRequest;
import com.ucsf.payload.response.ErrorResponse;
import com.ucsf.payload.response.SuccessResponse;
import com.ucsf.repository.UserConsentRepository;
import com.ucsf.repository.UserMetaDataRepository;
import com.ucsf.service.ConsentService;
import com.ucsf.service.LoggerService;
import com.ucsf.service.UserService;

@RestController
@RequestMapping("/api/consent")
public class ConsentController {

	@Autowired
	ConsentService consentService;

	@Autowired
	UserService userService;
	@Autowired
	private LoggerService loggerService;
	@Autowired
	private UserMetaDataRepository userMetaDataRepository;
	@Autowired
	private UserConsentRepository userConsentRepository;

	private static Logger log = LoggerFactory.getLogger(ConsentController.class);

	@GetMapping(value = "/getConsentForm")
	@ResponseBody
	public ResponseEntity<?> getConsentForm(HttpServletResponse response) {

		User user = null;
		JSONObject responseJson = new JSONObject();
		try {

			UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			if (userDetail != null && userDetail.getUsername() != null) {
				user = userService.findByEmail(userDetail.getUsername());
				loggerService.printLogs(log, "getConsentForm", "Getting user consent form for user " + user.getId());
			} else {
				loggerService.printLogs(log, "getConsentForm", "Invalid JWT signature.");
				responseJson.put("error", new ErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
						Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
				return new ResponseEntity(responseJson, HttpStatus.UNAUTHORIZED);
			}

			UserMetadata userMetadata = userMetaDataRepository.findByUserId(user.getId());

			if (userMetadata.getAge() == null && userMetadata.getAge() < 1) {
				loggerService.printLogs(log, "getConsentForm", "User age not specified.");
				responseJson.put("error", new ErrorResponse(ErrorCodes.USER_AGE_NOT_SPECIFIED.code(),
						Constants.USER_AGE_NOT_SPECIFIED.errordesc()));
				return new ResponseEntity(responseJson, HttpStatus.BAD_REQUEST);
			}

			ConsentForms consentForm = null;
			if (userMetadata.getAge() < 18) {
				if (userMetadata.isConsentAccepted()) {
					consentForm = consentService.getConsentFormByConsentType(ConsentType.ASSENT_FORM);
				} else {
					consentForm = consentService.getConsentFormByConsentType(ConsentType.CONSENT_FORM_FOR_BELOW_18);
				}
				responseJson.put("data", consentForm);
				return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
			} else {
				consentForm = consentService.getConsentFormByConsentType(ConsentType.CONSENT_FORM_FOR_ABOVE_18);
				responseJson.put("data", consentForm);
				return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
			}

		} catch (Exception e) {
			e.printStackTrace();
			loggerService.printErrorLogs(log, "getConsentForm", "Error while getting consent form. ");
			responseJson.put("error", new ErrorResponse(116, e.getMessage()));
			return new ResponseEntity(responseJson, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(value = "/saveUserConsent")
	@ResponseBody
	public ResponseEntity<?> saveUserConsent(@RequestBody ConsentRequest consent) {
		User user = null;
		JSONObject responseJson = new JSONObject();
		try {

			UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			if (userDetail != null && userDetail.getUsername() != null) {
				user = userService.findByEmail(userDetail.getUsername());
				loggerService.printLogs(log, "saveUserConsent", "Saving user consent for user " + user.getId());
			} else {
				loggerService.printLogs(log, "saveUserConsent", "Invalid JWT signature.");
				responseJson.put("error", new ErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
						Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
				return new ResponseEntity(responseJson, HttpStatus.UNAUTHORIZED);
			}

			UserMetadata userMetadata = userMetaDataRepository.findByUserId(user.getId());

			if (userMetadata.getAge() == null && userMetadata.getAge() < 1) {
				loggerService.printLogs(log, "saveUserConsent", "User age not specified.");
				responseJson.put("error", new ErrorResponse(ErrorCodes.USER_AGE_NOT_SPECIFIED.code(),
						Constants.USER_AGE_NOT_SPECIFIED.errordesc()));
				return new ResponseEntity(responseJson, HttpStatus.BAD_REQUEST);
			}

			UserConsent userConsent = new UserConsent();
			userConsent.setUserId(user.getId());
			userConsent.setDate(consent.getDate());
			if (userMetadata.getAge() < 18) {

				userConsent.setAdolescentName(consent.getParentName());
				userConsent.setParentName(consent.getParentName());
				if (userMetadata.isConsentAccepted()) {
					userConsent.setConsentType(ConsentType.ASSENT_FORM);
					userConsent.setType(FormType.ASSENT);
				} else {
					userConsent.setConsentType(ConsentType.CONSENT_FORM_FOR_BELOW_18);
					userConsent.setType(FormType.CONSENT);
					userMetadata.setConsentAccepted(true);
				}

			} else {
				userConsent.setPatientName(consent.getPatientName());
				userConsent.setConsentType(ConsentType.CONSENT_FORM_FOR_ABOVE_18);
				userConsent.setType(FormType.CONSENT);
				userMetadata.setConsentAccepted(true);
			}
			userConsentRepository.save(userConsent);
			userMetaDataRepository.save(userMetadata);

			responseJson.put("data", new SuccessResponse(true, "User consent saved successfully."));
			return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			loggerService.printErrorLogs(log, "saveUserConsent", "Error while saving user consent.");
			responseJson.put("error", new ErrorResponse(116, e.getMessage()));
			return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping(value = "/getConsent")
	@ResponseBody
	public ResponseEntity<?> getConsent() {
		JSONObject obj = new JSONObject();
		consentService.getConsent();
		obj.put("data", consentService.getConsent());
		return new ResponseEntity(obj.toMap(), HttpStatus.OK);
	}
}
