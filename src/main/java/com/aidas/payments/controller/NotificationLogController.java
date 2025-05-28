package com.aidas.payments.controller;

import com.aidas.payments.entity.NotificationLog;
import com.aidas.payments.service.NotificationLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationLogController {

    private final NotificationLogService service;

    public NotificationLogController(NotificationLogService service) {
        this.service = service;
    }

    @GetMapping
    public List<NotificationLog> getAllLogs() {
        return service.getAllLogs();
    }
}
