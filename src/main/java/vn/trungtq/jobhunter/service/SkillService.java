package vn.trungtq.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.trungtq.jobhunter.domain.Company;
import vn.trungtq.jobhunter.domain.Skill;
import vn.trungtq.jobhunter.domain.User;
import vn.trungtq.jobhunter.domain.response.ResultPaginationDTO;
import vn.trungtq.jobhunter.repository.SkillRepository;

import java.util.List;
import java.util.Optional;

@Service
public class SkillService {
    private final SkillRepository skillRepository;
    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill handleCreateSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public boolean checkNameExist(String name) {
        return this.skillRepository.existsByName(name);
    }
    public Skill handleGetSkill(long id) {
        return this.skillRepository.findById(id).orElse(null);
    }
    public Skill handleUpdateSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }
    public ResultPaginationDTO handleGetAllSkills(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> pageSkills = skillRepository.findAll(spec,pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotal(pageSkills.getTotalElements());
        meta.setPages(pageSkills.getTotalPages());
        rs.setMeta(meta);
        rs.setResult(pageSkills.getContent());
        return rs;
    }
    public void handleDeleteSkill(long id) {
        Optional<Skill> skill = this.skillRepository.findById(id);
        if (skill.isPresent()) {
            Skill skillToDelete = skill.get();
            skillToDelete.getJobs().forEach(job -> job.getSkills().remove(skillToDelete));
            skillToDelete.getSubscribers().forEach(subs -> subs.getSkills().remove(skillToDelete));

        }
        this.skillRepository.deleteById(id);
    }
}
