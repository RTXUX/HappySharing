package xyz.rtxux.HappySharing.Model;


import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class ImageDatabaseStorage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private byte[] data;
}
