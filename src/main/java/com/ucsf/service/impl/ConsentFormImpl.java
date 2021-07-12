package com.ucsf.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.ucsf.model.*;
import com.ucsf.repository.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ucsf.auth.model.User;
import com.ucsf.model.ConsentForms.ConsentType;
import com.ucsf.model.UserConsent.FormType;
import com.ucsf.payload.request.ConsentRequest;
import com.ucsf.service.ConsentService;
import com.ucsf.service.EmailService;

@Service("consentService")
public class ConsentFormImpl implements ConsentService{

	@Autowired ConsentFormRepository consentFormRepository;
	@Autowired private UserMetaDataRepository userMetaDataRepository;
	@Autowired private UserConsentRepository userConsentRepository;
	@Autowired EmailService emailService;
	@Autowired SurveyRepository surveyRepository;
	@Autowired UserTasksRepository userTasksRepository;
	@Autowired UserScreeningStatusRepository userScreeningStatusRepository;
	
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
			File patientSignatureFile = null;
			File parentSignatureFile = null;
			
			if (userMetadata.getAge() < 18) {
				userConsent.setParentName(consent.getParentName());
				userConsent.setAge(consent.getAge());
				
				if (userMetadata.isConsentAccepted()) {
					patientSignatureFile = decodeBase64String(consent.getPatientSignature(), fileName+".jpeg");
					userConsent.setPatientSignature(saveFile(patientSignatureFile, user.getId().toString()));
					userConsent.setConsentType(ConsentType.ASSENT_FORM);
					userConsent.setType(FormType.ASSENT);
					consentForm = getConsentFormByConsentType(ConsentType.ASSENT_FORM);
					UserScreeningStatus screeningStatus = userScreeningStatusRepository.findByUserId(user.getId());
					screeningStatus.setUserScreeningStatus(UserScreeningStatus.UserScreenStatus.TERMS_ACCEPTED);
					userScreeningStatusRepository.save(screeningStatus);
				} else {
					parentSignatureFile = decodeBase64String(consent.getParentSignature(), fileName+".jpeg");
					userConsent.setParentSignature(saveFile(parentSignatureFile, user.getId().toString()));
					userConsent.setConsentType(ConsentType.CONSENT_FORM_FOR_BELOW_18);
					userConsent.setType(FormType.CONSENT);
					userMetadata.setConsentAccepted(true);
					consentForm = getConsentFormByConsentType(ConsentType.CONSENT_FORM_FOR_BELOW_18);
				}
	
			} else {
				patientSignatureFile = decodeBase64String(consent.getPatientSignature(), fileName+".jpeg");
				userConsent.setPatientSignature(saveFile(patientSignatureFile, user.getId().toString()));
				userConsent.setConsentType(ConsentType.CONSENT_FORM_FOR_ABOVE_18);
				userConsent.setType(FormType.CONSENT);
				userMetadata.setConsentAccepted(true);
				consentForm = getConsentFormByConsentType(ConsentType.CONSENT_FORM_FOR_ABOVE_18);
				UserScreeningStatus screeningStatus = userScreeningStatusRepository.findByUserId(user.getId());
				screeningStatus.setUserScreeningStatus(UserScreeningStatus.UserScreenStatus.TERMS_ACCEPTED);
				userScreeningStatusRepository.save(screeningStatus);
			}
							
			userConsentRepository.save(userConsent);
			userMetaDataRepository.save(userMetadata);
					
			//send consent email to user
			userConsent = emailService.sendUserConsentEmail(user, "Skin Tracker "+userConsent.getType().toString().toLowerCase()+" Form", consentForm.getContent(), userConsent, fileName, patientSignatureFile, parentSignatureFile, consent.getAge(),userConsent.getType().toString());

			userConsentRepository.save(userConsent);

		

			//To-do - make this section dynamic with values from repositories.

			List<UserTasks> userTasksList = userTasksRepository.findByUserId(user.getId());
           System.out.println("userTasksList "+userTasksList);
			if(userTasksList == null || userTasksList.isEmpty()) {
				 System.out.println("userTasksList1 "+userTasksList);
				Date startDate = new Date();

				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.WEEK_OF_MONTH, 1);
				Date endDate = calendar.getTime();

				Calendar calendar2 = Calendar.getInstance();
				calendar2.add(Calendar.WEEK_OF_MONTH, 4);
				Date endDate2 = calendar.getTime();
				//System.out.println(endDate);

				List<UcsfSurvey> list = new ArrayList<>();
				list.add(surveyRepository.findByTitle("POEM"));
				list.add(surveyRepository.findByTitle("DLQI"));
				list.add(surveyRepository.findByTitle("NRS itch"));


				for (UcsfSurvey item : list) {

					UserTasks task = new UserTasks();
					task.setTitle(item.getTitle());
					task.setDescription(item.getDescription());
					task.setTaskType("survey");
					task.setStartDate(startDate);
					task.setEndDate(endDate);
					task.setDuration(1);
					task.setWeekCount(1);
					task.setProgress(0);
					task.setUserId(user.getId());
					task.setStudyId(item.getStudyId());
					task.setTaskId(item.getId());

					userTasksRepository.save(task);

				}

				UserTasks photographs = new UserTasks();
				photographs.setTitle("Photographs");
				photographs.setDescription("images");
				photographs.setTaskType("photos");
				photographs.setStartDate(startDate);
				photographs.setEndDate(endDate2);
				photographs.setUserId(user.getId());
				photographs.setDuration(4);
				photographs.setWeekCount(1);
				photographs.setStudyId(1l);
				photographs.setTaskId(user.getId() + 1000);
				photographs.setProgress(0);
				userTasksRepository.save(photographs);
				/*photographs = tasksRepository.findById(tasksRepository.findByTitle("Photographs").getId()).get();
				photographs.setTaskId(photographs.getId() + 1000);
				tasksRepository.save(photographs);*/

				UserTasks voice = new UserTasks();
				voice.setTitle("Voice diary");
				voice.setDescription("Voice Recordings");
				voice.setTaskType("voice");
				voice.setStartDate(startDate);
				voice.setEndDate(endDate);
				voice.setUserId(user.getId());
				voice.setDuration(1);
				voice.setWeekCount(1);
				voice.setStudyId(1l);
				voice.setTaskId(user.getId() + 1000 + 1);
				voice.setProgress(0);
				userTasksRepository.save(voice);

				UserTasks medicine = new UserTasks();
				medicine.setTitle("Medication usage");
				medicine.setDescription("Medicine usage");
				medicine.setTaskType("medicine");
				medicine.setStartDate(startDate);
				medicine.setEndDate(endDate);
				medicine.setUserId(user.getId());
				medicine.setDuration(1);
				medicine.setWeekCount(1);
				medicine.setStudyId(1l);
				medicine.setTaskId(user.getId() + 1000 + 2);
				medicine.setProgress(0);
				userTasksRepository.save(medicine);

				UserTasks reactions = new UserTasks();
				reactions.setTitle("Adverse events");
				reactions.setDescription("Side Effects");
				reactions.setTaskType("reactions");
				reactions.setStartDate(startDate);
				reactions.setEndDate(endDate);
				reactions.setUserId(user.getId());
				reactions.setDuration(1);
				reactions.setWeekCount(1);
				reactions.setStudyId(1l);
				reactions.setTaskId(user.getId() + 1000 + 3);
				reactions.setProgress(0);
				userTasksRepository.save(reactions);

			}

			if(patientSignatureFile != null)
				patientSignatureFile.delete();
			if(parentSignatureFile != null)
				parentSignatureFile.delete();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String saveFile(File file, String fileFolder) {
		try {
					
			String filePath = "src/main/resources/userConsentForms/"+fileFolder+"/"+file.getName();
			try {
				String path = "src/main/resources/userConsentForms/"+fileFolder+"/";
				Files.createDirectories(Paths.get(path));
				Path temp = Files.copy
						(Paths.get(file.getAbsolutePath()),
								(Paths.get("src/main/resources/userConsentForms/"+fileFolder+"/"+file.getName())));

			}catch(Exception e) {
				System.out.println("error");
			}

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
