package xyz.rtxux.HappySharing.Payload;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse {
    private int code;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data;

}
