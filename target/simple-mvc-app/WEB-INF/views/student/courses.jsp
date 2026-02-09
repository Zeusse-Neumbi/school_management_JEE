<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>My Courses - School Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .sidebar { position: fixed; height: 100vh; }
        .main-content { margin-left: var(--sidebar-width); width: calc(100% - var(--sidebar-width)); padding: 2rem; }
    </style>
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/layout/student_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">My Courses</div>
            <div class="user-profile">
                <span><strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem;">
            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>Code</th>
                            <th>Course Name</th>
                            <th>Teacher</th>
                            <th>Credits</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="course" items="${courses}">
                            <tr>
                                <td>${course.courseCode}</td>
                                <td>${course.courseName}</td>
                                <td>TBA</td> <!-- Teacher Name could be fetched if extended in DTO -->
                                <td>${course.credits}</td>
                                <td><span class="badge badge-success">Enrolled</span></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty courses}">
                             <tr>
                                <td colspan="5" style="text-align:center;">No courses found.</td>
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
