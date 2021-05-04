package com.ucsf.job;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ucsf.model.ConsentForms;
import com.ucsf.model.ConsentForms.ConsentType;
import com.ucsf.model.ConsentSection;
import com.ucsf.repository.ConsentFormRepository;
import com.ucsf.repository.ConsentSectionRepository;

@Service
public class LoadConsentFormData {
	
	@Autowired ConsentFormRepository consentFormRepository;
	@Autowired ConsentSectionRepository consentSectionRepository;

	//@Scheduled(cron="0 */1 * * * *") //please run this job first
	public void loadFormContent() {

		try {
			List<ConsentForms> forms = new ArrayList<>();
			for(String fileName : getResourceFiles("/consentForms/")) {
				System.out.println("saving... "+ fileName);
				
				File resource = new ClassPathResource("consentForms/"+fileName).getFile();
				String consent = new String(Files.readAllBytes(resource.toPath()));
				
				ConsentForms consentForm = new ConsentForms();
				if(fileName.toLowerCase().contains("assent")) {
					consentForm.setConsentType(ConsentType.ASSENT_FORM);
				} else if(fileName.toLowerCase().contains("parent")) {
					consentForm.setConsentType(ConsentType.CONSENT_FORM_FOR_BELOW_18);
				} else {
					consentForm.setConsentType(ConsentType.CONSENT_FORM_FOR_ABOVE_18);
				}
				
				consentForm.setContent(consent);
				consentForm.setFilePath("consentForms/"+fileName);
				forms.add(consentForm);
			}
			consentFormRepository.saveAll(forms);
			System.out.println("done");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//@Scheduled(cron="0 */1 * * * *")	//please run this job after loading form content
	public void loadSectionContent() {
		
		try {
			List<ConsentForms> forms = consentFormRepository.findAll();
			Map<ConsentType, Long> formsMap = new TreeMap<ConsentType, Long>();
			for(ConsentForms consentForm : forms) {
				formsMap.put(consentForm.getConsentType(), consentForm.getId());
			}
			
			List<ConsentSection> sections = new ArrayList<>();
			for(String sectionName : getResourceFiles("/sections/")) {
				System.out.println("saving... "+ sectionName);
				
				File sectionResource = new ClassPathResource("sections/"+sectionName).getFile();
				String section = new String(Files.readAllBytes(sectionResource.toPath()));

				ConsentSection consentSection = new ConsentSection();
				consentSection.setContent(section);
				consentSection.setFilePath("sections/"+sectionName);
				
				if(sectionName.toLowerCase().contains("assent")) {
					consentSection.setConsentFormId(formsMap.get(ConsentType.ASSENT_FORM));
				} else if(sectionName.toLowerCase().contains("parent")) {
					consentSection.setConsentFormId(formsMap.get(ConsentType.CONSENT_FORM_FOR_BELOW_18));
				} else {
					consentSection.setConsentFormId(formsMap.get(ConsentType.CONSENT_FORM_FOR_ABOVE_18));
				}
				
				sections.add(consentSection);
			}
			consentSectionRepository.saveAll(sections);
			System.out.println("done");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private List<String> getResourceFiles(String path) {
		List<String> filenames = new ArrayList<>();
		try {
			InputStream in = getResourceAsStream(path);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String resource;

			while ((resource = br.readLine()) != null) {
				filenames.add(resource);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(filenames);
		return filenames;
	}

	private InputStream getResourceAsStream(String resource) {
		
		final InputStream in = getContextClassLoader().getResourceAsStream(resource);
		return in == null ? getClass().getResourceAsStream(resource) : in;
	}

	private ClassLoader getContextClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
}
