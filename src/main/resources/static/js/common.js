// 全局工具函数
const App = {
  // 显示提示消息
  showAlert: function(message, type = 'info', container = '.card-body') {
    const alertHtml = `
      <div class="alert alert-${type} alert-dismissible fade show" role="alert">
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
      </div>
    `;
    $(container).prepend(alertHtml);
    
    // 5秒后自动关闭
    setTimeout(() => {
      $(container + ' .alert:first').alert('close');
    }, 5000);
  },
  
  // 格式化日期（示例）
  formatDate: function(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  },
  
  // 防抖函数
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

// 全局错误处理
$(window).on('error', function(e) {
  if (e.originalEvent && e.originalEvent.message) {
    console.error('全局错误:', e.originalEvent.message);
    // 可选：向用户显示友好提示
    // App.showAlert('系统发生错误，请稍后重试', 'danger');
  }
});

// 页面加载完成提示（开发用）
console.log('%c✅ 专家咨询预约系统前端已加载', 'color: #4361ee; font-weight: bold; font-size: 16px;');
console.log('%c💡 提示: 按F12打开开发者工具查看详细日志', 'color: #64748b;');
