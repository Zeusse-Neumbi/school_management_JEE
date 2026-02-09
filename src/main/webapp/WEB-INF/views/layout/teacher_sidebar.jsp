<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="sidebar glass-panel">
    <div class="sidebar-logo">
        <span style="font-size: 2rem;">👩‍🏫</span> Teacher Portal
    </div>
    <ul class="nav-links">
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/teacher/dashboard" class="${pageContext.request.requestURI.endsWith('/dashboard.jsp') ? 'active' : ''}">
                Dashboard
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/teacher/courses">
                My Courses
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/teacher/grades">
                Grade Mgmt
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/teacher/attendance">
                Attendance Mgmt
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/teacher/profile">
                Profile
            </a>
        </li>
        <li class="nav-item" style="margin-top: auto;">
            <a href="${pageContext.request.contextPath}/logout" style="color: var(--danger-color);">
                Logout
            </a>
        </li>
    </ul>
</div>
