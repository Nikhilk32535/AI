
# 🤖 Nikhil's AI Assistant

**Nikhil's AI Assistant** is a smart and modern Android chat application powered by Google's **Gemini Generative AI API**. It allows users to interact with AI in a conversational interface, with support for exporting conversations, Markdown rendering, and a beautiful, minimal UI.

---

## 📱 Features

- 🤖 **Chat with AI (Gemini 2.5 Pro)**
- 💬 Beautiful chat bubbles for user and bot
- ⏱️ Timestamp with each message
- ✍️ Markdown rendering for rich responses
- 📄 Export chat as **TXT** and **PDF**
- 📋 Copy or Share message on long-press
- 🧠 Smart "Typing..." indicator
- 🌐 Internet connectivity check
- 🌙 Clean, modern Material UI

---

## 🛠️ Tech Stack

- Java (Android SDK)
- Gemini API (`GenerativeModelFutures`)
- PDFDocument API for PDF export
- Markwon for Markdown
- RecyclerView for dynamic chat
- MaterialToolbar, LinearLayoutManager

---

## 📂 Project Structure

```

📁 app/
├── java/com/example/ai/
│   ├── MainActivity.java
│   ├── Message.java
│   ├── MessageAdapter.java
│   └── Utility/
│       ├── ChatExportUtils.java
│       ├── ChatExportPdfUtils.java
│       └── Utils.java
├── res/
│   ├── layout/
│   │   ├── activity\_main.xml
│   │   └── chat\_items.xml
│   └── drawable/
│       └── custom\_background.png

````

---

## 🔑 Gemini API Setup

1. Go to [https://makersuite.google.com/app](https://makersuite.google.com/app) and get your **Gemini API Key**.
2. Add the key to `local.properties` or your `gradle.properties`:

```properties
GEMINI_API_KEY=your_api_key_here
````

3. Access it in code via:

```java
String apiKey = BuildConfig.GEMINI_API_KEY;
```

---

## 🚀 How to Run

1. Clone the project:

```bash
git clone https://github.com/your-username/nikhils-ai-assistant.git
```

2. Open in **Android Studio**

3. Add your **Gemini API Key** in `local.properties`.

4. Build & Run the app on a device or emulator (Android 8.0+)

---

<img src="[https://github.com/user-attachments/assets/ec69a8c9-8074-488f-b2e6-1f833be569b5](https://github.com/user-attachments/assets/e1c9ce20-8592-48de-91d6-58ddbbfedf22)" width="300" />
<img src="[https://github.com/user-attachments/assets/ec69a8c9-8074-488f-b2e6-1f833be569b5](https://github.com/user-attachments/assets/b974af29-af1d-48dc-93a5-994c930e0d42)" width="300" />


---

## ⚙️ Permissions

Make sure you add the following permissions in `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
    android:maxSdkVersion="28"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

> On Android 10+ (API 29+), use **scoped storage** or request **MANAGE\_EXTERNAL\_STORAGE** if necessary.

---

## 📄 Exported Chat Formats

**TXT Example:**

```
───────────────
Nikhil's AI Chat
───────────────
[You - 10:15 AM]: Hello
[Bot - 10:15 AM]: Hi! How can I help you today?
```

**PDF Example:**

Includes similar structure but with clean formatting and automatic page breaks.

---

## 📧 Contact

* Developer: **Nikhil Kumar**
* GitHub: [@Nikhilk32535](https://github.com/Nikhilk32535)

---


## 🏷️ Tags

`android` `java` `gemini` `ai-assistant` `chatbot` `pdf-export` `markdown` `material-ui` `recyclerview` `ai-chat`

```
