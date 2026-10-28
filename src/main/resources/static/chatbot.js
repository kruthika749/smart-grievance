
const chatToggle = document.getElementById("chatbot-toggle");
const chatWindow = document.getElementById("chatbot-window");
const chatClose = document.getElementById("chatbot-close");
const chatForm = document.getElementById("chatbot-form");
const chatInput = document.getElementById("chatbot-input");
const chatMessages = document.getElementById("chatbot-messages");
const chatSend = document.getElementById("chatbot-send");

// Open or close the chatbot window
function setChatOpen(open) {
    chatWindow.classList.toggle("open", open);
    chatWindow.setAttribute("aria-hidden", String(!open));
    chatToggle.setAttribute("aria-expanded", String(open));

    if (open) {
        chatInput.focus();
    }
}

// Chatbot toggle button
chatToggle.addEventListener("click", () => {
    setChatOpen(!chatWindow.classList.contains("open"));
});

// Chatbot close button
chatClose.addEventListener("click", () => {
    setChatOpen(false);
    chatToggle.focus();
});

// Add a message to the chat window
function addMessage(message, sender) {
    const bubble = document.createElement("div");

    bubble.className = `chatbot-message ${sender}`;
    bubble.textContent = message;

    chatMessages.appendChild(bubble);
    chatMessages.scrollTop = chatMessages.scrollHeight;
}

// Send a message to the backend
chatForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const message = chatInput.value.trim();

    if (!message) {
        return;
    }

    // Display the user's message
    addMessage(message, "user");

    chatInput.value = "";
    chatInput.disabled = true;
    chatSend.disabled = true;

    // Display a temporary waiting message
    const waiting = document.createElement("div");

    waiting.className = "chatbot-message bot";
    waiting.textContent = "Thinking...";

    chatMessages.appendChild(waiting);
    chatMessages.scrollTop = chatMessages.scrollHeight;

    try {
        const response = await fetch("/api/chatbot/message", {
            method: "POST",

            // Send the server-side login session cookie
            credentials: "same-origin",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify({
                message: message
            })
        });

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
        }

        const data = await response.json();

        // Display the chatbot's response
        waiting.textContent =
            data.response || "Sorry, I couldn't generate a response.";

    } catch (error) {
        console.error("Chatbot request failed:", error);

        waiting.textContent =
            "Sorry, I couldn't connect to the assistant. Please try again.";

    } finally {
        // Re-enable the input
        chatInput.disabled = false;
        chatSend.disabled = false;
        chatInput.focus();
    }
});
