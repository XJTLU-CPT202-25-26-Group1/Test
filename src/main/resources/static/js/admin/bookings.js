document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll("input[name='reason']").forEach((input) => {
        input.setAttribute("placeholder", "Reason for rejection");
    });
});
