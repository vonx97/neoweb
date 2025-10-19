package controller;

import com.neosoft.neoweb.dto.SubscriptionRenewDTO;
import com.neosoft.neoweb.dto.SubscriptionRequestDTO;
import com.neosoft.neoweb.dto.SubscriptionUpdateDTO;
import com.neosoft.neoweb.entity.Subscription;
import com.neosoft.neoweb.services.SubscriptionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {


    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public Subscription createSubscription(@RequestBody SubscriptionRequestDTO dto) {
        return subscriptionService.createSubscription(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update")
    public Subscription updateSubscription(@RequestBody SubscriptionUpdateDTO dto) {
        return subscriptionService.updateSubscription(dto);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteUser/{id}")
    public String deleteSubscription(@PathVariable int id) {
        subscriptionService.deleteSubscription(id);
        return "Subscription with id " + id + " deleted successfully!";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/renew")
    public Subscription renewSubscription(@RequestBody SubscriptionRenewDTO dto) {
        return subscriptionService.renewSubscription(dto);
    }


}
