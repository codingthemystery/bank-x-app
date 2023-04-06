package za.co.cajones.bankx.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import lombok.AllArgsConstructor;
import za.co.cajones.bankx.model.Note;
import za.co.cajones.bankx.service.MessageService;

@AllArgsConstructor
@Service
public class FirebaseMessagingServiceImpl implements MessageService{

	// Bean created at startup (
	@Autowired
	private final FirebaseMessaging firebaseMessaging;


	@Override
	public String sendNotification(Note note, String token) throws FirebaseMessagingException {

		Notification notification = Notification.builder().setTitle(note.getSubject()).setBody(note.getContent())
				.build();

		Message message = Message.builder().setToken(token).setNotification(notification).putAllData(note.getData())
				.build();

		return firebaseMessaging.send(message);
	}

}
