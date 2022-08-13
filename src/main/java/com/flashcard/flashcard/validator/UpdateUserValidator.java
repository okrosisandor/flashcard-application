package com.flashcard.flashcard.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.flashcard.flashcard.entity.User;

@Component
public class UpdateUserValidator implements Validator{

	@Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object object, Errors errors) {

        User user = (User) object;

        if(user.getPassword().length() != 0 && user.getPassword().length() < 6){
            errors.rejectValue("password","Length", "Password must be at least 6 characters");
        }

        if((user.getPassword().length() != 0 || user.getConfirmPassword().length() != 0) && !user.getPassword().equals(user.getConfirmPassword())){
            errors.rejectValue("confirmPassword","Match", "Passwords must match");
        }

    }

}
