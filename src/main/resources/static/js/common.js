document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".js-confirm-logout").forEach((button) => {
        button.addEventListener("click", (event) => {
            if (!window.confirm("Are you sure you want to log out now?")) {
                event.preventDefault();
            }
        });
    });

    document.querySelectorAll("[data-dismiss-alert]").forEach((button) => {
        button.addEventListener("click", () => {
            const flash = button.closest(".flash");
            if (flash) {
                flash.remove();
            }
        });
    });

    document.querySelectorAll("[data-auto-dismiss='true']").forEach((flash) => {
        window.setTimeout(() => {
            flash.style.opacity = "0";
            window.setTimeout(() => flash.remove(), 250);
        }, 4500);
    });

    const sidebar = document.querySelector(".sidebar");
    const toggle = document.querySelector("[data-sidebar-toggle]");
    if (sidebar && toggle) {
        toggle.addEventListener("click", () => {
            sidebar.classList.toggle("is-open");
        });
    }
});
