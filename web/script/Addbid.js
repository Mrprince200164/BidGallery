// Fixed Addbid.js with better error handling
const coinButtons = document.querySelectorAll(".coin-btn");
const customCoinsInput = document.getElementById("customCoinsInput");
const addBidBtn = document.getElementById("addBidBtn");
const liveBidsList = document.getElementById("live-bids");
const userCoinBalanceDisplay = document.getElementById("user-coins");

// Changed from eventId to artId - this is the artwork ID from URL
const urlParams = new URLSearchParams(window.location.search);
const artId = urlParams.get("pid") || null;

let selectedCoinValue = parseInt(customCoinsInput.value); // default 500

function showMessage(message, type = "error") {
    // Simple alert or customize as needed
    alert(message);
}

// Enable or disable Add Bid button based on valid coin input and user balance
function validateBidButton() {
    const userBalance = parseInt(userCoinBalanceDisplay?.innerText || "0");
    if (!isNaN(selectedCoinValue) && selectedCoinValue > 0) {
        addBidBtn.disabled = false;
    } else {
        addBidBtn.disabled = true;
    }
}

// Coin button click
coinButtons.forEach(btn => {
    btn.addEventListener("click", () => {
        coinButtons.forEach(b => b.classList.remove("selected"));
        btn.classList.add("selected");
        selectedCoinValue = parseInt(btn.getAttribute("data-value"));
        customCoinsInput.value = selectedCoinValue;
        validateBidButton();
    });
});

// Custom coin input change
customCoinsInput.addEventListener("input", () => {
    coinButtons.forEach(b => b.classList.remove("selected"));
    selectedCoinValue = parseInt(customCoinsInput.value);
    validateBidButton();
});

// Initial validation
validateBidButton();

// Function to update live bids list (last 5 bids)
function updateLiveBids(bids, highestBidderEmail) {
    liveBidsList.innerHTML = "";

    if (!bids || bids.length === 0) {
        const li = document.createElement("li");
        li.innerHTML = "No bids yet";
        li.style.fontStyle = "italic";
        li.style.color = "#666";
        liveBidsList.appendChild(li);
        return;
    }

    // Show max 5 bids (server already limits to 5)
    bids.forEach(bid => {
        const li = document.createElement("li");
        li.innerHTML = `${bid.email} <span${bid.email === highestBidderEmail ? ' style="color:red;font-weight:bold;"' : ''}>${bid.bidAmountCoins}</span>`;
        liveBidsList.appendChild(li);
    });
}

// Function to load initial live bids when page loads
async function loadLiveBids() {
    if (!artId) {
        console.log("No artwork ID available");
        return;
    }

    try {
        const response = await fetch(`GetLiveBids?artId=${artId}`, {
            method: "GET"
        });

        // Check if response is ok before parsing
        if (!response.ok) {
            console.log(`HTTP error! status: ${response.status}`);
            return;
        }

        // Check content type to ensure it's JSON
        const contentType = response.headers.get("content-type");
        if (!contentType || !contentType.includes("application/json")) {
            const text = await response.text();
            console.log("Non-JSON response received:", text);
            return;
        }

        const json = await response.json();

        if (json.success && json.latestBids) {
            updateLiveBids(json.latestBids, json.highestBidderEmail);
        } else {
            console.log("Failed to load live bids:", json.message || "Unknown error");
        }
    } catch (error) {
        console.log("Error loading live bids:", error.message);
        console.log("Full error:", error);
    }
}

// Add Bid button click handler
async function Addbid() {
    if (!artId) {
        showMessage("Artwork ID is missing.");
        return;
    }

    const popup = Notification();
    
    console.log("Sending bid request with:", {
        bidAmount: selectedCoinValue,
        artId: parseInt(artId)
    });

    try {
        const response = await fetch("Addbid", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                bidAmount: selectedCoinValue,
                artId: parseInt(artId)  // Send artId instead of eventId
            })
        });

        console.log("Response status:", response.status);
        console.log("Response headers:", response.headers);

        // Check if response is ok
        if (!response.ok) {
            console.log(`HTTP error! status: ${response.status}`);
            popup.error({message: `Server error: ${response.status}`});
            return;
        }

        // Check content type to ensure it's JSON
        const contentType = response.headers.get("content-type");
        if (!contentType || !contentType.includes("application/json")) {
            const text = await response.text();
            console.log("Non-JSON response received:", text);
            popup.error({message: "Server returned invalid response format"});
            return;
        }

        const json = await response.json();
        console.log("Parsed response:", json);

        if (json.success) {
            popup.success({message: json.message || "Bid placed successfully!"});
            // Reload live bids to show updated list
            loadLiveBids();
            validateBidButton(); // To disable button if no balance
        } else {
            popup.error({message: json.content || json.message || "Bid failed!"});
        }
    } catch (error) {
        console.log("Fetch error:", error);
        
        // Check if it's a JSON parsing error
        if (error.message.includes("Unexpected token")) {
            popup.error({message: "Server returned invalid response. Please check if you're logged in."});
        } else {
            popup.error({message: "Error placing bid: " + error.message});
        }
    }
}

// Load live bids when page loads
document.addEventListener('DOMContentLoaded', loadLiveBids);

// Auto-refresh live bids every 10 seconds
setInterval(loadLiveBids, 10000);