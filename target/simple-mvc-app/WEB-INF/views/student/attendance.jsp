<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Attendance - School Management</title>
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
            <div class="page-title">Attendance Record</div>
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
                            <th>Date</th>
                            <th>Course</th>
                            <th>Status</th>
                            <th>Remarks</th>
                        </tr>
                    </thead>
                    <tbody>
                         <c:forEach var="enrollment" items="${enrollments}">
                            <c:set var="attendances" value="${attendanceMap[enrollment.id]}" />
                            <c:set var="course" value="${courseMap[enrollment.courseId]}" />
                            
                            <c:forEach var="att" items="${attendances}">
                                <tr>
                                    <td>${att.attendanceDate}</td>
                                    <td>${course.courseName}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${att.status == 'PRESENT'}"><span class="badge badge-success">Present</span></c:when>
                                            <c:when test="${att.status == 'LATE'}"><span class="badge badge-warning">Late</span></c:when>
                                            <c:otherwise><span class="badge badge-danger">Absent</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>-</td>
                                </tr>
                            </c:forEach>
                        </c:forEach>
                         <c:if test="${empty enrollments}">
                             <tr>
                                <td colspan="4" style="text-align:center;">No attendance records found.</td>
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
