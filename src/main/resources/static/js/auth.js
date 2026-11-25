// Elements used on the login page
const loginTab = document.getElementById("loginTab");
const registerTab = document.getElementById("registerTab");
const loginForm = document.getElementById("loginForm");
const registerForm = document.getElementById("registerForm");
const messageBox = document.getElementById("message");

// Shows the Login or Register form and highlights the matching tab
function showTab(showLogin) {
    loginForm.classList.toggle("hidden", !showLogin);
    registerForm.classList.toggle("hidden", showLogin);
    loginTab.classList.toggle("active", showLogin);
    registerTab.classList.toggle("active", !showLogin);
    messageBox.textContent = "";
}

loginTab.addEventListener("click", () => showTab(true));
registerTab.addEventListener("click", () => showTab(false));

// Shows a message under the form; isError chooses the red or green colour
function showMessage(text, isError) {
    messageBox.textContent = text;
    messageBox.className = "message " + (isError ? "error" : "success");
}

// Builds a readable error text from the backend's ApiErrorResponse JSON
function buildErrorText(data) {
    if (data && data.validationErrors) {
        return Object.values(data.validationErrors).join("\n");
    }
    return (data && data.message) ? data.message : "Something went wrong";
}

// Sends a POST request to the backend and returns the status and JSON body
async function postJson(url, body) {
    const response = await fetch(url, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body)
    });
    const data = await response.json().catch(() => null);
    return { ok: response.ok, data: data };
}

// Login: send the credentials, save the token and open the editor page
document.getElementById("loginBtn").addEventListener("click", async () => {
    const username = document.getElementById("loginUsername").value.trim();
    const password = document.getElementById("loginPassword").value;

    try {
        const result = await postJson("/api/auth/login", { username, password });

        if (!result.ok) {
            showMessage(buildErrorText(result.data), true);
            return;
        }

        // The token proves who the user is on every later request
        localStorage.setItem("token", result.data.token);
        localStorage.setItem("username", result.data.username);
        window.location.href = "editor.html";
    } catch (error) {
        showMessage("Cannot reach the server", true);
    }
});

// Register: create the account, then switch to the Login tab
document.getElementById("registerBtn").addEventListener("click", async () => {
    const username = document.getElementById("regUsername").value.trim();
    const email = document.getElementById("regEmail").value.trim();
    const password = document.getElementById("regPassword").value;

    try {
        const result = await postJson("/api/auth/register", { username, email, password });

        if (!result.ok) {
            showMessage(buildErrorText(result.data), true);
            return;
        }

        showTab(true);
        showMessage("Registration successful. Please log in.", false);
    } catch (error) {
        showMessage("Cannot reach the server", true);
    }
});
/*
-->1. Purpose
   auth.js connects the login page to the backend with the Fetch API.

-->2. How login works
   Click Login → fetch POST /api/auth/login → backend returns a JWT token
   → token is saved in localStorage → page opens editor.html

-->3. How errors are shown
   The backend sends ApiErrorResponse JSON. buildErrorText() shows the
   validationErrors messages, or the single message for errors like 401 and 409.

-->4. Why localStorage?
   It keeps the token after a page refresh, so the user stays logged in.
*/