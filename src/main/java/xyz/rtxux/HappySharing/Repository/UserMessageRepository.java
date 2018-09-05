package xyz.rtxux.HappySharing.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.rtxux.HappySharing.Model.UserMessage;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMessageRepository extends JpaRepository<UserMessage, Long> {

    List<UserMessage> findAllByToUser(Long toUser);

    List<UserMessage> findAllByFromUid(Long fromUid);

    List<UserMessage> findAllByFromUidAndToUser(Long from, Long toUser);
}
