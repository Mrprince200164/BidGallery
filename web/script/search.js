const itemsPerPage = 6;
let currentPage = 0;
let totalItems = 0;
let allArtists = []; // Store all artists for dropdown
let allMediums = []; // Store all mediums for checkboxes

document.addEventListener("DOMContentLoaded", function () {
    loadData();
    setupEventListeners();
});

function setupEventListeners() {
    // Search input listener with debounce
    const searchInput = document.getElementById("search-input");
    let searchTimeout;
    if (searchInput) {
        searchInput.addEventListener("input", function() {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(() => {
                currentPage = 0;
                applyFilters();
            }, 500); // 500ms delay for debouncing
        });
    }

    // Medium checkbox listeners
    const mediumCheckboxes = document.querySelectorAll('.medium-filter');
    mediumCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', () => {
            currentPage = 0;
            applyFilters();
        });
    });

    // Year input listener
    const yearInput = document.getElementById("year-input");
    if (yearInput) {
        let yearTimeout;
        yearInput.addEventListener("input", function() {
            clearTimeout(yearTimeout);
            yearTimeout = setTimeout(() => {
                currentPage = 0;
                applyFilters();
            }, 800);
        });
    }

    // Artist select listener
    const artistSelect = document.getElementById("artist-select");
    if (artistSelect) {
        artistSelect.addEventListener("change", () => {
            currentPage = 0;
            applyFilters();
        });
    }
}

async function loadData(page = 0) {
    try {
        // First time loading - get initial data and populate dropdowns
        if (page === 0 && allArtists.length === 0) {
            const response = await fetch("LoadData");
            if (response.ok) {
                const json = await response.json();
                if (json.success) {
                    // Store artists and mediums data
                    allArtists = json.artistList || [];
                    allMediums = json.mediumList || [];
                    
                    // Populate dropdowns and checkboxes
                    populateArtistDropdown(allArtists);
                    populateMediumCheckboxes(allMediums);
                    
                    // Display initial artworks
                    totalItems = json.allArtworkCount || 0;
                    updateArtworkView(json.artworks || []);
                    renderPagination(totalItems);
                    
                    // Update results count safely
                    setTimeout(() => updateResultsCount(totalItems), 100);
                    return;
                } else {
                    showNotification("Failed to load initial data", "error");
                }
            } else {
                showNotification("Network error occurred", "error");
            }
        }
        
        // For pagination or filtering, use search endpoint
        await applyFilters();
        
    } catch (error) {
        console.error("Error loading data:", error);
        showNotification("An error occurred while loading data", "error");
        
        // Show error state in artwork container
        const container = document.getElementById("artwork-container");
        if (container) {
            container.innerHTML = `
                <div style="grid-column: 1 / -1; text-align: center; padding: 40px; color: #dc3545;">
                    <h3>Error Loading Data</h3>
                    <p>Please refresh the page and try again.</p>
                    <button onclick="location.reload()" style="padding: 10px 20px; background: #007759; color: white; border: none; border-radius: 8px; cursor: pointer;">Refresh Page</button>
                </div>`;
        }
    }
}

function populateArtistDropdown(artists) {
    const artistSelect = document.getElementById("artist-select");
    if (!artistSelect) return;
    
    artistSelect.innerHTML = '<option value="">All Artists</option>';
    
    artists.forEach(artist => {
        const option = document.createElement('option');
        option.value = artist.id;
        option.textContent = artist.name;
        artistSelect.appendChild(option);
    });
}

function populateMediumCheckboxes(mediums) {
    const mediumContainer = document.querySelector('.filter-section:nth-of-type(2) .filter-options');
    if (!mediumContainer) return;
    
    // Clear existing checkboxes
    mediumContainer.innerHTML = '';
    
    mediums.forEach(medium => {
        const label = document.createElement('label');
        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.className = 'medium-filter';
        checkbox.value = medium.mediumName; // Use the actual medium name from database
        
        label.appendChild(checkbox);
        label.appendChild(document.createTextNode(' ' + medium.mediumName));
        mediumContainer.appendChild(label);
        
        // Add event listener to the new checkbox
        checkbox.addEventListener('change', () => {
            currentPage = 0;
            applyFilters();
        });
    });
}

async function applyFilters() {
    try {
        const searchValue = document.getElementById("search-input")?.value.trim() || "";
        const yearValue = document.getElementById("year-input")?.value.trim() || "";
        const artistValue = document.getElementById("artist-select")?.value || "";
        
        // Get selected mediums from checkboxes
        const selectedMediums = [];
        const mediumCheckboxes = document.querySelectorAll('.medium-filter:checked');
        mediumCheckboxes.forEach(checkbox => {
            selectedMediums.push(checkbox.value);
        });
        
        // Debug: Log what we're sending
        console.log("Applying filters:", {
            search: searchValue,
            mediums: selectedMediums,
            year: yearValue,
            artistId: artistValue,
            page: currentPage
        });
        
        // Prepare request data
        const requestData = {
            search: searchValue,
            mediums: selectedMediums.join(','),
            year: yearValue,
            artistId: artistValue,
            firstResult: currentPage * itemsPerPage,
            sort: "latest" // You can make this dynamic if needed
        };
        
        const response = await fetch("SearchArtwork", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestData)
        });
        
        if (response.ok) {
            const json = await response.json();
            console.log("Server response:", json); // Debug log
            
            if (json.success) {
                totalItems = json.allArtworkCount || 0;
                updateArtworkView(json.artworks || []);
                renderPagination(totalItems);
                
                // Show results count with delay to ensure DOM is ready
                setTimeout(() => updateResultsCount(totalItems), 50);
            } else {
                console.error("Server error:", json.error);
                showNotification(json.error || "Failed to load filtered results", "error");
            }
        } else {
            console.error("Network error:", response.status, response.statusText);
            showNotification("Network error occurred", "error");
        }
        
    } catch (error) {
        console.error("Error applying filters:", error);
        showNotification("An error occurred while filtering", "error");
    }
}

function updateResultsCount(count) {
    // Add or update results count display
    let resultsInfo = document.getElementById("results-info");
    if (!resultsInfo) {
        resultsInfo = document.createElement("div");
        resultsInfo.id = "results-info";
        resultsInfo.style.cssText = "margin-bottom: 20px; padding: 10px; background: #f8f9fa; border-radius: 8px; font-size: 14px; color: #666;";
        
        // Try multiple ways to insert the element safely
        const productGrid = document.getElementById("artwork-container");
        const mainWrapper = document.querySelector(".main-wrapper");
        
        if (productGrid && productGrid.parentNode) {
            // Insert before the product grid
            productGrid.parentNode.insertBefore(resultsInfo, productGrid);
        } else if (mainWrapper) {
            // If product grid not found, prepend to main wrapper
            mainWrapper.insertBefore(resultsInfo, mainWrapper.firstChild);
        } else {
            // Last resort - append to body
            document.body.appendChild(resultsInfo);
        }
    }
    
    resultsInfo.textContent = `Found ${count} artwork${count !== 1 ? 's' : ''}`;
}

function updateArtworkView(artworks) {
    const container = document.getElementById("artwork-container");
    if (!container) return;
    
    container.innerHTML = "";

    if (!artworks || artworks.length === 0) {
        container.innerHTML = `
            <div style="grid-column: 1 / -1; text-align: center; padding: 40px; color: #666;">
                <h3>No artworks found</h3>
                <p>Try adjusting your search criteria or filters.</p>
            </div>`;
        return;
    }

    artworks.forEach(item => {
        const card = document.createElement("div");
        card.className = "product-card";
        card.innerHTML = `
            <img src="${item.imageUrl}" alt="${item.title}" onerror="this.src='placeholder-image.jpg'">
            <div class="product-body">
                <h5>${item.title}</h5>
                <p><strong>Artist:</strong> ${item.artistName || 'Unknown'}</p>
                <p><strong>Medium:</strong> ${item.medium || 'N/A'}</p>
                <p><strong>Year:</strong> ${item.creationYear || 'N/A'}</p>
                <label><strong>Highest bid:</strong> <span class="highlight-bid">${item.highestBid || '0'}</span></label>
                <div class="date-row">
                    <span><strong>End Time:</strong> ${item.endTime}</span>
                </div>
                <button class="bid-button" onclick="window.location.href='SingleProductView.html?pid=${item.artworkId}'">Add your Bid</button>
                <button class="cart-button" onclick="addToCart(${item.artworkId})">Add to Cart</button>
            </div>
        `;
        container.appendChild(card);
    });
}

function renderPagination(totalItems) {
    const paginationContainer = document.getElementById("pagination-container");
    if (!paginationContainer) return;
    
    paginationContainer.innerHTML = "";

    const totalPages = Math.ceil(totalItems / itemsPerPage);
    
    if (totalPages <= 1) return; // No pagination needed

    // Previous button
    if (currentPage > 0) {
        const prevBtn = createPageButton("‹ Prev", currentPage - 1);
        prevBtn.style.cssText = "text-decoration: none; color: #555; padding: 8px 16px; border-radius: 8px; border: 1px solid #ddd; margin-right: 5px; cursor: pointer;";
        paginationContainer.appendChild(prevBtn);
    }

    // Page numbers (show max 5 pages)
    const startPage = Math.max(0, currentPage - 2);
    const endPage = Math.min(totalPages - 1, startPage + 4);

    if (startPage > 0) {
        const firstBtn = createPageButton("1", 0);
        paginationContainer.appendChild(firstBtn);
        if (startPage > 1) {
            const dots = document.createElement("span");
            dots.textContent = "...";
            dots.style.cssText = "padding: 8px 16px; color: #999;";
            paginationContainer.appendChild(dots);
        }
    }

    for (let i = startPage; i <= endPage; i++) {
        const pageBtn = createPageButton(i + 1, i, i === currentPage);
        paginationContainer.appendChild(pageBtn);
    }

    if (endPage < totalPages - 1) {
        if (endPage < totalPages - 2) {
            const dots = document.createElement("span");
            dots.textContent = "...";
            dots.style.cssText = "padding: 8px 16px; color: #999;";
            paginationContainer.appendChild(dots);
        }
        const lastBtn = createPageButton(totalPages, totalPages - 1);
        paginationContainer.appendChild(lastBtn);
    }

    // Next button
    if (currentPage < totalPages - 1) {
        const nextBtn = createPageButton("Next ›", currentPage + 1);
        nextBtn.style.cssText = "text-decoration: none; color: #555; padding: 8px 16px; border-radius: 8px; border: 1px solid #ddd; margin-left: 5px; cursor: pointer;";
        paginationContainer.appendChild(nextBtn);
    }
}

function createPageButton(text, page, isActive = false) {
    const btn = document.createElement("a");
    btn.href = "#";
    btn.textContent = text;
    btn.style.cssText = `
        text-decoration: none;
        color: ${isActive ? '#fff' : '#555'};
        background-color: ${isActive ? '#007bff' : 'transparent'};
        border: 1px solid ${isActive ? '#007bff' : '#ddd'};
        padding: 8px 16px;
        border-radius: 8px;
        margin: 0 2px;
        transition: background-color 0.2s, color 0.2s;
        cursor: pointer;
    `;
    
    if (!isActive) {
        btn.addEventListener('mouseenter', function() {
            this.style.backgroundColor = '#f0f0f0';
        });
        btn.addEventListener('mouseleave', function() {
            this.style.backgroundColor = 'transparent';
        });
    }
    
    btn.addEventListener("click", (e) => {
        e.preventDefault();
        if (page !== currentPage) {
            currentPage = page;
            applyFilters();
        }
    });
    
    return btn;
}

function addToCart(artworkId) {
    showNotification(`Artwork ${artworkId} added to cart!`, "success");
    // Add your actual cart logic here
}

function showNotification(message, type = "info") {
    // Simple notification system
    const notification = document.createElement("div");
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        border-radius: 8px;
        color: white;
        font-weight: 500;
        z-index: 1000;
        transition: opacity 0.3s ease;
        background-color: ${type === 'error' ? '#dc3545' : type === 'success' ? '#28a745' : '#17a2b8'};
    `;
    notification.textContent = message;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.style.opacity = '0';
        setTimeout(() => {
            if (document.body.contains(notification)) {
                document.body.removeChild(notification);
            }
        }, 300);
    }, 3000);
}

// Clear all filters function
function clearAllFilters() {
    document.getElementById("search-input").value = "";
    document.getElementById("year-input").value = "";
    document.getElementById("artist-select").value = "";
    
    const mediumCheckboxes = document.querySelectorAll('.medium-filter');
    mediumCheckboxes.forEach(checkbox => {
        checkbox.checked = false;
    });
    
    currentPage = 0;
    applyFilters();
}