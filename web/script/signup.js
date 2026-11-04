// Ensure container is selected safely
if (typeof container === 'undefined') {
    var container = document.querySelector(".container");
}

// Select form toggle buttons
const signupBtn = document.getElementById("sign-up-btn");
const signinBtn = document.getElementById("sign-in-btn");

// Select forms and popups
const signupForm = document.querySelector(".sign-up-form");
const verificationPopup = document.getElementById("verification-frame");

// Toggle between Sign In and Sign Up modes
signupBtn.addEventListener("click", () => {
    container.classList.add("sign-up-mode");
});

signinBtn.addEventListener("click", () => {
    container.classList.remove("sign-up-mode");
});

// Intercept form submission
signupForm.addEventListener("submit", function (e) {
    e.preventDefault(); // Prevent default form submit
    signUp();
});

async function signUp() {

    const user_dto = {
        firstName: document.getElementById("first_name").value,
        lastName: document.getElementById("last_name").value,
        mobile: document.getElementById("mobile").value,
        email: document.getElementById("email").value,
        password: document.getElementById("password").value
    };

  console.log(user_dto);

    const response = await fetch(
            
            "SignUp",
            {
                method: "POST",
                body: JSON.stringify(user_dto),
                headers: {
                    "Content-Type": "application/json"
                }
            }
    );
    console.log( response);
    
    if (response.ok) {
        const json = await response.json();

        if (json.success) {
            verificationPopup.style.display = "flex";
        } else {
            document.getElementById("message2").innerHTML = json.content;
        }
    } else {
        document.getElementById("message2").innerHTML = "Please try agin later";
    }
}

// Verify Account Function
async function verifyAccount() {
    const verificationCode = document.getElementById("verification-code").value.trim();

    const dto = {
        verification: verificationCode
    };

    const response = await fetch("Verification", {
        method: "POST",
        body: JSON.stringify(dto),
        headers: {
            "Content-Type": "application/json"
        }
    });

    if (response.ok) {
        const json = await response.json();

        if (json.success) {
            // Hide the popup
            verificationPopup.style.display = "none";

            // Redirect to homepage
            window.location.href = "index.html";
        } else {
            document.getElementById("massage3").innerHTML = json.content;
        }
    } else {
        document.getElementById("massage3").innerHTML = "Please try again later.";
    }

}
