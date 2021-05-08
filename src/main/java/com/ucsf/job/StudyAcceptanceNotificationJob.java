package com.ucsf.job;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.Date;
import com.ucsf.auth.model.User;
import com.ucsf.model.UserMetadata;
import com.ucsf.model.UserMetadata.StudyAcceptanceNotification;
import com.ucsf.model.UserMetadata.StudyStatus;
import com.ucsf.payload.request.Note;
import com.ucsf.repository.UserMetaDataRepository;
import com.ucsf.repository.UserRepository;
import com.ucsf.service.EmailService;
import com.ucsf.service.LoggerService;
import com.ucsf.service.PushNotificationService;
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

	@Autowired
	PushNotificationService pushNotificationService;

	private static Logger log = LoggerFactory.getLogger(StudyAcceptanceNotificationJob.class);

	 @Scheduled(cron="0 */1 * * * *")
	public void sendNotifications() {
		loggerService.printLogs(log, "sendNotifications",
				"Job started for sending study approval notifications " + new Date());
		Optional<User> user = null;
		User approvedUser = null;
		User disApprovedUser = null;
		List<UserMetadata> userMetaData = userMetaDataRepository.findByStudyStatus(StudyStatus.APPROVED);
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
					metaData.setStudyStatus(StudyStatus.ENROLLED);
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
					Note note = new Note();
					// Todo Dynamic Study name
					note.setContent(
							"Dear " + approvedUser.getFirstName() + "Your Eczema Tracking Study has been Approved");
					note.setSubject("Study Confirmation");
					String msgId = pushNotificationService.sendNotification(note, approvedUser.getDevideId());
					metaData.setNotifiedBy(StudyAcceptanceNotification.NOTIFIED_BY_PUSH);
					metaData.setStudyStatus(StudyStatus.ENROLLED);
					userMetaDataRepository.save(metaData);
					loggerService.printLogs(log, "sendNotifications", "Study approval push notification sent to user: "
							+ approvedUser.getEmail() + "At: " + new Date() + "msgId = " + msgId);
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
					metaData.setStudyStatus(StudyStatus.ENROLLED);
					userMetaDataRepository.save(metaData);
					loggerService.printLogs(log, "sendNotifications",
							"Study approval SMS sent to user: " + approvedUser.getEmail() + "phoneNumber: "
									+ approvedUser.getPhoneCode() + approvedUser.getPhoneNumber() + "At: "
									+ new Date());
				} catch (Exception e) {
					loggerService.printErrorLogs(log, "sendNotifications",
							"Error while sending study approval SMS to user: " + approvedUser.getEmail()
									+ "phoneNumber: " + approvedUser.getPhoneCode() + approvedUser.getPhoneNumber()
									+ "At: " + new Date());
				}
			}
		}

		List<UserMetadata> userMetaData2 = userMetaDataRepository.findByStudyStatus(StudyStatus.DISAPPROVED);
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
					metaData.setStudyStatus(StudyStatus.DISQUALIFIED);
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
					Note note = new Note();
					// Todo Dynamic Study name
					note.setContent(
							"Dear " + approvedUser.getFirstName() + "Your Eczema Tracking Study has been disApproved");
					note.setSubject("Study Confirmation");
					String msgId = pushNotificationService.sendNotification(note, approvedUser.getDevideId());
					metaData.setNotifiedBy(StudyAcceptanceNotification.NOTIFIED_BY_PUSH);
					metaData.setStudyStatus(StudyStatus.DISQUALIFIED);
					userMetaDataRepository.save(metaData);
					loggerService.printLogs(log, "sendNotifications",
							"Study disApproval push notification sent to user: " + disApprovedUser.getEmail() + "At: "
									+ new Date() + "msgId = " + msgId);
				} catch (Exception e) {
					loggerService.printErrorLogs(log, "sendNotifications",
							"Error while sending study disApproval push notification to user: "
									+ disApprovedUser.getEmail() + "At: " + new Date());
					System.out.println("1111");
				}
				// Notify by SMS
				try {
					Twilio.init(accoundSid, authToken);
					Message.creator(new PhoneNumber(disApprovedUser.getPhoneCode() + disApprovedUser.getPhoneNumber()),
							new PhoneNumber(twilioNumber), "Your UCSF study disApproved").create();
					metaData.setNotifiedBy(StudyAcceptanceNotification.NOTIFIED_BY_SMS);
					metaData.setStudyStatus(StudyStatus.DISQUALIFIED);
					userMetaDataRepository.save(metaData);
					loggerService.printLogs(log, "sendNotifications",
							"Study disApproval SMS sent to user: " + disApprovedUser.getEmail() + "phoneNumber: "
									+ disApprovedUser.getPhoneCode() + disApprovedUser.getPhoneNumber() + "At: "
									+ new Date());
				} catch (Exception e) {
					loggerService.printErrorLogs(log, "sendNotifications",
							"Error while sending study disApproval SMS to user: " + disApprovedUser.getEmail()
									+ "phoneNumber: " + disApprovedUser.getPhoneCode()
									+ disApprovedUser.getPhoneNumber() + "At: " + new Date());
				}
			}
		}

	}

}
