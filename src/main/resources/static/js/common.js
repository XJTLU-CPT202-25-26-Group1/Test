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

const LANGUAGE_STORAGE_KEY = "appLanguage";
const LANGUAGE_PAIRS = [
  ["Specialist Consultation Booking System / 医疗咨询预约系统", "Specialist Consultation Booking System / Medical Consultation Booking System"],
  ["官方就诊服务平台", "Official Care Service Portal"],
  ["互联网医疗服务台", "Online Medical Service Hub"],
  ["互联网医院风格重构版", "Hospital-style redesign"],
  ["预约、咨询、排班、审核一站式可见", "Appointments, consultations, scheduling, and reviews in one place"],
  ["门诊服务、预约管理、报告反馈一屏掌握", "Clinic services, booking management, and feedback on one screen"],
  ["排班、接诊、评价与个人信息统一管理", "Scheduling, consultations, feedback, and profile management in one place"],
  ["审核、专家管理、分类维护与账单总览", "Reviews, specialist management, categories, and billing in one console"],
  ["参考医院服务 App 的清爽入口样式，将客户、专家与管理员流程统一在同一套服务界面中。", "Inspired by hospital service apps, this interface unifies customer, specialist, and admin flows in one clean experience."],
  ["支持快速查看待处理预约、即将到来的咨询以及个人资料维护，所有入口保持可追踪、可返回。", "Quickly view pending bookings, upcoming consultations, and profile tools with clear, traceable entry points."],
  ["可直接进入排班、预约明细、反馈查看与资料页，确保接诊流程和可用时间展示清楚。", "Open availability, booking details, feedback, and profile pages directly with a clear consultation workflow."],
  ["审核台、专家库、分类与账单页面都保持统一信息层级，方便操作和状态查看。", "Review, specialist, category, and billing pages share a unified hierarchy for easier operations and status tracking."],
  ["服务入口", "Service Entry"],
  ["角色识别", "Role"],
  ["已登录，可直接进入工作区", "Signed in. Open your workspace directly"],
  ["首次使用请先注册或登录", "Please register or sign in first"],
  ["访客", "Guest"],
  ["首页", "Home"],
  ["找专家", "Find Specialists"],
  ["注册", "Register"],
  ["工作台", "Dashboard"],
  ["专家门诊", "Specialists"],
  ["我的预约", "My Bookings"],
  ["接诊日程", "Schedule"],
  ["排班管理", "Availability"],
  ["患者反馈", "Feedback"],
  ["预约审核", "Booking Review"],
  ["专家管理", "Specialists"],
  ["分类维护", "Categories"],
  ["账单总览", "Billing"],
  ["我的资料", "Profile"],
  ["登录", "Log In"],
  ["注册账户", "Create Account"],
  ["进入工作台", "Open Dashboard"],
  ["回到首页", "Back Home"],
  ["切换账号", "Switch Account"],
  ["退出登录", "Log Out"],
  ["登录失败。", "Login failed."],
  ["用户名或密码不正确，请重新输入。", "The username or password is incorrect. Please try again."],
  ["邮箱尚未验证。", "Email not verified."],
  ["请先完成邮箱验证后再登录。", "Please verify your email before signing in."],
  ["已安全退出登录。", "You have been signed out safely."],
  ["系统消息", "System message"],
  ["医疗咨询预约系统", "Medical Consultation Booking System"],
  ["以医院服务平台为灵感的统一门户，覆盖客户预约、专家接诊和管理员审核全流程。", "A unified portal inspired by hospital service platforms for customer bookings, specialist consultations, and admin reviews."],
  ["XJTLU 软件工程课程项目", "XJTLU Software Engineering Course Project"],
  ["服务流程", "Service Flow"],
  ["医院服务风格", "Hospital App Style"],
  ["像医院服务 App 一样清晰的专家咨询预约入口。", "A specialist booking entry point with the clarity of a hospital service app."],
  ["统一展示找专家、提交预约、排班管理、预约审核与账单概览。整个系统保持前后端原有流程不变，只把入口、信息层级和视觉风格重构得更清楚。", "Find specialists, submit bookings, manage schedules, review requests, and inspect billing from one place. The backend flow is unchanged; the entry points and visual hierarchy are simply clearer."],
  ["立即预约", "Book Now"],
  ["新用户注册", "Register"],
  ["从找专家到完成咨询，全流程状态都能追踪", "Track the full flow from specialist search to completed consultation"],
  ["选择专家", "Choose a Specialist"],
  ["按分类、日期与关键词筛选可预约专家。", "Filter bookable specialists by category, date, and keyword."],
  ["提交预约", "Submit Booking"],
  ["选择可用时段并填写咨询主题与备注。", "Choose an available slot and enter the consultation topic and notes."],
  ["审核与接诊", "Review and Consultation"],
  ["管理员审核后，专家即可按排班完成咨询。", "After admin review, specialists can complete the consultation according to schedule."],
  ["专家人数", "Specialists"],
  ["服务分类", "Categories"],
  ["待审核", "Pending Review"],
  ["快捷服务", "Quick Services"],
  ["高频入口", "Quick Entry"],
  ["参考你给的医院首页做成四宫格服务区，每个入口都连接到真实可用页面。", "This four-card service area follows your hospital homepage reference, and every card links to a real page."],
  ["预约挂号", "Book Appointment"],
  ["浏览专家、选择时段并发起预约申请。", "Browse specialists, choose a time slot, and submit a booking request."],
  ["资料管理", "Profile Management"],
  ["维护账户信息、联系方式与角色资料。", "Maintain account info, contact details, and role-related profile data."],
  ["流程提醒", "Notifications"],
  ["查看最近通知、状态变更和待处理事项。", "Check recent notifications, status changes, and pending tasks."],
  ["角色入口", "Role Entry"],
  ["不同角色，进入不同服务台", "Different roles, different workspaces"],
  ["保留原有权限和路由逻辑，通过更直观的卡片方式组织客户、专家和管理员的工作入口。", "The original permissions and routes stay intact, now organized with clearer card-based entry points."],
  ["客户端", "Customer Portal"],
  ["查找专家、提交预约、管理改期与取消、查看咨询反馈。", "Find specialists, submit bookings, manage reschedules and cancellations, and view feedback."],
  ["打开客户工作台", "Open Customer Dashboard"],
  ["专家端", "Specialist Portal"],
  ["管理排班、查看预约、完成咨询并查看患者反馈。", "Manage availability, view bookings, complete consultations, and review feedback."],
  ["打开专家工作台", "Open Specialist Dashboard"],
  ["管理员端", "Admin Portal"],
  ["审核预约、维护专家与分类、查看账单总览与审计记录。", "Review bookings, maintain specialists and categories, and inspect billing and audit logs."],
  ["打开管理控制台", "Open Admin Console"],
  ["专家总数", "Total Specialists"],
  ["当前平台内可管理、可展示的专家数量。", "The number of specialists currently available on the platform."],
  ["分类数量", "Total Categories"],
  ["支持筛选和维护的咨询分类数量。", "The number of consultation categories available for filtering and maintenance."],
  ["预约总量", "Total Bookings"],
  ["平台内所有预约请求和咨询记录。", "All booking requests and consultation records in the platform."],
  ["推荐专家", "Featured Specialists"],
  ["未登录也能先查看专家概况、专业方向和价格。", "Preview specialist profiles, expertise, and pricing even before signing in."],
  ["查看全部专家", "View All Specialists"],
  ["准备开始了吗？", "Ready to get started?"],
  ["现在就进入专家列表，提交第一条预约请求。", "Open the specialist list and submit your first booking request today."],
  ["开始预约", "Start Booking"],
  ["忘记密码", "Forgot Password"],
  ["客户服务台", "Customer Dashboard"],
  ["欢迎回来，", "Welcome back, "],
  ["这里保留了预约、改期、取消、通知和个人资料的全部功能，但入口改成了更接近医院服务 App 的清爽卡片式布局。", "All booking, reschedule, cancellation, notification, and profile functions are preserved, now presented in a cleaner hospital-app style card layout."],
  ["去预约", "Make a Booking"],
  ["常用服务", "Quick Services"],
  ["四宫格快捷入口对应真实页面，不做空展示。", "These four quick-entry cards connect to real pages, not placeholders."],
  ["线上复诊", "Follow-up Visits"],
  ["查看已预约记录，处理取消、改期和状态跟踪。", "Review booked items and manage cancellations, reschedules, and status tracking."],
  ["预约提醒", "Booking Alerts"],
  ["个人资料", "Profile"],
  ["更新昵称、邮箱与联系电话信息。", "Update your display name, email, and phone number."],
  ["全部预约", "All Bookings"],
  ["与当前账号关联的全部预约记录。", "All bookings linked to the current account."],
  ["仍在等待管理员审核的预约请求。", "Booking requests still waiting for administrator review."],
  ["即将到来", "Upcoming"],
  ["已经确认且尚未开始的咨询预约。", "Consultations that are confirmed and have not started yet."],
  ["已完成", "Completed"],
  ["可进入反馈与查看历史的预约数量。", "Bookings available for feedback and history review."],
  ["最近预约", "Recent Bookings"],
  ["这里保留原来的预约详情入口与状态展示。", "The original booking detail links and status displays remain here."],
  ["查看详情", "View Details"],
  ["最新提醒", "Recent Notifications"],
  ["保留原有通知数据，只优化展示层级。", "The original notification data is preserved with a clearer visual hierarchy."],
  ["当前没有新的预约提醒。", "There are no new booking alerts right now."],
  ["专家服务台", "Specialist Dashboard"],
  ["咨询费", "Fee"],
  ["常用入口", "Quick Actions"],
  ["围绕排班、接诊、反馈和资料四个高频场景重组。", "Reorganized around four high-frequency specialist tasks: availability, consultations, feedback, and profile."],
  ["新增、调整和删除未被占用的时段。", "Create, adjust, and remove unbooked time slots."],
  ["查看已分配预约并标记完成咨询。", "Review assigned bookings and mark consultations as completed."],
  ["查看已完成咨询后的评分与评论。", "Read ratings and comments submitted after completed consultations."],
  ["核对专家资料、账号信息和专业简介。", "Review specialist profile, account details, and professional summary."],
  ["当前分配给你的所有预约记录。", "All bookings currently assigned to you."],
  ["待接诊", "Confirmed"],
  ["已确认但尚未完成的咨询数量。", "Confirmed consultations that are still waiting to be delivered."],
  ["已经标记完成并可查看反馈的预约。", "Consultations already marked completed and available for feedback review."],
  ["空闲时段", "Open Slots"],
  ["未来仍可被客户预约的排班数量。", "Future slots that are still open for customer booking."],
  ["保留预约详情与完成按钮逻辑，重新整理展示层次。", "The booking detail and completion actions remain unchanged, with a cleaner display hierarchy."],
  ["工作提醒", "Work Alerts"],
  ["聚合最近与自己相关的预约状态变化。", "Shows recent booking status changes related to you."],
  ["管理控制台", "Admin Dashboard"],
  ["运营审核与主数据总览", "Operations Review and Master Data Overview"],
  ["管理员端保留审核、专家管理、分类维护、账单总览与审计记录等全部功能，并改造成更接近业务中台的卡片化布局。", "The admin side keeps reviews, specialist management, categories, billing, and audit history while presenting them in a cleaner operations-console layout."],
  ["去审核预约", "Review Bookings"],
  ["快捷管理", "Quick Admin Actions"],
  ["把管理员常用动作放到一层展示，减少跳转成本。", "High-frequency admin actions are surfaced together to reduce navigation cost."],
  ["处理待确认、待拒绝的预约申请并查看流程详情。", "Handle pending approval and rejection requests and inspect workflow details."],
  ["维护专家资料、状态、价格和分类信息。", "Maintain specialist profiles, status, pricing, and category data."],
  ["新增分类、更新名称并控制启停状态。", "Create categories, update names, and manage activation states."],
  ["查看可计费预约、确认金额和完成收入。", "Inspect billable bookings, confirmed amounts, and completed revenue."],
  ["仍等待管理员决策的预约数量。", "Bookings still waiting for an administrator decision."],
  ["系统内的预约总量。", "The total number of bookings in the system."],
  ["专家数量", "Specialists"],
  ["当前平台维护中的专家总数。", "The total number of specialists managed by the platform."],
  ["当前可用的咨询分类数量。", "The number of consultation categories currently available."],
  ["核心管理模块", "Core Admin Modules"],
  ["保留原有路由和功能，只重新组织视觉入口。", "The original routes and functions remain intact, only the visual entry points are reorganized."],
  ["预约流程", "Booking Workflow"],
  ["查看待审核列表、详情页与审计记录。", "View pending items, detail pages, and audit history."],
  ["专家库", "Specialist Directory"],
  ["创建、编辑、搜索与启停专家资料。", "Create, edit, search, and activate or deactivate specialist records."],
  ["分类库", "Category Library"],
  ["防止重复分类并支持启停控制。", "Prevent duplicate categories and manage activation states."],
  ["费用总览", "Billing Summary"],
  ["查看计费相关预约和金额汇总。", "Inspect billable bookings and fee totals."],
  ["最近流程动态", "Recent Workflow Activity"],
  ["展示最近的状态变化和操作人。", "Shows recent status changes and operators."],
  ["目前还没有新的流程动态。", "There is no recent workflow activity yet."],
  ["账号登录", "Account Sign-in"],
  ["登录后进入对应角色的医疗服务工作台。", "Sign in to enter the medical service workspace for your role."],
  ["客户可以管理预约与反馈，专家可以维护排班与接诊日程，管理员可以执行审核和主数据维护。", "Customers manage bookings and feedback, specialists maintain schedules, and administrators handle reviews and master data."],
  ["默认演示账号", "Demo Accounts"],
  ["新注册的客户和专家账户需要先完成邮箱验证才能首次登录。", "New customer and specialist accounts must verify email before first sign-in."],
  ["登录表单", "Sign-in Form"],
  ["欢迎回来", "Welcome Back"],
  ["请输入用户名和密码，进入你的专属工作台。", "Enter your username and password to open your workspace."],
  ["用户名", "Username"],
  ["密码", "Password"],
  ["显示", "Show"],
  ["隐藏", "Hide"],
  ["忘记密码？", "Forgot your password?"],
  ["重新发送验证邮件", "Resend Verification Email"],
  ["创建账户", "Create Account"],
  ["注册客户或专家账号，进入统一医疗服务平台。", "Register a customer or specialist account to enter the unified medical service platform."],
  ["客户可直接发起预约；专家注册时会同时创建专家资料，因此会额外填写分类、级别、收费与简介。", "Customers can book directly. Specialist registration also creates a specialist profile, so category, level, fee, and description fields are required."],
  ["注册说明", "Registration Notes"],
  ["管理员账号由系统内部维护，不在此处开放注册。", "Administrator accounts are managed internally and cannot be registered here."],
  ["客户和专家账号注册后需要先完成邮箱验证。", "Customer and specialist accounts must verify email after registration."],
  ["只有选择专家角色时才需要填写专家专属字段。", "Specialist-specific fields appear only when the specialist role is selected."],
  ["注册表单", "Registration Form"],
  ["填写账户信息", "Complete Your Account Details"],
  ["请完成基础资料与角色信息，系统会按角色跳转到对应工作台。", "Complete the basic profile and role information, and the system will route you to the appropriate dashboard."],
  ["显示名称", "Display Name"],
  ["联系电话", "Phone"],
  ["邮箱", "Email"],
  ["角色", "Role"],
  ["当角色选择为 ", "When the selected role is "],
  [" 时，下方会展开专家资料字段，仍然沿用原有后端注册逻辑。", ", the specialist profile fields below will appear while preserving the original backend registration logic."],
  ["专业分类", "Category"],
  ["级别", "Level"],
  ["时薪", "Hourly Fee"],
  ["专家简介", "Profile Description"],
  ["创建账户", "Create Account"],
  ["返回登录", "Back to Login"],
  ["密码找回", "Password Recovery"],
  ["为已验证账户申请重置密码邮件。", "Request a password reset email for a verified account."],
  ["请输入用户名和注册邮箱，系统会按原有逻辑发送密码重置说明。", "Enter the username and registered email address to receive the existing password reset instructions."],
  ["重置申请", "Reset Request"],
  ["忘记密码？", "Forgot Password?"],
  ["我们会把重置说明发送到你的注册邮箱。", "We will send reset instructions to your registered email address."],
  ["注册邮箱", "Registered Email"],
  ["发送重置邮件", "Send Reset Email"],
  ["密码重置", "Password Reset"],
  ["设置新的登录密码。", "Set a new sign-in password."],
  ["使用邮件中的用户名和重置令牌，设置不少于 6 位的新密码。", "Use the username and reset token from the email to set a new password with at least 6 characters."],
  ["更新密码", "Update Password"],
  ["完成密码重置", "Complete Password Reset"],
  ["填写下方表单后即可重新登录。", "Complete the form below and sign in again."],
  ["重置令牌", "Reset Token"],
  ["新密码", "New Password"],
  ["邮箱验证", "Email Verification"],
  ["重新发送验证链接。", "Send a fresh verification link."],
  ["如果账号已注册但尚未完成邮箱验证，可以在这里重新发送验证邮件。", "If the account is registered but not yet verified, you can resend the verification email here."],
  ["验证申请", "Verification Request"],
  ["请输入注册时使用的用户名和邮箱地址。", "Enter the username and email address used during registration."],
  ["发送验证邮件", "Send Verification Email"],
  ["账户设置", "Account Settings"],
  ["编辑资料", "Edit Profile"],
  ["更新显示名称、邮箱和电话，方便接收预约通知与流程提醒。", "Update your display name, email, and phone number for booking notifications and workflow alerts."],
  ["保存修改", "Save Changes"],
  ["请先登录后再编辑资料。", "Please sign in first to edit your profile."],
  ["客户门户", "Customer Portal"],
  ["专家门诊", "Specialist Directory"],
  ["按姓名、分类和可预约日期筛选专家，查看详情后直接选择时段发起预约。", "Search specialists by name, category, and available date, then choose a slot from the detail page."],
  ["关键词", "Keyword"],
  ["输入专家名或分类", "Search by specialist name or category"],
  ["分类", "Category"],
  ["全部分类", "All Categories"],
  ["可预约日期", "Available Date"],
  ["筛选专家", "Filter"],
  ["可预约", "Bookable"],
  ["咨询费：", "Fee: "],
  [" / 小时", " / hour"],
  ["查看详情", "View Details"],
  ["当前筛选条件下没有匹配的专家。", "No specialists matched the current filters."],
  ["专家详情", "Specialist Detail"],
  ["查看专家资料后选择一个开放时段，直接进入预约创建页面。", "Review the specialist profile and choose an open slot to create a booking."],
  ["返回专家列表", "Back to Specialists"],
  ["服务状态", "Status"],
  ["当前可预约", "Available for Booking"],
  ["可预约时段", "Available Slots"],
  ["可按日期过滤当前显示的时段。", "Filter the visible slots by date."],
  ["预约日期", "Selected Date"],
  ["筛选时段", "Filter Slots"],
  ["预约该时段", "Book This Slot"],
  ["当前日期下没有可预约时段。", "No available slots for the selected date."],
  ["预约创建", "Create Booking"],
  ["确认预约信息", "Confirm Booking Details"],
  ["核对专家与时段后提交预约申请，后端仍按原有逻辑进入待审核状态。", "Review the specialist and time slot, then submit the booking request. The backend flow remains unchanged and enters pending review."],
  ["预约摘要", "Booking Summary"],
  ["预约时段", "Selected Slot"],
  ["预约申请表", "Booking Request Form"],
  ["填写主题和备注后提交。", "Enter the topic and notes, then submit."],
  ["咨询主题", "Consultation Topic"],
  ["补充说明", "Notes"],
  ["简要描述本次咨询需求", "Briefly describe the consultation request"],
  ["填写背景情况、诉求重点或需要专家提前了解的信息", "Add background information, key requests, or anything the specialist should know in advance"],
  ["提交预约申请", "Submit Booking Request"],
  ["返回专家详情", "Back to Specialist Detail"],
  ["客户预约中心", "Customer Booking Center"],
  ["查看即将到来的预约、搜索历史记录、跟踪状态并进入取消、改期与反馈功能。", "View upcoming bookings, search history, track statuses, and open cancellation, reschedule, and feedback actions."],
  ["新建预约", "New Booking"],
  ["搜索预约", "Search Bookings"],
  ["按主题、专家或状态搜索", "Search by topic, specialist, or status"],
  ["搜索", "Search"],
  ["展示最近的预约审批和状态变化。", "Shows recent booking decisions and status changes."],
  ["即将到来的已确认预约", "Upcoming Confirmed Appointments"],
  ["仅展示未来时间且已确认的咨询。", "Displays only future consultations that have been confirmed."],
  ["目前没有即将到来的已确认预约。", "There are no upcoming confirmed bookings."],
  ["即将开始", "Upcoming"],
  ["全部预约记录", "All Booking Records"],
  ["保留取消、改期和详情按钮逻辑。", "The cancel, reschedule, and detail actions remain unchanged."],
  ["当前筛选条件下没有预约记录。", "No bookings matched the current filters."],
  ["专家：", "Specialist: "],
  ["日期：", "Date: "],
  ["时间：", "Time: "],
  ["时段：", "Slot: "],
  ["费用：", "Fee: "],
  ["取消预约", "Cancel Booking"],
  ["改期", "Reschedule"],
  ["预约开始前 24 小时内不可自助取消或改期。", "Self-service cancellation or rescheduling is disabled within 24 hours of the appointment."],
  ["预约详情", "Booking Detail"],
  ["查看预约信息", "Review Booking Details"],
  ["确认专家、时间、费用、状态以及反馈提交情况。", "Review the specialist, time, fee, status, and feedback details."],
  ["返回预约列表", "Back to Bookings"],
  ["专家：", "Specialist: "],
  ["客户备注", "Customer Notes"],
  ["咨询反馈", "Consultation Feedback"],
  ["预约完成后可在此提交评分和评价。", "Feedback becomes available after the booking is completed."],
  ["只有在预约完成后才能提交反馈。", "Feedback can only be submitted after completion."],
  ["评分", "Rating"],
  ["评价内容", "Comment"],
  ["请选择评分", "Select a rating"],
  ["非常满意", "Excellent"],
  ["满意", "Good"],
  ["一般", "Average"],
  ["不满意", "Poor"],
  ["非常不满意", "Very Poor"],
  ["分享你的咨询体验", "Share your consultation experience"],
  ["提交反馈", "Submit Feedback"],
  ["预约改期", "Reschedule Booking"],
  ["选择新的时段", "Choose a New Slot"],
  ["先查看当前预约信息，再从可用备选时段中选择新的咨询时间。", "Review the current booking and select a new consultation slot from the available alternatives."],
  ["当前预约", "Current Booking"],
  ["主题", "Topic"],
  ["原时段", "Current Slot"],
  ["选择新时段", "Choose a New Slot"],
  ["仅展示当前仍可预约的备选时段。", "Only currently available alternative slots are listed."],
  ["暂时没有可用于改期的新时段。", "There are no alternative slots available right now."],
  ["确认改期", "Confirm Reschedule"],
  ["返回预约详情", "Back to Booking Detail"],
  ["客户资料维护", "Customer Profile"],
  ["更新昵称、邮箱和联系电话，后端保存逻辑保持不变。", "Update display name, email, and phone number while keeping the backend save flow unchanged."],
  ["保存资料", "Save Profile"],
  ["接诊日程", "Consultation Schedule"],
  ["查看已分配咨询、进入详情，并对已确认预约执行“完成咨询”操作。", "Review assigned consultations, open details, and mark confirmed bookings as completed."],
  ["周视图", "Weekly View"],
  ["最新提醒", "Recent Notifications"],
  ["展示最近与你相关的预约状态变化。", "Shows recent status changes related to your bookings."],
  ["分配给我的预约", "Assigned Consultations"],
  ["保留详情入口和完成操作。", "The detail entry and completion action remain unchanged."],
  ["标记完成", "Mark Completed"],
  ["专家排班", "Availability Management"],
  ["排班管理：", "Availability for: "],
  ["添加开放时段、修改未预约时段，并让已预约时段保持锁定。", "Create open consultation slots, update unbooked slots, and keep booked slots locked."],
  ["返回工作台", "Back to Dashboard"],
  ["新增排班时段", "Add New Slot"],
  ["每次创建一个时段，后端会阻止时间冲突。", "Create one slot at a time. The backend prevents overlaps."],
  ["日期", "Date"],
  ["开始时间", "Start Time"],
  ["结束时间", "End Time"],
  ["新增时段", "Add Slot"],
  ["当前排班表", "Current Availability Schedule"],
  ["已预约的行会保持可见，但不可编辑和删除。", "Booked rows remain visible but cannot be edited or deleted."],
  ["已预约", "Booked"],
  ["时段 ID", "Slot ID"],
  ["操作", "Actions"],
  ["更新", "Update"],
  ["删除", "Delete"],
  ["已预约时段不可编辑。", "Booked slots are locked."],
  ["反馈中心", "Feedback Center"],
  ["查看完成咨询后提交的评分与评价。", "Review ratings and comments submitted after completed consultations."],
  ["返回接诊日程", "Back to Schedule"],
  ["目前还没有收到患者反馈。", "No feedback has been submitted yet."],
  ["预约主题：", "Booking Topic: "],
  ["提交时间：", "Submitted: "],
  ["专家资料", "Specialist Profile"],
  ["专家个人资料", "Specialist Profile"],
  ["只读展示当前登录专家的账号信息与专业信息。", "Read-only account and professional summary for the current specialist."],
  ["账号信息", "Account Information"],
  ["联系电话", "Phone"],
  ["专业信息", "Professional Information"],
  ["姓名", "Name"],
  ["状态", "Status"],
  ["咨询费", "Fee Rate"],
  ["专家简介", "Description"],
  ["周排班视图", "Weekly Schedule"],
  ["本周接诊计划", "Weekly Schedule"],
  ["查看当前周的预约安排，并快速浏览最近完成的咨询记录。", "Review this week's consultations and quickly browse recent completed appointments."],
  ["上一周", "Previous Week"],
  ["下一周", "Next Week"],
  ["返回日程列表", "Back to Schedule"],
  ["周范围", "Week Range"],
  ["本周还没有排入预约。", "No bookings are scheduled for this week."],
  ["客户", "Customer"],
  ["最近完成的咨询", "Recent Completed Consultations"],
  ["用于快速查看历史记录。", "A compact history view for quick history review."],
  ["目前还没有完成的咨询历史。", "No completed consultation history is available yet."],
  ["管理员审核", "Administrator Workflow"],
  ["预约审核台", "Booking Review"],
  ["处理待审核请求，查看完整预约记录，并保留流程审计表格。", "Handle pending requests, review full booking records, and keep the audit table."],
  ["待审核预约", "Pending Booking Review"],
  ["这里是需要确认或拒绝的预约列表。", "This is the list of bookings waiting to be confirmed or rejected."],
  ["目前没有待审核预约。", "There are no pending bookings right now."],
  ["客户：", "Customer: "],
  ["确认", "Confirm"],
  ["拒绝", "Reject"],
  ["填写拒绝原因", "Enter rejection reason"],
  ["全部预约", "All Bookings"],
  ["保留系统级预约记录与详情入口。", "System-wide booking records and detail links remain intact."],
  ["审计日志", "Audit Log"],
  ["展示预约状态流转与操作记录。", "Shows booking workflow changes and operator history."],
  ["原状态", "Old Status"],
  ["新状态", "New Status"],
  ["操作人", "Operator"],
  ["备注", "Remark"],
  ["分类管理", "Category Management"],
  ["专业分类维护", "Expertise Categories"],
  ["新增、编辑、启用或停用专家分类，后端重复校验逻辑保持不变。", "Create, update, activate, or deactivate expertise categories while preserving backend duplicate validation."],
  ["新增分类", "Create Category"],
  ["分类名称不能重复。", "Duplicate category names are blocked."],
  ["分类名称", "Category Name"],
  ["输入新分类名称", "New category name"],
  ["添加分类", "Add Category"],
  ["分类列表", "Category List"],
  ["可直接更新名称与切换状态。", "Update names and toggle status directly."],
  ["更新名称", "Update Name"],
  ["停用", "Deactivate"],
  ["启用", "Activate"],
  ["专家资料维护", "Manage Specialists"],
  ["创建、搜索、编辑与启停专家记录，同时保持原有后端表单处理和列表逻辑。", "Create, search, edit, and toggle specialist records while preserving the original backend form and list logic."],
  ["搜索专家", "Search Specialists"],
  ["按专家姓名或分类搜索", "Search by specialist name or category"],
  ["全部状态", "All Statuses"],
  ["筛选", "Filter"],
  ["新增专家", "Create Specialist"],
  ["直接把专家资料写入系统主数据。", "Add a specialist profile directly to the system master data."],
  ["姓名", "Name"],
  ["咨询费", "Fee Rate"],
  ["请选择分类", "Select Category"],
  ["填写专家简介", "Profile description"],
  ["创建专家", "Create Specialist"],
  ["专家列表", "Specialist Directory"],
  ["包含编辑和启停操作。", "Includes edit and activate/deactivate actions."],
  ["编辑", "Edit"],
  ["账单中心", "Billing Center"],
  ["查看可计费预约数量、已确认金额、已完成收入以及明细列表。", "Review billable bookings, confirmed value, completed revenue, and detailed records."],
  ["可计费预约", "Billable Bookings"],
  ["计入账单总览的已确认和已完成预约。", "Confirmed and completed bookings included in billing visibility."],
  ["已确认金额", "Confirmed Value"],
  ["当前处于确认状态的咨询总金额。", "The current total value of confirmed consultations."],
  ["已完成收入", "Completed Revenue"],
  ["已完成咨询产生的收入总额。", "Revenue generated by completed consultations."],
  ["计费明细", "Billable Booking Records"],
  ["以下预约纳入费用统计。", "Bookings below are included in fee calculation."],
  ["目前还没有可计费的预约。", "There are no billable bookings yet."],
  ["审核详情", "Booking Review Detail"],
  ["查看客户、专家、时段、费用和流程记录，再执行确认或拒绝。", "Inspect customer, specialist, slot, fee, and audit details before confirming or rejecting."],
  ["返回审核列表", "Back to Booking Review"],
  ["客户邮箱", "Customer Email"],
  ["拒绝原因", "Rejection Reason"],
  ["流程记录", "Audit History"],
  ["展示该预约的全部状态流转。", "Shows every workflow transition for this booking."],
  ["该预约还没有审计记录。", "No audit records are available for this booking yet."],
  ["确认预约", "Confirm Booking"],
  ["拒绝预约", "Reject Booking"],
  ["编辑专家资料", "Edit Specialist"],
  ["更新专家姓名、价格、分类和简介信息。", "Update specialist name, fee, category, and profile description."],
  ["返回专家列表", "Back to Specialist List"],
  ["保存修改", "Save Changes"],
  ["取消", "Cancel"],
  ["访问控制", "Access Control"],
  ["你当前没有权限访问这个页面。", "You do not have permission to open this page."],
  ["请返回上一级页面，或回到首页使用具备相应权限的账号重新登录。", "Please return to the previous page or go back home and sign in with an account that has the required permissions."],
  ["回到首页", "Go to Home"],
  ["前往登录", "Go to Login"],
  ["页面不存在", "Page Not Found"],
  ["你访问的页面不存在。", "The page you requested could not be found."],
  ["可能是地址已失效、页面已移动，或当前请求不再有效。你可以使用下面的入口继续操作。", "The address may be outdated, the page may have moved, or the request may no longer be valid. Use the links below to continue."],
  ["CUSTOMER", "Customer"],
  ["SPECIALIST", "Specialist"],
  ["ADMIN", "Administrator"],
  ["Customer", "客户"],
  ["Specialist", "专家"],
  ["Administrator", "管理员"],
  ["Guest", "访客"],
  ["PENDING", "Pending"],
  ["CONFIRMED", "Confirmed"],
  ["COMPLETED", "Completed"],
  ["CANCELLED", "Cancelled"],
  ["ACTIVE", "Active"],
  ["INACTIVE", "Inactive"],
  ["Pending", "待处理"],
  ["Confirmed", "已确认"],
  ["Completed", "已完成"],
  ["Cancelled", "已取消"],
  ["Active", "启用"],
  ["Inactive", "停用"],
  ["Close", "关闭"],
  ["切换中英文", "Switch Language"],
  ["请输入用户名。", "Please enter your username."],
  ["请输入密码。", "Please enter your password."],
  ["例如 Associate / Senior / Consultant", "For example Associate / Senior / Consultant"],
  ["例如 200", "For example 200"]
];

function getPreferredLanguage() {
  const stored = window.localStorage.getItem(LANGUAGE_STORAGE_KEY);
  if (stored === "zh" || stored === "en") {
    return stored;
  }
  return document.documentElement.lang && document.documentElement.lang.toLowerCase().startsWith("en") ? "en" : "zh";
}

function getTranslationEntries(targetLanguage) {
  const pairs = LANGUAGE_PAIRS.map(([zh, en]) => targetLanguage === "en" ? [zh, en] : [en, zh]);
  return pairs.sort((left, right) => right[0].length - left[0].length);
}

function translateString(rawValue, entries) {
  if (!rawValue) {
    return rawValue;
  }

  let nextValue = rawValue;
  entries.forEach(([source, target]) => {
    if (source && nextValue.includes(source)) {
      nextValue = nextValue.replaceAll(source, target);
    }
  });
  return nextValue;
}

function translateTextNodes(targetLanguage) {
  const entries = getTranslationEntries(targetLanguage);
  const walker = document.createTreeWalker(document.documentElement, NodeFilter.SHOW_TEXT, {
    acceptNode(node) {
      if (!node.parentElement) {
        return NodeFilter.FILTER_REJECT;
      }

      if (node.parentElement.closest("[data-no-translate='true']")) {
        return NodeFilter.FILTER_REJECT;
      }

      const tagName = node.parentElement.tagName;
      if (["SCRIPT", "STYLE", "NOSCRIPT"].includes(tagName)) {
        return NodeFilter.FILTER_REJECT;
      }

      if (!node.nodeValue || !node.nodeValue.trim()) {
        return NodeFilter.FILTER_REJECT;
      }

      return NodeFilter.FILTER_ACCEPT;
    }
  });

  const textNodes = [];
  let currentNode = walker.nextNode();
  while (currentNode) {
    textNodes.push(currentNode);
    currentNode = walker.nextNode();
  }

  textNodes.forEach((node) => {
    node.nodeValue = translateString(node.nodeValue, entries);
  });
}

function translateAttributes(targetLanguage) {
  const entries = getTranslationEntries(targetLanguage);
  document.querySelectorAll("[placeholder], [title], [aria-label], input[type='button'], input[type='submit']").forEach((element) => {
    if (element.closest("[data-no-translate='true']")) {
      return;
    }

    ["placeholder", "title", "aria-label", "value"].forEach((attributeName) => {
      if (!element.hasAttribute(attributeName)) {
        return;
      }
      const attributeValue = element.getAttribute(attributeName);
      const translatedValue = translateString(attributeValue, entries);
      if (translatedValue !== attributeValue) {
        element.setAttribute(attributeName, translatedValue);
      }
    });
  });
}

function updateLanguageToggleButtons(language) {
  const nextLabel = language === "zh" ? "EN" : "中文";
  const nextTitle = language === "zh" ? "Switch to English" : "切换到中文";
  document.querySelectorAll("[data-language-toggle]").forEach((button) => {
    button.setAttribute("title", nextTitle);
    button.setAttribute("aria-label", nextTitle);
  });
  document.querySelectorAll("[data-language-toggle-label]").forEach((label) => {
    label.textContent = nextLabel;
  });
}

function applyLanguage(language) {
  document.documentElement.dataset.appLanguage = language;
  document.documentElement.lang = language === "en" ? "en" : "zh-CN";
  translateTextNodes(language);
  translateAttributes(language);
  updateLanguageToggleButtons(language);
  window.localStorage.setItem(LANGUAGE_STORAGE_KEY, language);
}

function bindLanguageToggle() {
  document.querySelectorAll("[data-language-toggle]").forEach((button) => {
    button.addEventListener("click", () => {
      const currentLanguage = document.documentElement.dataset.appLanguage || getPreferredLanguage();
      const nextLanguage = currentLanguage === "zh" ? "en" : "zh";
      applyLanguage(nextLanguage);
    });
  });
}

function getScreenMode() {
  const width = window.innerWidth;
  if (width < 640) {
    return "mobile";
  }
  if (width < 992) {
    return "tablet";
  }
  if (width < 1440) {
    return "desktop";
  }
  return "wide";
}

function applyScreenMode() {
  const mode = getScreenMode();
  document.documentElement.dataset.screenMode = mode;
  document.body.dataset.screenMode = mode;
}

function bindResponsiveLayout() {
  applyScreenMode();
  window.addEventListener("resize", App.debounce(applyScreenMode, 100));
}

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
  bindLanguageToggle();
  bindResponsiveLayout();
  applyLanguage(getPreferredLanguage());

  console.log("%cSpecialist Consultation Booking System UI loaded", "color: #4361ee; font-weight: bold; font-size: 16px;");
  console.log("%cTip: resize the window or use the translate toggle to test adaptive layout and bilingual UI.", "color: #64748b;");
});
