<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Admin Dashboard - School Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .sidebar { position: fixed; height: 100vh; }
        .main-content { margin-left: var(--sidebar-width); width: calc(100% - var(--sidebar-width)); padding: 2rem; }
    </style>
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/layout/admin_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">System Overview</div>
            <div class="user-profile">
                <span>Admin <strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar" style="background: linear-gradient(135deg, #ef4444, #b91c1c);">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="metric-grid">
            <div class="glass-panel metric-card">
                <div class="metric-value">${userCount}</div>
                <div class="metric-label">Total Users</div>
            </div>
            <div class="glass-panel metric-card">
                <div class="metric-value" style="color: var(--primary-color);">${studentCount}</div>
                <div class="metric-label">Students</div>
            </div>
            <div class="glass-panel metric-card">
                <div class="metric-value" style="color: var(--secondary-color);">${teacherCount}</div>
                <div class="metric-label">Teachers</div>
            </div>
             <div class="glass-panel metric-card">
                <div class="metric-value" style="color: var(--accent-color);">${courseCount}</div>
                <div class="metric-label">Courses</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem;">
            <h3>📊 System Health & Activity</h3>
            <p style="margin-top: 1rem; color: var(--text-muted);">
                System is running smoothly. Last backup was performed 2 hours ago.
            </p>
            <div style="margin-top: 2rem; height: 100px; background: rgba(0,0,0,0.05); border-radius: 12px; display: flex; align-items: center; justify-content: center; color: var(--text-muted);">
                [Activity Chart Placeholder]
            </div>
        </div>
    </div>
</div>

</body>
</html>
