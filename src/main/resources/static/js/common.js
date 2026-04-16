// Global utility helpers
const App = {
  // Render a dismissible alert
  showAlert: function(message, type = 'info', container = '.card-body') {
    const alertHtml = `
      <div class="alert alert-${type} alert-dismissible fade show" role="alert">
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
      </div>
    `;
    $(container).prepend(alertHtml);
    
    // Auto-close after 5 seconds
    setTimeout(() => {
      $(container + ' .alert:first').alert('close');
    }, 5000);
  },
  
  // Format a date-like value for UI display
  formatDate: function(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleString('en-GB', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  },
  
  // Debounce helper
  debounce: function(func, wait) {
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

// Global error handling
$(window).on('error', function(e) {
  if (e.originalEvent && e.originalEvent.message) {
    console.error('Global error:', e.originalEvent.message);
    // Optional friendly UI message:
    // App.showAlert('A system error occurred. Please try again later.', 'danger');
  }
});

// Dev-only console notice
console.log('%cSpecialist Consultation Booking System UI loaded', 'color: #4361ee; font-weight: bold; font-size: 16px;');
console.log('%cTip: open DevTools for detailed logs.', 'color: #64748b;');
