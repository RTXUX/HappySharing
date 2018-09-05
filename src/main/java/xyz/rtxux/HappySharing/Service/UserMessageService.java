package xyz.rtxux.HappySharing.Service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.rtxux.HappySharing.Model.UserMessage;
import xyz.rtxux.HappySharing.Repository.UserMessageRepository;

import java.time.Instant;
import java.util.List;

@Service
public class UserMessageService {

    @Autowired
    private UserMessageRepository userMessageRepository;

    @Transactional
    public UserMessage sendMessageFromSystemTo(Long user, String message) {
        UserMessage userMessage = UserMessage.builder()
                .fromUid(0L)
                .toUser(user)
                .time(Instant.now())
                .message(message)
                .build();
        return userMessageRepository.save(userMessage);
    }


    public List<UserMessage> findAllMessageToUser(Long user) {
        return userMessageRepository.findAllByToUser(user);
    }

    public List<UserMessage> findAllMessageFromUserToUser(Long from, Long to) {
        return userMessageRepository.findAllByFromUidAndToUser(from, to);
    }

}
