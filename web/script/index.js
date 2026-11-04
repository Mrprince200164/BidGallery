async function checkSignIn() {
    const response = await fetch("CheckSignIn");
    if (response.ok) {
        const json = await response.json();
        const response_dto = json.response_dto;

        if (response_dto.success) {
            const user = response_dto.content;

            // Hide login button if visible
            const loginBtn = document.querySelector(".login-btn");
            if (loginBtn) {
                loginBtn.style.display = "none";
            }

            // ✅ Hide admin-only tabs for non-admin users
            const isAdmin = user.isAdmin;
            const shouldHideTab = (isAdmin === 0 || isAdmin === "0" || isAdmin === false || isAdmin === "false");

            if (shouldHideTab) {
                const tabSelectors = [
                    // Betting Events tab
                    'a[href="#account-events"]',
                    'a[data-toggle="list"][href="#account-events"]',
                    '.list-group-item[href="#account-events"]',

                    // Edit Users tab
                    'a[href="#account-users"]',
                    'a[data-toggle="list"][href="#account-users"]',
                    '.list-group-item[href="#account-users"]'
                ];

                let foundAny = false;
                for (const selector of tabSelectors) {
                    const tab = document.querySelector(selector);
                    if (tab) {
                        tab.style.display = "none";
                        const parentLi = tab.closest('li');
                        if (parentLi) {
                            parentLi.style.display = "none";
                        }
                        foundAny = true;
                    }
                }

                if (!foundAny) {
                    console.warn("Admin-only tabs not found with given selectors");
                }
            }
        } else {
            console.log("Not signed in");
        }

        // ✅ Load 3 most recent betting events
        const events = json.betting_event_cards;
        const trendingContainer = document.getElementById("trendingContainer");
        if (trendingContainer && events) {
            trendingContainer.innerHTML = ""; // Clear existing cards

            events.forEach(event => {
                const card = document.createElement("div");
                card.className = "custom-card";
                const endTime = new Date(event.endTime);
                const formattedEndTime = endTime.toLocaleString();

                card.innerHTML = `
                    <img src="${event.imageUrl}" class="custom-card-image" alt="${event.title}">
                    <div class="custom-card-body">
                        <h5>${event.title}</h5>
                        <label><strong>Highest bid:</strong> <span class="highlight-bid">${event.highestBidCoins}</span> </label>
                        <div class="date-row">
                            <span><strong>End Time:</strong> ${formattedEndTime}</span>
                        </div>
                        <button class="custom-btn" type="button" onclick="window.location.href='SingleProductView.html?pid=${event.artworkId}'">Add your Bid</button>
                    </div>
                `;

                trendingContainer.appendChild(card);
            });
        }
    }
}

function openCart() {
    document.getElementById("cartPopup").style.display = "block";
}

function closeCart() {
    document.getElementById("cartPopup").style.display = "none";
}
