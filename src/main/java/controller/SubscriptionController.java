package controller;

import com.neosoft.neoweb.dto.SubscriptionRenewDTO;
import com.neosoft.neoweb.dto.SubscriptionRequestDTO;
import com.neosoft.neoweb.dto.SubscriptionUpdateDTO;
import com.neosoft.neoweb.entity.Subscription;
import com.neosoft.neoweb.services.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <h2>SubscriptionController</h2>
 *
 * <p>
 * Bu controller, sistemdeki abonelik işlemlerinin yönetilmesinden sorumludur.
 * Yalnızca <b>ADMIN</b> rolündeki kullanıcılar erişim yetkisine sahiptir.
 * </p>
 *
 * <h3>Endpoint Özeti</h3>
 * <ul>
 *   <li><b>POST /subscriptions</b> → Yeni abonelik oluşturur</li>
 *   <li><b>PUT /subscriptions/{id}</b> → Var olan bir aboneliği günceller</li>
 *   <li><b>DELETE /subscriptions/{id}</b> → Aboneliği siler</li>
 *   <li><b>POST /subscriptions/{id}/renew</b> → Aboneliği yeniler</li>
 * </ul>
 *
 * <p>
 * Bu controller, iş mantığını {@link com.neosoft.neoweb.services.SubscriptionService}
 * servisi aracılığıyla yürütür.
 * </p>
 *
 * <h3>Tasarım Notları</h3>
 * <ul>
 *   <li>Her endpoint, {@code @PreAuthorize("hasRole('ADMIN')")} ile korunur.</li>
 *   <li>DTO’lar {@code @Validated} ile doğrulanır, böylece eksik veya hatalı veri gönderimi engellenir.</li>
 *   <li>{@link ResponseEntity} kullanılarak her işlemde uygun HTTP yanıtı döndürülür.</li>
 * </ul>
 */
@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    /**
     * Controller yapıcı metodu.
     *
     * @param subscriptionService Abonelik işlemlerini yöneten servis katmanı
     */
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    /**
     * Yeni bir abonelik oluşturur.
     * <p>
     * Sadece ADMIN kullanıcılar tarafından çağrılabilir.
     * </p>
     *
     * @param dto Yeni abonelik oluşturmak için gerekli bilgiler:
     *            {@link SubscriptionRequestDTO} (userId, planId, paymentMethod, currency, autoRenew)
     * @return Oluşturulan {@link Subscription} nesnesi
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<Subscription> createSubscription(@Validated @RequestBody SubscriptionRequestDTO dto) {
        return ResponseEntity.ok(subscriptionService.createSubscription(dto));
    }

    /**
     * Var olan bir aboneliği günceller.
     * <p>
     * Güncellenebilir alanlar: plan, durum, otomatik yenileme, ödeme bilgisi.
     * </p>
     *
     * @param id  Güncellenecek aboneliğin ID’si (path değişkeni)
     * @param dto Güncelleme için gerekli alanları içeren {@link SubscriptionUpdateDTO}
     * @return Güncellenmiş {@link Subscription} nesnesi
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Subscription> updateSubscription(
            @PathVariable int id,
            @Validated @RequestBody SubscriptionUpdateDTO dto) {
        dto.setSubscriptionId(id);
        return ResponseEntity.ok(subscriptionService.updateSubscription(dto));
    }

    /**
     * Belirtilen aboneliği sistemden siler.
     *
     * @param id Silinecek aboneliğin ID’si
     * @return Başarılı silme durumunda bilgilendirici mesaj
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSubscription(@PathVariable int id) {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.ok("Subscription with id " + id + " deleted successfully!");
    }

    /**
     * Belirtilen aboneliği yeniler.
     * <p>
     * Genellikle bitmek üzere olan bir abonelikte çağrılır. Yeni bitiş tarihi planın süresine göre uzatılır.
     * </p>
     *
     * @param id  Yenilenecek aboneliğin ID’si
     * @param dto Yenileme için ödeme yöntemi, para birimi vb. bilgileri içeren {@link SubscriptionRenewDTO}
     * @return Yenilenmiş {@link Subscription} nesnesi
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}/renew")
    public ResponseEntity<Subscription> renewSubscription(
            @PathVariable int id,
            @Validated @RequestBody SubscriptionRenewDTO dto) {
        dto.setSubscriptionId(id);
        return ResponseEntity.ok(subscriptionService.renewSubscription(dto));
    }
}
