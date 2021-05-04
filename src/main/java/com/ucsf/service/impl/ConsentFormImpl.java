package com.ucsf.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ucsf.model.ConsentForms;
import com.ucsf.model.ConsentForms.ConsentType;
import com.ucsf.model.UserConsent;
import com.ucsf.payload.request.ConsentRequest;
import com.ucsf.repository.UserConsentRepository;
import com.ucsf.repository.ConsentFormRepository;
import com.ucsf.service.ConsentService;

@Service("consentService")
public class ConsentFormImpl implements ConsentService{

	//@Autowired
	//UserConsentRepository consentFormRepository;
	
	@Autowired
	ConsentFormRepository consentFormRepository;
	
	/*@Override
	public void save(ConsentRequest consent) {
		UserConsent form = new UserConsent();
		
		consentFormRepository.save(form);
	}*/

	@Override
	public List<ConsentForms> getConsent() {
		 List<ConsentForms> forms = (List<ConsentForms>) consentFormRepository.findAll();
		return forms;
	}

	@Override
	public ConsentForms getConsentFormByConsentType(ConsentType consentType) {
		return consentFormRepository.getConsentFormByConsentType(consentType);
	}

}
