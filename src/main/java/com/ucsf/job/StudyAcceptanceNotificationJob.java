package com.ucsf.job;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.Date;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsNotification;
import com.notnoop.apns.ApnsService;
import com.ucsf.auth.model.User;
import com.ucsf.model.UserMetadata;
import com.ucsf.model.UserMetadata.StudyAcceptanceNotification;
import com.ucsf.repository.UserMetaDataRepository;
import com.ucsf.repository.UserRepository;
import com.ucsf.service.EmailService;
import com.ucsf.service.LoggerService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@EnableAutoConfiguration
@EnableScheduling
@Service
public class StudyAcceptanceNotificationJob {

	@Value("${spring.mail.from}")
	String fromEmail;

	@Value("${twilio.account.sid}")
	String accoundSid;

	@Value("${twilio.auth.token}")
	String authToken;

	@Value("${twilio.number}")
	String twilioNumber;

	@Autowired
	UserMetaDataRepository userMetaDataRepository;

	@Autowired
	EmailService emailService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	private LoggerService loggerService;

	@Autowired
	Environment env;

	private static Logger log = LoggerFactory.getLogger(StudyAcceptanceNotificationJob.class);

	// @Scheduled(cron="0 */1 * * * *")
	public void sendNotifications() {
		loggerService.printLogs(log, "sendNotifications",
				"Job started for sending study approval notifications " + new Date());
		Optional<User> user = null;
		User approvedUser = null;
		User disApprovedUser = null;
		List<UserMetadata> userMetaData = userMetaDataRepository.findByStudyStatus("approved");
		if (userMetaData != null && userMetaData.size() > 0) {
			for (UserMetadata metaData : userMetaData) {
				user = userRepository.findById(metaData.getUserId());
				if (user.isPresent()) {
					approvedUser = user.get();
				}
				// Notify by Email
				try {
					emailService.sendStudyApprovalEmail(fromEmail, approvedUser.getEmail(),
							"Study Approval From UCSF Team",
							approvedUser.getFirstName() + " " + approvedUser.getLastName());
					metaData.setNotifiedBy(StudyAcceptanceNotification.NOTIFIED_BY_EMAIL);
					userMetaDataRepository.save(metaData);
					loggerService.printLogs(log, "sendNotifications",
							"Study approval mail sent to user: " + approvedUser.getEmail() + "At: " + new Date());
				} catch (Exception e) {
					loggerService.printErrorLogs(log, "sendNotifications",
							"Error while sending study approval mail to user: " + approvedUser.getEmail() + "At: "
									+ new Date());
				}
				// Notify By Push Notification
				try {
					ApnsService service;
					InputStream inputStream = null;
					try {
						inputStream = new ClassPathResource("NewEczemPushp12.p12").getInputStream();// add certificate
																									// in resource field
					} catch (IOException e) {
						loggerService.printErrorLogs(log, "sendNotifications",
								"Error while getting input stream from push notification certificate "
										+ approvedUser.getEmail() + "At: " + new Date());
					}

					if (!Arrays.asList(env.getActiveProfiles()).contains("pro")) {
						System.out.println("push running with Prod environment");

						service = APNS.newService().withCert(inputStream, "123456").withProductionDestination().build();
					} else {
						System.out.println("push running with Local environment");

						service = APNS.newService().withCert(inputStream, "123456").withSandboxDestination().build();
					}
					String payload = APNS.newPayload().customField("customData", "").alertBody("").alertTitle("")
							.customField("Type", "Survey").build();

					ApnsNotification apns = service.push(approvedUser.getDevideId(), payload);
					metaData.setNotifiedBy(StudyAcceptanceNotification.NOTIFIED_BY_PUSH);
					userMetaDataRepository.save(metaData);
					loggerService.printLogs(log, "sendNotifications", "Study approval push notification sent to user: "
							+ approvedUser.getEmail() + "At: " + new Date());
				} catch (Exception e) {
					loggerService.printErrorLogs(log, "sendNotifications",
							"Error while sending study approval push notification to user: " + approvedUser.getEmail()
									+ "At: " + new Date());
				}
				// Notify by SMS
				try {
					Twilio.init(accoundSid, authToken);
					Message.creator(new PhoneNumber(approvedUser.getPhoneCode() + approvedUser.getPhoneNumber()),
							new PhoneNumber(twilioNumber), "Your UCSF study approved").create();
					metaData.setNotifiedBy(StudyAcceptanceNotification.NOTIFIED_BY_SMS);
					userMetaDataRepository.save(metaData);
					loggerService.printLogs(log, "sendNotifications",
							"Study approval SMS sent to user: " + approvedUser.getEmail()
									+ "phoneNumber: " + approvedUser.getPhoneCode() + approvedUser.getPhoneNumber()
									+ "At: " + new Date());
				} catch (Exception e) {
					loggerService.printErrorLogs(log, "sendNotifications",
							"Error while sending study approval SMS to user: " + approvedUser.getEmail()
									+ "phoneNumber: " + approvedUser.getPhoneCode() + approvedUser.getPhoneNumber()
									+ "At: " + new Date());
				}
			}
		}
		
		List<UserMetadata> userMetaData2 = userMetaDataRepository.findByStudyStatus("disapproved");
		if (userMetaData2 != null && userMetaData2.size() > 0) {
			for (UserMetadata metaData : userMetaData2) {
				user = userRepository.findById(metaData.getUserId());
				if (user.isPresent()) {
					disApprovedUser = user.get();
				}
				// Notify by Email
				try {
					emailService.sendStudyDisApprovalEmail(fromEmail, disApprovedUser.getEmail(),
							"Study DisApproval From UCSF Team",
							disApprovedUser.getFirstName() + " " + disApprovedUser.getLastName());
					metaData.setNotifiedBy(StudyAcceptanceNotification.NOTIFIED_BY_EMAIL);
					userMetaDataRepository.save(metaData);
					loggerService.printLogs(log, "sendNotifications",
							"Study approval mail sent to user: " + disApprovedUser.getEmail() + "At: " + new Date());
				} catch (Exception e) {
					loggerService.printErrorLogs(log, "sendNotifications",
							"Error while sending study DisApproval mail to user: " + disApprovedUser.getEmail() + "At: "
									+ new Date());
				}
				// Notify By Push Notification
				try {
					ApnsService service;
					InputStream inputStream = null;
					try {
						inputStream = new ClassPathResource("Certificates.p12").getInputStream();// add certificate
																									// in resource field
					} catch (IOException e) {
						loggerService.printErrorLogs(log, "sendNotifications",
								"Error while getting input stream from push notification certificate "
										+ disApprovedUser.getEmail() + "At: " + new Date());
					}

					if (!Arrays.asList(env.getActiveProfiles()).contains("pro")) {
						System.out.println("push running with Prod environment");

						service = APNS.newService().withCert(inputStream, "123456").withProductionDestination().build();
					} else {
						System.out.println("push running with Local environment");

						service = APNS.newService().withCert(inputStream, "123456").withSandboxDestination().build();
					}
					String payload = APNS.newPayload().customField("customData", "").alertBody("Hi "+disApprovedUser.getFirstName()+" Your UCSF study has been disapproved").alertTitle("UCSF Study Status").build();

					ApnsNotification apns = service.push(disApprovedUser.getDevideId(), payload);
					metaData.setNotifiedBy(StudyAcceptanceNotification.NOTIFIED_BY_PUSH);
					userMetaDataRepository.save(metaData);
					loggerService.printLogs(log, "sendNotifications", "Study disApproval push notification sent to user: "
							+ disApprovedUser.getEmail() + "At: " + new Date());
				} catch (Exception e) {
					loggerService.printErrorLogs(log, "sendNotifications",
							"Error while sending study disApproval push notification to user: " + disApprovedUser.getEmail()
									+ "At: " + new Date());
				}
				// Notify by SMS
				try {
					Twilio.init(accoundSid, authToken);
					Message.creator(new PhoneNumber(disApprovedUser.getPhoneCode() + disApprovedUser.getPhoneNumber()),
							new PhoneNumber(twilioNumber), "Your UCSF study disApproved").create();
					metaData.setNotifiedBy(StudyAcceptanceNotification.NOTIFIED_BY_SMS);
					userMetaDataRepository.save(metaData);
					loggerService.printLogs(log, "sendNotifications",
							"Study disApproval SMS sent to user: " + disApprovedUser.getEmail()
									+ "phoneNumber: " + disApprovedUser.getPhoneCode() + disApprovedUser.getPhoneNumber()
									+ "At: " + new Date());
				} catch (Exception e) {
					loggerService.printErrorLogs(log, "sendNotifications",
							"Error while sending study disApproval SMS to user: " + disApprovedUser.getEmail()
									+ "phoneNumber: " + disApprovedUser.getPhoneCode() + disApprovedUser.getPhoneNumber()
									+ "At: " + new Date());
				}
			}
		}
	}

}
