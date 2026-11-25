// Returns the current JWT token from the browser storage
function getToken() {
    return localStorage.getItem("token");
}

// Go back to the login page if the user is not logged in
if (!getToken()) {
    window.location.href = "index.html";
}

// Elements used on the editor page
const languageSelect = document.getElementById("language");
const codeArea = document.getElementById("code");
const stdinArea = document.getElementById("stdin");
const runBtn = document.getElementById("runBtn");
const aiBtn = document.getElementById("aiBtn");
const saveBtn = document.getElementById("saveBtn");
const statusBadge = document.getElementById("statusBadge");
const outputBox = document.getElementById("outputBox");
const errorBox = document.getElementById("errorBox");
const statsBox = document.getElementById("statsBox");
const aiBox = document.getElementById("aiBox");
const toastBox = document.getElementById("toast");

// Show the logged-in username in the top bar
document.getElementById("userName").textContent = localStorage.getItem("username") || "";

// Starter code for each language; Java must use the class name Main
const TEMPLATES = {
    java: 'public class Main {\n    public static void main(String[] args) {\n        System.out.println("Hello, World!");\n    }\n}',
    python: 'print("Hello, World!")',
    cpp: '#include <iostream>\nusing namespace std;\n\nint main() {\n    cout << "Hello, World!" << endl;\n    return 0;\n}',
    c: '#include <stdio.h>\n\nint main() {\n    printf("Hello, World!\\n");\n    return 0;\n}',
    javascript: 'console.log("Hello, World!");'
};

let currentLanguage = languageSelect.value;
let lastError = "";      // error text of the last run, sent to the AI
let toastTimer = null;   // timer that hides the toast message

// Put the starter code in the editor when the page opens
codeArea.value = TEMPLATES[currentLanguage];

// Shows a small message at the bottom of the screen for 3 seconds
function toast(text, isError) {
    clearTimeout(toastTimer);
    toastBox.textContent = text;
    toastBox.className = "toast" + (isError ? " error" : "");
    toastTimer = setTimeout(() => toastBox.classList.add("hidden"), 3000);
}

// Removes the token and returns to the login page
function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    window.location.href = "index.html";
}
document.getElementById("logoutBtn").addEventListener("click", logout);

// Builds a readable error text from the backend's ApiErrorResponse JSON
function buildErrorText(data) {
    if (data && data.validationErrors) {
        return Object.values(data.validationErrors).join(", ");
    }
    return (data && data.message) ? data.message : "Something went wrong";
}

// Calls the backend with the JWT token; returns the JSON or throws an Error with the message
async function api(url, options = {}) {
    const response = await fetch(url, {
        ...options,
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + getToken()
        }
    });

    // Missing or expired token: go back to login
    if (response.status === 401 || response.status === 403) {
        logout();
        throw new Error("Session expired. Please log in again.");
    }

    // 204 means success with no body, for example after a delete
    if (response.status === 204) {
        return null;
    }

    const data = await response.json().catch(() => null);
    if (!response.ok) {
        throw new Error(buildErrorText(data));
    }
    return data;
}

// Disables a button and changes its text while a request is running
function setBusy(button, busy, busyText) {
    if (busy) {
        button.dataset.label = button.textContent;
        button.textContent = busyText;
    } else {
        button.textContent = button.dataset.label;
    }
    button.disabled = busy;
}

// Switches the starter code when the language changes, but never replaces the user's own code
languageSelect.addEventListener("change", () => {
    const text = codeArea.value.trim();
    if (text === "" || text === TEMPLATES[currentLanguage].trim()) {
        codeArea.value = TEMPLATES[languageSelect.value];
    }
    currentLanguage = languageSelect.value;
});

// The Tab key inserts 4 spaces instead of leaving the editor
codeArea.addEventListener("keydown", (event) => {
    if (event.key === "Tab") {
        event.preventDefault();
        const start = codeArea.selectionStart;
        codeArea.value = codeArea.value.slice(0, start) + "    " + codeArea.value.slice(codeArea.selectionEnd);
        codeArea.selectionStart = codeArea.selectionEnd = start + 4;
    }
});

// Shows one of the right-side tabs and loads its data when needed
function showTab(name) {
    document.querySelectorAll(".side-tab").forEach(tab =>
        tab.classList.toggle("active", tab.dataset.tab === name));
    document.querySelectorAll(".panel").forEach(panel =>
        panel.classList.toggle("hidden", panel.id !== "panel-" + name));

    if (name === "history") loadHistory();
    if (name === "saved") loadSaved();
}
document.querySelectorAll(".side-tab").forEach(tab =>
    tab.addEventListener("click", () => showTab(tab.dataset.tab)));

// RUN: send the code to /api/code/run and show the result
runBtn.addEventListener("click", async () => {
    setBusy(runBtn, true, "Running...");
    try {
        const data = await api("/api/code/run", {
            method: "POST",
            body: JSON.stringify({
                language: languageSelect.value,
                sourceCode: codeArea.value,
                stdin: stdinArea.value
            })
        });

        // Status badge: green for Accepted, red for any error
        statusBadge.textContent = data.status;
        statusBadge.className = "status-badge " + (data.status === "Accepted" ? "ok" : "fail");
        outputBox.textContent = data.output || "(no output)";

        // Error box appears only when there is an error message
        lastError = data.error || "";
        errorBox.textContent = lastError;
        errorBox.classList.toggle("hidden", lastError === "");

        statsBox.textContent = "Time: " + (data.time || "-") + " s   |   Memory: " + (data.memory || "-") + " KB";
        showTab("output");
    } catch (error) {
        toast(error.message, true);
    } finally {
        setBusy(runBtn, false);
    }
});

// AI EXPLAIN: send the code and the last error to /api/ai/analyze
aiBtn.addEventListener("click", async () => {
    setBusy(aiBtn, true, "Thinking...");
    showTab("ai");
    aiBox.textContent = "Asking AI...";
    try {
        const data = await api("/api/ai/analyze", {
            method: "POST",
            body: JSON.stringify({
                sourceCode: codeArea.value,
                language: languageSelect.value,
                errorLog: lastError,
                prompt: "Explain this code, identify any errors and suggest improvements."
            })
        });
        aiBox.textContent = data.response;
    } catch (error) {
        aiBox.textContent = error.message;
    } finally {
        setBusy(aiBtn, false);
    }
});

// SAVE: ask for a title and send the program to /api/code/save
saveBtn.addEventListener("click", async () => {
    const title = prompt("Enter a title for this program:");
    if (!title || title.trim() === "") return;

    try {
        await api("/api/code/save", {
            method: "POST",
            body: JSON.stringify({
                title: title.trim(),
                language: languageSelect.value,
                sourceCode: codeArea.value
            })
        });
        toast("Program saved", false);
    } catch (error) {
        toast(error.message, true);
    }
});

// Puts a program from history or the saved list back into the editor
function loadIntoEditor(language, sourceCode, stdin) {
    const lang = (language || "").toLowerCase();
    languageSelect.value = lang;
    if (languageSelect.value !== lang) {
        languageSelect.selectedIndex = 0;
    }
    currentLanguage = languageSelect.value;
    codeArea.value = sourceCode;
    stdinArea.value = stdin || "";
}

// Creates one clickable row for the history or saved list
function buildItem(title, meta, onClick, onDelete) {
    const row = document.createElement("div");
    row.className = "item";

    const text = document.createElement("div");
    const titleEl = document.createElement("div");
    titleEl.className = "item-title";
    titleEl.textContent = title;
    const metaEl = document.createElement("div");
    metaEl.className = "item-meta";
    metaEl.textContent = meta;
    text.append(titleEl, metaEl);
    row.appendChild(text);
    row.addEventListener("click", onClick);

    // The saved list has a Delete button; the history list does not
    if (onDelete) {
        const del = document.createElement("button");
        del.className = "item-delete";
        del.textContent = "Delete";
        del.addEventListener("click", (event) => {
            event.stopPropagation();
            onDelete();
        });
        row.appendChild(del);
    }
    return row;
}

// HISTORY: load past runs from /api/code/history
async function loadHistory() {
    const list = document.getElementById("historyList");
    try {
        const items = await api("/api/code/history");
        list.replaceChildren();
        if (items.length === 0) {
            list.innerHTML = '<p class="empty-text">No runs yet.</p>';
            return;
        }
        items.forEach(item => list.appendChild(buildItem(
            item.language + " - " + item.status,
            new Date(item.createdAt).toLocaleString(),
            () => { loadIntoEditor(item.language, item.sourceCode, item.stdin); toast("Loaded into editor", false); }
        )));
    } catch (error) {
        toast(error.message, true);
    }
}

// SAVED: load saved programs from /api/code/saved
async function loadSaved() {
    const list = document.getElementById("savedList");
    try {
        const items = await api("/api/code/saved");
        list.replaceChildren();
        if (items.length === 0) {
            list.innerHTML = '<p class="empty-text">No saved programs yet.</p>';
            return;
        }
        items.forEach(item => list.appendChild(buildItem(
            item.title,
            item.language + " - updated " + new Date(item.updatedAt).toLocaleString(),
            () => { loadIntoEditor(item.language, item.sourceCode, ""); toast("Loaded into editor", false); },
            async () => {
                try {
                    await api("/api/code/saved/" + item.id, { method: "DELETE" });
                    loadSaved();
                } catch (error) {
                    toast(error.message, true);
                }
            }
        )));
    } catch (error) {
        toast(error.message, true);
    }
}
/*
-->1. Purpose
   editor.js connects the editor page to the backend with the Fetch API.

-->2. How it works
   Run        → POST /api/code/run      → status, output, error, time, memory
   AI Explain → POST /api/ai/analyze    → explanation, using the last error as errorLog
   Save       → POST /api/code/save     → program saved with a title
   History    → GET  /api/code/history  → past runs, click one to reload it
   Saved      → GET  /api/code/saved    → saved programs, click to load, Delete to remove

-->3. Why getToken() and the api() helper?
   getToken() always reads the latest token from localStorage. api() adds it to every
   request, turns backend errors into readable messages, and sends the user back to
   the login page when the token is missing or expired.

-->4. Why textContent and not innerHTML?
   Code and output can contain characters like < and >. textContent shows them
   as plain text, so they can never run as HTML or script.
*/