    function goToGeneralTab() {
        const tab = document.querySelector('.list-group-item[href="#account-general"]');
        if (tab) {
            tab.click();
        }
    }

async function loadFeatures() {
    const response = await fetch("LoadFeatures");
    if (response.ok) {
        const json = await response.json();
        console.log(json);

        const mediumList = json.mediumList;
        const artworkstatusList = json.artworkstatusList;
        const artworksList = json.artworksList;

        loadSelectOptions("medium-select", mediumList, ["mediumid", "mediumName"]);
        loadSelectOptions("artwork-status", artworkstatusList, ["statusId", "statusName"]);
        loadSelectOptions("artwork-select", artworksList, ["artworkId", "title"]);

        // Listen for artwork selection changes
        const artworkSelect = document.getElementById("artwork-select");
        artworkSelect.addEventListener("change", function () {
            const selectedId = parseInt(this.value);
            const selectedArtwork = artworksList.find(a => a.artworkId === selectedId);

            console.log(selectedArtwork);

            if (selectedArtwork) {
                document.getElementById("artworkDescription").value = selectedArtwork.description || "";
                document.getElementById("startingBidCoins").value = selectedArtwork.estimatedCoinValue || "";
                document.getElementById("highestBidderUserId").value = selectedArtwork.ownerUserId || "";
            }
        });

    } else {
        console.log("Error loading data");
    }

    function loadSelectOptions(selectTagId, list, propertyArray) {
        const selectTag = document.getElementById(selectTagId);
        list.forEach(item => {
            const optionTag = document.createElement("option");
            optionTag.value = item[propertyArray[0]];
            optionTag.innerHTML = item[propertyArray[1]];
            selectTag.appendChild(optionTag);
        });
    }
}


async function ArtworkListing() {

    const titleTag = document.getElementById("title");
    const descriptionTag = document.getElementById("description");
    const creationyearTag = document.getElementById("creation-year");
    const diamensionTag = document.getElementById("diamension");
    const mediumSelectTag = document.getElementById("medium-select");
    const coinvalueTag = document.getElementById("coin-value");
    const ArtimageTag = document.getElementById("artImage");
    const ArtistTag = document.getElementById("Artist");
    const CurrentOwnerTag = document.getElementById("currentOwner");
    const showMessage = document.getElementById("message");

    console.log(creationyearTag.value);

    const data = new FormData();
    data.append("title", titleTag.value);
    data.append("description", descriptionTag.value);
    data.append("creationYear", creationyearTag.value);
    data.append("diamension", diamensionTag.value);
    data.append("mediumId", mediumSelectTag.value);
    data.append("coinValue", coinvalueTag.value);
    data.append("Artist", ArtistTag.value);
    data.append("artImage", ArtimageTag.files[0]);
    data.append("currentOwner", CurrentOwnerTag.value);


    const response = await fetch(
            "ArtworkListing",
            {
                method: "POST",
                body: data
            }
    );

    if (response.ok) {
        const json = await response.json();

        const popup = Notification();

        if (json.success) {

            titleTag.value = "";
            descriptionTag.value = "";
            diamensionTag.value = "";
            creationyearTag.value = null;
            mediumSelectTag.value = 0;
            coinvalueTag.value = "";
            ArtimageTag.value = null;
            ArtistTag.value = '';
            CurrentOwnerTag.value = '';

//            loadFeatures();

            popup.success({
                message: json.content
            });

        } else {

            popup.error({
                message: json.content
            });
        }

    } else {
        console.log("try agin");
    }
}

async function saveEvents() {
    const artworkSelectTag = document.getElementById("artwork-select");
    const descriptionTag = document.getElementById("artworkDescription");
    const eventNameTag = document.getElementById("eventName");
    const startTimeTag = document.getElementById("startTime");
    const endTimeTag = document.getElementById("endTime");
    const startingBidCoinsTag = document.getElementById("startingBidCoins");
    const highestBidUserTag = document.getElementById("highestBidderUserId");

    const popup = Notification();

    if (artworkSelectTag.value === "0") {
        popup.error({message: "Please select an artwork."});
        return;
    }
    if (eventNameTag.value.trim() === "") {
        popup.error({message: "Event name is required."});
        return;
    }
    if (startTimeTag.value === "" || endTimeTag.value === "") {
        popup.error({message: "Start time and end time are required."});
        return;
    }
    if (
            startingBidCoinsTag.value.trim() === "" ||
            isNaN(startingBidCoinsTag.value) ||
            parseInt(startingBidCoinsTag.value) <= 0
            ) {
        popup.error({message: "Starting bid coins must be a positive number."});
        return;
    }
    if (highestBidUserTag.value === "0" || highestBidUserTag.value === "") {
        popup.error({message: "Please select a highest bidder user."});
        return;
    }

    const data = {
        artworkId: artworkSelectTag.value,
        eventName: eventNameTag.value,
        startTime: startTimeTag.value,
        endTime: endTimeTag.value,
        startingBidCoins: startingBidCoinsTag.value,
        highestBidderUserId: highestBidUserTag.value // ✅ required
    };

    try {
        const response = await fetch("AddEvents", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        });

        const result = await response.json();
        if (result.success) {
            popup.success({message: result.content});

            // Clear form
            artworkSelectTag.value = "0";
            descriptionTag.value = "";
            eventNameTag.value = "";
            startTimeTag.value = "";
            endTimeTag.value = "";
            startingBidCoinsTag.value = "";
            highestBidUserTag.value = "0";
        } else {
            popup.error({message: result.content});
        }

    } catch (error) {
        console.error("Fetch failed:", error);
        popup.error({message: "Network error. Please check your connection."});
    }
}

async function updateUser() {
    const userDto = {
        firstName: document.getElementById("first_name").value.trim(),
        lastName: document.getElementById("last_name").value.trim(),
        email: document.getElementById("email").value.trim(),
        password: document.getElementById("password").value.trim(),
        mobile: document.getElementById("mobile").value.trim(),
        isArtist: document.getElementById("isArtist").checked ? 1 : 0,
        address: document.getElementById("address").value.trim()
    };

  const popup = Notification();
  
    console.log("Updating user:", userDto);

    const response = await fetch("UpdateUser", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(userDto)
    });

    if (response.ok) {
        const json = await response.json();
        if (json.success) {
            popup.success("User updated successfully!");
            // Optionally reload or update UI here
        } else {
            popup.error("Update failed: " + json.content);
        }
    } else {
        popup.error("Error updating user. Please try again later.");
    }
}

async function updateArtist() {
    const artistDto = {
        bio: document.getElementById("bio").value.trim(),
        birthDate: document.getElementById("birthDate").value,  // yyyy-MM-dd format
        deathDate: document.getElementById("deathDate").value,  // yyyy-MM-dd or empty
        nationality: document.getElementById("nationality").value.trim(),
        style: document.getElementById("style").value.trim()
    };

     const popup = Notification();
     
    console.log("Updating artist info:", artistDto);

    const response = await fetch("UpdateArtist", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(artistDto)
    });

    if (response.ok) {
        const json = await response.json();
        if (json.success) {
             popup.success("Artist profile updated successfully!");
        } else {
              popup.error("Update failed: " + json.content);
        }
    } else {
          popup.error("Error updating artist profile. Please try again later.");
    }
}
