package com.ucsf.service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import com.ucsf.model.Notifications;
import com.ucsf.repository.NotificationsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.ucsf.auth.model.User;
import com.ucsf.model.UserMetadata;
import com.ucsf.model.UserScreeningStatus;
import com.ucsf.model.UserMetadata.StudyAcceptanceNotification;
import com.ucsf.model.UserMetadata.StudyStatus;
import com.ucsf.model.UserScreeningStatus.UserScreenStatus;
import com.ucsf.payload.request.Note;
import com.ucsf.repository.UserMetaDataRepository;
import com.ucsf.repository.UserRepository;
import com.ucsf.repository.UserScreeningStatusRepository;

@Service
public class StudyNotificationService {

	private static Logger log = LoggerFactory.getLogger(StudyNotificationService.class);
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

	@Autowired
	NotificationsRepository notificationsRepository;

	@Value("${spring.mail.from}")
	String fromEmail;

	@Value("${twilio.account.sid}")
	String accoundSid;

	@Value("${twilio.auth.token}")
	String authToken;

	@Value("${twilio.number}")
	String twilioNumber;

	@Autowired
	UserScreeningStatusRepository userScreeningStatusRepository;

	public void sendApproveNotifications(Long userId) {
		loggerService.printLogs(log, "sendApproveNotifications", "sendApproveNotifications " + new Date());
		Optional<User> user = null;
		User approvedUser = null;
		user = userRepository.findById(userId);
		if (user.isPresent()) {
			approvedUser = user.get();
		}
		UserScreeningStatus userStatus = userScreeningStatusRepository.findByUserId(userId);
		if (userStatus != null) {
			try {
				emailService.sendStudyApprovalEmail(fromEmail, approvedUser.getEmail(), "Study Approval From Skin Tracker Team",
						approvedUser.getFirstName() + " " + approvedUser.getLastName());
				//metaData.setNotifiedBy(StudyAcceptanceNotification.NOTIFIED_BY_EMAIL);
				userStatus.setUserScreeningStatus(UserScreenStatus.ENROLLED);
				userScreeningStatusRepository.save(userStatus);
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
				note.setContent("Dear " + approvedUser.getFirstName() + " Your Eczema Tracking Study has been Approved");
				note.setSubject("Study Confirmation");
				note.setType("approval");
				Map<String, String> data = new TreeMap<String, String>();
				data.put("type", "approval");
				data.put("2", "value2");

				note.setData(data);
				String token = approvedUser.getDevideId();
				String msgId = pushNotificationService.sendNotification(note,token);

				//Adding sent notification to db
				Notifications notification = new Notifications();
				notification.setDate(new Date());
				notification.setDescription(note.getContent());
				notification.setType(Notifications.NotificationType.PUSH);
				notification.setKind(Notifications.NotificationKind.APPROVEDINSTUDY);
				notification.setKindDescription("approved-study-1");
				notification.setUserId(approvedUser.getId());
				notificationsRepository.save(notification);

				//metaData.setNotifiedBy(StudyAcceptanceNotification.NOTIFIED_BY_PUSH);
				userStatus.setUserScreeningStatus(UserScreenStatus.ENROLLED);
				userScreeningStatusRepository.save(userStatus);
				loggerService.printLogs(log, "sendNotifications", "Study approval push notification sent to user: "
						+ approvedUser.getEmail() + "At: " + new Date() + "msgId = " + msgId);
			} catch (Exception e) {
				System.out.println("Push Notification Error "+ e.getMessage());
				loggerService.printErrorLogs(log, "sendNotifications",
						"Error while sending study approval push notification to user: " + approvedUser.getEmail()
								+ "At: " + new Date());
			}
			// Notify by SMS
			try {
				Twilio.init(accoundSid, authToken);
				Message.creator(new PhoneNumber(approvedUser.getPhoneCode() + approvedUser.getPhoneNumber()),
						new PhoneNumber(twilioNumber), "Your Eczema Tracking Study has been approved").create();
				//metaData.setNotifiedBy(StudyAcceptanceNotification.NOTIFIED_BY_SMS);
				userStatus.setUserScreeningStatus(UserScreenStatus.ENROLLED);
				userScreeningStatusRepository.save(userStatus);
				loggerService.printLogs(log, "sendNotifications",
						"Study approval SMS sent to user: " + approvedUser.getEmail() + "phoneNumber: "
								+ approvedUser.getPhoneCode() + approvedUser.getPhoneNumber() + "At: " + new Date());
			} catch (Exception e) {
				loggerService.printErrorLogs(log, "sendNotifications",
						"Error while sending study approval SMS to user: " + approvedUser.getEmail() + "phoneNumber: "
								+ approvedUser.getPhoneCode() + approvedUser.getPhoneNumber() + "At: " + new Date());
			}
		}
	}

	public void sendDisapproveNotifications(Long userId) {
		loggerService.printLogs(log, "sendDisapproveNotifications", "sendDisapproveNotifications " + new Date());
		Optional<User> user = null;
		User disApprovedUser = null;
		user = userRepository.findById(userId);
		if (user.isPresent()) {
			disApprovedUser = user.get();
		}
		UserScreeningStatus userStatus = userScreeningStatusRepository.findByUserId(userId);
		if (userStatus != null) {
			try {
				emailService.sendStudyDisApprovalEmail(fromEmail, disApprovedUser.getEmail(),
						"Study Disapproval From Skin Tracker Team",
						disApprovedUser.getFirstName() + " " + disApprovedUser.getLastName());
				///metaData.setNotifiedBy(StudyAcceptanceNotification.NOTIFIED_BY_EMAIL);
				userStatus.setUserScreeningStatus(UserScreenStatus.NOT_ELIGIBLE);
				userScreeningStatusRepository.save(userStatus);
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
						"Dear " + disApprovedUser.getFirstName() + " Your Eczema Tracking Study has been disapproved");
				note.setSubject("Study Confirmation");
				Map<String, String> data = new TreeMap<String, String>();
				data.put("type", "disapproval");
				data.put("2", "value2");

				note.setData(data);
				note.setType("disapproval");
				String msgId = pushNotificationService.sendNotification(note, disApprovedUser.getDevideId());

				//Adding sent notification to db
				Notifications notification = new Notifications();
				notification.setDate(new Date());
				notification.setDescription(note.getContent());
				notification.setType(Notifications.NotificationType.PUSH);
				notification.setKind(Notifications.NotificationKind.DISAPPROVEDINSTUDY);
				notification.setDescription("disapproved-study-1");
				notification.setUserId(disApprovedUser.getId());
				notificationsRepository.save(notification);

				//userStatus.setNotifiedBy(StudyAcceptanceNotification.NOTIFIED_BY_PUSH);
				userStatus.setUserScreeningStatus(UserScreenStatus.NOT_ELIGIBLE);
				userScreeningStatusRepository.save(userStatus);
				loggerService.printLogs(log, "sendNotifications", "Study disApproval push notification sent to user: "
						+ disApprovedUser.getEmail() + "At: " + new Date() + "msgId = " + msgId);
			} catch (Exception e) {
				System.out.println("Push Notification Error "+ e.getMessage());
				loggerService.printErrorLogs(log, "sendNotifications",
						"Error while sending study disApproval push notification to user: " + disApprovedUser.getEmail()
								+ "At: " + new Date());
			}
			// Notify by SMS
			try {
				Twilio.init(accoundSid, authToken);
				Message.creator(new PhoneNumber(disApprovedUser.getPhoneCode() + disApprovedUser.getPhoneNumber()),
						new PhoneNumber(twilioNumber), "Your Eczema Tracking Study disapproved").create();
				//metaData.setNotifiedBy(StudyAcceptanceNotification.NOTIFIED_BY_SMS);
				userStatus.setUserScreeningStatus(UserScreenStatus.NOT_ELIGIBLE);
				userScreeningStatusRepository.save(userStatus);
				loggerService.printLogs(log, "sendNotifications",
						"Study disApproval SMS sent to user: " + disApprovedUser.getEmail() + "phoneNumber: "
								+ disApprovedUser.getPhoneCode() + disApprovedUser.getPhoneNumber() + "At: "
								+ new Date());
			} catch (Exception e) {
				loggerService.printErrorLogs(log, "sendNotifications",
						"Error while sending study disapproval SMS to user: " + disApprovedUser.getEmail()
								+ "phoneNumber: " + disApprovedUser.getPhoneCode() + disApprovedUser.getPhoneNumber()
								+ "At: " + new Date());
			}
		}
	}
}
