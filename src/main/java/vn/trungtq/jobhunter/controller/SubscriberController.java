package vn.trungtq.jobhunter.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.trungtq.jobhunter.domain.Subscriber;
import vn.trungtq.jobhunter.service.SubscriberService;
import vn.trungtq.jobhunter.util.SecurityUtil;
import vn.trungtq.jobhunter.util.anotation.ApiMessage;
import vn.trungtq.jobhunter.util.error.IdInvalidException;

import java.security.Security;

@RestController
public class SubscriberController {
    private final SubscriberService subscriberService;
    public SubscriberController(final SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    @ApiMessage("Create a new subscriber")
    public ResponseEntity<Subscriber> createSubscriber(@Valid @RequestBody Subscriber subscriber) throws IdInvalidException {
        if(this.subscriberService.checkEmailExist(subscriber.getEmail())){
            throw new IdInvalidException("Email " + subscriber.getEmail() + " đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED). body(this.subscriberService.handleCreateSubscriber(subscriber));
    }
    @PutMapping("/subscribers")
    public ResponseEntity<Subscriber> updateSubscriber(@RequestBody Subscriber subscriber) throws IdInvalidException {
        Subscriber curSubscriber = this.subscriberService.handleGetSubscriber(subscriber.getId());
        if (curSubscriber == null) {
            throw  new IdInvalidException(
                    "Subscriber không tồn tại"
            );
        }
        Subscriber updatedSubscriber = this.subscriberService.handleUpdateSubscriber(subscriber,curSubscriber);
        return ResponseEntity.status(HttpStatus.OK).body(updatedSubscriber);
    }

    @PostMapping("/subscribers/skills")
    @ApiMessage("Get subscriber's skill")
    public ResponseEntity<Subscriber> getSubscriberSkill() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get() : "";
        return ResponseEntity.ok().body(this.subscriberService.findByEmail(email));
    }
//    @GetMapping("/subscribers")
//    public ResponseEntity<ResultPaginationDTO> getAllSubscriber(@Filter Specification<Subscriber> spec, Pageable pageable){
//        ResultPaginationDTO rs = this.subscriberService.handleGetAllSubscribers(spec,pageable);
//        return ResponseEntity.status(HttpStatus.OK).body(rs);
//    }
//    @DeleteMapping("/subscribers/{id}")
//    public ResponseEntity<Void> deleteSubscriber(@PathVariable("id") long id)  throws IdInvalidException {
//        Subscriber curSubscriber = this.subscriberService.handleGetSubscriber(id);
//        if (curSubscriber == null) {
//            throw  new IdInvalidException(
//                    "Subscriber không tồn tại"
//            );
//        }
//        this.subscriberService.handleDeleteSubscriber(id);
//        return ResponseEntity.ok().build();
//    }
//    @GetMapping("/subscribers/{id}")
//    public ResponseEntity<Subscriber> getSubscriber(@PathVariable("id") long id) throws IdInvalidException {
//        Subscriber curSubscriber = this.subscriberService.handleGetSubscriber(id);
//        if (curSubscriber == null) {
//            throw  new IdInvalidException(
//                    "Subscriber không tồn tại"
//            );
//        }
//        return ResponseEntity.status(HttpStatus.OK).body(curSubscriber);
//    }

}
