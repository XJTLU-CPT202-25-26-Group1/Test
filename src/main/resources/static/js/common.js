document.addEventListener("DOMContentLoaded", () => {
    const navToggle = document.querySelector("[data-nav-toggle]");
    const navMenu = document.querySelector("[data-nav-menu]");

    if (navToggle && navMenu) {
        navToggle.addEventListener("click", () => {
            navMenu.classList.toggle("is-open");
        });
    }

    const flashMessage = document.querySelector("[data-flash-message]");
    const flashClose = document.querySelector("[data-flash-close]");

    if (flashMessage && flashClose) {
        flashClose.addEventListener("click", () => {
            flashMessage.remove();
        });

        window.setTimeout(() => {
            if (document.body.contains(flashMessage)) {
                flashMessage.remove();
            }
        }, 5000);
    }

    document.querySelectorAll("[data-danger-confirm]").forEach((element) => {
        element.addEventListener("click", (event) => {
            const message = element.getAttribute("data-danger-confirm") || "Are you sure?";
            if (!window.confirm(message)) {
                event.preventDefault();
            }
        });
    });
});
