document.addEventListener("DOMContentLoaded", () => {
    const form = document.querySelector("[data-slot-edit-form]");
    if (form) {
        document.querySelectorAll("[data-slot-edit-trigger]").forEach((button) => {
            button.addEventListener("click", () => {
                form.querySelector("#slotId").value = button.dataset.slotId;
                form.querySelector("#editDate").value = button.dataset.slotDate;
                form.querySelector("#editStartTime").value = button.dataset.slotStart;
                form.querySelector("#editEndTime").value = button.dataset.slotEnd;
            });
        });
    }

    document.querySelectorAll(".js-confirm-action").forEach((button) => {
        button.addEventListener("click", (event) => {
            const message = button.dataset.confirmMessage || "Are you sure?";
            if (!window.confirm(message)) {
                event.preventDefault();
            }
        });
    });
});
