package xyz.rtxux.HappySharing.Model.Audit;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@JsonIgnoreProperties(
        value = {"createdBy", "updatedBy"},
        allowGetters = true
)
public class UserDateAudit extends DateAudit {
    @CreatedBy
    @Column(updatable = false)
    @Getter
    @Setter
    private Long createdBy;

    @LastModifiedBy
    @Getter
    @Setter
    private Long updatedBy;

}
