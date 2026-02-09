<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="sidebar glass-panel">
    <div class="sidebar-logo">
        <span style="font-size: 2rem;">🎓</span> Student Zone
    </div>
    <ul class="nav-links">
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/student/dashboard" class="${pageContext.request.requestURI.endsWith('/dashboard.jsp') ? 'active' : ''}">
                Dashboard
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/student/courses">
                My Courses
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/student/grades">
                Grades
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/student/attendance">
                Attendance
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/student/profile">
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
