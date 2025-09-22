# Proje Notları - 2025-08-30

## Yapılan İşler

- **User Entity Güncellemesi**
  - Veritabanı kolon isimleri ile entity alanları eşleştirildi.
  - `USERNAME`, `IDENTITY_NUMBER` gibi alanların isimleri ve tipleri kontrol edildi.

- **UserRepository Hatası Çözümü**
  - `findByUsername` metodundaki hata çözüldü.
  - Entity'deki alan isimleri ile repository sorguları uyumlu hale getirildi.

- **LoginController ve Authentication Yapısı**
  - Kullanıcı giriş işlemi için controller yazıldı.
  - Kullanıcı adı ve şifre doğrulaması yapıldı.
  - JWT token üretimi entegre edildi.
  - Refresh token mekanizması eklendi.

- **JwtUtil Sınıfı**
  - JWT token oluşturma, doğrulama ve kullanıcı adı çıkarma fonksiyonları geliştirildi.
  - Token süresi **60*60*1000 ms** olarak ayarlandı:

    ```java
    private static final long validityInMs = 60 * 60 * 1000; // 60 dk
    ```

  - Token geçerliliği bu süre sonunda sona erer ve yenilenmesi gerekir.
  - Token doğrulama sırasında geçersiz veya süresi dolmuş tokenlar için hata yönetimi eklendi.

- **Güncelleme Sistemi (Update System)**
  - Client versiyon kontrolü için `/check` endpoint’i eklendi.
    - İstemci, kendi versiyonunu sorgulayarak güncel olup olmadığını öğrenir.
    - Sunucu, `"up-to-date"` veya `"update-available:<yeniVersiyon>"` cevabı verir.
  - Versiyona göre güncelleme paketi indirmek için `/package/{version}` endpoint’i hazırlandı.
  - Güncelleme paketleri `client-<version>.zip` olarak saklanır ve gönderilir.
  - Dosya indirirken `Content-Length` header’ı eklenerek istemcinin indirme ilerlemesini takip etmesi sağlandı.
  - Tüm güncelleme endpointleri JWT ile korundu, token doğrulaması yapıldı.

## Yapılan İşler (2025-09-19)

- **JWT ile Rol Bazlı Authentication**
  - Kullanıcı girişinde JWT token üretimi sırasında roller claim olarak eklendi.
  - Rollerin başına `"ROLE_"` prefix’i token üretimi sırasında ekleniyor.
  - JWT filter’ı ile gelen isteklerde token doğrulandı ve roller SecurityContext’e set edildi.

- **Method-level Security ile Endpoint Koruma**
  - `@EnableMethodSecurity` aktifleştirildi.
  - `@PreAuthorize` annotation’ları ile rol bazlı erişim kontrolü sağlandı.
  - Örnek: `/neoweb/userList` endpoint’i yalnızca `ADMIN` rolüne sahip kullanıcılar tarafından erişilebilir.

- **UserController Güncellemesi**
  - `/userList` endpoint’i `@PreAuthorize("hasRole('ADMIN')")` ile korundu.
  - Admin olmayan kullanıcılar artık bu endpoint’e erişemiyor.

- **SecurityConfig Güncellemesi**
  - CSRF devre dışı bırakıldı.
  - `/login` ve `/refresh-token` endpoint’leri herkese açık bırakıldı.
  - Diğer tüm endpoint’ler authentication gerektiriyor.
  - Session yönetimi **stateless** olarak ayarlandı.
  - JWT filter’ı, `UsernamePasswordAuthenticationFilter` öncesine eklendi.
  - 
# ENDPOINTLER

## 🔐 Authentication

| Method | Endpoint | Body | Headers | Response |
|--------|----------|------|---------|----------|
| **POST** | `/neoweb/login` | ```json { "username": "user", "password": "pass" } ``` | - | ```json { "success": true, "message": "Giriş başarılı", "token": "...", "refreshToken": "..." } ``` |
| **POST** | `/neoweb/refresh-token` | ```json { "username": "user", "refreshToken": "..." } ``` | - | ```json { "accessToken": "..." } ``` |

---

## 👤 User API (Rol Bazlı)

| Method | Endpoint | Body | Headers | Response | Rol Gereksinimi |
|--------|----------|------|---------|----------|----------------|
| **GET** | `/neoweb/userList` | - | `Authorization: Bearer <token>` | Liste<User> | `ADMIN` |

---

## 📦 Update Package API

| Method | Endpoint | Params / Body | Headers | Response |
|--------|----------|---------------|---------|----------|
| **GET** | `/neoweb/update/check` | `?version=1.0.0` | `Authorization: Bearer <token>` | `"up-to-date"` veya `"update-available:<latestVersion>"` |
| **GET** | `/neoweb/update/package/{version}` | - | `Authorization: Bearer <token>` | ZIP dosyası (`client-{version}.zip`) |

---

## 🖥️ Client Version API

| Method | Endpoint | Body | Headers | Response |
|--------|----------|------|---------|----------|
| **POST** | `/neoweb/client/check-version` | ```json { "clientId": "abc", "version": "1.0.0" } ``` | `Authorization: Bearer <token>` | ```json { "upToDate": false, "latestVersion": "1.3.0", "downloadPath": "/api/update/package/1.3.0", "releaseNotes": "Yeni sürüm mevcut. Yüklemeniz önerilir.", "checksum": "..." } ``` |

---

## 🧪 Test API

| Method | Endpoint | Body | Headers | Response |
|--------|----------|------|---------|----------|
| **POST** | `/api/test` | - | - | `"test"` |


# Subscription & Payment API (2025-09-22)

## 📃 DTO Kullanımı

- **SubscriptionRequestDTO**
  - `userId` (Integer) – Kullanıcı ID
  - `planId` (Integer) – Plan ID
  - `paymentMethod` (PaymentMethods enum) – Ödeme yöntemi
  - `currency` (CurrencyType enum) – Para birimi
  - `autoRenew` (Boolean) – Opsiyonel, default true

- **SubscriptionUpdateDTO**
  - `subscriptionId` (Integer) – Güncellenecek abonelik
  - `planId` (Integer) – Opsiyonel yeni plan
  - `status` (SubscriptionStatus enum) – Opsiyonel
  - `autoRenew` (Boolean) – Opsiyonel
  - `paymentMethod` (PaymentMethods enum) – Opsiyonel ödeme değişimi
  - `currency` (CurrencyType enum) – Opsiyonel ödeme değişimi

---

## ➕ Create Subscription

| Method | Endpoint | Body | Headers | Response |
|--------|----------|------|---------|----------|
| **POST** | `/subscriptions/create` | ```json { "userId": 1, "planId": 2, "paymentMethod": "CREDIT_CARD", "currency": "TRY", "autoRenew": true } ``` | `Authorization: Bearer <token>` | `Subscription` entity döner, Payment kaydı otomatik oluşturulur |

---

## ✏️ Update Subscription

| Method | Endpoint | Body | Headers | Response |
|--------|----------|------|---------|----------|
| **PUT** | `/subscriptions/update` | ```json { "subscriptionId": 10, "planId": 3, "status": "ACTIVE", "autoRenew": false, "paymentMethod": "BANK_TRANSFER", "currency": "USD" } ``` | `Authorization: Bearer <token>` | Güncellenmiş `Subscription` entity döner, Payment değişiklikleri opsiyonel olarak uygulanır |

---

## 🗑️ Delete Subscription

| Method | Endpoint | Body | Headers | Response |
|--------|----------|------|---------|----------|
| **DELETE** | `/subscriptions/delete/{id}` | - | `Authorization: Bearer <token>` | Başarılı ise `"Subscription deleted successfully"` döner. İlişkili Payment kayıtları manuel veya cascade ile silinir |
