\# CodeRunner: Online Compiler with AI Code Assistance



A web platform where users write, compile and run code in the browser without installing any software, and get AI help to understand and fix their code.



\## Features



\- User registration and login with Spring Security, JWT and BCrypt

\- Code editor with a language dropdown and a custom input (stdin) box

\- Code execution through Judge0 CE, showing output, compile errors and runtime errors

\- AI Explain: code explanation, error identification and improvement suggestions (Gemini, with a Groq fallback)

\- Save, update and delete programs

\- Run history stored in Oracle Database

\- Centralized exception handling with one standard error format



\## Tech Stack



| Layer | Technologies |

|---|---|

| Frontend | HTML5, CSS3, JavaScript (Fetch API, async/await) |

| Backend | Java 17, Spring Boot, Spring MVC |

| Architecture | Layered: Controller, Service, Repository, Entity, DTO |

| Database | Oracle Database XE, Spring Data JPA, Hibernate |

| Security | Spring Security, JWT, BCrypt |

| External APIs | Judge0 CE (code execution), Google Gemini and Groq (AI help), called with RestClient |

| Testing | JUnit 5, Mockito, Postman |

| Build tool | Maven |



\## Architecture



```

Browser (Fetch) -> Spring Security filters (JWT) -> Controller -> Service

&#x20;       -> Judge0 / Gemini / Groq   or   Repository -> Oracle DB

&#x20;       -> response DTO -> JSON -> Browser

```



Package structure:



```

com.vishal.onlinecompiler

&#x20;|- config       SecurityConfig, RestClientConfig

&#x20;|- controller   AuthController, CodeController, SavedCodeController, AiController

&#x20;|- service      AuthService, CodeExecutionService, AiService, HistoryService, SavedCodeService

&#x20;|- repository   UserRepository, CodeSubmissionRepository, SavedCodeRepository

&#x20;|- entity       User, CodeSubmission, SavedCode

&#x20;|- dto          Request and response classes

&#x20;|- security     JwtUtil, JwtAuthenticationFilter, CustomUserDetailsService

&#x20;|- exception    GlobalExceptionHandler

```



\## API Endpoints



| Method | Endpoint | Description | Auth |

|---|---|---|---|

| POST | `/api/auth/register` | Create an account | No |

| POST | `/api/auth/login` | Log in and receive a JWT | No |

| POST | `/api/code/run` | Run code through Judge0 | Yes |

| POST | `/api/ai/analyze` | AI explanation and debugging | Yes |

| GET | `/api/code/history` | Past runs of the logged-in user | Yes |

| POST | `/api/code/save` | Save a program | Yes |

| GET | `/api/code/saved` | List saved programs | Yes |

| PUT | `/api/code/saved/{id}` | Update a saved program | Yes |

| DELETE | `/api/code/saved/{id}` | Delete a saved program | Yes |



Protected endpoints need the header `Authorization: Bearer <token>`.



\## Getting Started



\### Prerequisites



\- Java 17 or later

\- Maven

\- Oracle Database XE (18c or newer)

\- A free Gemini API key (aistudio.google.com) and optionally a free Groq API key (console.groq.com)



\### 1. Create the database user



In SQL Plus, connected as `system`:



```sql

ALTER SESSION SET CONTAINER = XEPDB1;

CREATE USER compiler IDENTIFIED BY your\_password;

GRANT CONNECT, RESOURCE TO compiler;

ALTER USER compiler QUOTA UNLIMITED ON USERS;

```



Tables are created automatically by Hibernate on the first run.



\### 2. Set environment variables



| Variable | Purpose |

|---|---|

| `DB\_PASSWORD` | Password of the Oracle user `compiler` |

| `JWT\_SECRET` | Secret for signing tokens, at least 32 characters |

| `GEMINI\_API\_KEY` | Google Gemini API key |

| `GROQ\_API\_KEY` | Groq API key (optional fallback) |



Code execution uses the free public Judge0 CE endpoint, so it needs no key.



\### 3. Run



```

mvn spring-boot:run

```



Open `http://localhost:8081` (the port is set in `application.properties`).



\### 4. Run the tests



```

mvn test

```



\## How it works



1\. A user registers and logs in. The password is stored as a BCrypt hash, and the server returns a signed JWT.

2\. The browser sends the token with every request. A filter verifies it before any protected endpoint runs.

3\. On Run, the backend maps the language to a Judge0 language id, sends the code to Judge0, saves the result in Oracle and returns the output.

4\. On AI Explain, the backend builds a prompt from the code, language and last error, and asks Gemini. If a model is busy or retired, it tries the next model and then Groq.

5\. All errors go through one handler and return the same JSON format.



\## Author



Vishal Falke

