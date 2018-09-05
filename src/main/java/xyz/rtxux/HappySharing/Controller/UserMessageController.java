package xyz.rtxux.HappySharing.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.rtxux.HappySharing.Exception.AppException;
import xyz.rtxux.HappySharing.Exception.BadRequestException;
import xyz.rtxux.HappySharing.Model.User;
import xyz.rtxux.HappySharing.Model.UserMessage;
import xyz.rtxux.HappySharing.Payload.UserMessageJson;
import xyz.rtxux.HappySharing.Repository.UserRepository;
import xyz.rtxux.HappySharing.Security.UserPrincipal;
import xyz.rtxux.HappySharing.Service.UserMessageService;
import xyz.rtxux.HappySharing.Util.Mapper.MyMapper;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/message")
public class UserMessageController {

    @Autowired
    private UserMessageService userMessageService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<UserMessageJson> getMyMessages(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        var user = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new AppException("You seems to be Authorized but user not found"));
        return userMessageService.findAllMessageToUser(user.getId()).stream().map(MyMapper::userMessageJsonFromUserMessage).collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping(params = {"from"})
    public List<UserMessageJson> getMyMessageFrom(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam Long from) {
        var user = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new AppException("You seems to be Authorized but user not found"));
        return userMessageService.findAllMessageFromUserToUser(from, user.getId()).stream().map(MyMapper::userMessageJsonFromUserMessage).collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping(params = {"peer"})
    public List<UserMessageJson> getMessagePeeredWith(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam Long peer) {
        Long user1, user2;
        user1 = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new AppException("You seems to be Authorized but user not found")).getId();
        if (peer!=0L) {
            user2 = userRepository.findById(peer).orElseThrow(() -> new BadRequestException("Peer user not found")).getId();
        } else {
            user2 = 0L;
        }
        return Stream.of(userMessageService.findAllMessageFromUserToUser(user1,user2),userMessageService.findAllMessageFromUserToUser(user2,user1))
                .flatMap(Collection::parallelStream)
                .map(MyMapper::userMessageJsonFromUserMessage)
                .collect(Collectors.toList());
    }
}
