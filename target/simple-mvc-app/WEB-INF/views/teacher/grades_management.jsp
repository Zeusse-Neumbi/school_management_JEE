<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Grades Management - School Management</title>
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
            <div class="page-title">Grades Management</div>
            <div class="user-profile">
                <span><strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar" style="background: linear-gradient(135deg, #f59e0b, #d97706);">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem; margin-bottom: 2rem;">
            <h3>📝 Record New Grade</h3>
            <form action="${pageContext.request.contextPath}/teacher/grades" method="post" style="margin-top: 1rem; display: grid; grid-template-columns: 1fr 1fr 1fr auto; gap: 1rem; align-items: end;">
                <div class="input-group" style="margin-bottom: 0;">
                    <label>Course</label>
                    <select class="input-field">
                        <option>CS101 - Intro to Programming</option>
                        <option>CS202 - Data Structures</option>
                    </select>
                </div>
                <div class="input-group" style="margin-bottom: 0;">
                    <label>Student ID</label>
                    <input type="text" class="input-field" placeholder="Search student...">
                </div>
                <div class="input-group" style="margin-bottom: 0;">
                    <label>Grade (0-20)</label>
                    <input type="number" class="input-field" min="0" max="20" step="0.5">
                </div>
                <button type="submit" class="btn btn-primary">Save Grade</button>
            </form>
        </div>

        <div class="glass-panel" style="padding: 2rem;">
            <h3>Recent Grading</h3>
            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>Student</th>
                            <th>Course</th>
                            <th>Assessment</th>
                            <th>Grade</th>
                            <th>Date</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>John Doe</td>
                            <td>CS101</td>
                            <td>Midterm</td>
                            <td>15/20</td>
                            <td>Today</td>
                            <td><a href="#" class="btn btn-sm btn-danger">Edit</a></td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

</body>
</html>
