package vn.trungtq.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.web.bind.annotation.RestController;
import vn.trungtq.jobhunter.domain.Subscriber;

@RestController
public interface SubscriberRepository extends JpaRepository<Subscriber, Long>, JpaSpecificationExecutor<Subscriber> {
    boolean existsByName(String name);
    boolean existsByEmail(String email);
}
