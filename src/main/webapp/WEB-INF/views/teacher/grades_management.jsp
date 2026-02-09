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
            
            <form method="get" action="${pageContext.request.contextPath}/teacher/grades" style="margin-bottom: 1rem;">
                <div class="input-group">
                    <label>Select Course</label>
                    <select name="courseId" class="input-field" onchange="this.form.submit()">
                        <option value="">-- Select a Course --</option>
                        <c:forEach var="c" items="${courses}">
                            <option value="${c.id}" ${c.id == selectedCourseId ? 'selected' : ''}>
                                ${c.courseCode} - ${c.courseName}
                            </option>
                        </c:forEach>
                    </select>
                </div>
            </form>

            <c:if test="${not empty selectedCourseId}">
                <form action="${pageContext.request.contextPath}/teacher/grades" method="post" style="margin-top: 1rem; display: grid; grid-template-columns: 1fr 1fr auto; gap: 1rem; align-items: end;">
                    <div class="input-group" style="margin-bottom: 0;">
                        <label>Student</label>
                        <select name="enrollmentId" class="input-field" required>
                            <option value="">-- Select Student --</option>
                            <c:forEach var="sMap" items="${students}">
                                <option value="${sMap.enrollmentId}">
                                    ${sMap.user.firstName} ${sMap.user.lastName} (${sMap.student.studentNumber})
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="input-group" style="margin-bottom: 0;">
                        <label>Grade (0-20)</label>
                        <input type="number" name="grade" class="input-field" min="0" max="20" step="0.5" required>
                    </div>
                    <button type="submit" class="btn btn-primary">Save Grade</button>
                    <!-- hidden to keep referer flow if needed -->
                </form>
            </c:if>
        </div>

        <div class="glass-panel" style="padding: 2rem;">
            <h3>Recent Grading</h3>
            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>Student ID</th>
                            <th>Name</th>
                            <th>Grade</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="sMap" items="${students}">
                            <tr>
                                <td>${sMap.student.studentNumber}</td>
                                <td>${sMap.user.firstName} ${sMap.user.lastName}</td>
                                <td>
                                    <c:set var="eid" value="${sMap.enrollmentId}" />
                                    <c:choose>
                                        <c:when test="${gradesMap.containsKey(eid)}">
                                            ${gradesMap[eid]} / 20
                                        </c:when>
                                        <c:otherwise>
                                            <span style="color: #999;">Not Graded</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty students}">
                            <tr>
                                <td colspan="3" style="text-align: center; color: #666;">Select a course to view students and grades.</td>
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
