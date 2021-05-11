package com.ucsf.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ucsf.auth.model.User;
import com.ucsf.model.ConsentForms;
import com.ucsf.model.UserConsent;
import com.ucsf.model.ConsentForms.ConsentType;
import com.ucsf.model.UserConsent.FormType;
import com.ucsf.model.UserMetadata;
import com.ucsf.payload.request.ConsentRequest;
import com.ucsf.repository.ConsentFormRepository;
import com.ucsf.repository.UserConsentRepository;
import com.ucsf.repository.UserMetaDataRepository;
import com.ucsf.service.AmazonClientService;
import com.ucsf.service.ConsentService;
import com.ucsf.service.EmailService;

@Service("consentService")
public class ConsentFormImpl implements ConsentService{

	@Autowired ConsentFormRepository consentFormRepository;
	@Autowired AmazonClientService amazonClientService;
	@Autowired private UserMetaDataRepository userMetaDataRepository;
	@Autowired private UserConsentRepository userConsentRepository;
	@Autowired EmailService emailService;
	
	@Override
	public List<ConsentForms> getConsent() {
		 List<ConsentForms> forms = (List<ConsentForms>) consentFormRepository.findAll();
		return forms;
	}

	@Override
	public ConsentForms getConsentFormByConsentType(ConsentType consentType) {
		return consentFormRepository.getConsentFormByConsentType(consentType);
	}
	
	@Override
	public void saveUserConsent(User user, ConsentRequest consent, UserMetadata userMetadata) {
		try {
			ConsentForms consentForm = null;
			UserConsent userConsent = new UserConsent();
			userConsent.setUserId(user.getId());
			userConsent.setDate(consent.getDate());
			userConsent.setPatientName(consent.getPatientName());
			
			String fileName = user.getId()+"_"+new Date().getTime();
			File patientSignatureFile = decodeBase64String(consent.getPatientSignature(), fileName+".jpeg");
			File parentSignatureFile = null;
			userConsent.setPatientSignature(saveFile(patientSignatureFile, user.getId().toString()));
			if (userMetadata.getAge() < 18) {
				userConsent.setParentName(consent.getParentName());
				parentSignatureFile = decodeBase64String(consent.getParentSignature(), fileName+".jpeg");
				userConsent.setParentSignature(saveFile(parentSignatureFile, user.getId().toString()));
				userConsent.setAge(consent.getAge());
				if (userMetadata.isConsentAccepted()) {
					userConsent.setConsentType(ConsentType.ASSENT_FORM);
					userConsent.setType(FormType.ASSENT);
					consentForm = getConsentFormByConsentType(ConsentType.ASSENT_FORM);
				} else {
					userConsent.setConsentType(ConsentType.CONSENT_FORM_FOR_BELOW_18);
					userConsent.setType(FormType.CONSENT);
					userMetadata.setConsentAccepted(true);
					consentForm = getConsentFormByConsentType(ConsentType.CONSENT_FORM_FOR_BELOW_18);
				}
	
			} else {
				userConsent.setConsentType(ConsentType.CONSENT_FORM_FOR_ABOVE_18);
				userConsent.setType(FormType.CONSENT);
				userMetadata.setConsentAccepted(true);
				consentForm = getConsentFormByConsentType(ConsentType.CONSENT_FORM_FOR_ABOVE_18);
			}
							
			userConsentRepository.save(userConsent);
			userMetaDataRepository.save(userMetadata);
					
			//send consent email to user
			userConsent = emailService.sendUserConsentEmail(user, "UCSF Consent", consentForm.getContent(), userConsent, fileName, patientSignatureFile, parentSignatureFile, consent.getAge());
			
			userConsentRepository.save(userConsent);
			
			patientSignatureFile.delete();
			if(parentSignatureFile != null) {
				parentSignatureFile.delete();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String saveFile(File file, String fileFolder) {
		try {
					
			String filePath = fileFolder+"/"+file.getName();
			amazonClientService.awsCreateFolder(fileFolder);
			amazonClientService.awsPutObject(file, filePath);
			
			return filePath;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private File decodeBase64String(String signatureImageUrl, String fileName) {
		File file = new File(fileName);
		byte[] decodedBytes = Base64.decodeBase64(signatureImageUrl);		
		try {
			FileUtils.writeByteArrayToFile(file, decodedBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}

}
