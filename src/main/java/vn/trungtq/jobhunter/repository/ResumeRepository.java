package vn.trungtq.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.web.bind.annotation.RestController;
import vn.trungtq.jobhunter.domain.Resume;
import vn.trungtq.jobhunter.domain.Skill;

import java.util.List;

@RestController
public interface ResumeRepository extends JpaRepository<Resume, Long>, JpaSpecificationExecutor<Resume> {
    List<Resume> findByIdIn(List<Long> id);
}
