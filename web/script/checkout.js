const popup = Notification();



    // Payment completed
    payhere.onCompleted = function (orderId) {
        console.log("Payment completed. OrderID:", orderId);
        popup.success({ message: "Thank you, payment completed!" });
        window.location.href = "index.html";
    };

    // Payment dismissed
    payhere.onDismissed = function () {
        console.log("Payment dismissed");
        popup.error({ message: "Payment was canceled." });
    };

    // Payment error
    payhere.onError = function (error) {
        console.log("Error:", error);
        popup.error({ message: "Payment error occurred: " + error });
    };

    async function purchasePackage(packageType) {
        const response = await fetch("Checkout", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ packageType })
        });

        if (response.ok) {
            const json = await response.json();
            console.log(json);

            if (json.success) {
                payhere.startPayment(json.payhereJson);
            } else {
                popup.error({ message: json.message });
            }
        } else {
            popup.error({ message: "Server error. Please try again later." });
        }
    }

    // Buttons
    async function basicPackage() {
        await purchasePackage("BASIC");
    }

    async function proPackage() {
        await purchasePackage("PRO");
    }

    async function enterprisePackage() {
        await purchasePackage("ENTERPRISE");
    }