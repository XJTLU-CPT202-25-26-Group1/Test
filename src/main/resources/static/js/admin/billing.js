document.addEventListener("DOMContentLoaded", () => {
    const rows = document.querySelectorAll("tbody tr");
    rows.forEach((row, index) => {
        row.style.animationDelay = `${index * 45}ms`;
    });
});
