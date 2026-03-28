package com.example.jiraissueservice.model;

import java.time.Instant;

public record NotificationMessage(String watcher, String issueId, String message, Instant timestamp) {
}
