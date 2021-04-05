package com.ucsf.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserController {

	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public ResponseEntity<?> createAuthenticationToken() throws Exception {
		return ResponseEntity.ok("success");
	}

}
