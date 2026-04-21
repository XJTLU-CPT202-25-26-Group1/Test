document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll("form").forEach((form) => {
        form.autocomplete = "off";
    });
});
