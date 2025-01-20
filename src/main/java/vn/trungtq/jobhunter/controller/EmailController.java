package vn.trungtq.jobhunter.controller;

import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.trungtq.jobhunter.service.EmailService;
import vn.trungtq.jobhunter.service.SubscriberService;

@RestController
@RequestMapping
public class EmailController {

    private final EmailService emailService;
    private final SubscriberService subscriberService;
    public EmailController(EmailService emailService, SubscriberService subscriberService) {
        this.emailService = emailService;
        this.subscriberService = subscriberService;
    }
    @GetMapping("/email")
//    @Scheduled(cron = "*/30 * * * * *")
//    @Transactional
    public String sendSimpleEmail() {
//        this.emailService.sendSimpleEmail();
//        this.emailService.sendEmailSync(
//                "utngo07@gmail.com","test send email","<h1><br>hrkkio</br></h1>",false, true
//        );
//        this.emailService.sendEmailFromTemplateSync(
//                "utngo07@gmail.com","test send emai",
//                "job"
//        );
        this.subscriberService.sendSubscribersEmailJobs();
        return "OK";

    }
}
