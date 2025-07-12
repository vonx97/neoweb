package controller;

import com.neosoft.neoweb.services.UpdateService;
import com.neosoft.neoweb.model.VersionRequest;
import com.neosoft.neoweb.model.VersionResponse;
import com.neosoft.neoweb.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/neoweb/client")
public class VersionController {

    private final UpdateService updateService;

    public VersionController(UpdateService updateService) {
        this.updateService = updateService;
    }

    @PostMapping("/check-version")
    public ResponseEntity<VersionResponse> checkVersion(
            @RequestBody VersionRequest request,
            @RequestHeader("Authorization") String token) {

        if (!isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String latest = updateService.getLatestVersion();
            boolean upToDate = latest.equals(request.getVersion());

            if (upToDate) {
                return ResponseEntity.ok(new VersionResponse(true, latest, null, null, null));
            }

            Path zipFile = updateService.getUpdateZip(latest);
            if (!Files.exists(zipFile)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new VersionResponse(false, latest, null, "Güncelleme dosyası sunucuda bulunamadı.", null)
                );
            }

            String checksum = updateService.calculateChecksum(zipFile);
            String downloadPath = "/api/update/package/" + latest;

            return ResponseEntity.ok(
                    new VersionResponse(false, latest, downloadPath, "Yeni sürüm mevcut. Yüklemeniz önerilir.", checksum)
            );

        } catch (IOException | NoSuchAlgorithmException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public boolean isValidToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return false;
        }

        String jwt = token.substring(7); // "Bearer " kısmını çıkar

        try {
            String username = JwtUtil.validateTokenAndGetUsername(jwt);
            return username != null;  // Token geçerli ve doğrulanmış demek
        } catch (Exception e) {
            return false;  // Token geçersiz
        }
    }


}
