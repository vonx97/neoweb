package controller;

import com.neosoft.neoweb.Services.UpdateService;
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

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"client-" + version + ".zip\"")
                .body(resource);
    }

    private boolean isValidToken(String token) {
        // Token doğrulama örneği
        return token != null && token.startsWith("Bearer ");
    }
}
