document.addEventListener("DOMContentLoaded", () => {
    const roleField = document.querySelector("[data-role-select]");
    const specialistFields = document.querySelector("[data-specialist-fields]");

    if (!roleField || !specialistFields) {
        return;
    }

    const specialistInputs = specialistFields.querySelectorAll("input, select, textarea");

    const syncSpecialistFields = () => {
        const isSpecialist = roleField.value === "SPECIALIST";
        specialistFields.classList.toggle("hidden", !isSpecialist);

        specialistInputs.forEach((input) => {
            if (input.hasAttribute("data-required-for-specialist")) {
                input.required = isSpecialist;
            }
        });
    };

    roleField.addEventListener("change", syncSpecialistFields);
    syncSpecialistFields();
});
