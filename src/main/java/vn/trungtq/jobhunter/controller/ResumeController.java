package vn.trungtq.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.trungtq.jobhunter.domain.Resume;
import vn.trungtq.jobhunter.domain.response.ResultPaginationDTO;
import vn.trungtq.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.trungtq.jobhunter.domain.response.resume.ResFetchResumeDTO;
import vn.trungtq.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.trungtq.jobhunter.service.ResumeService;
import vn.trungtq.jobhunter.service.ResumeService;
import vn.trungtq.jobhunter.util.anotation.ApiMessage;
import vn.trungtq.jobhunter.util.error.IdInvalidException;

@RestController
public class ResumeController {
    private final ResumeService resumeService;
    public ResumeController(final ResumeService resumeService) {
        this.resumeService = resumeService;
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
        ResultPaginationDTO rs = this.resumeService.handleGetAllResumes(spec,pageable);
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
