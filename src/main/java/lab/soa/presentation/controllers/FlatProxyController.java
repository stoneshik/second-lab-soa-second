package lab.soa.presentation.controllers;

import java.util.Enumeration;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import lab.soa.domain.models.BalconyType;
import lab.soa.domain.models.PriceType;
import lab.soa.domain.models.SortType;
import lab.soa.domain.models.TransportType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/flats")
@RequiredArgsConstructor
public class FlatProxyController {
    private final RestTemplate restTemplate;

    @GetMapping("/find-with-balcony/{priceType}/{balconyType}")
    public ResponseEntity<?> findWithBalcony(
        @PathVariable PriceType priceType,
        @PathVariable BalconyType balconyType,
        HttpServletRequest originalRequest
    ) {
        String protocol = originalRequest.getProtocol();
        String scheme = originalRequest.getScheme();
        boolean isSecure = originalRequest.isSecure();
        log.info(
            "Incoming request: {} {}{}",
            originalRequest.getMethod(),
            originalRequest.getRequestURI(),
            originalRequest.getQueryString() != null ? "?" + originalRequest.getQueryString() : ""
        );
        log.info("Request protocol: {}, scheme: {}, secure: {}", protocol, scheme, isSecure);
        String targetUrl = String.format(
            "/api/v1/flats/find-with-balcony/%s/%s",
            priceType.name(),
            balconyType.name()
        );
        log.info("Proxying request to target service: {}", targetUrl);
        HttpHeaders headers = copyHeaders(originalRequest);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                targetUrl,
                HttpMethod.GET,
                entity,
                String.class
            );
            log.info("Response received from target service: {}", response.getStatusCode());
            return ResponseEntity
                .status(response.getStatusCode())
                .body(response.getBody());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Error from target service: {} - {}", e.getStatusCode(), e.getStatusText());
            return ResponseEntity
                .status(e.getStatusCode())
                .body(e.getResponseBodyAsString());
        }
    }

    @GetMapping("/get-ordered-by-time-to-metro/{transportType}/{sortType}")
    public ResponseEntity<?> getOrderedByTimeToMetro(
        @PathVariable TransportType transportType,
        @PathVariable SortType sortType,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        HttpServletRequest originalRequest
    ) {
        log.info(
            "Incoming HTTPS request: {} {} (Secure: {})",
            originalRequest.getMethod(),
            originalRequest.getRequestURI(),
            originalRequest.isSecure()
        );
        String baseUrl = String.format(
            "/api/v1/flats/get-ordered-by-time-to-metro/%s/%s",
            transportType.name(),
            sortType.name()
        );
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl)
            .queryParam("page", page)
            .queryParam("size", size);
        originalRequest.getParameterMap().forEach((key, values) -> {
            if (!key.equals("page") && !key.equals("size")) {
                for (String value : values) {
                    builder.queryParam(key, value);
                }
            }
        });
        String targetUrl = builder.toUriString();
        log.info("Proxying to target URL: {}", targetUrl);
        HttpHeaders headers = copyHeaders(originalRequest);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                targetUrl,
                HttpMethod.GET,
                entity,
                String.class
            );
            log.info("Target service responded with: {}", response.getStatusCode());
            return ResponseEntity
                .status(response.getStatusCode())
                .body(response.getBody());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Target service error: {}", e.getMessage());
            return ResponseEntity
                .status(e.getStatusCode())
                .body(e.getResponseBodyAsString());
        }
    }

    private HttpHeaders copyHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        log.debug("Copying headers from incoming request:");
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            Enumeration<String> headerValues = request.getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                String headerValue = headerValues.nextElement();
                headers.add(headerName, headerValue);
                log.debug("  {}: {}", headerName, headerValue);
            }
        }
        return headers;
    }
}
