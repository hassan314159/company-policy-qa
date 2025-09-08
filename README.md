# Company Policy Assistant (Spring AI + Ollama + Qdrant)

A Spring Boot app that lets you **upload company policy PDFs** and then **ask questions** about them.  
Under the hood it uses:

- **Spring AI** for chat + embeddings
- **Ollama** for local LLMs (`llama3.2:3b` by default)
- **Qdrant** as a vector store for policy chunks

---

## Quick start

### 1) Prereqs
- Docker + Docker Compose
### 2) Build and Start
```bash
docker compose build
docker compose up -d
```

This brings up:

- ollama on http://localhost:11434
- qdrant on http://localhost:6333 (HTTP) and grpc://localhost:6334 (gRPC)
- app on http://localhost:8080

## Endpoints

### 1) Load policies (ETL)

```bash
POST /v1/etl/load
Content-Type: multipart/form-data
Form field: file=<your PDF>
```

Example

```bash
curl -X POST "http://localhost:8080/v1/etl/load" \
-F "file=@/path/to/PolicyHandbook.pdf"
```

### 2) Ask questions (Inference)

```bash
POST /v1/inference/ask
Content-Type: application/json
Body: { "question": "<your question>" }
```

Example

```bash
curl -X POST "http://localhost:8080/v1/inference/ask" \
  -H "Content-Type: application/json" \
  -d '{ "question": "What is the parental leave policy?" }'
```