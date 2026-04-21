document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".specialist-card").forEach((card, index) => {
        card.style.animationDelay = `${index * 60}ms`;
    });
});
