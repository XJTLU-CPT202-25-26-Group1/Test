const App = {
  showAlert(message, type = "info", container = ".card-body") {
    const target = document.querySelector(container);
    if (!target) {
      return;
    }

    const wrapper = document.createElement("div");
    wrapper.className = `alert alert-${type} alert-dismissible fade show`;
    wrapper.setAttribute("role", "alert");
    wrapper.innerHTML = `
      <span>${message}</span>
      <button type="button" class="btn-close" aria-label="Close"></button>
    `;

    const closeButton = wrapper.querySelector(".btn-close");
    closeButton.addEventListener("click", () => wrapper.remove());

    target.prepend(wrapper);

    window.setTimeout(() => {
      if (wrapper.isConnected) {
        wrapper.remove();
      }
    }, 5000);
  },

  formatDate(dateString) {
    if (!dateString) {
      return "";
    }

    const date = new Date(dateString);
    return date.toLocaleString("en-GB", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit"
    });
  },

  debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
      const later = () => {
        clearTimeout(timeout);
        func(...args);
      };
      clearTimeout(timeout);
      timeout = setTimeout(later, wait);
    };
  }
};

function closeAllMenus() {
  document.querySelectorAll(".dropdown-menu.show").forEach((menu) => {
    menu.classList.remove("show");
  });

  document.querySelectorAll(".account-trigger[aria-expanded='true']").forEach((trigger) => {
    trigger.setAttribute("aria-expanded", "false");
  });
}

function bindAlertDismiss() {
  document.querySelectorAll(".alert .btn-close").forEach((button) => {
    if (button.dataset.bound === "true") {
      return;
    }

    button.dataset.bound = "true";
    button.addEventListener("click", () => {
      const alert = button.closest(".alert");
      if (alert) {
        alert.remove();
      }
    });
  });
}

function bindNavbarFallback() {
  const toggler = document.querySelector(".navbar-toggler");
  const collapse = document.querySelector("#navbarNav");

  if (!toggler || !collapse) {
    return;
  }

  toggler.addEventListener("click", () => {
    const expanded = toggler.getAttribute("aria-expanded") === "true";
    toggler.setAttribute("aria-expanded", String(!expanded));
    collapse.classList.toggle("show");
  });
}

function bindDropdownFallback() {
  const trigger = document.querySelector(".account-trigger");
  const menu = document.querySelector(".account-menu");

  if (!trigger || !menu) {
    return;
  }

  trigger.addEventListener("click", (event) => {
    event.preventDefault();
    event.stopPropagation();
    const willOpen = !menu.classList.contains("show");
    closeAllMenus();
    menu.classList.toggle("show", willOpen);
    trigger.setAttribute("aria-expanded", String(willOpen));
  });

  menu.addEventListener("click", (event) => {
    event.stopPropagation();
  });

  document.addEventListener("click", () => {
    closeAllMenus();
  });

  document.addEventListener("keydown", (event) => {
    if (event.key === "Escape") {
      closeAllMenus();
    }
  });
}

window.addEventListener("error", (event) => {
  if (event.message) {
    console.error("Global error:", event.message);
  }
});

document.addEventListener("DOMContentLoaded", () => {
  bindAlertDismiss();
  bindNavbarFallback();
  bindDropdownFallback();

  console.log("%cSpecialist Consultation Booking System UI loaded", "color: #4361ee; font-weight: bold; font-size: 16px;");
  console.log("%cTip: open DevTools for detailed logs.", "color: #64748b;");
});
