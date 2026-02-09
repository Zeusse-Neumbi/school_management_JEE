<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Attendance Management - School Management</title>
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
            <div class="page-title">Attendance Tracking</div>
            <div class="user-profile">
                <span><strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar" style="background: linear-gradient(135deg, #f59e0b, #d97706);">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem;">
            <div class="flex-between mb-2">
                <h3>Select Class Session</h3>
                <input type="date" class="input-field" style="width: auto;">
            </div>

            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>Student ID</th>
                            <th>Name</th>
                            <th>Status (Click to Toggle)</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>S10293</td>
                            <td>Alice Walker</td>
                            <td><button class="btn btn-sm btn-primary" style="background: var(--success-color);">Present</button></td>
                        </tr>
                        <tr>
                            <td>S10294</td>
                            <td>Bob Martin</td>
                            <td><button class="btn btn-sm btn-primary" style="background: var(--danger-color);">Absent</button></td>
                        </tr>
                        <tr>
                            <td>S10295</td>
                            <td>Charlie Brown</td>
                            <td><button class="btn btn-sm btn-primary" style="background: var(--success-color);">Present</button></td>
                        </tr>
                    </tbody>
                </table>
            </div>
            
            <div class="text-center mt-2">
                 <button class="btn btn-primary">Save Attendance Record</button>
            </div>
        </div>
    </div>
</div>

</body>
</html>
