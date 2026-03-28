package com.example.jiraissueservice.repository;

import com.example.jiraissueservice.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRepository extends JpaRepository<Issue, String> {
}
