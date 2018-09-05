package xyz.rtxux.HappySharing.Model;

import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.rtxux.HappySharing.Model.Audit.UserDateAudit;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@NoArgsConstructor
@Data
public class ImageInfo extends UserDateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private ImageStorageType imageStorageType;

    private String storageIdentity;

}


