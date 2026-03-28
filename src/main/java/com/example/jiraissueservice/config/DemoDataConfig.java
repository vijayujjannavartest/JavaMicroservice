package com.example.jiraissueservice.config;

import com.example.jiraissueservice.model.Issue;
import com.example.jiraissueservice.service.IssueService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Set;

@Configuration
public class DemoDataConfig {

    @Bean
    CommandLineRunner preloadIssues(IssueService issueService) {
        return args -> {
            if (!issueService.findAll().isEmpty()) {
                return;
            }

            Issue demoIssue = new Issue();
            demoIssue.setSummary("Build Jira issue microservice");
            demoIssue.setDescription("Create issue CRUD APIs and a Jira-like issue detail UI.");
            demoIssue.setStatus("In Progress");
            demoIssue.setPriority("High");
            demoIssue.setAssignee("Ava");
            demoIssue.setReporter("You");
            demoIssue.setLabels(List.of("learning", "spring-boot"));
            demoIssue.setWatchers(Set.of("Ava", "You"));

            issueService.create(demoIssue);
        };
    }
}
