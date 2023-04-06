package za.co.cajones.bankx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.messaging.FirebaseMessagingException;

import lombok.extern.slf4j.Slf4j;
import za.co.cajones.bankx.model.Note;
import za.co.cajones.bankx.service.MessageService;

@Slf4j
@RestController
@RequestMapping("notifications")
public class MessageController {

    @Autowired
    private MessageService messageService;

    private Note note;

	@ResponseBody
	public String sendNotification(@RequestBody Note note,
	                               @RequestParam String token) throws FirebaseMessagingException {
	    log.debug("Notification: " + note.toString());
		return messageService.sendNotification(note, token);
	}

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<Exception> handleAllExceptions(RuntimeException ex) {
        return new ResponseEntity<>(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
