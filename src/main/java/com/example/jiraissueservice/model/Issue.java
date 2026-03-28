package com.example.jiraissueservice.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "issues")
public class Issue {

    @Id
    @Column(nullable = false, updatable = false, length = 32)
    private String id;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String summary;

    @Size(max = 5000)
    @Column(length = 5000)
    private String description;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String status;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String priority;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String assignee;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String reporter;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "issue_labels", joinColumns = @JoinColumn(name = "issue_id"))
    @Column(name = "label", nullable = false, length = 120)
    @OrderBy("label asc")
    private List<String> labels = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "issue_watchers", joinColumns = @JoinColumn(name = "issue_id"))
    @Column(name = "watcher", nullable = false, length = 120)
    private Set<String> watchers = new HashSet<>();

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        if (id == null || id.isBlank()) {
            id = "JIS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        updatedAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels == null ? new ArrayList<>() : new ArrayList<>(labels);
    }

    public Set<String> getWatchers() {
        return watchers;
    }

    public void setWatchers(Set<String> watchers) {
        this.watchers = watchers == null ? new HashSet<>() : new HashSet<>(watchers);
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
