package com.flashcard.flashcard.rest;

import static com.flashcard.flashcard.security.SecurityConstants.TOKEN_PREFIX;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flashcard.flashcard.entity.User;
import com.flashcard.flashcard.payload.JWTLoginSucessReponse;
import com.flashcard.flashcard.payload.LoginRequest;
import com.flashcard.flashcard.security.JwtTokenProvider;
import com.flashcard.flashcard.service.UserService;
import com.flashcard.flashcard.service.ValidationService;
import com.flashcard.flashcard.validator.UpdateUserValidator;
import com.flashcard.flashcard.validator.UserValidator;

@RestController
@RequestMapping("/api/users")
//To enable cross-domain communication (apply to all methods)
@CrossOrigin
public class UserController {

    @Autowired
    private ValidationService validationService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserValidator userValidator;
    
    @Autowired
    private UpdateUserValidator updateUserValidator;
    
    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult result){
        ResponseEntity<?> errorMap = validationService.validate(result);
        if(errorMap != null) return errorMap;

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = TOKEN_PREFIX +  tokenProvider.generateToken(authentication);

        return ResponseEntity.ok(new JWTLoginSucessReponse(true, jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user, BindingResult result){
        // Validate passwords match
    	userValidator.validate(user, result);

        ResponseEntity<?> errorMap = validationService.validate(result);
        if(errorMap != null) return errorMap;

        User newUser = userService.saveUser(user);

        return  new ResponseEntity<User>(newUser, HttpStatus.CREATED);
    }
    
    @PatchMapping("")
    public ResponseEntity<?> updateUser(@Valid @RequestBody User user, BindingResult result){
    	
    	if(user.getUsername() == "" && user.getPassword() == "" && user.getConfirmPassword() == "") {
    		return null;
    	}
    	
    	BindingResult newBindingResult = new BeanPropertyBindingResult(user, "user");
    	
    	if(user.getUsername() != "") {
    		for (FieldError error : result.getFieldErrors()) {
    			if(error.getField().equals("username")) {
    				newBindingResult.addError(result.getFieldError("username"));
    				break;
    			}
			}
    	}
    	
    	updateUserValidator.validate(user, newBindingResult);
    	
    	ResponseEntity<?> errorMap = validationService.validate(newBindingResult);
        if(errorMap != null) return errorMap;
        
        System.out.println(user);

        User updatedUser = userService.updateUser(user);

        return  new ResponseEntity<User>(updatedUser, HttpStatus.OK);    
    }
}
