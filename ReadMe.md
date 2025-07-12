# Proje Notları - 2025-07-11

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
