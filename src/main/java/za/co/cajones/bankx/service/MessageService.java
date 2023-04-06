package za.co.cajones.bankx.service;

import com.google.firebase.messaging.FirebaseMessagingException;

import za.co.cajones.bankx.model.Note;


public interface MessageService {


	String sendNotification(Note note, String token) throws FirebaseMessagingException;

}