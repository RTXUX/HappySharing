package xyz.rtxux.HappySharing.Util.Mapper;

import xyz.rtxux.HappySharing.Model.Item;
import xyz.rtxux.HappySharing.Model.UserMessage;
import xyz.rtxux.HappySharing.Payload.ItemJson;
import xyz.rtxux.HappySharing.Payload.UserMessageJson;

public class MyMapper {

    public static ItemJson.ItemJsonBuilder itemJsonFromItem(Item item) {
        return ItemJson.builder()
                .id(item.getId())
                .owner_id(item.getOwner().getId())
                .name(item.getName())
                .description(item.getDescription())
                .location(item.getLocation())
                .price(item.getPrice())
                .duration(item.getDuration())
                .status(itemStatusToInt(item));
    }

    public static Integer itemStatusToInt(Item item) {
        int status = 0;
        status|=item.isPublished()?1:0;
        status|=(item.isBorrowerLocked()?1:0)<<1;
        status|=(item.isAccepted()?1:0)<<2;
        status|=(item.isReturned()?1:0)<<3;
        return status;
    }

    public static UserMessageJson userMessageJsonFromUserMessage(UserMessage userMessage) {
        return UserMessageJson.builder()
                .from(userMessage.getFromUid())
                .id(userMessage.getId())
                .timestamp(userMessage.getTime().getEpochSecond())
                .message(userMessage.getMessage())
                .build();
    }
}
