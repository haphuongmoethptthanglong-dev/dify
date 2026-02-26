# Phase 0: Contract Inventory — Dify Python→Java Migration

> Generated: 2026-02-26
> Source: Python Flask API at `/api`
> Target: Java Spring Boot at `/api-java` (Strangler Pattern)

---

## Table of Contents

1. [Endpoint Inventory](#1-endpoint-inventory)
2. [Auth Behavior Matrix](#2-auth-behavior-matrix)
3. [Error Code Catalog](#3-error-code-catalog)
4. [Async Task Inventory](#4-async-task-inventory)
5. [Scheduled Task Inventory](#5-scheduled-task-inventory)
6. [CORS Policy Matrix](#6-cors-policy-matrix)
7. [Database Schema Snapshot](#7-database-schema-snapshot)

---

## 1. Endpoint Inventory

**Grand Total: ~400+ unique API endpoints across 7 blueprint groups**

### 1.1 Blueprint Summary

| Blueprint   | URL Prefix     | Endpoints | Auth Type          | Description                  |
| ----------- | -------------- | --------- | ------------------ | ---------------------------- |
| Console     | `/console/api` | ~250+     | JWT Passport       | Admin dashboard API          |
| Service API | `/v1`          | ~78       | API Token (Bearer) | External developer API       |
| Web API     | `/api`         | ~42       | JWT Passport       | Webapp (end-user facing)     |
| Inner API   | `/inner/api`   | 19        | X-Inner-Api-Key    | Service-to-service (plugins) |
| Files       | `/files`       | 5         | Mixed              | File upload/download         |
| MCP         | `/mcp`         | 1         | Server Code        | MCP JSON-RPC                 |
| Trigger     | `/triggers`    | 3         | Webhook Secret     | Plugin/webhook triggers      |

### 1.2 Console API (`/console/api`) — ~250+ endpoints

#### Root Controllers (9 endpoints)

| Method   | Path                                   | Auth | Source             |
| -------- | -------------------------------------- | ---- | ------------------ |
| GET/POST | `/console/api/init`                    | None | `init_validate.py` |
| GET      | `/console/api/ping`                    | None | `ping.py`          |
| GET/POST | `/console/api/setup`                   | None | `setup.py`         |
| GET      | `/console/api/version`                 | None | `version.py`       |
| GET      | `/console/api/spec/schema-definitions` | JWT  | `spec.py`          |
| GET      | `/console/api/features`                | JWT  | `feature.py`       |
| GET      | `/console/api/system-features`         | None | `feature.py`       |

#### Auth (23 endpoints) — `auth/`

- Login/logout, refresh token, email code login, reset password
- OAuth login/callback (GitHub, Google, etc.)
- OAuth server provider endpoints
- Email registration flow, account activation
- Data source bearer auth (CRUD)
- Data source OAuth (login, callback, binding, sync)

#### Account (19 endpoints) — `account.py`

- Profile CRUD, name/avatar/language/theme/timezone/password updates
- Account init, integrates, delete/verify/feedback
- Education verify/submit/autocomplete
- Change email flow (send/validity/reset/check-unique)

#### Admin (4 endpoints) — `admin.py`

- Insert/delete explore apps/banners (cloud only)

#### API Keys (6 endpoints) — `apikey.py`

- CRUD for app and dataset API keys

#### App Management (12+ endpoints) — `app.py`

- App CRUD, copy, export, name/icon, site-enable/api-enable, trace

#### App Import (3 endpoints) — `app_import.py`

- Import, confirm, check-dependencies

#### Annotations (14 endpoints) — `annotation.py`

- CRUD, batch import/status, reply actions, hit histories, export

#### Audio (3 endpoints) — `audio.py`

- Audio-to-text, text-to-audio, voice list

#### Completion/Chat (4 endpoints) — `completion.py`

- Completion messages, chat messages, stop tasks

#### Conversations (6 endpoints) — `conversation.py`

- Completion/chat conversation list/detail/delete

#### Conversation Variables (1 endpoint) — `conversation_variables.py`

#### Generator/LLM (5 endpoints) — `generator.py`

- Rule/code/structured-output/instruction generation, instruction template

#### Messages (6 endpoints) — `message.py`

- Chat message list, feedback CRUD, annotation count, suggested questions, message detail

#### Model Config (1 endpoint) — `model_config.py`

#### Site (2 endpoints) — `site.py`

- Update site, reset access token

#### Statistics (8 endpoints) — `statistic.py`

- Daily messages/conversations/end-users/token-costs/session-interactions/satisfaction/response-time/tokens-per-second

#### Workflow Statistics (4 endpoints) — `workflow_statistic.py`

- Daily workflow runs/terminals/token-costs, average app interactions

#### Workflow (25+ endpoints) — `workflow.py`

- Draft CRUD/run, advanced-chat draft run, iteration/loop node runs
- Human-input form preview/run/delivery-test, publish, default block configs
- Convert-to-workflow, workflow CRUD, node last-run, trigger run/node/run-all

#### Workflow Run (8 endpoints) — `workflow_run.py`

- List/count/detail/export/node-executions/pause-details for both workflow and advanced-chat

#### Workflow Draft Variables (8 endpoints) — `workflow_draft_variable.py`

- Variable collection CRUD, node variables, individual variable CRUD/reset
- Conversation/system/environment variables

#### Workflow App Log (2 endpoints) — `workflow_app_log.py`

#### MCP Server (2 endpoints) — `mcp_server.py`

- App MCP server CRUD, refresh

#### Workflow Triggers (3 endpoints) — `workflow_trigger.py`

- Webhook trigger, app triggers list, trigger enable

#### Ops Trace (4 endpoints) — `ops_trace.py`

- Trace config CRUD

#### Extensions (6 endpoints) — `extension.py`

- Code-based extension list, API-based extension CRUD

#### Files (4 endpoints) — `files.py`

- Upload, preview, support-type

#### Remote Files (2 endpoints) — `remote_files.py`

#### Human Input Form (3 endpoints) — `human_input_form.py`

#### Prompt Templates (1 endpoint) — `advanced_prompt_template.py`

#### Agent Log (1 endpoint) — `agent.py`

#### Workspace (9 endpoints) — `workspace.py`

- List, all-workspaces, current, switch, custom-config, webapp-logo, info, permission

#### Members (8 endpoints) — `members.py`

- List, invite, cancel, update-role, dataset-operators, owner transfer flow

#### Plugin (31 endpoints) — `plugin.py`

- Debugging key, list, latest versions, installations, icon, asset
- Upload (pkg/github/bundle), install (pkg/github/marketplace)
- Fetch manifest/tasks, delete tasks, upgrade, uninstall
- Permission, dynamic options, preferences, auto-upgrade, readme

#### Tool Providers (37+ endpoints) — `tool_providers.py`

- List providers, builtin/api/workflow tool CRUD, credentials, icons, schemas
- MCP tools, OAuth callbacks, default credentials, custom OAuth clients

#### Trigger Providers (16 endpoints) — `trigger_providers.py`

- List, info, icon, subscriptions CRUD
- Builder create/get/verify/update/logs/build, OAuth authorize/callback, client manage

#### Model Providers (7+ endpoints) — `model_providers.py`

- List, credentials CRUD/switch/validate, icon, preferred-provider-type, checkout-url

#### Load Balancing (2 endpoints) — `load_balancing_config.py`

#### Agent Providers (2 endpoints) — `agent_providers.py`

#### Endpoints (7 endpoints) — `endpoint.py`

- Create, list, list-plugin, delete, update, enable, disable

#### Billing (4 endpoints) — `billing/`

- Subscription, invoices, partner tenants, compliance download

#### Tags (5 endpoints) — `tag/tags.py`

- List, create, update, delete, binding create

#### Datasets (60+ endpoints) — `datasets/`

- Dataset CRUD, use-check, queries, indexing-estimate, related-apps, indexing-status
- API keys, api-base-info, retrieval-setting, error-docs, permission-users, auto-disable-logs
- Process rules, document CRUD, download, batch operations, metadata, status actions
- Segment CRUD, batch import, child chunks CRUD
- Hit testing, data source integrates, Notion sync/preview/indexing
- External knowledge API CRUD, external datasets, external hit testing
- Document pipeline execution logs, summary generation/status

#### Explore/Installed Apps (25+ endpoints) — `explore/`

- Completion/chat, conversations, messages, saved messages, audio
- Workflow, installed apps CRUD, parameters, meta
- Recommended apps list/detail, banners
- Trial app endpoints (chat, completion, workflow, conversations, messages, audio, parameters)

### 1.3 Service API (`/v1`) — ~78 endpoints

| Domain           | Count | Endpoints                                                         |
| ---------------- | ----- | ----------------------------------------------------------------- |
| App              | 4     | Index, parameters, meta, info                                     |
| Completion/Chat  | 4     | Completion/chat messages + stop                                   |
| Audio            | 2     | Audio-to-text, text-to-audio                                      |
| Annotations      | 6     | Reply action, status, list, create, update, delete                |
| Files            | 2     | Upload, preview                                                   |
| Conversations    | 5     | List, delete, rename, variables, variable detail                  |
| Messages         | 4     | List, feedback, app feedbacks, suggested                          |
| Site             | 1     | Get site                                                          |
| Workflow         | 5     | Run detail, run, run-by-id, stop, logs                            |
| Datasets         | 13    | CRUD, status, tags CRUD, tag binding/unbinding                    |
| Documents        | 8     | Create/update by text/file, list, indexing-status, detail, delete |
| Segments         | 5+    | CRUD + child chunks CRUD                                          |
| Hit Testing      | 2     | hit-testing, retrieve                                             |
| Metadata         | 7     | CRUD, built-in, actions, document metadata                        |
| RAG Pipeline     | 4     | Datasource plugins, node run, pipeline run, file upload           |
| End User         | 1     | Get end user                                                      |
| Workspace Models | 1     | Available models by type                                          |

### 1.4 Web API (`/api`) — ~42 endpoints

| Domain           | Count | Endpoints                                                          |
| ---------------- | ----- | ------------------------------------------------------------------ |
| App              | 4     | Parameters, meta, access-mode, permission                          |
| Audio            | 2     | Audio-to-text, text-to-audio                                       |
| Completion/Chat  | 4     | Completion/chat + stop                                             |
| Conversations    | 5     | List, delete, rename, pin, unpin                                   |
| Features         | 1     | System features                                                    |
| Files            | 1     | Upload                                                             |
| Forgot Password  | 3     | Send reset email, check token validity, reset password (enterprise)|
| Human Input      | 2     | Form get/submit                                                    |
| Login            | 5     | Login, status, logout, email-code-login, validity (enterprise SSO) |
| Messages         | 4     | List, feedback, more-like-this, suggested                          |
| Passport         | 1     | Get passport token                                                 |
| Remote Files     | 2     | Info, upload                                                       |
| Saved Messages   | 3     | List, create, delete                                               |
| Site             | 1     | Get site                                                           |
| Workflow         | 3     | Run, stop, events                                                  |
| Workflow Events  | 1     | Event stream after workflow resume                                 |

### 1.5 Inner API (`/inner/api`) — 19 endpoints

All POST only. Auth: `X-Inner-Api-Key` header.

| Category           | Count | Endpoints                                                                                                                                                                                    |
| ------------------ | ----- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Mail               | 2     | Send email, send inner email                                                                                                                                                                 |
| Workspace          | 2     | Workspace operations                                                                                                                                                                         |
| Plugin Invocations | 15    | LLM, LLM-structured-output, text-embedding, rerank, TTS, speech2text, moderation, tool, parameter-extractor, question-classifier, app, encrypt, summary, upload-file-request, fetch-app-info |

### 1.6 Files (`/files`) — 5 endpoints

| Method | Path                                           | Auth       |
| ------ | ---------------------------------------------- | ---------- |
| POST   | `/files/upload/for-plugin`                     | Plugin key |
| GET    | `/files/tools/<file_id>.<ext>`                 | Signature  |
| GET    | `/files/<file_id>/image-preview`               | Signature  |
| GET    | `/files/<file_id>/file-preview`                | Signature  |
| GET    | `/files/workspaces/<workspace_id>/webapp-logo` | None       |

### 1.7 MCP (`/mcp`) — 1 endpoint

| Method | Path                            | Auth                   |
| ------ | ------------------------------- | ---------------------- |
| POST   | `/mcp/server/<server_code>/mcp` | Server code validation |

### 1.8 Trigger (`/triggers`) — 3 endpoints

| Method | Path                                   | Auth           |
| ------ | -------------------------------------- | -------------- |
| ALL    | `/triggers/plugin/<endpoint_id>`       | Plugin auth    |
| ALL    | `/triggers/webhook/<webhook_id>`       | Webhook secret |
| ALL    | `/triggers/webhook-debug/<webhook_id>` | Webhook secret |

---

## 2. Auth Behavior Matrix

### 2.1 Per-Blueprint Auth

| Blueprint   | URL Prefix     | Auth Type    | Token Header                          | Token Format                                              | User Resolution                 | Key File               |
| ----------- | -------------- | ------------ | ------------------------------------- | --------------------------------------------------------- | ------------------------------- | ---------------------- |
| Console     | `/console/api` | JWT Passport | `Authorization: Bearer <token>`       | JWT HS256 (`user_id`, `exp`, `iss`, `sub`)                | `Account` via `ext_login.py:23` | `libs/passport.py`     |
| Service API | `/v1`          | API Token    | `Authorization: Bearer <api_key>`     | UUID from `ApiToken` model                                | `Account` (owner) or `EndUser`  | `service_api/wraps.py` |
| Web         | `/api`         | JWT Passport | `X-App-Code` + `Authorization`/Cookie | JWT (`app_code`, `app_id`, `end_user_id`, `token_source`) | `EndUser` via `web/wraps.py:27` | `libs/passport.py`     |
| Inner API   | `/inner/api`   | Static Key   | `X-Inner-Api-Key`                     | Static key from `dify_config.INNER_API_KEY`               | None (service-to-service)       | `inner_api/wraps.py`   |
| Files       | `/files`       | Mixed        | `Authorization`, `X-CSRF-Token`       | Console JWT or API Token                                  | `Account`                       | Inherits parent        |
| MCP         | `/mcp`         | Server Code  | URL path `server_code`                | Code lookup in DB                                         | `EndUser` (type="mcp")          | `ext_login.py:89`      |
| Trigger     | `/triggers`    | Webhook      | `Authorization`, `X-App-Code`         | Config-based                                              | Varies                          | Per-trigger            |

### 2.2 Auth Decorators

#### Console (`controllers/console/wraps.py`)

| Decorator                                               | What It Checks                  | Error on Failure                     |
| ------------------------------------------------------- | ------------------------------- | ------------------------------------ |
| `@login_required` (`libs/login.py:41`)                  | User authenticated, CSRF valid  | 401                                  |
| `@account_initialization_required` (`:39`)              | Account status != UNINITIALIZED | AccountNotInitializedError           |
| `@setup_required` (`:217`)                              | DifySetup exists (self-hosted)  | NotInitValidateError / NotSetupError |
| `@only_edition_self_hosted` (`:74`)                     | EDITION == SELF_HOSTED          | 404                                  |
| `@only_edition_cloud` (`:52`)                           | EDITION == CLOUD                | 404                                  |
| `@only_edition_enterprise` (`:63`)                      | ENTERPRISE_ENABLED              | 404                                  |
| `@edit_permission_required` (`:311`)                    | User has edit permission        | 403                                  |
| `@is_admin_or_owner_required` (`:329`)                  | User is admin or owner          | 403                                  |
| `@cloud_edition_billing_enabled` (`:85`)                | Billing feature enabled         | 403                                  |
| `@cloud_edition_billing_resource_check` (`:97`)         | Resource limits not exceeded    | 403                                  |
| `@cloud_edition_billing_knowledge_limit_check` (`:139`) | Not SANDBOX plan for knowledge  | 403                                  |
| `@cloud_edition_billing_rate_limit_check` (`:162`)      | Rate limits not exceeded        | 403                                  |
| `@enterprise_license_required` (`:235`)                 | License active                  | UnauthorizedAndForceLogout           |
| `@email_password_login_enabled` (`:247`)                | Email/password login enabled    | 403                                  |
| `@email_register_enabled` (`:260`)                      | Registration enabled            | 403                                  |
| `@enable_change_email` (`:273`)                         | Email change enabled            | 403                                  |
| `@is_allow_transfer_owner` (`:286`)                     | Owner transfer permitted        | 403                                  |
| `@knowledge_pipeline_publish_enabled` (`:299`)          | Knowledge pipeline publish on   | 403                                  |
| `@annotation_import_rate_limit` (`:345`)                | Annotation import rate limit    | 429                                  |
| `@cloud_utm_record` (`:198`)                            | Records UTM info (no block)     | (passthrough)                        |

#### Service API (`controllers/service_api/wraps.py`)

| Decorator                                               | What It Checks                              | Error on Failure |
| ------------------------------------------------------- | ------------------------------------------- | ---------------- |
| `@validate_app_token` (`:47`)                           | API token valid, app exists, tenant active  | 401/403          |
| `@validate_dataset_token` (`:216`)                      | Dataset API token valid, dataset accessible | 401/403/404      |
| `@cloud_edition_billing_resource_check` (`:127`)        | Resource limits                             | 403              |
| `@cloud_edition_billing_knowledge_limit_check` (`:157`) | Plan check                                  | 403              |
| `@cloud_edition_billing_rate_limit_check` (`:179`)      | Rate limits                                 | 403              |

#### Web API (`controllers/web/wraps.py`)

| Decorator                     | What It Checks                                              | Error on Failure |
| ----------------------------- | ----------------------------------------------------------- | ---------------- |
| `@validate_jwt_token` (`:27`) | JWT passport valid, app exists, site valid, end_user exists | 401/404          |

#### Inner API (`controllers/inner_api/wraps.py`)

| Decorator                                 | What It Checks                 | Error on Failure |
| ----------------------------------------- | ------------------------------ | ---------------- |
| `@billing_inner_api_only` (`:17`)         | INNER_API enabled, key matches | 404/401          |
| `@enterprise_inner_api_only` (`:33`)      | INNER_API enabled, key matches | 404/401          |
| `@enterprise_inner_api_user_auth` (`:49`) | HMAC signature validation      | Pass-through     |
| `@plugin_inner_api_only` (`:85`)          | PLUGIN_DAEMON_KEY matches      | 404              |

### 2.3 JWT Configuration

| Parameter        | Value                                           |
| ---------------- | ----------------------------------------------- |
| Algorithm        | HS256                                           |
| Secret           | `dify_config.SECRET_KEY`                        |
| Expiration       | `ACCESS_TOKEN_EXPIRE_MINUTES` (default 60 min)  |
| Issuer           | `EDITION` value                                 |
| Console Subject  | `"Console API Passport"`                        |
| Web Payload      | `{app_code, app_id, end_user_id, token_source}` |
| API Token Cache  | Redis via `ApiTokenCache`                       |
| API Token Scopes | `"app"`, `"dataset"`                            |

### 2.4 Flask-Login Resolution (`extensions/ext_login.py`)

| Blueprint | Auth Method               | User Type                |
| --------- | ------------------------- | ------------------------ |
| console   | JWT passport verification | `Account`                |
| inner_api | JWT passport verification | `Account`                |
| web       | Webapp passport or JWT    | `EndUser`                |
| mcp       | Server code lookup        | `EndUser` (type="mcp")   |
| Admin API | `ADMIN_API_KEY` header    | `Account` (tenant owner) |

---

## 3. Error Code Catalog

**Total: 141 BaseHTTPException subclasses across 14 files**

### 3.1 Summary by Location

| File                                        | Count |
| ------------------------------------------- | ----- |
| `controllers/console/error.py`              | 17    |
| `controllers/console/auth/error.py`         | 22    |
| `controllers/console/app/error.py`          | 19    |
| `controllers/console/workspace/error.py`    | 6     |
| `controllers/console/explore/error.py`      | 7     |
| `controllers/console/datasets/error.py`     | 13    |
| `controllers/web/error.py`                  | 21    |
| `controllers/service_api/app/error.py`      | 15    |
| `controllers/service_api/dataset/error.py`  | 9     |
| `controllers/service_api/end_user/error.py` | 1     |
| `controllers/common/errors.py`              | 5     |
| `core/tools/errors.py`                      | 1     |
| `core/app/apps/workflow/errors.py`          | 1     |
| `services/human_input_service.py`           | 4     |

### 3.2 Console Errors (`controllers/console/error.py`)

| Class                         | error_code                      | HTTP | Description                    |
| ----------------------------- | ------------------------------- | ---- | ------------------------------ |
| AlreadySetupError             | `already_setup`                 | 403  | Dify already installed         |
| NotSetupError                 | `not_setup`                     | 401  | Dify not initialized           |
| NotInitValidateError          | `not_init_validated`            | 401  | Init validation incomplete     |
| InitValidateFailedError       | `init_validate_failed`          | 401  | Init validation failed         |
| AccountNotLinkTenantError     | `account_not_link_tenant`       | 403  | Account not linked to tenant   |
| AlreadyActivateError          | `already_activate`              | 403  | Account already activated      |
| NotAllowedCreateWorkspace     | `not_allowed_create_workspace`  | 400  | Workspace creation denied      |
| WorkspaceMembersLimitExceeded | `limit_exceeded`                | 400  | Member limit exceeded          |
| WorkspacesLimitExceeded       | `limit_exceeded`                | 400  | Workspace limit exceeded       |
| AccountBannedError            | `account_banned`                | 400  | Account banned                 |
| AccountNotFound               | `account_not_found`             | 400  | Account not found              |
| EmailSendIpLimitError         | `email_send_ip_limit`           | 429  | IP rate limit on email         |
| UnauthorizedAndForceLogout    | `unauthorized_and_force_logout` | 401  | Force logout                   |
| AccountInFreezeError          | `account_in_freeze`             | 400  | Account deleted within 30d     |
| EducationVerifyLimitError     | `education_verify_limit`        | 429  | Education verify rate limit    |
| EducationActivateLimitError   | `education_activate_limit`      | 429  | Education activate rate limit  |
| ComplianceRateLimitError      | `compliance_rate_limit`         | 429  | Compliance download rate limit |

### 3.3 Console Auth Errors (`controllers/console/auth/error.py`)

| Class                                          | error_code                                        | HTTP | Description                |
| ---------------------------------------------- | ------------------------------------------------- | ---- | -------------------------- |
| ApiKeyAuthFailedError                          | `auth_failed`                                     | 500  | Auth failed                |
| InvalidEmailError                              | `invalid_email`                                   | 400  | Invalid email              |
| PasswordMismatchError                          | `password_mismatch`                               | 400  | Passwords don't match      |
| InvalidTokenError                              | `invalid_or_expired_token`                        | 400  | Token invalid/expired      |
| PasswordResetRateLimitExceededError            | `password_reset_rate_limit_exceeded`              | 429  | Too many reset emails      |
| EmailRegisterRateLimitExceededError            | `email_register_rate_limit_exceeded`              | 429  | Too many register emails   |
| EmailChangeRateLimitExceededError              | `email_change_rate_limit_exceeded`                | 429  | Too many change emails     |
| OwnerTransferRateLimitExceededError            | `owner_transfer_rate_limit_exceeded`              | 429  | Too many transfer emails   |
| EmailCodeError                                 | `email_code_error`                                | 400  | Invalid/expired email code |
| EmailOrPasswordMismatchError                   | `email_or_password_mismatch`                      | 400  | Email/password mismatch    |
| AuthenticationFailedError                      | `authentication_failed`                           | 401  | Invalid credentials        |
| EmailPasswordLoginLimitError                   | `email_code_login_limit`                          | 429  | Too many password attempts |
| EmailCodeLoginRateLimitExceededError           | `email_code_login_rate_limit_exceeded`            | 429  | Too many login emails      |
| EmailCodeAccountDeletionRateLimitExceededError | `email_code_account_deletion_rate_limit_exceeded` | 429  | Too many deletion emails   |
| EmailPasswordResetLimitError                   | `email_password_reset_limit`                      | 429  | Failed reset attempts      |
| EmailRegisterLimitError                        | `email_register_limit`                            | 429  | Failed register attempts   |
| EmailChangeLimitError                          | `email_change_limit`                              | 429  | Failed change attempts     |
| EmailAlreadyInUseError                         | `email_already_in_use`                            | 400  | Email taken                |
| OwnerTransferLimitError                        | `owner_transfer_limit`                            | 429  | Failed transfer attempts   |
| NotOwnerError                                  | `not_owner`                                       | 400  | Not workspace owner        |
| CannotTransferOwnerToSelfError                 | `cannot_transfer_owner_to_self`                   | 400  | Self-transfer              |
| MemberNotInTenantError                         | `member_not_in_tenant`                            | 400  | Member not in workspace    |

### 3.4 Console App Errors (`controllers/console/app/error.py`)

| Class                                 | error_code                            | HTTP | Description                   |
| ------------------------------------- | ------------------------------------- | ---- | ----------------------------- |
| AppNotFoundError                      | `app_not_found`                       | 404  | App not found                 |
| ProviderNotInitializeError            | `provider_not_initialize`             | 400  | No model provider credentials |
| ProviderQuotaExceededError            | `provider_quota_exceeded`             | 400  | Hosted model quota exhausted  |
| ProviderModelCurrentlyNotSupportError | `model_currently_not_support`         | 400  | Model not supported           |
| ConversationCompletedError            | `conversation_completed`              | 400  | Conversation ended            |
| AppUnavailableError                   | `app_unavailable`                     | 400  | App unavailable               |
| CompletionRequestError                | `completion_request_error`            | 400  | Completion failed             |
| AppMoreLikeThisDisabledError          | `app_more_like_this_disabled`         | 403  | Feature disabled              |
| NoAudioUploadedError                  | `no_audio_uploaded`                   | 400  | No audio                      |
| AudioTooLargeError                    | `audio_too_large`                     | 413  | Audio too large               |
| UnsupportedAudioTypeError             | `unsupported_audio_type`              | 415  | Audio type not allowed        |
| ProviderNotSupportSpeechToTextError   | `provider_not_support_speech_to_text` | 400  | STT not supported             |
| DraftWorkflowNotExist                 | `draft_workflow_not_exist`            | 404  | Draft not initialized         |
| DraftWorkflowNotSync                  | `draft_workflow_not_sync`             | 409  | Workflow out of sync          |
| TracingConfigNotExist                 | `trace_config_not_exist`              | 400  | Trace config missing          |
| TracingConfigIsExist                  | `trace_config_is_exist`               | 400  | Trace config exists           |
| TracingConfigCheckError               | `trace_config_check_error`            | 400  | Invalid credentials           |
| InvokeRateLimitError                  | `rate_limit_error`                    | 429  | Rate limit                    |
| NeedAddIdsError                       | `need_add_ids`                        | 400  | IDs required                  |

### 3.5 Console Workspace Errors (`controllers/console/workspace/error.py`)

| Class                           | error_code                      | HTTP | Description                 |
| ------------------------------- | ------------------------------- | ---- | --------------------------- |
| RepeatPasswordNotMatchError     | `repeat_password_not_match`     | 400  | Passwords don't match       |
| CurrentPasswordIncorrectError   | `current_password_incorrect`    | 400  | Wrong current password      |
| InvalidInvitationCodeError      | `invalid_invitation_code`       | 400  | Bad invite code             |
| AccountAlreadyInitedError       | `account_already_inited`        | 400  | Account already initialized |
| AccountNotInitializedError      | `account_not_initialized`       | 400  | Account not initialized     |
| InvalidAccountDeletionCodeError | `invalid_account_deletion_code` | 400  | Bad deletion code           |

### 3.6 Console Explore Errors (`controllers/console/explore/error.py`)

| Class                                         | error_code                                      | HTTP | Description          |
| --------------------------------------------- | ----------------------------------------------- | ---- | -------------------- |
| NotCompletionAppError                         | `not_completion_app`                            | 400  | Wrong app mode       |
| NotChatAppError                               | `not_chat_app`                                  | 400  | Wrong app mode       |
| NotWorkflowAppError                           | `not_workflow_app`                              | 400  | Wrong app mode       |
| AppSuggestedQuestionsAfterAnswerDisabledError | `app_suggested_questions_after_answer_disabled` | 403  | Feature disabled     |
| AppAccessDeniedError                          | `access_denied`                                 | 403  | Access denied        |
| TrialAppNotAllowed                            | `trial_app_not_allowed`                         | 403  | Trial not allowed    |
| TrialAppLimitExceeded                         | `trial_app_limit_exceeded`                      | 403  | Trial limit exceeded |

### 3.7 Console Dataset Errors (`controllers/console/datasets/error.py`)

| Class                          | error_code                       | HTTP | Description               |
| ------------------------------ | -------------------------------- | ---- | ------------------------- |
| DatasetNotInitializedError     | `dataset_not_initialized`        | 400  | Dataset still indexing    |
| ArchivedDocumentImmutableError | `archived_document_immutable`    | 403  | Archived doc not editable |
| DatasetNameDuplicateError      | `dataset_name_duplicate`         | 409  | Duplicate name            |
| InvalidActionError             | `invalid_action`                 | 400  | Invalid action            |
| DocumentAlreadyFinishedError   | `document_already_finished`      | 400  | Doc already processed     |
| DocumentIndexingError          | `document_indexing`              | 400  | Doc being processed       |
| InvalidMetadataError           | `invalid_metadata`               | 400  | Bad metadata              |
| WebsiteCrawlError              | `crawl_failed`                   | 500  | Crawl failed              |
| DatasetInUseError              | `dataset_in_use`                 | 409  | Dataset in use            |
| IndexingEstimateError          | `indexing_estimate_error`        | 500  | Indexing estimate failed  |
| ChildChunkIndexingError        | `child_chunk_indexing_error`     | 500  | Child chunk index failed  |
| ChildChunkDeleteIndexError     | `child_chunk_delete_index_error` | 500  | Child chunk delete failed |
| PipelineNotFoundError          | `pipeline_not_found`             | 404  | Pipeline not found        |

### 3.8 Web Errors (`controllers/web/error.py`)

| Class                                         | error_code                                      | HTTP | Description             |
| --------------------------------------------- | ----------------------------------------------- | ---- | ----------------------- |
| AppUnavailableError                           | `app_unavailable`                               | 400  | App unavailable         |
| NotCompletionAppError                         | `not_completion_app`                            | 400  | Wrong app mode          |
| NotChatAppError                               | `not_chat_app`                                  | 400  | Wrong app mode          |
| NotWorkflowAppError                           | `not_workflow_app`                              | 400  | Wrong app mode          |
| ConversationCompletedError                    | `conversation_completed`                        | 400  | Conversation ended      |
| ProviderNotInitializeError                    | `provider_not_initialize`                       | 400  | No provider credentials |
| ProviderQuotaExceededError                    | `provider_quota_exceeded`                       | 400  | Quota exhausted         |
| ProviderModelCurrentlyNotSupportError         | `model_currently_not_support`                   | 400  | Model not supported     |
| CompletionRequestError                        | `completion_request_error`                      | 400  | Completion failed       |
| AppMoreLikeThisDisabledError                  | `app_more_like_this_disabled`                   | 403  | Feature disabled        |
| AppSuggestedQuestionsAfterAnswerDisabledError | `app_suggested_questions_after_answer_disabled` | 403  | Feature disabled        |
| NoAudioUploadedError                          | `no_audio_uploaded`                             | 400  | No audio                |
| AudioTooLargeError                            | `audio_too_large`                               | 413  | Audio too large         |
| UnsupportedAudioTypeError                     | `unsupported_audio_type`                        | 415  | Audio type not allowed  |
| ProviderNotSupportSpeechToTextError           | `provider_not_support_speech_to_text`           | 400  | STT not supported       |
| WebAppAuthRequiredError                       | `web_sso_auth_required`                         | 401  | SSO auth required       |
| WebAppAuthAccessDeniedError                   | `web_app_access_denied`                         | 401  | Access denied           |
| InvokeRateLimitError                          | `rate_limit_error`                              | 429  | Rate limit              |
| WebFormRateLimitExceededError                 | `web_form_rate_limit_exceeded`                  | 429  | Form rate limit         |
| NotFoundError                                 | `not_found`                                     | 404  | Not found               |
| InvalidArgumentError                          | `invalid_param`                                 | 400  | Invalid parameter       |

### 3.9 Service API App Errors (`controllers/service_api/app/error.py`)

| Class                                 | error_code                            | HTTP | Description             |
| ------------------------------------- | ------------------------------------- | ---- | ----------------------- |
| AppUnavailableError                   | `app_unavailable`                     | 400  | App unavailable         |
| NotCompletionAppError                 | `not_completion_app`                  | 400  | Wrong app mode          |
| NotChatAppError                       | `not_chat_app`                        | 400  | Wrong app mode          |
| NotWorkflowAppError                   | `not_workflow_app`                    | 400  | Wrong app mode          |
| ConversationCompletedError            | `conversation_completed`              | 400  | Conversation ended      |
| ProviderNotInitializeError            | `provider_not_initialize`             | 400  | No provider credentials |
| ProviderQuotaExceededError            | `provider_quota_exceeded`             | 400  | Quota exhausted         |
| ProviderModelCurrentlyNotSupportError | `model_currently_not_support`         | 400  | Model not supported     |
| CompletionRequestError                | `completion_request_error`            | 400  | Completion failed       |
| NoAudioUploadedError                  | `no_audio_uploaded`                   | 400  | No audio                |
| AudioTooLargeError                    | `audio_too_large`                     | 413  | Audio too large         |
| UnsupportedAudioTypeError             | `unsupported_audio_type`              | 415  | Audio type not allowed  |
| ProviderNotSupportSpeechToTextError   | `provider_not_support_speech_to_text` | 400  | STT not supported       |
| FileNotFoundError                     | `file_not_found`                      | 404  | File not found          |
| FileAccessDeniedError                 | `file_access_denied`                  | 403  | File access denied      |

### 3.10 Service API Dataset Errors (`controllers/service_api/dataset/error.py`)

| Class                          | error_code                    | HTTP | Description            |
| ------------------------------ | ----------------------------- | ---- | ---------------------- |
| DatasetNotInitializedError     | `dataset_not_initialized`     | 400  | Dataset still indexing |
| ArchivedDocumentImmutableError | `archived_document_immutable` | 403  | Archived doc           |
| DatasetNameDuplicateError      | `dataset_name_duplicate`      | 409  | Duplicate name         |
| InvalidActionError             | `invalid_action`              | 400  | Invalid action         |
| DocumentAlreadyFinishedError   | `document_already_finished`   | 400  | Doc processed          |
| DocumentIndexingError          | `document_indexing`           | 400  | Doc being processed    |
| InvalidMetadataError           | `invalid_metadata`            | 400  | Bad metadata           |
| DatasetInUseError              | `dataset_in_use`              | 409  | Dataset in use         |
| PipelineRunError               | `pipeline_run_error`          | 500  | Pipeline run error     |

### 3.11 Common & Other Errors

**`controllers/common/errors.py`** (5):
| Class | error_code | HTTP |
|-------|-----------|------|
| FileTooLargeError | `file_too_large` | 413 |
| UnsupportedFileTypeError | `unsupported_file_type` | 415 |
| BlockedFileExtensionError | `file_extension_blocked` | 400 |
| TooManyFilesError | `too_many_files` | 400 |
| NoFileUploadedError | `no_file_uploaded` | 400 |

**`controllers/service_api/end_user/error.py`** (1):
| Class | error_code | HTTP |
|-------|-----------|------|
| EndUserNotFoundError | `end_user_not_found` | 404 |

**`core/tools/errors.py`** (1):
| Class | error_code | HTTP |
|-------|-----------|------|
| WorkflowToolHumanInputNotSupportedError | `workflow_tool_human_input_not_supported` | 400 |

**`core/app/apps/workflow/errors.py`** (1):
| Class | error_code | HTTP |
|-------|-----------|------|
| WorkflowPausedInBlockingModeError | `workflow_paused_in_blocking_mode` | 400 |

**`services/human_input_service.py`** (4):
| Class | error_code | HTTP |
|-------|-----------|------|
| FormSubmittedError | `human_input_form_submitted` | 412 |
| FormNotFoundError | `human_input_form_not_found` | 404 |
| InvalidFormDataError | `invalid_form_data` | 400 |
| FormExpiredError | `human_input_form_expired` | 412 |

### 3.12 Non-BaseHTTPException Errors (reference only)

Internal error hierarchies not exposed as HTTP responses:

- `core/mcp/error.py` — MCPError, MCPConnectionError, MCPAuthError, MCPRefreshTokenError
- `core/errors/error.py` — LLMError, LLMBadRequestError, ProviderTokenNotInitError, QuotaExceededError
- `core/tools/errors.py` — ToolProviderNotFoundError, ToolNotFoundError, etc.
- `core/workflow/errors.py` — Workflow errors
- `core/trigger/errors.py` — Trigger errors
- `core/datasource/errors.py` — Datasource errors

---

## 4. Async Task Inventory (Celery)

**Total: 67 tasks across 19 queues**

### 4.1 Celery Configuration (`extensions/ext_celery.py`)

| Setting        | Value                                        |
| -------------- | -------------------------------------------- |
| Broker         | Redis (configurable via `CELERY_BROKER_URL`) |
| Backend        | Configurable via `CELERY_RESULT_BACKEND`     |
| SSL            | Configurable via `BROKER_USE_SSL`            |
| Sentinel       | Configurable via `CELERY_USE_SENTINEL`       |
| Task Discovery | Explicit imports in `ext_celery.py:108-113`  |

### 4.2 Queue Inventory

| Queue                           | Purpose                                     |
| ------------------------------- | ------------------------------------------- |
| `dataset`                       | Normal priority dataset/document operations |
| `priority_dataset`              | High priority dataset operations            |
| `pipeline`                      | Normal RAG pipeline execution               |
| `priority_pipeline`             | High priority RAG pipeline                  |
| `mail`                          | Email delivery                              |
| `workflow_storage`              | Workflow execution persistence              |
| `workflow_draft_var`            | Workflow draft variable cleanup             |
| `workflow_based_app_execution`  | App-level workflow execution                |
| `professional_queue`            | Professional tier workflow execution        |
| `team_queue`                    | Team tier workflow execution                |
| `sandbox_queue`                 | Sandbox/free tier workflow execution        |
| `schedule_executor`             | Scheduled task execution                    |
| `schedule_poller`               | Schedule polling                            |
| `app_deletion`                  | App deletion                                |
| `trigger_refresh_executor`      | Trigger subscription refresh                |
| `triggered_workflow_dispatcher` | Trigger dispatch                            |
| `plugin`                        | Plugin operations                           |
| `ops_trace`                     | Trace processing                            |
| `conversation`                  | Conversation operations                     |

### 4.3 Task Inventory

#### Workflow Execution Tasks

| Task                              | File                                              | Queue                        | Retries | Description                       |
| --------------------------------- | ------------------------------------------------- | ---------------------------- | ------- | --------------------------------- |
| execute_workflow_professional     | `tasks/async_workflow_tasks.py:45`                | professional_queue           | -       | Workflow exec (professional tier) |
| execute_workflow_team             | `tasks/async_workflow_tasks.py:61`                | team_queue                   | -       | Workflow exec (team tier)         |
| execute_workflow_sandbox          | `tasks/async_workflow_tasks.py:77`                | sandbox_queue                | -       | Workflow exec (free tier)         |
| resume_workflow_execution         | `tasks/async_workflow_tasks.py:192`               | default                      | -       | Resume paused workflow            |
| workflow_based_app_execution_task | `tasks/app_generate/workflow_execute_task.py:255` | workflow_based_app_execution | -       | Execute workflow-based app        |
| resume_app_execution              | `tasks/app_generate/workflow_execute_task.py:489` | workflow_based_app_execution | -       | Resume paused app execution       |

#### Trigger Tasks

| Task                               | File                                             | Queue                         | Retries | Description                  |
| ---------------------------------- | ------------------------------------------------ | ----------------------------- | ------- | ---------------------------- |
| dispatch_triggered_workflows_async | `tasks/trigger_processing_tasks.py:441`          | triggered_workflow_dispatcher | -       | Dispatch triggered workflows |
| trigger_subscription_refresh       | `tasks/trigger_subscription_refresh_tasks.py:84` | trigger_refresh_executor      | -       | Refresh trigger subscription |

#### Document/Dataset Indexing Tasks

| Task                                          | File                                            | Queue            | Retries | Description               |
| --------------------------------------------- | ----------------------------------------------- | ---------------- | ------- | ------------------------- |
| document_indexing_task (deprecated)           | `tasks/document_indexing_task.py:22`            | dataset          | -       | Legacy doc indexing       |
| normal_document_indexing_task                 | `tasks/document_indexing_task.py:219`           | dataset          | -       | Normal priority indexing  |
| priority_document_indexing_task               | `tasks/document_indexing_task.py:233`           | priority_dataset | -       | Priority indexing         |
| duplicate_document_indexing_task (deprecated) | `tasks/duplicate_document_indexing_task.py:23`  | dataset          | -       | Legacy duplicate indexing |
| normal_duplicate_document_indexing_task       | `tasks/duplicate_document_indexing_task.py:164` | dataset          | -       | Normal dup indexing       |
| priority_duplicate_document_indexing_task     | `tasks/duplicate_document_indexing_task.py:180` | priority_dataset | -       | Priority dup indexing     |
| generate_summary_index_task                   | `tasks/generate_summary_index_task.py:17`       | dataset          | -       | Generate summary index    |
| regenerate_summary_index_task                 | `tasks/regenerate_summary_index_task.py:19`     | dataset          | -       | Regenerate summary index  |
| retry_document_indexing_task                  | `tasks/retry_document_indexing_task.py:21`      | dataset          | -       | Retry failed doc indexing |
| sync_website_document_indexing_task           | `tasks/sync_website_document_indexing_task.py:19`| dataset          | -       | Sync website doc indexing |

#### Dataset Cleanup Tasks

| Task                       | File                                     | Queue   | Retries | Description            |
| -------------------------- | ---------------------------------------- | ------- | ------- | ---------------------- |
| clean_dataset_task         | `tasks/clean_dataset_task.py:32`         | dataset | -       | Clean deleted dataset  |
| clean_document_task        | `tasks/clean_document_task.py:18`        | dataset | -       | Clean deleted document |
| batch_clean_document_task  | `tasks/batch_clean_document_task.py:21`  | dataset | -       | Batch clean documents  |
| clean_notion_document_task | `tasks/clean_notion_document_task.py:15` | dataset | -       | Clean Notion document  |

#### Segment/Index Tasks

| Task                               | File                                             | Queue   | Retries | Description               |
| ---------------------------------- | ------------------------------------------------ | ------- | ------- | ------------------------- |
| add_document_to_index_task         | `tasks/add_document_to_index_task.py:20`         | dataset | -       | Add doc to index          |
| create_segment_to_index_task       | `tasks/create_segment_to_index_task.py:17`       | dataset | -       | Create segment index      |
| batch_create_segment_to_index_task | `tasks/batch_create_segment_to_index_task.py:26` | dataset | -       | Batch create segments     |
| delete_segment_from_index_task     | `tasks/delete_segment_from_index_task.py:16`     | dataset | -       | Delete segment from index |
| disable_segment_from_index_task    | `tasks/disable_segment_from_index_task.py:15`    | dataset | -       | Disable segment           |
| enable_segment_to_index_task       | `tasks/enable_segment_to_index_task.py:19`       | dataset | -       | Enable segment            |
| disable_segments_from_index_task   | `tasks/disable_segments_from_index_task.py:17`   | dataset | -       | Disable segments (batch)  |
| enable_segments_to_index_task      | `tasks/enable_segments_to_index_task.py:21`      | dataset | -       | Enable segments (batch)   |
| remove_document_from_index_task    | `tasks/remove_document_from_index_task.py:17`    | dataset | -       | Remove doc from index     |
| recover_document_indexing_task     | `tasks/recover_document_indexing_task.py:14`     | dataset | -       | Recover indexing          |
| document_indexing_update_task      | `tasks/document_indexing_update_task.py:17`      | dataset | -       | Update indexing           |
| document_indexing_sync_task        | `tasks/document_indexing_sync_task.py:19`        | dataset | -       | Sync indexing             |
| deal_dataset_vector_index_task     | `tasks/deal_dataset_vector_index_task.py:19`     | dataset | -       | Deal with vector index    |
| deal_dataset_index_update_task     | `tasks/deal_dataset_index_update_task.py:16`     | dataset | -       | Deal with index update    |

#### RAG Pipeline Tasks

| Task                           | File                                                      | Queue             | Retries | Description           |
| ------------------------------ | --------------------------------------------------------- | ----------------- | ------- | --------------------- |
| rag_pipeline_run_task          | `tasks/rag_pipeline/rag_pipeline_run_task.py:30`          | pipeline          | -       | Run RAG pipeline      |
| priority_rag_pipeline_run_task | `tasks/rag_pipeline/priority_rag_pipeline_run_task.py:30` | priority_pipeline | -       | Priority RAG pipeline |

#### Persistence Tasks (with retry)

| Task                              | File                                           | Queue              | Retries | Delay | Description             |
| --------------------------------- | ---------------------------------------------- | ------------------ | ------- | ----- | ----------------------- |
| save_workflow_node_execution_task | `tasks/workflow_node_execution_tasks.py:25`    | workflow_storage   | 3       | 60s   | Save node execution     |
| save_workflow_execution_task      | `tasks/workflow_execution_tasks.py:23`         | workflow_storage   | 3       | 60s   | Save workflow execution |
| save_workflow_execution_task      | `tasks/workflow_draft_var_tasks.py:14`         | workflow_draft_var | 3       | 60s   | Save draft var files    |
| remove_app_and_related_data_task  | `tasks/remove_app_and_related_data_task.py:56` | app_deletion       | 3       | 180s* | Remove app + data       |

> \* `remove_app_and_related_data_task` has `max_retries=3` but no explicit `default_retry_delay` — uses Celery default (180s).

#### Mail Tasks

| Task                                             | File                                          | Queue | Description            |
| ------------------------------------------------ | --------------------------------------------- | ----- | ---------------------- |
| send_email_register_mail_task                    | `tasks/mail_register_task.py:14`              | mail  | Registration email     |
| send_email_register_mail_task_when_account_exist | `tasks/mail_register_task.py:50`              | mail  | Registration (exists)  |
| send_change_mail_task                            | `tasks/mail_change_mail_task.py:13`           | mail  | Change email           |
| send_change_mail_completed_notification_task     | `tasks/mail_change_mail_task.py:45`           | mail  | Change completed       |
| send_invite_member_mail_task                     | `tasks/mail_invite_member_task.py:14`         | mail  | Invite member          |
| send_owner_transfer_confirm_task                 | `tasks/mail_owner_transfer_task.py:13`        | mail  | Owner transfer confirm |
| send_old_owner_transfer_notify_email_task        | `tasks/mail_owner_transfer_task.py:54`        | mail  | Old owner notify       |
| send_new_owner_transfer_notify_email_task        | `tasks/mail_owner_transfer_task.py:95`        | mail  | New owner notify       |
| send_deletion_success_task                       | `tasks/mail_account_deletion_task.py:13`      | mail  | Deletion success       |
| send_account_deletion_verification_code          | `tasks/mail_account_deletion_task.py:48`      | mail  | Deletion verification  |
| send_email_code_login_mail_task                  | `tasks/mail_email_code_login.py:13`           | mail  | Email code login       |
| dispatch_human_input_email_task                  | `tasks/mail_human_input_delivery_task.py:146` | mail  | Human input email      |
| send_reset_password_mail_task                    | `tasks/mail_reset_password_task.py:14`        | mail  | Password reset         |
| send_reset_password_mail_task_when_account_not_exist | `tasks/mail_reset_password_task.py:50`    | mail  | Reset (no account)     |
| send_inner_email_task                            | `tasks/mail_inner_task.py:44`                 | mail  | Inner/enterprise email |

#### Annotation Tasks

| Task                            | File                                                     | Queue   | Description              |
| ------------------------------- | -------------------------------------------------------- | ------- | ------------------------ |
| add_annotation_to_index_task    | `tasks/annotation/add_annotation_to_index_task.py:15`    | dataset | Add annotation index     |
| batch_import_annotations_task   | `tasks/annotation/batch_import_annotations_task.py:19`   | dataset | Batch import annotations |
| enable_annotation_reply_task    | `tasks/annotation/enable_annotation_reply_task.py:20`    | dataset | Enable reply             |
| disable_annotation_reply_task   | `tasks/annotation/disable_annotation_reply_task.py:17`   | dataset | Disable reply            |
| delete_annotation_index_task    | `tasks/annotation/delete_annotation_index_task.py:14`    | dataset | Delete annotation index  |
| update_annotation_to_index_task | `tasks/annotation/update_annotation_to_index_task.py:15` | dataset | Update annotation index  |

#### Other Tasks

| Task                                         | File                                                       | Queue             | Description               |
| -------------------------------------------- | ---------------------------------------------------------- | ----------------- | ------------------------- |
| delete_conversation_task                     | `tasks/delete_conversation_task.py:16`                     | conversation      | Delete conversation       |
| delete_account_task                          | `tasks/delete_account_task.py:14`                          | dataset           | Delete account            |
| process_tenant_plugin_autoupgrade_check_task | `tasks/process_tenant_plugin_autoupgrade_check_task.py:75` | plugin            | Plugin auto-upgrade       |
| process_trace_tasks                          | `tasks/ops_trace_task.py:18`                               | ops_trace         | Process trace data        |
| run_schedule_trigger                         | `tasks/workflow_schedule_tasks.py:22`                      | schedule_executor | Execute scheduled trigger |
| check_and_handle_human_input_timeouts        | `tasks/human_input_timeout_tasks.py:56`                    | schedule_executor | Scan expired forms        |
| poll_workflow_schedules                      | `schedule/workflow_schedule_task.py:17`                    | schedule_poller   | Poll schedules            |

---

## 5. Scheduled Task Inventory (Celery Beat)

**Total: 14 beat tasks, all feature-gated**

| Name                               | Schedule                                 | Task                                                      | Feature Flag                                                  |
| ---------------------------------- | ---------------------------------------- | --------------------------------------------------------- | ------------------------------------------------------------- |
| clean_embedding_cache_task         | `crontab(0, 2, */{day})`                 | `schedule.clean_embedding_cache_task`                     | `ENABLE_CLEAN_EMBEDDING_CACHE_TASK`                           |
| clean_unused_datasets_task         | `crontab(0, 3, */{day})`                 | `schedule.clean_unused_datasets_task`                     | `ENABLE_CLEAN_UNUSED_DATASETS_TASK`                           |
| create_tidb_serverless_task        | `crontab(0, *)`                          | `schedule.create_tidb_serverless_task`                    | `ENABLE_CREATE_TIDB_SERVERLESS_TASK`                          |
| update_tidb_serverless_status_task | `timedelta(10min)`                       | `schedule.update_tidb_serverless_status_task`             | `ENABLE_UPDATE_TIDB_SERVERLESS_STATUS_TASK`                   |
| clean_messages                     | `crontab(0, 4, */{day})`                 | `schedule.clean_messages`                                 | `ENABLE_CLEAN_MESSAGES`                                       |
| mail_clean_document_notify_task    | `crontab(0, 10, Mon)`                    | `schedule.mail_clean_document_notify_task`                | `ENABLE_MAIL_CLEAN_DOCUMENT_NOTIFY_TASK`                      |
| datasets-queue-monitor             | `timedelta({QUEUE_MONITOR_INTERVAL}min)` | `schedule.queue_monitor_task`                             | `ENABLE_DATASETS_QUEUE_MONITOR`                               |
| human_input_form_timeout           | `timedelta({INTERVAL}min)`               | `human_input_form_timeout.check_and_resume`               | `ENABLE_HUMAN_INPUT_TIMEOUT_TASK`                             |
| check_upgradable_plugin_task       | `crontab(*/15)`                          | `schedule.check_upgradable_plugin_task`                   | `ENABLE_CHECK_UPGRADABLE_PLUGIN_TASK` + `MARKETPLACE_ENABLED` |
| clean_workflow_runlogs_precise     | `crontab(0, 2)`                          | `schedule.clean_workflow_runlogs_precise`                 | `WORKFLOW_LOG_CLEANUP_ENABLED`                                |
| clean_workflow_runs_task           | `crontab(0, 0)`                          | `schedule.clean_workflow_runs_task`                       | `ENABLE_WORKFLOW_RUN_CLEANUP_TASK`                            |
| workflow_schedule_task             | `timedelta({POLLER_INTERVAL}min)`        | `schedule.workflow_schedule_task.poll_workflow_schedules` | `ENABLE_WORKFLOW_SCHEDULE_POLLER_TASK`                        |
| trigger_provider_refresh           | `timedelta({REFRESH_INTERVAL}min)`       | `schedule.trigger_provider_refresh_task`                  | `ENABLE_TRIGGER_PROVIDER_REFRESH_TASK`                        |
| batch_update_api_token_last_used   | `timedelta({UPDATE_INTERVAL}min)`        | `schedule.update_api_token_last_used_task`                | `ENABLE_API_TOKEN_LAST_USED_UPDATE_TASK`                      |

---

## 6. CORS Policy Matrix

### 6.1 Per-Blueprint CORS Configuration

| Blueprint     | URL Prefix                   | Origins                                                  | Credentials | Methods                                | Allowed Headers                                                   | Exposed Headers              |
| ------------- | ---------------------------- | -------------------------------------------------------- | ----------- | -------------------------------------- | ----------------------------------------------------------------- | ---------------------------- |
| Service API   | `/v1`                        | `*` (all origins, no origins param set)                   | No          | GET,PUT,POST,DELETE,OPTIONS,PATCH      | Content-Type, X-App-Code, X-Passport, Authorization               | X-Version, X-Env, X-Trace-Id |
| Web (embed)   | `/api/chat-messages*`        | `WEB_API_CORS_ALLOW_ORIGINS` (default `*`)               | No          | GET,POST,OPTIONS                       | Content-Type, X-App-Code                                          | X-Version, X-Env, X-Trace-Id |
| Web (default) | `/api/*`                     | `WEB_API_CORS_ALLOW_ORIGINS` (default `*`)               | Yes         | GET,PUT,POST,DELETE,OPTIONS,PATCH      | Content-Type, X-App-Code, X-Passport, X-CSRF-Token, Authorization | X-Version, X-Env, X-Trace-Id |
| Console       | `/console/api/*`             | `CONSOLE_CORS_ALLOW_ORIGINS` (default→`CONSOLE_WEB_URL`) | Yes         | GET,PUT,POST,DELETE,OPTIONS,PATCH      | Content-Type, X-App-Code, X-Passport, X-CSRF-Token, Authorization | X-Version, X-Env, X-Trace-Id |
| Files         | `/files/*`                   | All (no origins param)                                   | No          | GET,PUT,POST,DELETE,OPTIONS,PATCH      | Content-Type, X-App-Code, X-Passport, X-CSRF-Token                | X-Version, X-Env, X-Trace-Id |
| Trigger       | `/triggers/*`                | All (no origins param)                                   | No          | GET,PUT,POST,DELETE,OPTIONS,PATCH,HEAD | Content-Type, Authorization, X-App-Code                           | X-Version, X-Env, X-Trace-Id |
| FastOpenAPI   | `/fastopenapi/console/api/*` | `CONSOLE_CORS_ALLOW_ORIGINS`                             | Yes         | GET,PUT,POST,DELETE,OPTIONS,PATCH      | Content-Type, X-App-Code, X-Passport, X-CSRF-Token, Authorization | X-Version, X-Env, X-Trace-Id |
| Inner API     | `/inner/api/*`               | **NO CORS**                                              | N/A         | N/A                                    | N/A                                                               | N/A                          |
| MCP           | `/mcp/*`                     | **NO CORS**                                              | N/A         | N/A                                    | N/A                                                               | N/A                          |

### 6.2 Configuration Variables

| Variable                     | Default                    | Description                     |
| ---------------------------- | -------------------------- | ------------------------------- |
| `CONSOLE_CORS_ALLOW_ORIGINS` | `""` (→ `CONSOLE_WEB_URL`) | Console allowed origins         |
| `WEB_API_CORS_ALLOW_ORIGINS` | `"*"`                      | Web/Service API allowed origins |

Source: `configs/feature/__init__.py:477-495`

### 6.3 Header Constants (`extensions/ext_blueprints.py:5-10`)

```python
BASE_CORS_HEADERS = ("Content-Type", HEADER_NAME_APP_CODE, HEADER_NAME_PASSPORT)
SERVICE_API_HEADERS = (*BASE_CORS_HEADERS, "Authorization")
AUTHENTICATED_HEADERS = (*SERVICE_API_HEADERS, HEADER_NAME_CSRF_TOKEN)
FILES_HEADERS = (*BASE_CORS_HEADERS, HEADER_NAME_CSRF_TOKEN)
EMBED_HEADERS = ("Content-Type", HEADER_NAME_APP_CODE)
EXPOSED_HEADERS = ("X-Version", "X-Env", "X-Trace-Id")
```

### 6.4 Response Headers Added by Middleware

| Header     | Value                         | Source                                |
| ---------- | ----------------------------- | ------------------------------------- |
| X-Version  | `dify_config.project.version` | `extensions/ext_app_metrics.py:12-17` |
| X-Env      | `dify_config.DEPLOY_ENV`      | `extensions/ext_app_metrics.py:12-17` |
| X-Trace-Id | OpenTelemetry trace ID        | `app_factory.py:46-47`                |

---

## 7. Database Schema Snapshot

### 7.1 Migration Status

| Metric                     | Value                                                                                                                                                                          |
| -------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Total Migrations           | 165                                                                                                                                                                            |
| Migration Heads (branches) | 1                                                                                                                                                                              |
| Head Revision              | `fce013ca180e`                                                                                                                                                                 |
| Total Model Classes        | 116                                                                                                                                                                            |

### 7.2 Core Tables

| Model Class       | Table Name             | PK Type | Key Foreign Keys       |
| ----------------- | ---------------------- | ------- | ---------------------- |
| Account           | `accounts`             | UUID v4 | -                      |
| Tenant            | `tenants`              | UUID v4 | -                      |
| TenantAccountJoin | `tenant_account_joins` | UUID v4 | tenant_id, account_id  |
| AccountIntegrate  | `account_integrates`   | UUID v4 | account_id             |
| InvitationCode    | `invitation_codes`     | Integer | -                      |
| App               | `apps`                 | UUID v4 | tenant_id, workflow_id |
| AppModelConfig    | `app_model_configs`    | UUID v4 | app_id                 |
| Site              | `sites`                | UUID v4 | app_id                 |
| ApiToken          | `api_tokens`           | UUID v4 | tenant_id              |
| EndUser           | `end_users`            | UUID v4 | tenant_id              |
| UploadFile        | `upload_files`         | UUID v4 | tenant_id              |
| OperationLog      | `operation_logs`       | UUID v4 | tenant_id              |
| Tag               | `tags`                 | UUID v4 | tenant_id              |
| TagBinding        | `tag_bindings`         | UUID v4 | tenant_id, tag_id      |

### 7.3 Conversation Tables

| Model Class         | Table Name               | PK Type | Key Foreign Keys        |
| ------------------- | ------------------------ | ------- | ----------------------- |
| Conversation        | `conversations`          | UUID v4 | app_id, tenant_id       |
| Message             | `messages`               | UUID v4 | conversation_id, app_id |
| MessageAgentThought | `message_agent_thoughts` | UUID v4 | message_id              |
| MessageFile         | `message_files`          | UUID v4 | message_id              |
| MessageAnnotation   | `message_annotations`    | UUID v4 | message_id              |
| MessageChain        | `message_chains`         | UUID v4 | message_id              |
| MessageFeedback     | `message_feedbacks`      | UUID v4 | message_id              |
| SavedMessage        | `saved_messages`         | UUID v4 | app_id, message_id      |
| PinnedConversation  | `pinned_conversations`   | UUID v4 | app_id, conversation_id |

### 7.4 Dataset Tables

| Model Class              | Table Name                    | PK Type | Key Foreign Keys      |
| ------------------------ | ----------------------------- | ------- | --------------------- |
| Dataset                  | `datasets`                    | UUID v4 | tenant_id             |
| Document                 | `documents`                   | UUID v4 | dataset_id            |
| DocumentSegment          | `document_segments`           | UUID v4 | document_id           |
| DatasetProcessRule       | `dataset_process_rules`       | UUID v4 | dataset_id            |
| DatasetQuery             | `dataset_queries`             | UUID v4 | dataset_id            |
| DatasetCollectionBinding | `dataset_collection_bindings` | UUID v4 | tenant_id             |
| Embedding                | `embeddings`                  | UUID v4 | document_id           |
| DatasetKeywordTable      | `dataset_keyword_tables`      | UUID v4 | dataset_id            |
| Whitelist                | `whitelists`                  | UUID v4 | tenant_id             |
| DatasetPermission        | `dataset_permissions`         | UUID v4 | tenant_id, dataset_id |

### 7.5 Workflow Tables

| Model Class                  | Table Name                         | PK Type | Key Foreign Keys           |
| ---------------------------- | ---------------------------------- | ------- | -------------------------- |
| Workflow                     | `workflows`                        | UUID v4 | tenant_id, app_id          |
| WorkflowRun                  | `workflow_runs`                    | UUID v4 | workflow_id, tenant_id     |
| WorkflowNodeExecution        | `workflow_node_executions`         | UUID v4 | workflow_run_id            |
| WorkflowNodeExecutionOffload | `workflow_node_execution_offloads` | UUID v4 | workflow_node_execution_id |
| WorkflowPause                | `workflow_pauses`                  | UUID v4 | workflow_run_id            |
| ConversationVariable         | `conversation_variables`           | UUID v4 | conversation_id            |
| WorkflowAppLog               | `workflow_app_logs`                | UUID v4 | app_id                     |
| WorkflowArchiveLog           | `workflow_archive_logs`            | UUID v4 | tenant_id                  |

### 7.6 Provider Tables

| Model Class              | Table Name                     | PK Type | Key Foreign Keys  |
| ------------------------ | ------------------------------ | ------- | ----------------- |
| Provider                 | `providers`                    | UUID v7 | tenant_id         |
| ProviderModel            | `provider_models`              | UUID v4 | tenant_id         |
| ProviderModelSetting     | `provider_model_settings`      | UUID v4 | provider_model_id |
| TenantDefaultModel       | `tenant_default_models`        | UUID v4 | tenant_id         |
| LoadBalancingModelConfig | `load_balancing_model_configs` | UUID v4 | provider_model_id |

### 7.7 Tool Tables

| Model Class               | Table Name                    | PK Type | Key Foreign Keys |
| ------------------------- | ----------------------------- | ------- | ---------------- |
| BuiltinToolProvider       | `tool_builtin_providers`      | UUID v4 | tenant_id        |
| ApiToolProvider           | `tool_api_providers`          | UUID v4 | tenant_id        |
| WorkflowToolProvider      | `tool_workflow_providers`     | UUID v4 | tenant_id        |
| ToolFile                  | `tool_files`                  | UUID v4 | tenant_id        |
| ToolConversationVariables | `tool_conversation_variables` | UUID v4 | conversation_id  |
| ToolModelInvoke           | `tool_model_invokes`          | UUID v4 | tool_id          |

### 7.8 Trigger Tables

| Model Class          | Table Name                | PK Type | Key Foreign Keys       |
| -------------------- | ------------------------- | ------- | ---------------------- |
| AppTrigger           | `app_triggers`            | UUID v4 | tenant_id, app_id      |
| TriggerSubscription  | `trigger_subscriptions`   | UUID v4 | tenant_id              |
| WorkflowSchedulePlan | `workflow_schedule_plans` | UUID v4 | tenant_id, workflow_id |

### 7.9 Other Tables

| Model Class        | Table Name             | PK Type | Key Foreign Keys |
| ------------------ | ---------------------- | ------- | ---------------- |
| DifySetup          | `dify_setups`          | String* | -                |
| RecommendedApp     | `recommended_apps`     | UUID v4 | app_id           |
| InstalledApp       | `installed_apps`       | UUID v4 | tenant_id, app_id|
| CeleryTask         | `celery_taskmeta`      | Integer | -                |
| CeleryTaskSet      | `celery_tasksetmeta`   | Integer | -                |
| APIBasedExtension  | `api_based_extensions` | UUID v4 | tenant_id        |
| DatasourceProvider | `datasource_providers` | UUID v7 | tenant_id        |
| TenantCreditPool   | `tenant_credit_pools`  | UUID v4 | tenant_id        |
| ExporleBanner      | `exporle_banners`      | UUID v4 | -                |

> \* `DifySetup` uses `version` (String) as its primary key, not UUID. `ExporleBanner`/`exporle_banners` is a typo in the codebase (not `ExploreBanner`/`explore_banners`).

### 7.10 Key Schema Observations

1. **Multi-tenancy**: Nearly all tables have `tenant_id` foreign key for workspace isolation
2. **PK Strategy**: UUID v4 (StringUUID) for most tables; UUID v7 for newer tables (Provider, DatasourceProvider); Integer for Celery metadata; String for DifySetup (`version` as PK)
3. **No views/materialized views** found in migrations
4. **Soft deletes**: Some tables use `is_deleted` flag (conversations), not SQL CASCADE
5. **Index pattern**: Consistent indexes on `tenant_id`, `app_id`, `conversation_id`

---

## Appendix A: Migration Priority Matrix

Based on the plan's bounded context priorities:

| Priority | Context       | Key Tables                                            | Endpoint Count | Complexity |
| -------- | ------------- | ----------------------------------------------------- | -------------- | ---------- |
| P0       | IAM           | accounts, tenants, tenant_account_joins, api_tokens   | ~50            | Medium     |
| P1       | App           | apps, app_model_configs, sites                        | ~40            | High       |
| P1       | Conversation  | conversations, messages, message\_\*                  | ~20            | High       |
| P1       | File          | upload_files, tool_files                              | ~10            | Low        |
| P2       | Knowledge     | datasets, documents, document_segments, embeddings    | ~80            | Very High  |
| P2       | ModelProvider | providers, provider*models, load_balancing*\*         | ~15            | Medium     |
| P2       | Billing       | (cloud tables)                                        | ~4             | Low        |
| P3       | Workflow      | workflows, workflow_runs, workflow_node_executions    | ~50            | Very High  |
| P3       | Tools         | tool\_\*, api_tool_providers, workflow_tool_providers | ~40            | High       |
| P3       | Observability | operation_logs, trace configs                         | ~10            | Low        |

---

## Appendix B: Cross-Blueprint Error Code Overlap

Error codes duplicated across blueprints (must maintain parity):

| error_code                            | Console       | Service API | Web |
| ------------------------------------- | ------------- | ----------- | --- |
| `app_unavailable`                     | Yes           | Yes         | Yes |
| `not_completion_app`                  | Yes (explore) | Yes         | Yes |
| `not_chat_app`                        | Yes (explore) | Yes         | Yes |
| `not_workflow_app`                    | Yes (explore) | Yes         | Yes |
| `conversation_completed`              | Yes           | Yes         | Yes |
| `provider_not_initialize`             | Yes           | Yes         | Yes |
| `provider_quota_exceeded`             | Yes           | Yes         | Yes |
| `model_currently_not_support`         | Yes           | Yes         | Yes |
| `completion_request_error`            | Yes           | Yes         | Yes |
| `no_audio_uploaded`                   | Yes           | Yes         | Yes |
| `audio_too_large`                     | Yes           | Yes         | Yes |
| `unsupported_audio_type`              | Yes           | Yes         | Yes |
| `provider_not_support_speech_to_text` | Yes           | Yes         | Yes |
| `rate_limit_error`                    | Yes           | -           | Yes |
| `dataset_not_initialized`             | Yes           | Yes         | -   |
| `archived_document_immutable`         | Yes           | Yes         | -   |
| `dataset_name_duplicate`              | Yes           | Yes         | -   |
| `dataset_in_use`                      | Yes           | Yes         | -   |

> **Java Migration Note**: These shared error codes should be defined in the `common` module and reused across controller modules to avoid drift.
