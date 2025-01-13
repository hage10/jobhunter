package vn.trungtq.jobhunter.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.trungtq.jobhunter.util.SecurityUtil;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "companies")
@Getter
@Setter
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên công ty không được để trống")
    private String name;

    private String address;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    private String logo;

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    @JsonIgnore // bỏ danh sách user khi lấy danh sách công ty
    List<User> users;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    @JsonIgnore // bỏ danh sách công việc khi lấy danh sách công ty
    List<Job> jobs;

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
