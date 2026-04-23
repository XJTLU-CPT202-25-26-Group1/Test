document.addEventListener("DOMContentLoaded", () => {
    const navRoot = document.querySelector("[data-nav-root]");
    const navToggle = document.querySelector("[data-nav-toggle]");
    const navMenu = document.querySelector("[data-nav-menu]");

    if (navRoot && navToggle && navMenu) {
        const closeMenu = () => {
            navMenu.classList.remove("is-open");
            navToggle.setAttribute("aria-expanded", "false");
        };

        navToggle.addEventListener("click", () => {
            const nextState = !navMenu.classList.contains("is-open");
            navMenu.classList.toggle("is-open", nextState);
            navToggle.setAttribute("aria-expanded", String(nextState));
        });

        document.addEventListener("click", (event) => {
            if (!navRoot.contains(event.target)) {
                closeMenu();
            }
        });

        document.addEventListener("keydown", (event) => {
            if (event.key === "Escape") {
                closeMenu();
            }
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
