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


# ENDPOINTLER


Bu proje Spring Boot ile geliştirilmiş bir backend uygulamasıdır. Aşağıda mevcut endpointler listelenmiştir.

---

## 🔐 Authentication

| Method | Endpoint | Body | Headers | Response |
|--------|----------|------|---------|----------|
| **POST** | `/neoweb/login` | ```json { "username": "user", "password": "pass" } ``` | - | ```json { "success": true, "message": "Giriş başarılı", "token": "...", "refreshToken": "..." } ``` |
| **POST** | `/neoweb/refresh-token` | ```json { "username": "user", "refreshToken": "..." } ``` | - | ```json { "accessToken": "..." } ``` |

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

---

## 🔑 Authorization

Tüm korumalı endpointlerde aşağıdaki header kullanılmalıdır:

