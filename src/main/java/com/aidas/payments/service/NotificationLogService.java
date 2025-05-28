package com.aidas.payments.service;

import com.aidas.payments.entity.NotificationLog;
import com.aidas.payments.repository.NotificationLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationLogService {

    private final NotificationLogRepository repository;

    public NotificationLogService(NotificationLogRepository repository) {
        this.repository = repository;
    }

    public List<NotificationLog> getAllLogs() {
        return repository.findAll();
    }

    public NotificationLog save(NotificationLog log) {
        return repository.save(log);
    }

    public NotificationLog getById(Long id) {
        return repository.getById(id);
    }
}

