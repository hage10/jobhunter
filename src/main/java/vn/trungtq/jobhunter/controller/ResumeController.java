package vn.trungtq.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.trungtq.jobhunter.domain.Company;
import vn.trungtq.jobhunter.domain.Job;
import vn.trungtq.jobhunter.domain.Resume;
import vn.trungtq.jobhunter.domain.User;
import vn.trungtq.jobhunter.domain.response.ResultPaginationDTO;
import vn.trungtq.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.trungtq.jobhunter.domain.response.resume.ResFetchResumeDTO;
import vn.trungtq.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.trungtq.jobhunter.service.ResumeService;
import vn.trungtq.jobhunter.service.ResumeService;
import vn.trungtq.jobhunter.service.UserService;
import vn.trungtq.jobhunter.util.SecurityUtil;
import vn.trungtq.jobhunter.util.anotation.ApiMessage;
import vn.trungtq.jobhunter.util.error.IdInvalidException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ResumeController {
    private final ResumeService resumeService;
    private final UserService userService;

    private final FilterBuilder filterBuilder;
    private final FilterSpecificationConverter filterSpecificationConverter;
    public ResumeController( ResumeService resumeService,UserService userService,
                             FilterBuilder filterBuilder,  FilterSpecificationConverter filterSpecificationConverter) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.filterBuilder = filterBuilder;
        this.filterSpecificationConverter = filterSpecificationConverter;
    }

    @PostMapping("/resumes")
    @ApiMessage("Create a new resume")
    public ResponseEntity<ResCreateResumeDTO> createResume(@Valid @RequestBody Resume resume) throws IdInvalidException {

        boolean isIdExist = this.resumeService.checkResumeExistByUserAndJob(resume);
        if (!isIdExist) {
            throw  new IdInvalidException("User id/ job id không tồn tại");
        }
        ResCreateResumeDTO newResume = this.resumeService.handleCreateResume(resume);
        return ResponseEntity.status(HttpStatus.CREATED). body(newResume);
    }
    @PutMapping("/resumes")
    public ResponseEntity<ResUpdateResumeDTO> updateResume(@RequestBody Resume resume) throws IdInvalidException {
        Resume curResume = this.resumeService.handleGetResume(resume.getId());
        if (curResume == null) {
            throw  new IdInvalidException(
                    "Resume không tồn tại"
            );
        }
        curResume.setStatus(resume.getStatus());

        ResUpdateResumeDTO updatedResume = this.resumeService.handleUpdateResume(curResume);

        return ResponseEntity.status(HttpStatus.OK).body(updatedResume);
    }

    @GetMapping("/resumes")
    public ResponseEntity<ResultPaginationDTO> getAllResume(@Filter Specification<Resume> spec, Pageable pageable){

        List<Long> arrJobIds = null;
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get() : null;

        User currentUser = this.userService.handleGetUserByUsername(email);
        if (currentUser != null) {
            Company userCompany = currentUser.getCompany();
            if (userCompany != null) {
                List<Job> companyJobs = userCompany.getJobs();
                if (companyJobs != null && !companyJobs.isEmpty()) {
                    arrJobIds = companyJobs.stream().map(x -> x.getId())
                            .collect(Collectors.toList());
                }
            }
        }
        Specification<Resume> jobInSpec = filterSpecificationConverter.convert(filterBuilder.field("job")
                .in(filterBuilder.input(arrJobIds)).get());

        Specification<Resume> finalSpec = jobInSpec.and(spec);

        ResultPaginationDTO rs = this.resumeService.handleGetAllResumes(finalSpec,pageable);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }

    @DeleteMapping("/resumes/{id}")
    public ResponseEntity<Void> deleteResume(@PathVariable("id") long id)  throws IdInvalidException {
        Resume curResume = this.resumeService.handleGetResume(id);
        if (curResume == null) {
            throw  new IdInvalidException(
                    "Resume không tồn tại"
            );
        }
        this.resumeService.handleDeleteResume(id);
        return ResponseEntity.ok().body(null);
    }
    @GetMapping("/resumes/{id}")
    public ResponseEntity<ResFetchResumeDTO> getResume(@PathVariable("id") long id) throws IdInvalidException {
        Resume curResume = this.resumeService.handleGetResume(id);
        if (curResume == null) {
            throw  new IdInvalidException(
                    "Resume không tồn tại"
            );
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.resumeService.getResume(curResume));
    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("Get resume by user")
    public ResponseEntity<ResultPaginationDTO> getResumeByUser(Pageable pageable)  {
        return ResponseEntity.ok().body(this.resumeService.getResumeByUser(pageable));
    }
}
