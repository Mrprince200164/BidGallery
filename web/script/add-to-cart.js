// const products = [
//      { id: 1, name: "Mona Lisa", desc: "Elegant portrait by Leonardo da Vinci", image: "Resources/Mona_Lisa_by_Leonardo_Da_Vinci.jpg" },
//      { id: 2, name: "The Scream", desc: "Emotional artwork by Edvard Munch", image: "Resources/The_Scream_by_Edvard_Munch.jpg" },
//      { id: 3, name: "The Starry Night", desc: "Famous night sky by Van Gogh", image: "Resources/The_Starry_Night_by_Vincent_Van_Gogh.jpg" },
//      { id: 4, name: "Girl with a Pearl Earring", desc: "Dutch masterpiece by Vermeer", image: "https://upload.wikimedia.org/wikipedia/commons/d/d7/Meisje_met_de_parel.jpg" },
//    ];

    let cart = [];

    const productList = document.getElementById("productList");
    const cartCount = document.getElementById("cartCount");
    const searchInput = document.getElementById("searchInput");

    function displayProducts(filtered = products) {
      productList.innerHTML = "";
      filtered.forEach(product => {
        productList.innerHTML += `
          <div class="card">
            <img src="${product.image}" alt="${product.name}">
            <h3>${product.name}</h3>
            <p>${product.desc}</p>
            <button onclick="addToCart(${product.id})">Add to Cart</button>
          </div>
        `;
      });
    }

    function addToCart(productId) {
      const item = products.find(p => p.id === productId);
      cart.push(item);
      cartCount.textContent = cart.length;
    }

    searchInput.addEventListener("input", () => {
      const value = searchInput.value.toLowerCase();
      const filtered = products.filter(p => p.name.toLowerCase().includes(value));
      displayProducts(filtered);
    });

    displayProducts();