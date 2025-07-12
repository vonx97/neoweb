package controller;

import com.neosoft.neoweb.services.UpdateService;
import com.neosoft.neoweb.security.JwtUtil;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/neoweb/update")
public class UpdatePackageController {

    private final UpdateService updateService;

    public UpdatePackageController(UpdateService updateService) {
        this.updateService = updateService;
    }

    @GetMapping("/check")
    public ResponseEntity<String> checkVersion(
            @RequestParam("version") String clientVersion,
            @RequestHeader("Authorization") String token) throws IOException {

        if (!isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String latestVersion = updateService.getLatestVersion();

        if (latestVersion.equals(clientVersion)) {
            return ResponseEntity.ok("up-to-date");
        } else {
            return ResponseEntity.ok("update-available:" + latestVersion);
        }
    }


    @GetMapping("/package/{version}")
    public ResponseEntity<Resource> downloadPackage(
            @PathVariable String version,
            @RequestHeader("Authorization") String token) throws IOException {

        if (!isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Path zipPath = updateService.getUpdateZip(version);

        if (!Files.exists(zipPath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(zipPath.toUri());
        long contentLength = Files.size(zipPath);

        return ResponseEntity.ok()
                .contentLength(contentLength)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"client-" + version + ".zip\"")
                .body(resource);
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
