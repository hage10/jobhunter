package vn.trungtq.jobhunter.domain.response;

import lombok.Getter;
import lombok.Setter;
import vn.trungtq.jobhunter.util.enums.LevelEnum;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class ResCreateJobDTO {
    private Long id;

    private String name;

    private String location;

    private double salary;

    private int quantity;

    private LevelEnum level;

    private String description;

    private Instant startDate;

    private Instant endDate;

    private Boolean active;

    private List<String> skills;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;
}
