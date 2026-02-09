<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Teacher Dashboard - School Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .sidebar { position: fixed; height: 100vh; }
        .main-content { margin-left: var(--sidebar-width); width: calc(100% - var(--sidebar-width)); padding: 2rem; }
    </style>
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/layout/teacher_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">Instructor Dashboard</div>
            <div class="user-profile">
                <span>Welcome, <strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar" style="background: linear-gradient(135deg, #f59e0b, #d97706);">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="metric-grid">
            <div class="glass-panel metric-card">
                <div class="metric-value">${courseCount}</div>
                <div class="metric-label">Active Courses</div>
            </div>
            <div class="glass-panel metric-card">
                <div class="metric-value" style="color: var(--secondary-color);">${studentCount}</div>
                <div class="metric-label">Total Students</div>
            </div>
            <div class="glass-panel metric-card">
                <div class="metric-value" style="color: var(--primary-color);">0</div> <!- - TODO: Pending Grades - ->
                <div class="metric-label">Pending Grades</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem;">
            <h3>🗓️ Today's Schedule</h3>
            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>Time</th>
                            <th>Course</th>
                            <th>Room</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>09:00 AM</td>
                            <td>CS101 - Intro to Programming</td>
                            <td>Room 304</td>
                            <td><a href="#" class="btn btn-sm btn-primary">Start Class</a></td>
                        </tr>
                        <tr>
                            <td>11:00 AM</td>
                            <td>CS202 - Data Structures</td>
                            <td>Lab A</td>
                            <td><a href="#" class="btn btn-sm btn-primary">Start Class</a></td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

</body>
</html>
