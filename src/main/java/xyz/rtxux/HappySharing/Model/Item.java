package xyz.rtxux.HappySharing.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.springframework.lang.Nullable;
import xyz.rtxux.HappySharing.Model.Audit.UserDateAudit;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Indexed
@Entity
@NoArgsConstructor
public class Item extends UserDateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Long id;

    @NotNull
    @Getter @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @NotBlank
    @Getter @Setter
    @Field
    private String name;

    @Lob
    @Getter @Setter
    @Field
    private String description;

    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    @Getter @Setter
    private List<ImageInfo> images = null;

    @Embedded
    @Getter @Setter
    private LocationInfo location;

    @Getter @Setter
    private double price;

    @Getter @Setter
    private Long duration;

    @Getter @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @Nullable
    @JoinColumn(name = "borrower_id")
    private User borrower;

    @Getter @Setter
    private Integer status;

    @Getter @Setter
    private boolean published;

    @Getter @Setter
    private boolean borrowerLocked;

    @Getter @Setter
    private boolean accepted;

    @Getter @Setter
    private boolean returned;

}
