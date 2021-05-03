package com.ucsf.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ucsf.model.ConsentForms;
import com.ucsf.model.UserConsent;
import com.ucsf.payload.request.ConsentRequest;
import com.ucsf.repository.ConsentFormRepository;
import com.ucsf.repository.ConsentFormsRepository;
import com.ucsf.service.ConsentService;

@Service("consentService")
public class ConsentFormImpl implements ConsentService{

	@Autowired
	ConsentFormRepository consentFormRepository;
	
	@Autowired
	ConsentFormsRepository consentFormsRepository;
	
	@Override
	public void save(ConsentRequest consent) {
		UserConsent form = new UserConsent();
		
		consentFormRepository.save(form);
	}

	@Override
	public List<ConsentForms> getConsent() {
		 List<ConsentForms> forms = (List<ConsentForms>) consentFormsRepository.findAll();
		return forms;
	}

}
