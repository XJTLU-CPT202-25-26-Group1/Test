function bindPasswordToggle() {
  const toggle = document.querySelector("#togglePassword");
  const passwordField = document.querySelector("#password");

  if (!toggle || !passwordField) {
    return;
  }

  toggle.addEventListener("click", () => {
    const isPassword = passwordField.getAttribute("type") === "password";
    passwordField.setAttribute("type", isPassword ? "text" : "password");
    toggle.textContent = isPassword ? "隐藏" : "显示";
  });
}

function bindFormValidation() {
  const forms = document.querySelectorAll(".needs-validation");
  forms.forEach((form) => {
    form.addEventListener("submit", (event) => {
      if (!form.checkValidity()) {
        event.preventDefault();
        event.stopPropagation();
      }
      form.classList.add("was-validated");
    });
  });
}

function clearTransientParams() {
  if (!window.history.replaceState) {
    return;
  }

  const url = new URL(window.location.href);
  const transientKeys = ["error", "logout", "unverified"];
  let changed = false;

  transientKeys.forEach((key) => {
    if (url.searchParams.has(key)) {
      url.searchParams.delete(key);
      changed = true;
    }
  });

  if (changed) {
    const next = url.pathname + (url.search ? url.search : "");
    window.history.replaceState(null, "", next);
  }
}

function bindRoleToggle() {
  const roleSelect = document.querySelector("#roleSelect");
  const specialistFields = document.querySelectorAll(".specialist-only");
  const categorySelect = document.querySelector("#categorySelect");

  if (!roleSelect || specialistFields.length === 0) {
    return;
  }

  const toggleFields = () => {
    const specialistMode = roleSelect.value === "SPECIALIST";
    specialistFields.forEach((field) => {
      field.style.display = specialistMode ? "" : "none";
    });

    if (categorySelect) {
      categorySelect.required = specialistMode;
    }
  };

  toggleFields();
  roleSelect.addEventListener("change", toggleFields);
}

document.addEventListener("DOMContentLoaded", () => {
  bindPasswordToggle();
  bindFormValidation();
  clearTransientParams();
  bindRoleToggle();
});
