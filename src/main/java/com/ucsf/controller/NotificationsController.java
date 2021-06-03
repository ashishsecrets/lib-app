package com.ucsf.controller;

import com.ucsf.auth.model.User;
import com.ucsf.common.Constants;
import com.ucsf.common.ErrorCodes;
import com.ucsf.model.Notifications;
import com.ucsf.payload.response.ErrorResponse;
import com.ucsf.payload.response.NotificationListResponse;
import com.ucsf.payload.response.SuccessResponse;
import com.ucsf.repository.UserRepository;
import com.ucsf.service.LoggerService;
import com.ucsf.service.NotificationsService;
import com.ucsf.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin
@RequestMapping("/api/notifications")
@Api(tags = "Notifications Controller")
public class NotificationsController {
	
	@Autowired
	NotificationsService notificationsService;

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	UserService userService;

	@Autowired
	LoggerService loggerService;

	private static final Logger log = LoggerFactory.getLogger(StudyImageController.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ApiOperation(value = "Get list of notifications", notes = "Get notifications list by user", code = 200, httpMethod = "GET", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "List of all notifications by user", response = NotificationListResponse.class) })
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@PreAuthorize("hasRole('PATIENT')")
	public ResponseEntity<?> getNotifications() {

		NotificationListResponse response = new NotificationListResponse();

		JSONObject responseJson = new JSONObject();

		User user = null;

		Boolean isSuccess = false;

		try {
			UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

			if (userDetail != null && userDetail.getUsername() != null) {
				String email = userDetail.getUsername();
				user = userRepository.findByEmail(email);
				isSuccess = true;

			} else {
				loggerService.printLogs(log, "notificationsService", "Invalid User");
				responseJson.put("error", new ErrorResponse(ErrorCodes.USER_NOT_FOUND.code(),
						Constants.USER_NOT_FOUND.errordesc()));
				return new ResponseEntity(responseJson.toMap(), HttpStatus.UNAUTHORIZED);
			}
		} catch (ClassCastException e) {
			e.printStackTrace();
		}

		if(!isSuccess){
			responseJson.put("error", new ErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
					Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
			return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
		}

		try {
			if(notificationsService.getListByUser(user) != null || !notificationsService.getListByUser(user).isEmpty()){
			response.setNotificationsList(notificationsService.getListByUser(user));
			responseJson.put("data", response);
			}
		else{
			responseJson.put("error", new ErrorResponse(ErrorCodes.NO_STUDY_FOUND.code(), Constants.NO_STUDY_FOUND.errordesc()));
			return new ResponseEntity(responseJson.toMap(), HttpStatus.NO_CONTENT);
				}
			} catch (NullPointerException e) {
			e.printStackTrace();
		}

		return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ApiOperation(value = "Get study team notifications", notes = "Get notifications list by studyteam", code = 200, httpMethod = "GET", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "List of all notifications by studyteam", response = NotificationListResponse.class) })
	@RequestMapping(value = "/listNotifications", method = RequestMethod.GET)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'STUDYTEAM','PHYSICIAN')")
	public ResponseEntity<?> getStudyTeamNotifications() {
		User user = null;
		JSONObject responseJson = new JSONObject();
		UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		if (userDetail != null && userDetail.getUsername() != null) {
			user = userService.findByEmail(userDetail.getUsername());
			loggerService.printLogs(log, "getAllPatients", "Saving user consent for user " + user.getId());
		} else {
			loggerService.printLogs(log, "getAllPatients", "Invalid JWT signature.");
			responseJson.put("error", new ErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
					Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
			return new ResponseEntity(responseJson.toMap(), HttpStatus.UNAUTHORIZED);
		}
		NotificationListResponse response = new NotificationListResponse();	
		 response.setNotificationsList(notificationsService.getListBySentTo("studyTeam"));
		responseJson.put("data", response);
		return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ApiOperation(value = "Mark Read/Unread Notification", notes = "Mark Read/Unread Notification", code = 200, httpMethod = "POST", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Mark Read/Unread Notification", response = SuccessResponse.class) })
	@RequestMapping(value = "/read-unread/{id}", method = RequestMethod.POST)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'STUDYTEAM','PHYSICIAN')")
	public ResponseEntity<?> updateNotificationsStatus(@PathVariable Long id,@RequestParam Boolean isRead) {
		JSONObject responseJson = new JSONObject();
		Notifications notification = notificationsService.updateStatus(id, isRead);
		if(notification != null) {
			responseJson.put("data", new SuccessResponse(true,"Notification status updated"));
		}
		else {
			responseJson.put("error", new ErrorResponse(118,"Error while updating notification status"));
		}
		return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);
	}

}
