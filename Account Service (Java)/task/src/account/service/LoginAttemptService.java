package account.service;

import account.model.User;
import account.repository.UserRepository;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {

    public static final int MAX_ATTEMPT = 4;
    private LoadingCache<String, Integer> attemptsCache;

    @Autowired
    private HttpServletRequest request;

    private final EventService eventService;
    private final UserRepository userRepository;

    public LoginAttemptService(EventService eventService, UserRepository userRepository) {
        super();
        this.eventService = eventService;
        this.userRepository = userRepository;
        attemptsCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.DAYS)
                .build(new CacheLoader<>() {
                    @Override
                    public Integer load(final String key) {
                        return 0;
                    }
                });
    }

    public void loginFailed(final String userEmail) {
        eventService.logLoginFailed(userEmail);
        Optional<User> userOptional = userRepository.findUserByEmailIgnoreCase(userEmail);
        if (userOptional.isEmpty()) return;
        User user = userOptional.get();

        if (user.isAdmin() || !user.isNonLocked()) return;

        int attempts = attemptsCache.asMap().getOrDefault(userEmail, 0);
        attempts++;
        attemptsCache.put(userEmail, attempts);

        if (attempts > MAX_ATTEMPT) {
            eventService.logBruteForce(userEmail);
            user.setNonLocked(false);
            eventService.logLockUser(userEmail);
            userRepository.save(user);
        }
    }

    public void cleanAttempts(final String userEmail) {
        attemptsCache.put(userEmail, 0);
    }
}