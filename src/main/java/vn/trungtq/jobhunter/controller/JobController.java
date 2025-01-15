package vn.trungtq.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.trungtq.jobhunter.domain.Job;
import vn.trungtq.jobhunter.domain.response.ResCreateJobDTO;
import vn.trungtq.jobhunter.domain.response.ResUpdateJobDTO;
import vn.trungtq.jobhunter.domain.response.ResultPaginationDTO;
import vn.trungtq.jobhunter.service.JobService;
import vn.trungtq.jobhunter.util.anotation.ApiMessage;
import vn.trungtq.jobhunter.util.error.IdInvalidException;

@RestController
public class JobController {
    private final JobService jobService;
    public JobController(final JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    @ApiMessage("Create a new job")
    public ResponseEntity<ResCreateJobDTO> createJob(@Valid @RequestBody Job job) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED). body(this.jobService.handleCreateJob(job));
    }
    @PutMapping("/jobs")
    public ResponseEntity<ResUpdateJobDTO> updateJob(@Valid @RequestBody Job job) throws IdInvalidException {
        Job curJob = this.jobService.handleGetJob(job.getId());
        if (curJob == null) {
            throw  new IdInvalidException(
                    "Job không tồn tại"
            );
        }
        ResUpdateJobDTO updatedJob = this.jobService.handleUpdateJob(job,curJob);
        return ResponseEntity.status(HttpStatus.OK).body(updatedJob);
    }
    @GetMapping("/jobs")
    public ResponseEntity<ResultPaginationDTO> getAllJob(@Filter Specification<Job> spec, Pageable pageable){
        ResultPaginationDTO rs = this.jobService.handleGetAllJobs(spec,pageable);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }
    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable("id") long id)  throws IdInvalidException {
        Job curJob = this.jobService.handleGetJob(id);
        if (curJob == null) {
            throw  new IdInvalidException(
                    "Job không tồn tại"
            );
        }
        this.jobService.handleDeleteJob(id);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/jobs/{id}")
    public ResponseEntity<Job> getJob(@PathVariable("id") long id) throws IdInvalidException {
        Job curJob = this.jobService.handleGetJob(id);
        if (curJob == null) {
            throw  new IdInvalidException(
                    "Job không tồn tại"
            );
        }
        return ResponseEntity.status(HttpStatus.OK).body(curJob);
    }

}
