package controller;

import com.neosoft.neoweb.dto.SubscriptionRequestDTO;
import com.neosoft.neoweb.entity.Subscription;
import com.neosoft.neoweb.services.SubscriptionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {


    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/create")
    public Subscription createSubscription(@RequestBody SubscriptionRequestDTO dto) {
        return subscriptionService.createSubscription(dto);
    }

}
