document.addEventListener("DOMContentLoaded", () => {
    const roleSelect = document.querySelector("[data-role-select]");
    const specialistFields = document.querySelector("[data-specialist-fields]");

    if (!roleSelect || !specialistFields) {
        return;
    }

    const syncSpecialistFields = () => {
        const specialistMode = roleSelect.value === "SPECIALIST";
        specialistFields.classList.toggle("hidden", !specialistMode);
        specialistFields.querySelectorAll("select, input, textarea").forEach((field) => {
            if (field.dataset.specialistRequired === "true") {
                field.required = specialistMode;
            }
        });
    };

    roleSelect.addEventListener("change", syncSpecialistFields);
    syncSpecialistFields();
});
