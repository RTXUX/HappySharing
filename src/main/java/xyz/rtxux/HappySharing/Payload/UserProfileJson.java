package xyz.rtxux.HappySharing.Payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileJson {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long user_id;
    private String nickName;
    private String phone;
    private String gender;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String,Object> others;
}
