package vn.trungtq.jobhunter.domain.dto.response;

import lombok.Getter;
import lombok.Setter;
import vn.trungtq.jobhunter.util.enums.GenderEnum;

import java.time.Instant;

@Getter
@Setter
public class ResUpdateUserDTO {
    private long id;
    private String name;
    private String email;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant createdAt;
    private Instant updatedAt;
    private String CreateBy;
    private String UpdateBy;

}
