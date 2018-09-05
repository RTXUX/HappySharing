package xyz.rtxux.HappySharing.Payload;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank
    @Getter
    @Setter
    private String usernameOrEmail;

    @NotBlank
    @Getter
    @Setter
    private String password;

}
