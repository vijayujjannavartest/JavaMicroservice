# Jira Issue Service (Learning Project)

A Spring Boot microservice with a lightweight Jira-like issue UI.

## Features
- Issue CRUD APIs (`/api/issues`)
- Persistent relational storage with Spring Data JPA + H2
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

## Run
```bash
mvn spring-boot:run
```
Open: `http://localhost:8080`
H2 Console: `http://localhost:8080/h2-console`

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
- Replace local H2 with managed RDS PostgreSQL
- Containerize with Docker
- Deploy on ECS Fargate or Elastic Beanstalk
- Store frontend as static files in S3 + CloudFront (or keep served by Spring Boot)
- Add CI/CD pipeline (GitHub Actions)
