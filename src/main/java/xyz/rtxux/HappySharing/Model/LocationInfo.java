package xyz.rtxux.HappySharing.Model;

import lombok.Data;

import javax.persistence.Embeddable;

@Embeddable
@Data
public class LocationInfo {
    private double longitude;
    private double latitude;
    private String locationDescription;
}
