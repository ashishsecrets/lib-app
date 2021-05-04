package com.ucsf.controller;

import com.ucsf.auth.model.User;
import com.ucsf.common.Constants;
import com.ucsf.common.ErrorCodes;
import com.ucsf.model.StudyImages;
import com.ucsf.payload.response.ErrorResponse;
import com.ucsf.payload.response.StudyBodyPartsResponse;
import com.ucsf.repository.UserRepository;
import com.ucsf.service.ImageUrlService;
import com.ucsf.service.LoggerService;
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

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/images")
@Api(tags = "Screening-Answer Controller")
public class StudyImageController {

    @Autowired
    ImageUrlService imageUrlService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private LoggerService loggerService;

    private static final Logger log = LoggerFactory.getLogger(StudyImageController.class);

    @ApiOperation(value = "Get parts", notes = "Get body parts", code = 200, httpMethod = "GET", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Parts fetched successfully", response = StudyBodyPartsResponse.class) })
    @RequestMapping(value = "/get-body-parts/{studyId}", method = RequestMethod.GET)
    public ResponseEntity<?> saveScreeningAnswers(@PathVariable Long studyId) throws Exception {

        StudyBodyPartsResponse response = new StudyBodyPartsResponse();

        JSONObject responseJson = new JSONObject();

        User user = null;

        try {
            UserDetails userDetail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (userDetail != null && userDetail.getUsername() != null) {
                String email = userDetail.getUsername();
                user = userRepository.findByEmail(email);

            } else {
                loggerService.printLogs(log, "imageUrlService", "Invalid User");
                responseJson.put("error", new ErrorResponse(ErrorCodes.USER_NOT_FOUND.code(),
                        Constants.USER_NOT_FOUND.errordesc()));
                return new ResponseEntity(responseJson, HttpStatus.UNAUTHORIZED);
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        List<StudyImages> list =  imageUrlService.getImageUrls(studyId, user.getId());

        if(!list.isEmpty()){
        responseJson.put("data", list);
        }
        else{
            responseJson.put("error", new ErrorResponse(ErrorCodes.NO_STUDY_FOUND.code(), Constants.NO_STUDY_FOUND.errordesc()));
            return new ResponseEntity(responseJson, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity(responseJson.toMap(), HttpStatus.ACCEPTED);

    }
}
