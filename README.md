# Jira Issue Service (Learning Project)

A Spring Boot microservice with a lightweight Jira-like issue UI.

## Features
- Issue CRUD APIs (`/api/issues`)
- Jira-like single issue view in browser
- Top navigation with **Create** action
- Delete issue from three-dot menu in issue view (top-right)
- Watchers field with notification inbox endpoint (`/api/issues/notifications/{watcher}`)

## Issue fields (10)
1. id
2. summary
3. description
4. status
5. priority
6. assignee
7. reporter
8. labels
9. watchers
10. updatedAt

## Run from terminal
```bash
mvn spring-boot:run
```
Open: `http://localhost:8080`

## Run in IntelliJ IDEA
1. Open IntelliJ IDEA.
2. Click **Open** and choose this folder (`JavaMicroservice`).
3. Wait for Maven import/indexing to finish.
4. Ensure JDK is set to **17**:
   - `File -> Project Structure -> Project SDK -> 17`.
5. Open `src/main/java/com/example/jiraissueservice/JiraIssueServiceApplication.java`.
6. Click the green run icon next to `main(...)` and select **Run 'JiraIssueServiceApplication'**.
7. When logs show `Started JiraIssueServiceApplication`, open `http://localhost:8080`.

## How to test the UI manually
Use this checklist in your browser once the app is running:

1. **Issue list loads**
   - Left panel should show at least one seeded issue (e.g. `JIS-...`).
2. **Open issue details**
   - Click an issue from the left panel.
   - Right panel should show summary, description, assignee, reporter, labels, watchers, status, priority.
3. **Create issue**
   - Click **Create** in top nav.
   - Fill fields and save.
   - New issue should appear in left list and open in detail view.
4. **Edit issue**
   - Open issue, click three dots (`⋯`) at top-right, choose **Edit issue**.
   - Change summary/description and save.
   - Updated values should be visible immediately.
5. **Delete issue**
   - Open issue, click three dots (`⋯`) -> **Delete issue**.
   - Issue should disappear from list.
6. **Watcher flow**
   - Add watcher from watcher input.
   - Click **Check my notifications** and enter watcher name.
   - You should see notifications when issue is created/updated/deleted or watcher is added.

## Optional API sanity test with curl
```bash
# list issues
curl http://localhost:8080/api/issues

# create issue
curl -X POST http://localhost:8080/api/issues \
  -H 'Content-Type: application/json' \
  -d '{
    "summary":"UI smoke test",
    "description":"Created from curl",
    "status":"To Do",
    "priority":"Medium",
    "assignee":"Alex",
    "reporter":"You",
    "labels":["test"],
    "watchers":["Alex"]
  }'
```

## Troubleshooting (important)
If Maven dependency download fails with HTTP 403 in your environment:
- Try running from a network that allows `https://repo.maven.apache.org`.
- In IntelliJ, open Maven settings and ensure it is using **Maven Central**.
- If your company network blocks Maven Central, configure proxy in Maven (`~/.m2/settings.xml`).

## API quick reference
- `GET /api/issues`
- `GET /api/issues/{id}`
- `POST /api/issues`
- `PUT /api/issues/{id}`
- `DELETE /api/issues/{id}`
- `POST /api/issues/{id}/watchers` with `{ "watcher": "name" }`
- `DELETE /api/issues/{id}/watchers/{watcher}`
- `GET /api/issues/notifications/{watcher}`

## Next steps (AWS path)
- Add persistent DB (RDS PostgreSQL)
- Containerize with Docker
- Deploy on ECS Fargate or Elastic Beanstalk
- Store frontend as static files in S3 + CloudFront (or keep served by Spring Boot)
- Add CI/CD pipeline (GitHub Actions)
