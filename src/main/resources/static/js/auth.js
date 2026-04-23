document.addEventListener("DOMContentLoaded", () => {
    const roleField = document.querySelector("[data-role-select]");
    const specialistFields = document.querySelector("[data-specialist-fields]");
    const passwordToggles = document.querySelectorAll("[data-password-toggle]");
    const confirmFields = document.querySelectorAll("[data-confirm-password]");
    const uppercaseFields = document.querySelectorAll("[data-uppercase]");

    if (roleField && specialistFields) {
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
    }

    passwordToggles.forEach((toggle) => {
        const targetId = toggle.getAttribute("data-target");
        const target = targetId ? document.getElementById(targetId) : null;

        if (!target) {
            return;
        }

        toggle.addEventListener("click", () => {
            const reveal = target.type === "password";
            target.type = reveal ? "text" : "password";
            toggle.textContent = reveal ? "Hide" : "Show";
        });
    });

    confirmFields.forEach((confirmField) => {
        const targetId = confirmField.getAttribute("data-confirm-target");
        const target = targetId ? document.getElementById(targetId) : null;

        if (!target) {
            return;
        }

        const validateMatch = () => {
            if (!confirmField.value) {
                confirmField.setCustomValidity("");
                return;
            }

            if (confirmField.value !== target.value) {
                confirmField.setCustomValidity("The passwords do not match.");
                return;
            }

            confirmField.setCustomValidity("");
        };

        target.addEventListener("input", validateMatch);
        confirmField.addEventListener("input", validateMatch);
        validateMatch();
    });

    uppercaseFields.forEach((field) => {
        field.addEventListener("input", () => {
            field.value = field.value.toUpperCase();
        });
    });
});
