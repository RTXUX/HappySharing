package xyz.rtxux.HappySharing.Payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.rtxux.HappySharing.Model.LocationInfo;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemJson {
    public ItemJson(String name, String description, LocationInfo location, double price, Integer status) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.price = price;
        this.status = status;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    private Long owner_id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long borrower_id;
    private String name;
    private String description;
    private LocationInfo location;
    private double price;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Long> images;

    private Long duration;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer status;

    public ItemJson(Long id, Long owner_id, String name, String description, LocationInfo location, double price, Long duration, Integer status) {
        this.id = id;
        this.owner_id = owner_id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.price = price;
        this.duration = duration;
        this.status = status;
    }
}
