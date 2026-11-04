window.addEventListener("DOMContentLoaded", async () => {
    const res = await fetch("LoadProfileData");
    const data = await res.json();

    if (!data.success) {
        alert("Failed to load profile data");
        return;
    }

    const user = data.user;
    const artist = data.artist;

    // General fields
    document.querySelector('input[placeholder="John"]').value = user.firstName;
    document.querySelector('input[placeholder="Doe"]').value = user.lastName;
    document.querySelector('input[placeholder="john@example.com"]').value = user.email;
    document.querySelector('input[placeholder="********"]').value = user.password;
    document.querySelector('input[placeholder="+94 77 123 4567"]').value = user.mobile;
    document.querySelector('input[placeholder="Enter your address"]').value = user.address;
    document.querySelector(".coin-balance").value = `Coins: ${user.coins}`;

    // Artist checkbox
    document.getElementById("isArtist").checked = user.isArtist === 1;

    // Artist tab
    if (artist) {
        document.querySelector('textarea[placeholder="Enter artist bio"]').value = artist.bio;
        document.querySelectorAll('input[type="date"]')[0].value = artist.birthDate;
        document.querySelectorAll('input[type="date"]')[1].value = artist.deathDate;
        document.querySelector('input[placeholder="Enter nationality"]').value = artist.nationality;
        document.querySelector('input[placeholder*="style"]').value = artist.style;
    }

    // My Collection
    const artworkRow = document.querySelector("#account-mycollection .row");
    artworkRow.innerHTML = "";

    data.artworks.forEach(a => {
        const col = document.createElement("div");
        col.className = "col-md-4 d-flex justify-content-center";

        col.innerHTML = `
            <div class="custom-card mb-4">
                <img src="${a.imageUrl}" class="card-img-top" alt="${a.title}">
                <div class="card-body">
                    <h6 class="card-title mb-1">${a.title}</h6>
                    <p class="text-muted mb-1">${a.medium} | ${a.dimensions} | ${a.creationYear}</p>
                    <p><strong>Status:</strong> ${a.status}</p>
                    <p><strong>Value:</strong> ${a.value} coins</p>

                </div>
            </div>
        `;

        artworkRow.appendChild(col);
    });
});
