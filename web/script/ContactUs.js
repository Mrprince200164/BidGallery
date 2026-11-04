function emailSend() {
    const userName = document.getElementById('name').value.trim();
    const subject = document.getElementById('subject').value.trim();
    const userEmail = document.getElementById('email').value.trim();
    const userMessage = document.getElementById('usermassage').value.trim();

    if (!userName || !userEmail || !subject || !userMessage) {
        swal("Warning", "Please fill in all fields.", "warning");
        return;
    }

    const messageBody = `
        <h3>New Contact Form Submission</h3>
        <p><strong>Name:</strong> ${userName}</p>
        <p><strong>Email:</strong> ${userEmail}</p>
        <p><strong>Subject:</strong> ${subject}</p>
        <p><strong>Message:</strong> ${userMessage}</p>
    `;

    Email.send({
        Host: "smtp.elasticemail.com", // Elastic Email's SMTP host
        Username: "mrprince20011@gmail.com", // Your Elastic Email verified sender
        Password: "4dfb6864-fe1f-4fce-8f81-7b9399c09b72", // Use your Elastic Email API key here
        To: "mrprince20011@gmail.com", // Recipient email, can be same as From if verified
        From: "mrprince2979@gmail.com", // Your Elastic Email verified sender
        Subject: subject,
        Body: messageBody, // Use Body for HTML content
    }).then(
            message => {
                if (message === "OK") { // Elastic Email typically returns "OK" on success
                    swal("Successful", "Email has been sent successfully!", "success");
                } else {
                    console.error("Email.send Error:", message);
                    swal("Error", "Failed to send email! Check the console for details.", "error");
                }
            }
    ).catch(error => {
        console.error("Network Error or Email.send failed:", error);
        swal("Error", "A network error occurred or email sending failed!", "error");
    });
}