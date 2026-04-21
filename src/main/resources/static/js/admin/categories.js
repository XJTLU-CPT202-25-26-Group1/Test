document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll("input[name='name']").forEach((input) => {
        input.addEventListener("blur", () => {
            input.value = input.value.trim();
        });
    });
});
