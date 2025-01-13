package vn.trungtq.jobhunter.domain.response;

import lombok.Getter;
import lombok.Setter;
import vn.trungtq.jobhunter.util.enums.GenderEnum;

import java.time.Instant;

@Getter
@Setter
public class ResCreateUserDTO {
    private long id;
    private String name;
    private String email;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant createdAt;
    private String CreateBy;
    private CompanyUser company;

    @Getter
    @Setter
    public static class CompanyUser{
        private long id;
        private String name;
    }

}
