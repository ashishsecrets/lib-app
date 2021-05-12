package com.ucsf.service;

import java.io.File;
import java.util.List;

import com.ucsf.auth.model.User;
import com.ucsf.model.ConsentForms;
import com.ucsf.model.ConsentForms.ConsentType;
import com.ucsf.model.UserMetadata;
import com.ucsf.payload.request.ConsentRequest;

public interface ConsentService {
	//void save(ConsentRequest consent);
	List<ConsentForms> getConsent();
	ConsentForms getConsentFormByConsentType(ConsentType consentType);
	void saveUserConsent(User user, ConsentRequest consent, UserMetadata userMetadata);
	String saveFile(File pdfFile, String string);
}
