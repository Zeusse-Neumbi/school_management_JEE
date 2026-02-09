<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>My Grades - School Management</title>
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
            <div class="page-title">My Grades</div>
            <div class="user-profile">
                <span><strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem;">
            <div class="flex-between mb-2">
                <h3>Academic Performance</h3>
                <div class="glass-panel" style="padding: 0.5rem 1rem;">
                    <strong>GPA: 3.8</strong>
                </div>
            </div>
            
            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>Course</th>
                            <th>Assessment</th>
                            <th>Score</th>
                            <th>Date</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="enrollment" items="${enrollments}">
                            <c:set var="grades" value="${gradesMap[enrollment.id]}" />
                            <c:set var="course" value="${courseMap[enrollment.courseId]}" />
                            
                            <c:forEach var="grade" items="${grades}">
                                <tr>
                                    <td>${course.courseName}</td>
                                    <td>Assessment</td>
                                    <td><strong>${grade.grade}</strong> / 20</td>
                                    <td>${grade.dateRecorded}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${grade.grade >= 10}"><span class="badge badge-success">Passed</span></c:when>
                                            <c:otherwise><span class="badge badge-danger">Failed</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:forEach>
                         <c:if test="${empty enrollments}">
                             <tr>
                                <td colspan="5" style="text-align:center;">No grades recorded yet.</td>
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
