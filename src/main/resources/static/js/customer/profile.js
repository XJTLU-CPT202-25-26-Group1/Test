document.addEventListener("DOMContentLoaded", () => {
    const emailInput = document.querySelector("input[type='email']");
    if (!emailInput) {
        return;
    }

    emailInput.addEventListener("blur", () => {
        emailInput.value = emailInput.value.trim().toLowerCase();
    });
});
