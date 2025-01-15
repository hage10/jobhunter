package vn.trungtq.jobhunter.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.trungtq.jobhunter.util.SecurityUtil;
import vn.trungtq.jobhunter.util.enums.ResumeStateEnum;

import java.time.Instant;

@Table(name = "resumes")
@Entity
@Getter
@Setter
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Url không được để trống(Upload CV chưa thành công)")
    private String url;

    @Enumerated(EnumType.STRING)
    private ResumeStateEnum status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

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
