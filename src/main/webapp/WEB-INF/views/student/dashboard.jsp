<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Student Dashboard - School Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .sidebar { position: fixed; height: 100vh; }
        .main-content { margin-left: var(--sidebar-width); width: calc(100% - var(--sidebar-width)); padding: 2rem; }
    </style>
</head>
<body>

<c:if test="${sessionScope.impersonating}">
    <div style="background: #f97316; color: white; padding: 10px; text-align: center; position: sticky; top: 0; z-index: 9999;">
        <strong>⚠️ Viewing as: ${sessionScope.user.firstName} ${sessionScope.user.lastName}</strong>
        <a href="${pageContext.request.contextPath}/admin/stop-impersonate" 
           class="btn btn-sm" style="margin-left: 20px; background: white; color: #f97316; border: none;">
           Stop Impersonating
        </a>
    </div>
</c:if>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/layout/student_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">Dashboard</div>
            <div class="user-profile">
                <span>Welcome, <strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="metric-grid">
            <div class="glass-panel metric-card">
                <div class="metric-value">${courseCount}</div>
                <div class="metric-label">Enrolled Courses</div>
            </div>
            <div class="glass-panel metric-card">
                <div class="metric-value" style="color: var(--warning-color);">${gpa}</div>
                <div class="metric-label">Average Grade</div>
            </div>
            <div class="glass-panel metric-card">
                <div class="metric-value" style="color: var(--success-color);">${attendanceRate}%</div>
                <div class="metric-label">Attendance</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem;">
            <h3>📢 Quick Updates</h3>
            <p style="color: var(--text-muted);">
                Welcome to your student portal. Check your latest grades and upcoming classes here.
            </p>
        </div>
    </div>
</div>

</body>
</html>
