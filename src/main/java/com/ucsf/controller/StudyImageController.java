package com.ucsf.controller;

import com.ucsf.auth.model.User;
import com.ucsf.common.Constants;
import com.ucsf.common.ErrorCodes;
import com.ucsf.model.StudyImages;
import com.ucsf.model.UserMetadata;
import com.ucsf.payload.request.BodyPartRequest;
import com.ucsf.payload.request.ConsentRequest;
import com.ucsf.payload.response.ErrorResponse;
import com.ucsf.payload.response.StudyBodyPartsResponse;
import com.ucsf.payload.response.StudyImageUrlData;
import com.ucsf.payload.response.SuccessResponse;
import com.ucsf.repository.UserMetaDataRepository;
import com.ucsf.repository.UserRepository;
import com.ucsf.service.ImageUrlService;
import com.ucsf.service.LoggerService;
import com.ucsf.service.UserService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/images")
@Api(tags = "Study-Image Controller")
public class StudyImageController {

    @Autowired
    ImageUrlService imageUrlService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private LoggerService loggerService;

    @Autowired
    private UserMetaDataRepository userMetaDataRepository;

    @Autowired
    UserService userService;

    private static final Logger log = LoggerFactory.getLogger(StudyImageController.class);

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@ApiOperation(value = "Get parts", notes = "Get body parts", code = 200, httpMethod = "GET", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Parts fetched successfully", response = StudyBodyPartsResponse.class) })
    @RequestMapping(value = "/get-body-parts/{studyId}", method = RequestMethod.GET)
    public ResponseEntity<?> saveScreeningAnswers(@PathVariable Long studyId) throws Exception {

        StudyBodyPartsResponse response = new StudyBodyPartsResponse();

        JSONObject responseJson = new JSONObject();

        Boolean isSuccess = false;

        User user = null;

        int totalCount = 0;

        try {
            UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (userDetail != null && userDetail.getUsername() != null) {
                String email = userDetail.getUsername();
                user = userRepository.findByEmail(email);
                isSuccess = true;

            } else {
                loggerService.printLogs(log, "imageUrlService", "Invalid User");
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

        List<StudyImageUrlData> newList = null;

        try {
            List<StudyImages> list = imageUrlService.getImageUrls(studyId, user.getId());


        newList = new ArrayList<>();

        if(list != null){
            for(StudyImages item : list){
                StudyImageUrlData data = new StudyImageUrlData(item.getId(), item.getName(), item.getCount());
                totalCount += item.getCount();
                newList.add(data);
            }
        }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if(newList != null){
            response.setList(newList);
            response.setTotalCount(totalCount);
        responseJson.put("data", response);
        }
        else{
            responseJson.put("error", new ErrorResponse(ErrorCodes.NO_STUDY_FOUND.code(), Constants.NO_STUDY_FOUND.errordesc()));
            return new ResponseEntity(responseJson, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity(responseJson.toMap(), HttpStatus.ACCEPTED);

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @PostMapping(value = "/save-part-image")
    @ResponseBody
    public ResponseEntity<?> savePartImage(@RequestBody BodyPartRequest request) {
        User user = null;
        JSONObject responseJson = new JSONObject();
        try {

            UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            if (userDetail != null && userDetail.getUsername() != null) {
                user = userService.findByEmail(userDetail.getUsername());
                loggerService.printLogs(log, "savePartImage", "Saving user body part image " + user.getEmail());
            } else {
                loggerService.printLogs(log, "savePartImage", "Invalid JWT signature.");
                responseJson.put("error", new ErrorResponse(ErrorCodes.INVALID_AUTHORIZATION_HEADER.code(),
                        Constants.INVALID_AUTHORIZATION_HEADER.errordesc()));
                return new ResponseEntity(responseJson.toMap(), HttpStatus.UNAUTHORIZED);
            }


            imageUrlService.saveImage(user, request);

            responseJson.put("data", new SuccessResponse(true, "User body part image saved successfully."));
            return new ResponseEntity(responseJson.toMap(), HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            loggerService.printErrorLogs(log, "savePartImage", "Error while saving user image.");
            responseJson.put("error", new ErrorResponse(116, e.getMessage()));
            return new ResponseEntity(responseJson.toMap(), HttpStatus.BAD_REQUEST);
        }
    }
}
