package yandex.task.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
import java.util.Map;

@RestController
public class ReportsController {
    @Value("${introspect.url}")
    private String introspectUrl;
    private static final String CLIENT_ID = "reports-api";
    private static final String CLIENT_SECRET = "oNwoLQdvJAvRcL89SydqCWCe5ry1jMgq";

    @CrossOrigin(origins = {"http://localhost:3000", "http://frontend:3000"})
    @GetMapping("/reports")
    public ReportResponse getReport(@AuthenticationPrincipal final Jwt jwt) {
        if (!isTokenActive(jwt.getTokenValue())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token revoked or invalid");
        }
        final var username = (String) jwt.getClaim("preferred_username");
        return new ReportResponse(
                "Report for " + username,
                "Successful report"
        );
    }

    private boolean isTokenActive(final String token) {
        try {
            final var credentials = CLIENT_ID + ":" + CLIENT_SECRET;
            final var authHeader = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
            final var requestBody = "token=" + token;
            final var headers = new HttpHeaders();
            headers.set("Authorization", authHeader);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            final var request = new HttpEntity<>(requestBody, headers);
            final var response = new RestTemplate().postForEntity(
                    introspectUrl,
                    request,
                    Map.class
            );
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return Boolean.TRUE.equals(response.getBody().get("active"));
            }
            return false;
        } catch (final Exception e) {
            return false;
        }
    }
}
