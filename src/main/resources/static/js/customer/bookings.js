document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".js-confirm-action").forEach((button) => {
        button.addEventListener("click", (event) => {
            const message = button.dataset.confirmMessage || "Are you sure?";
            if (!window.confirm(message)) {
                event.preventDefault();
            }
        });
    });
});
