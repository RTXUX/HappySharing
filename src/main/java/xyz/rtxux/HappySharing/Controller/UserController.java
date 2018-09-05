package xyz.rtxux.HappySharing.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.rtxux.HappySharing.Model.User;
import xyz.rtxux.HappySharing.Model.UserProfile;
import xyz.rtxux.HappySharing.Payload.ApiResponse;
import xyz.rtxux.HappySharing.Payload.UserProfileJson;
import xyz.rtxux.HappySharing.Repository.UserProfileRepository;
import xyz.rtxux.HappySharing.Repository.UserRepository;
import xyz.rtxux.HappySharing.Security.UserPrincipal;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;
    @RequestMapping("/checkUsernameAvailability")
    public ResponseEntity<ApiResponse> checkUsernameAvailability(@RequestParam("username") @Valid @Size(max=15,min = 3) String username) {
        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.ok(new ApiResponse(1, Map.of("message","Username Exists")));
        } else {

        }
        return ResponseEntity.ok(new ApiResponse(0,null));
    }
    @RequestMapping("/checkEmailAvailability")
    public ResponseEntity<ApiResponse> checkEmailAvailability(@RequestParam("email") @Valid @Email String email) {
        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.ok(new ApiResponse(1, Map.of("message","Email Exists")));
        } else {

        }
        return ResponseEntity.ok(new ApiResponse(0,null));
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long id) {
        var userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) return ResponseEntity.notFound().build();
        User user = userOpt.orElseThrow();
        var userProfileOpt = userProfileRepository.findByUser(user);
        if (!userProfileOpt.isPresent()) return ResponseEntity.notFound().build();
        var userProfile = userProfileOpt.orElseThrow();
        UserProfileJson userProfileJson = UserProfileJson.builder()
                .user_id(user.getId())
                .nickName(userProfile.getNickName())
                .description(userProfile.getDescription())
                .gender(userProfile.getGender())
                .phone(userProfile.getPhone())
                .others(null)
                .build();
        return ResponseEntity.ok(userProfileJson);
    }

    @PostMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    @Transactional
    public ResponseEntity<?> updateProfile(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody UserProfileJson userProfileJson) {
        /*if (!(userPrincipal.getId().equals(id)&&userProfileJson.getUser_id().equals(id))) {
            return ResponseEntity.badRequest().body(new ApiResponse(1, Map.of("message", "Id not matching")));
        }*/
        var userOpt = userRepository.findById(userPrincipal.getId());
        if (!userOpt.isPresent()) return ResponseEntity.badRequest().body(new ApiResponse(1, Map.of("message", "No such user")));
        var user = userOpt.get();
        UserProfile userProfile = UserProfile.builder()
                .user(user)
                .description(userProfileJson.getDescription())
                .gender(userProfileJson.getGender())
                .nickName(userProfileJson.getNickName())
                .others(null)
                .phone(userProfileJson.getPhone())
                .build();
        var userProfOpt = userProfileRepository.findByUser(user);
        /*if (userProfOpt.isPresent()) {
            userProfile.setId(userProfOpt.get().getId());
        }*/
        userProfile = userProfileRepository.save(userProfile);
        return ResponseEntity.ok(new ApiResponse(0,Map.of("message","Successfully updated user's profile")));

    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("profile")
    public ResponseEntity<?> getCurrentUserProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return getUserProfile(userPrincipal.getId());

    }


}
