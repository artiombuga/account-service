package account.service;

import account.enums.Action;
import account.model.Event;
import account.repository.EventRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class EventService {
    private final EventRepository eventRepository;
    private final HttpServletRequest request;

    public EventService(EventRepository eventRepository, HttpServletRequest request) {
        this.eventRepository = eventRepository;
        this.request = request;
    }

    public void logCreateUser(String email) {
        logEvent(Action.CREATE_USER, null, email);
    }

    public void logLoginFailed(String subject) {
        logEvent(Action.LOGIN_FAILED, subject, (request.getRequestURI().substring(request.getContextPath().length())));
    }

    public void logGrantRole(String adminEmail, String role, String userEmail) {
        logEvent(Action.GRANT_ROLE, adminEmail, "Grant role " + role + " to " + userEmail);
    }

    public void logRemoveRole(String adminEmail, String role, String userEmail) {
        logEvent(Action.REMOVE_ROLE, adminEmail, "Remove role " + role + " from " + userEmail);
    }

    public void logDeleteUser(String adminEmail, String userEmail) {
        logEvent(Action.DELETE_USER, adminEmail, userEmail);
    }

    public void logChangePassword(String email) {
        logEvent(Action.CHANGE_PASSWORD, email, email);
    }

    public void logAccessDenied(String email) {
        logEvent(Action.ACCESS_DENIED, email, (request.getRequestURI().substring(request.getContextPath().length())));
    }

    public void logBruteForce(String email) {
        logEvent(Action.BRUTE_FORCE, email, (request.getRequestURI().substring(request.getContextPath().length())));
    }

    public void logLockUser(String email) {
        logEvent(Action.LOCK_USER, email, "Lock user " + email);
    }

    public void logUnlockUser(String adminEmail, String userEmail) {
        logEvent(Action.UNLOCK_USER, adminEmail, "Unlock user " + userEmail);
    }

    public void logEvent(Action action, String subject, String object) {
        Event event = new Event();
        event.setAction(action.toString());
        event.setSubject(subject == null ? "Anonymous" : subject);
        event.setObject(object);
        event.setPath(request.getRequestURI().substring(request.getContextPath().length()));
        eventRepository.save(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
}
