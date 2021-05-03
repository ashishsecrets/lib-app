package com.ucsf.service;

import java.util.List;

import com.ucsf.model.ConsentForms;
import com.ucsf.payload.request.ConsentRequest;

public interface ConsentService {
	void save(ConsentRequest consent);
	List<ConsentForms> getConsent();

}
