package vn.trungtq.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.trungtq.jobhunter.domain.Job;
import vn.trungtq.jobhunter.domain.Skill;
import vn.trungtq.jobhunter.domain.response.ResCreateJobDTO;
import vn.trungtq.jobhunter.domain.response.ResUpdateJobDTO;
import vn.trungtq.jobhunter.domain.response.ResultPaginationDTO;
import vn.trungtq.jobhunter.repository.JobRepository;
import vn.trungtq.jobhunter.repository.SkillRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    public JobService(JobRepository jobRepository, SkillRepository skillRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
    }

    public ResCreateJobDTO handleCreateJob(Job job) {
        if(job.getSkills() != null){
            List<Long> reqSkills = job.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            job.setSkills(dbSkills);
        }
        Job newJob = this.jobRepository.save(job);
        //convert response
        ResCreateJobDTO resCreateJobDTO = new ResCreateJobDTO();
        resCreateJobDTO.setId(newJob.getId());
        resCreateJobDTO.setName(newJob.getName());
        resCreateJobDTO.setDescription(newJob.getDescription());
        resCreateJobDTO.setLevel(newJob.getLevel());
        resCreateJobDTO.setLocation(newJob.getLocation());
        resCreateJobDTO.setActive(newJob.getActive());
        resCreateJobDTO.setQuantity(newJob.getQuantity());
        resCreateJobDTO.setSalary(newJob.getSalary());
        resCreateJobDTO.setEndDate(newJob.getEndDate());
        resCreateJobDTO.setStartDate(newJob.getStartDate());
        resCreateJobDTO.setCreatedAt(newJob.getCreatedAt());
        resCreateJobDTO.setCreatedBy(newJob.getCreatedBy());
        if(newJob.getSkills() != null){
            List<String> skill = newJob.getSkills()
                    .stream().map(s->s.getName()).collect(Collectors.toList());
            resCreateJobDTO.setSkills(skill);
        }
        return resCreateJobDTO;
    }

    public boolean checkNameExist(String name) {
        return this.jobRepository.existsByName(name);
    }
    public Job handleGetJob(long id) {
        return this.jobRepository.findById(id).orElse(null);
    }
    public ResUpdateJobDTO handleUpdateJob(Job job) {
        if(job.getSkills() != null){
            List<Long> reqSkills = job.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            job.setSkills(dbSkills);
        }
        Job updatedJob = this.jobRepository.save(job);
        ResUpdateJobDTO resUpdateJobDTO = new ResUpdateJobDTO();
        resUpdateJobDTO.setId(updatedJob.getId());
        resUpdateJobDTO.setName(updatedJob.getName());
        resUpdateJobDTO.setDescription(updatedJob.getDescription());
        resUpdateJobDTO.setLevel(updatedJob.getLevel());
        resUpdateJobDTO.setLocation(updatedJob.getLocation());
        resUpdateJobDTO.setActive(updatedJob.getActive());
        resUpdateJobDTO.setQuantity(updatedJob.getQuantity());
        resUpdateJobDTO.setSalary(updatedJob.getSalary());
        resUpdateJobDTO.setEndDate(updatedJob.getEndDate());
        resUpdateJobDTO.setStartDate(updatedJob.getStartDate());
        resUpdateJobDTO.setUpdatedAt(updatedJob.getUpdatedAt());
        resUpdateJobDTO.setUpdatedBy(updatedJob.getUpdatedBy());
        if(updatedJob.getSkills() != null){
            List<String> skills = updatedJob.getSkills()
                    .stream().map(s->s.getName()).collect(Collectors.toList());
            resUpdateJobDTO.setSkills(skills);
        }
        return  resUpdateJobDTO;
    }
    public ResultPaginationDTO handleGetAllJobs(Specification<Job> spec, Pageable pageable) {
        Page<Job> pageJobs = jobRepository.findAll(spec,pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotal(pageJobs.getTotalElements());
        meta.setPages(pageJobs.getTotalPages());
        rs.setMeta(meta);
        rs.setResult(pageJobs.getContent());
        return rs;
    }
    public void handleDeleteJob(long id) {
        this.jobRepository.deleteById(id);
    }
}
