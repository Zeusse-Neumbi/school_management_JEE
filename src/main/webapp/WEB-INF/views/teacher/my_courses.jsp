<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Manage Courses - School Management</title>
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
            <div class="page-title">My Courses</div>
            <div class="user-profile">
                <span><strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar" style="background: linear-gradient(135deg, #f59e0b, #d97706);">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem;">
            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>Code</th>
                            <th>Course Name</th>
                            <th>Students</th>
                            <th>Credits</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="course" items="${courses}">
                            <tr>
                                <td>${course.courseCode}</td>
                                <td>${course.courseName}</td>
                                <td>${studentCountMap[course.id]}</td>
                                <td>${course.credits}</td>
                                <td>
                                    <a href="#" class="btn btn-sm btn-primary">Class List</a>
                                    <a href="#" class="btn btn-sm btn-danger">Edit Info</a>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty courses}">
                             <tr>
                                <td colspan="5" style="text-align:center;">No active courses found.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

</body>
</html>
