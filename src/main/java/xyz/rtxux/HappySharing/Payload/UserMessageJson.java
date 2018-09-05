package xyz.rtxux.HappySharing.Payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserMessageJson {

    private Long id;
    private Long from;
    private Long timestamp;
    private String message;
}
