package com.example.jiraissueservice.service;

import com.example.jiraissueservice.model.Issue;
import com.example.jiraissueservice.repository.IssueRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
public class IssueService {

    private final IssueRepository issueRepository;
    private final WatcherNotificationService watcherNotificationService;

    public IssueService(IssueRepository issueRepository, WatcherNotificationService watcherNotificationService) {
        this.issueRepository = issueRepository;
        this.watcherNotificationService = watcherNotificationService;
    }

    @Transactional(readOnly = true)
    public List<Issue> findAll() {
        return issueRepository.findAll().stream()
                .sorted(Comparator.comparing(Issue::getUpdatedAt).reversed())
                .toList();
    }

    @Transactional(readOnly = true)
    public Issue findById(String id) {
        return getIssueOrThrow(id);
    }

    @Transactional
    public Issue create(Issue issue) {
        Issue saved = issueRepository.save(issue);
        watcherNotificationService.notifyWatchers(saved.getId(), saved.getWatchers(), "created");
        return saved;
    }

    @Transactional
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

        Issue saved = issueRepository.save(existing);
        watcherNotificationService.notifyWatchers(id, saved.getWatchers(), "updated");
        return saved;
    }

    @Transactional
    public void delete(String id) {
        Issue removed = getIssueOrThrow(id);
        issueRepository.delete(removed);
        watcherNotificationService.notifyWatchers(id, removed.getWatchers(), "deleted");
    }

    @Transactional
    public Issue addWatcher(String id, String watcher) {
        Issue issue = getIssueOrThrow(id);
        issue.getWatchers().add(watcher);
        Issue saved = issueRepository.save(issue);
        watcherNotificationService.notifyWatchers(id, List.of(watcher), "watcher-added");
        return saved;
    }

    @Transactional
    public Issue removeWatcher(String id, String watcher) {
        Issue issue = getIssueOrThrow(id);
        issue.getWatchers().remove(watcher);
        return issueRepository.save(issue);
    }

    private Issue getIssueOrThrow(String id) {
        return issueRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Issue not found: " + id));
    }
}
