package vn.trungtq.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.trungtq.jobhunter.domain.Skill;
import vn.trungtq.jobhunter.domain.response.ResultPaginationDTO;
import vn.trungtq.jobhunter.service.SkillService;
import vn.trungtq.jobhunter.util.anotation.ApiMessage;
import vn.trungtq.jobhunter.util.error.IdInvalidException;

import java.util.Optional;

@RestController
public class SkillController {
    private final SkillService skillService;
    public SkillController(final SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage("Create a new skill")
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody Skill skill) throws IdInvalidException {
        boolean isEmailExist = this.skillService.checkNameExist(skill.getName());
        if (isEmailExist) {
            throw  new IdInvalidException(
                    "Skill " + skill.getName() + " đã tồn tại"
            );
        }
        Skill newSkill = this.skillService.handleCreateSkill(skill);
        return ResponseEntity.status(HttpStatus.CREATED). body(newSkill);
    }
    @PutMapping("/skills")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill skill) throws IdInvalidException {
        Skill curSkill = this.skillService.handleGetSkill(skill.getId());
        if (curSkill == null) {
            throw  new IdInvalidException(
                    "Skill không tồn tại"
            );
        }
        boolean isEmailExist = this.skillService.checkNameExist(skill.getName());
        if (isEmailExist) {
            throw  new IdInvalidException(
                    "Skill " + skill.getName() + " đã tồn tại"
            );
        }
        curSkill.setName(skill.getName());
        Skill updatedSkill = this.skillService.handleUpdateSkill(curSkill);
        return ResponseEntity.status(HttpStatus.OK).body(updatedSkill);
    }
    @GetMapping("/skills")
    public ResponseEntity<ResultPaginationDTO> getAllSkill(@Filter Specification<Skill> spec, Pageable pageable){
        ResultPaginationDTO rs = this.skillService.handleGetAllSkills(spec,pageable);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }
    @DeleteMapping("/skills/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable("id") long id)  throws IdInvalidException {
        Skill curSkill = this.skillService.handleGetSkill(id);
        if (curSkill == null) {
            throw  new IdInvalidException(
                    "Skill không tồn tại"
            );
        }
        this.skillService.handleDeleteSkill(id);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/skills/{id}")
    public ResponseEntity<Skill> getSkill(@PathVariable("id") long id) throws IdInvalidException {
        Skill curSkill = this.skillService.handleGetSkill(id);
        if (curSkill == null) {
            throw  new IdInvalidException(
                    "Skill không tồn tại"
            );
        }
        return ResponseEntity.status(HttpStatus.OK).body(curSkill);
    }

}
