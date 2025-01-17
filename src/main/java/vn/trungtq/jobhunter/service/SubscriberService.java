package vn.trungtq.jobhunter.service;

import org.springframework.stereotype.Service;
import vn.trungtq.jobhunter.domain.Subscriber;
import vn.trungtq.jobhunter.domain.Skill;
import vn.trungtq.jobhunter.repository.SubscriberRepository;
import vn.trungtq.jobhunter.repository.SkillRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    public SubscriberService(SubscriberRepository subscriberRepository, SkillRepository skillRepository) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
    }

    public Subscriber handleCreateSubscriber(Subscriber job) {
        if(job.getSkills() != null){
            List<Long> reqSkills = job.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            job.setSkills(dbSkills);
        }

        return  this.subscriberRepository.save(job);
    }

    public boolean checkEmailExist(String email) {
        return this.subscriberRepository.existsByEmail(email);
    }
    public Subscriber handleGetSubscriber(long id) {
        return this.subscriberRepository.findById(id).orElse(null);
    }
    public Subscriber handleUpdateSubscriber(Subscriber subscriber,Subscriber subscriberInDB) {
        if(subscriber.getSkills() != null){
            List<Long> reqSkills = subscriber.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            subscriberInDB.setSkills(dbSkills);
        }
        return  this.subscriberRepository.save(subscriberInDB);
    }
//    public ResultPaginationDTO handleGetAllSubscribers(Specification<Subscriber> spec, Pageable pageable) {
//        Page<Subscriber> pageSubscribers = jobRepository.findAll(spec,pageable);
//        ResultPaginationDTO rs = new ResultPaginationDTO();
//        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
//        meta.setPage(pageable.getPageNumber() + 1);
//        meta.setPageSize(pageable.getPageSize());
//        meta.setTotal(pageSubscribers.getTotalElements());
//        meta.setPages(pageSubscribers.getTotalPages());
//        rs.setMeta(meta);
//        rs.setResult(pageSubscribers.getContent());
//        return rs;
//    }
//    public void handleDeleteSubscriber(long id) {
//        this.subscriberRepository.deleteById(id);
//    }
}
