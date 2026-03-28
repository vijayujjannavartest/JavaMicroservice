package com.example.jiraissueservice.controller;

import com.example.jiraissueservice.model.Issue;
import com.example.jiraissueservice.model.NotificationMessage;
import com.example.jiraissueservice.service.IssueService;
import com.example.jiraissueservice.service.WatcherNotificationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/issues")
@CrossOrigin
public class IssueController {

    private final IssueService issueService;
    private final WatcherNotificationService watcherNotificationService;

    public IssueController(IssueService issueService, WatcherNotificationService watcherNotificationService) {
        this.issueService = issueService;
        this.watcherNotificationService = watcherNotificationService;
    }

    @GetMapping
    public List<Issue> all() {
        return issueService.findAll();
    }

    @GetMapping("/{id}")
    public Issue byId(@PathVariable String id) {
        return issueService.findById(id);
    }

    @PostMapping
    public Issue create(@Valid @RequestBody Issue issue) {
        return issueService.create(issue);
    }

    @PutMapping("/{id}")
    public Issue update(@PathVariable String id, @Valid @RequestBody Issue issue) {
        return issueService.update(id, issue);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        issueService.delete(id);
    }

    @PostMapping("/{id}/watchers")
    public Issue addWatcher(@PathVariable String id, @RequestBody Map<String, @NotBlank String> payload) {
        return issueService.addWatcher(id, payload.get("watcher"));
    }

    @DeleteMapping("/{id}/watchers/{watcher}")
    public Issue removeWatcher(@PathVariable String id, @PathVariable String watcher) {
        return issueService.removeWatcher(id, watcher);
    }

    @GetMapping("/notifications/{watcher}")
    public List<NotificationMessage> inbox(@PathVariable String watcher) {
        return watcherNotificationService.getInbox(watcher);
    }
}
