package com.example.jiraissueservice.service;

import com.example.jiraissueservice.model.NotificationMessage;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WatcherNotificationService {

    private final Map<String, List<NotificationMessage>> inboxByWatcher = new ConcurrentHashMap<>();

    public void notifyWatchers(String issueId, Iterable<String> watchers, String event) {
        for (String watcher : watchers) {
            var notification = new NotificationMessage(
                    watcher,
                    issueId,
                    "Issue " + issueId + " was " + event,
                    Instant.now()
            );
            inboxByWatcher.computeIfAbsent(watcher, ignored -> new ArrayList<>()).add(notification);
        }
    }

    public List<NotificationMessage> getInbox(String watcher) {
        return inboxByWatcher.getOrDefault(watcher, List.of())
                .stream()
                .sorted(Comparator.comparing(NotificationMessage::timestamp).reversed())
                .toList();
    }
}
