# Changelog

All notable changes to TaskFlow are recorded here. Format loosely follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [Unreleased]

_Nothing yet._

## [0.0.1] — 2026-06-01

### Added

- Task CRUD API (`/tasks`) scoped to the authenticated user, with optional
  tag filtering.
- Email-only login at `/auth/login` issuing an HS256 JWT.
- Static single-page web UI served from `src/main/resources/static/index.html`.
- `/health` endpoint.
