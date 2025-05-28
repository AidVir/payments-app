package com.aidas.payments.service;

import com.aidas.payments.dto.CountryResponse;
import com.aidas.payments.entity.NotificationLog;
import com.aidas.payments.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AsyncTaskService {

    private final PaymentRepository repository;
    private final RestTemplate rest;

    public void resolveCountry(String ip, Long paymentId) {
        try {
            CountryResponse countryResponse = rest.getForObject(
                    "http://ip-api.com/json/" + ip + "?fields=country",
                    CountryResponse.class
            );

            Optional<String> countryName = Optional.ofNullable(countryResponse)
                    .map(CountryResponse::getCountry);

            countryName.ifPresentOrElse(
                    country -> log.info("Resolved country '{}' for payment ID {}", country, paymentId),
                    () -> log.warn("Country not resolved for payment ID {} (IP: {})", paymentId, ip)
            );

        } catch (Exception e) {
            log.warn("Exception while resolving country for payment ID {} (IP: {}): {}", paymentId, ip, e.getMessage());
        }
    }

    @Async
    public void notify(String url, Long paymentId) {
        boolean success = false;

        try {
            ResponseEntity<String> response = rest.getForEntity(url, String.class);
            success = response.getStatusCode().is2xxSuccessful();

            if (success) {
                log.info("Successfully notified external service for payment ID {} (URL: {})", paymentId, url);
            } else {
                log.warn("Failed to notify external service for payment ID {} (URL: {}): HTTP {}", paymentId, url, response.getStatusCode().value());
            }

        } catch (Exception e) {
            log.warn("Exception while notifying external service for payment ID {} (URL: {}): {}", paymentId, url, e.getMessage());
        }

        NotificationLog logEntry = new NotificationLog();
        logEntry.setSuccess(success);
        logEntry.setTargetService(url);
        logEntry.setTimestamp(LocalDateTime.now());

        repository.findById(paymentId).ifPresent(p -> {
            p.setNotificationLog(logEntry);
            repository.save(p);
        });
    }
}