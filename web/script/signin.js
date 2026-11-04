const sign_in_btn = document.querySelector("#sign-in-btn");
const sign_up_btn = document.querySelector("#sign-up-btn");
if (typeof container === 'undefined') {
  var container = document.querySelector(".container");
}


// Toggle sign-in / sign-up view
sign_up_btn.addEventListener("click", () => {
  container.classList.add("sign-up-mode");
});

sign_in_btn.addEventListener("click", () => {
  container.classList.remove("sign-up-mode");
});

  
async function signIn() {

    const user_dto = {
        email: document.getElementById("email").value,
        password: document.getElementById("password").value
    };

    const response = await fetch(
            "SignIn",
            {
                method: "POST",
                body: JSON.stringify(user_dto),
                headers: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) {
        const json = await response.json();

        if (json.success) {
            window.location = "index.html";

        } else {

            if (json.content === "Unverified") {
              verificationPopup.style.display = "flex";

            } else {
                document.getElementById("message1").innerHTML = json.content;
            }
        }
    } else {
        document.getElementById("message1").innerHTML = "Please try agin later";
    }
}
