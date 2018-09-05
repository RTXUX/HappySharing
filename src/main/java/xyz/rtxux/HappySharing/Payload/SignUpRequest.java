package xyz.rtxux.HappySharing.Payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Map;

public class SignUpRequest {

    @NotBlank
    @Size(min = 3, max = 15)
    @Getter
    @Setter
    private String username;

    @NotBlank
    @Size(max = 40)
    @Email
    @Getter
    @Setter
    private String email;

    @NotBlank
    @Size(min = 6, max = 20)
    @Getter
    @Setter
    private String password;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter @Setter
    private Map<String, Object> details;
}
