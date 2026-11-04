async function loadProduct() {
    const parameters = new URLSearchParams(window.location.search);

    if (parameters.has("pid")) {
        const pid = parameters.get("pid");
        const response = await fetch("LoadSingleProduct?pid=" + pid);

        if (response.ok) {
            
           
            const json = await response.json();

            const artwork = json.artwork;
            const event = json.event;
            const bids = json.bids;
            const userId = json.userId;
//            const userCoins = json.userCoins;
              console.log(json);
            // 🖼 Set main image from DB URL
            document.getElementById("artwork-image").src = artwork.imageUrl;

            // 🖌 Set artwork details
//            document.getElementById("event-name").innerText = event.eventName;
            document.getElementById("title").innerText = artwork.title;
            document.getElementById("description").innerText = artwork.description;
            document.getElementById("creation-year").innerText = new Date(artwork.date_time).getFullYear();
            document.getElementById("artist-name").innerText = artwork.artist.user.firstName;
            document.getElementById("dimensions").innerText = artwork.diamensions;

            // ⏱ Set event times
            document.getElementById("start-time").innerText = new Date(event.startTime).toLocaleString();
            document.getElementById("end-time").innerText = new Date(event.endTime).toLocaleString();


            // 💰 User's available coins
//            document.getElementById("user-coins").innerText = userCoins ?? "Not Logged In";

            // 🛒 Setup "Add to Cart" button
//            document.getElementById("addToCartBtn").addEventListener("click", (e) => {
//                addToCart(event.id, userId);
//                e.preventDefault();
//            });
//            // 🔁 Live bids
//            const bidContainer = document.getElementById("live-bids");
//            bidContainer.innerHTML = ""; // Clear existing

//            bids.forEach(bid => {
//                const li = document.createElement("li");
//                li.textContent = ${bid.bidder.email} - ${bid.bidAmountCoins} coins;
//                bidContainer.appendChild(li);
//            });

        } else {
            window.location = "index.html";
        }
    } else {
        window.location = "index.html";
    }
}



async function addToCart(eventId, userId) {
    const popup = Notification();

    try {
        const response = await fetch("AddToCart", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                bettingEventId: eventId,
                userId: userId
            })
        });

        if (response.ok) {
            const json = await response.json();
            if (json.success) {
                popup.success({ message: json.content });
            } else {
                popup.error({ message: json.content });
            }
        } else {
            popup.error({ message: "Unable to process your request" });
        }
    } catch (error) {
        popup.error({ message: "Something went wrong: " + error.message });
    }
}