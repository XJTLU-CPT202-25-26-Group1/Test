$(document).ready(function() {
  // Toggle password visibility
  $('#togglePassword').on('click', function() {
    const $passwordField = $('#password');
    const fieldType = $passwordField.attr('type');
    $passwordField.attr('type', fieldType === 'password' ? 'text' : 'password');
    
    // Toggle icon
    $(this).html(
      fieldType === 'password' 
        ? '<i class="bi bi-eye-slash"></i>' 
        : '<i class="bi bi-eye"></i>'
    );
  });

  // Enhanced form validation
  const form = document.querySelector('.needs-validation');
  if (form) {
    form.addEventListener('submit', function(event) {
      if (!form.checkValidity()) {
        event.preventDefault();
        event.stopPropagation();
      }
      form.classList.add('was-validated');
    }, false);
  }

  // Remove transient URL parameters after rendering
  if (window.history.replaceState) {
    const url = new URL(window.location);
    if (url.searchParams.has('error') || url.searchParams.has('logout')) {
      url.searchParams.delete('error');
      url.searchParams.delete('logout');
      window.history.replaceState(null, null, url.pathname);
    }
  }

  // Optional focus styling
  $('.form-control').on('focus', function() {
    $(this).closest('.input-group').addClass('shadow-sm');
  }).on('blur', function() {
    $(this).closest('.input-group').removeClass('shadow-sm');
  });
});
