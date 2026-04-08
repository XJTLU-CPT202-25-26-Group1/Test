$(document).ready(function() {
  // 密码可见性切换
  $('#togglePassword').on('click', function() {
    const $passwordField = $('#password');
    const fieldType = $passwordField.attr('type');
    $passwordField.attr('type', fieldType === 'password' ? 'text' : 'password');
    
    // 切换图标
    $(this).html(
      fieldType === 'password' 
        ? '<i class="bi bi-eye-slash"></i>' 
        : '<i class="bi bi-eye"></i>'
    );
  });

  // 表单验证增强
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

  // 自动清除URL参数（避免刷新后重复显示错误）
  if (window.history.replaceState) {
    const url = new URL(window.location);
    if (url.searchParams.has('error') || url.searchParams.has('logout')) {
      url.searchParams.delete('error');
      url.searchParams.delete('logout');
      window.history.replaceState(null, null, url.pathname);
    }
  }

  // 焦点动画（可选增强）
  $('.form-control').on('focus', function() {
    $(this).closest('.input-group').addClass('shadow-sm');
  }).on('blur', function() {
    $(this).closest('.input-group').removeClass('shadow-sm');
  });
});
