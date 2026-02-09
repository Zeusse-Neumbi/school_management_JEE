<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="sidebar glass-panel">
    <div class="sidebar-logo">
        <span style="font-size: 2rem;">🛡️</span> Admin Panel
    </div>
    <ul class="nav-links">
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="${pageContext.request.requestURI.endsWith('/dashboard.jsp') ? 'active' : ''}">
                Dashboard
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/admin/users">
                User Management
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/admin/teachers">
                Teachers
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/admin/students">
                Students
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/admin/courses">
                Courses
            </a>
        </li>
        <li class="nav-item" style="margin-top: auto;">
            <a href="${pageContext.request.contextPath}/logout" style="color: var(--danger-color);">
                Logout
            </a>
        </li>
    </ul>
</div>
