package com.flashcard.flashcard.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flashcard.flashcard.entity.Deck;
import com.flashcard.flashcard.entity.User;
import com.flashcard.flashcard.exception.UsernameAlreadyExistsException;
import com.flashcard.flashcard.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    
    @Autowired
    private DeckService deckService;

    public User saveUser (User newUser){
        try{
            newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
            newUser.setUsername(newUser.getUsername());
            newUser.setConfirmPassword("");
            return userRepository.save(newUser);

        }catch (Exception e){
            throw new UsernameAlreadyExistsException("Username '" + newUser.getUsername() + "' already exists");
        }
    }
    
    @Transactional
    public User updateUser (User user){
        try{
        	User updated = userRepository.getById(user.getId());
        	Iterable<Deck> decks = deckService.getDecks(updated.getUsername());
        	
        	System.out.println(updated.getUsername());
        	
        	if(user.getUsername() != "") {
        		updated.setUsername(user.getUsername());
        	}
        	
        	List<Deck> decksList = StreamSupport.stream(decks.spliterator(), false)
    			    .collect(Collectors.toList());
        	
        	for(Deck deck : decksList) {
        		deck.setUsername(updated.getUsername());
        	}
        	
        	updated.setDecks(decksList);
        	
        	if(user.getPassword() != "") {
        		updated.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        		updated.setConfirmPassword("");
        	}

            return userRepository.save(updated);

        }catch (Exception e){
            throw new UsernameAlreadyExistsException("Username '" + user.getUsername() + "' already exists");
        }
    }

}
