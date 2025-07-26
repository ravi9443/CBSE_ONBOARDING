# 🔁 Retry Service – CBSE Student Onboarding Integration
This project is built to simulate and manage student onboarding into the CBSE system via external APIs, with a robust retry mechanism in place to handle failures efficiently. It is designed with Kafka, MongoDB, and a dedicated Student CRUD service to offer end-to-end workflow automation and fault-tolerant integration.

## 📌 Project Highlights
#### 📚 Student CRUD System: Handles creation, retrieval, updating, and deletion of student records and initiates the CBSE onboarding process.

#### 📨 Kafka-Based Event Streaming: Publishes onboarding events and listens for retry triggers, ensuring decoupled and scalable communication.

#### 💾 MongoDB for Persistence: Stores retry events and configuration details, allowing efficient querying and flexible retry policies.

#### 🔁 Custom Retry Mechanism: Supports scheduled retries for server errors or other transient failures, with configurable retry intervals and limits.
***
# 🛠️ How It Works
#### A student is onboarded in the internal system.

#### An API call is made to the CBSE system (mocked response for now).

#### Based on the response:

✅ Success → Task is marked as CLOSED.

🔁 Server Error → Task remains OPEN and is retried later.

🚫 Student Already Enrolled → Task is marked FAILED and no further retries are made.

#### A retry mechanism picks up failed tasks with status OPEN and reschedules them according to defined retry rules.
***
