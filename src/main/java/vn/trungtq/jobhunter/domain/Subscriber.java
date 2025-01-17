package vn.trungtq.jobhunter.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.trungtq.jobhunter.util.SecurityUtil;

import java.time.Instant;
import java.util.List;

@Table(name = "subscribers")
@Entity
@Getter
@Setter
public class Subscriber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name không được để trống")
    private String name;

    @NotBlank(message = "email không được để trống")
    private String email;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "subscribers" })
    @JoinTable(name = "subscriber_skill", joinColumns = @JoinColumn(name =
            "subscriber_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private List<Skill> skills;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    @PrePersist
    protected void onCreate() {
        String createdBy = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : null;
        this.setCreatedBy(createdBy);
        this.setCreatedAt(Instant.now());
    }
    @PreUpdate
    protected void onUpdate() {
        String updateBy = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : null;
        this.setUpdatedBy(updateBy);
        this.setUpdatedAt(Instant.now());
    }
}
