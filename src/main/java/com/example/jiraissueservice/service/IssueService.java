package com.example.jiraissueservice.service;

import com.example.jiraissueservice.model.Issue;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class IssueService {

    private final ConcurrentMap<String, Issue> issues = new ConcurrentHashMap<>();
    private final WatcherNotificationService watcherNotificationService;

    public IssueService(WatcherNotificationService watcherNotificationService) {
        this.watcherNotificationService = watcherNotificationService;
    }

    public List<Issue> findAll() {
        return issues.values().stream()
                .sorted(Comparator.comparing(Issue::getUpdatedAt).reversed())
                .toList();
    }

    public Issue findById(String id) {
        return getIssueOrThrow(id);
    }

    public Issue create(Issue issue) {
        String id = "JIS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        issue.setId(id);
        issue.setUpdatedAt(Instant.now());
        if (issue.getWatchers() == null) {
            issue.setWatchers(Set.of());
        }
        issues.put(id, issue);
        watcherNotificationService.notifyWatchers(id, issue.getWatchers(), "created");
        return issue;
    }

    public Issue update(String id, Issue updatedIssue) {
        Issue existing = getIssueOrThrow(id);
        existing.setSummary(updatedIssue.getSummary());
        existing.setDescription(updatedIssue.getDescription());
        existing.setStatus(updatedIssue.getStatus());
        existing.setPriority(updatedIssue.getPriority());
        existing.setAssignee(updatedIssue.getAssignee());
        existing.setReporter(updatedIssue.getReporter());
        existing.setLabels(updatedIssue.getLabels());
        existing.setWatchers(updatedIssue.getWatchers());
        existing.setUpdatedAt(Instant.now());

        watcherNotificationService.notifyWatchers(id, existing.getWatchers(), "updated");
        return existing;
    }

    public void delete(String id) {
        Issue removed = issues.remove(id);
        if (removed == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Issue not found: " + id);
        }
        watcherNotificationService.notifyWatchers(id, removed.getWatchers(), "deleted");
    }

    public Issue addWatcher(String id, String watcher) {
        Issue issue = getIssueOrThrow(id);
        issue.getWatchers().add(watcher);
        issue.setUpdatedAt(Instant.now());
        watcherNotificationService.notifyWatchers(id, List.of(watcher), "watcher-added");
        return issue;
    }

    public Issue removeWatcher(String id, String watcher) {
        Issue issue = getIssueOrThrow(id);
        issue.getWatchers().remove(watcher);
        issue.setUpdatedAt(Instant.now());
        return issue;
    }

    private Issue getIssueOrThrow(String id) {
        Issue issue = issues.get(id);
        if (issue == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Issue not found: " + id);
        }
        return issue;
    }
}
