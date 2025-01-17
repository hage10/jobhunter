package vn.trungtq.jobhunter.service;

import org.springframework.stereotype.Service;
import vn.trungtq.jobhunter.domain.Job;
import vn.trungtq.jobhunter.domain.Subscriber;
import vn.trungtq.jobhunter.domain.Skill;
import vn.trungtq.jobhunter.domain.response.email.ResEmailJob;
import vn.trungtq.jobhunter.repository.JobRepository;
import vn.trungtq.jobhunter.repository.SubscriberRepository;
import vn.trungtq.jobhunter.repository.SkillRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;

    public SubscriberService(SubscriberRepository subscriberRepository, SkillRepository skillRepository,
                             JobRepository jobRepository, EmailService emailService) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
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
    public ResEmailJob convertJobToSendEmail(Job job) {
        ResEmailJob res = new ResEmailJob();
        res.setName(job.getName());
        res.setSalary(job.getSalary());
        res.setCompany(new ResEmailJob.CompanyEmail(job.getCompany().getName()));
        List<Skill> skills = job.getSkills();
        List<ResEmailJob.SkillEmail> s = skills.stream().map(skill -> new ResEmailJob.SkillEmail(skill.getName()))
                .collect(Collectors.toList());
        res.setSkills(s);
        return res;
    }

    public void sendSubscribersEmailJobs() {
        List<Subscriber> listSubs = this.subscriberRepository.findAll();
        if (listSubs != null && listSubs.size() > 0) {
            for (Subscriber sub : listSubs) {
                List<Skill> listSkills = sub.getSkills();
                if (listSkills != null && listSkills.size() > 0) {
                    List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);
                    if (listJobs != null && listJobs.size() > 0) {
                         List<ResEmailJob> arr = listJobs.stream().map(
                         job -> this.convertJobToSendEmail(job)).collect(Collectors.toList());
                        this.emailService.sendEmailFromTemplateSync(
                                sub.getEmail(),
                                "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay",
                                "job",
                                sub.getName(),
                                listJobs);
                    }
                }
            }
        }
    }
}
