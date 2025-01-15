package vn.trungtq.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.trungtq.jobhunter.domain.Job;
import vn.trungtq.jobhunter.domain.Resume;
import vn.trungtq.jobhunter.domain.Resume;
import vn.trungtq.jobhunter.domain.User;
import vn.trungtq.jobhunter.domain.response.ResultPaginationDTO;
import vn.trungtq.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.trungtq.jobhunter.domain.response.resume.ResFetchResumeDTO;
import vn.trungtq.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.trungtq.jobhunter.repository.JobRepository;
import vn.trungtq.jobhunter.repository.ResumeRepository;
import vn.trungtq.jobhunter.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    public ResumeService(ResumeRepository resumeRepository, JobRepository jobRepository, UserRepository userRepository) {
        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    public ResCreateResumeDTO handleCreateResume(Resume resume) {

        Resume newResume = this.resumeRepository.save(resume);
        ResCreateResumeDTO resCreateResumeDTO = new ResCreateResumeDTO();
        resCreateResumeDTO.setId(newResume.getId());
        resCreateResumeDTO.setCreatedAt(resume.getCreatedAt());
        resCreateResumeDTO.setCreatedBy(resume.getCreatedBy());
        return resCreateResumeDTO;
    }

    public Resume handleGetResume(long id) {
        return this.resumeRepository.findById(id).orElse(null);
    }
    public ResFetchResumeDTO getResume(Resume resume) {
        ResFetchResumeDTO resFetchResumeDTO = new ResFetchResumeDTO();
        resFetchResumeDTO.setId(resume.getId());
        resFetchResumeDTO.setEmail(resume.getEmail());
        resFetchResumeDTO.setUrl(resume.getUrl());
        resFetchResumeDTO.setStatus(resume.getStatus());
        resFetchResumeDTO.setCreatedAt(resume.getCreatedAt());
        resFetchResumeDTO.setCreatedBy(resume.getCreatedBy());
        resFetchResumeDTO.setUpdatedAt(resume.getUpdatedAt());
        resFetchResumeDTO.setUpdatedBy(resume.getUpdatedBy());
        if (resume.getJob() != null) {
            resFetchResumeDTO.setCompanyName(resume.getJob().getCompany().getName());
        }
        resFetchResumeDTO.setJob(new ResFetchResumeDTO.JobResume(resume.getJob().getId(),resume.getJob().getName()));
        resFetchResumeDTO.setUser(new ResFetchResumeDTO.UserResume(resume.getUser().getId(),resume.getUser().getName()));
        return resFetchResumeDTO;
    }

    public ResUpdateResumeDTO handleUpdateResume(Resume resume) {
        Resume updatedResume = this.resumeRepository.save(resume);
        ResUpdateResumeDTO resUpdateResumeDTO = new ResUpdateResumeDTO();
        resUpdateResumeDTO.setId(updatedResume.getId());
        resUpdateResumeDTO.setUpdatedAt(updatedResume.getUpdatedAt());
        resUpdateResumeDTO.setUpdatedBy(updatedResume.getUpdatedBy());
        return resUpdateResumeDTO;
    }
    public ResultPaginationDTO handleGetAllResumes(Specification<Resume> spec, Pageable pageable) {
        Page<Resume> pageResumes = resumeRepository.findAll(spec,pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotal(pageResumes.getTotalElements());
        meta.setPages(pageResumes.getTotalPages());
        rs.setMeta(meta);
        List<ResFetchResumeDTO> listResume = pageResumes.getContent()
                .stream().map(resume -> getResume(resume))
                .collect(Collectors.toList());
        rs.setResult(listResume);
        return rs;
    }
    public void handleDeleteResume(long id) {
        Optional<Resume> resume = this.resumeRepository.findById(id);
        if (resume.isPresent()) {
            Resume resumeToDelete = resume.get();
//            resumeToDelete.getJobs().forEach(job -> job.getResumes().remove(resumeToDelete));

        }
        this.resumeRepository.deleteById(id);
    }
    public boolean checkResumeExistByUserAndJob(Resume resume) {
        if (resume.getUser() == null){
            return false;
        }
        Optional<User> userOptional = this.userRepository.findById(resume.getUser().getId());
        if (userOptional.isEmpty()) {
            return false;
        }

        if (resume.getJob() == null){
            return false;
        }
        Optional<Job> jobOptional = this.jobRepository.findById(resume.getJob().getId());
        if (jobOptional.isEmpty()) {
            return false;
        }
        return true;
    }
}
