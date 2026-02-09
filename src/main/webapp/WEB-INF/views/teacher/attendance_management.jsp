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
            <form method="get" action="${pageContext.request.contextPath}/teacher/attendance" style="margin-bottom: 1rem;">
                <div class="flex-between mb-2" style="flex-wrap: wrap; gap: 1rem;">
                    <h3>Select Class Session</h3>
                    <div style="display: flex; gap: 10px; flex-wrap: wrap;">
                        <select name="courseId" class="input-field" style="width: auto;" onchange="this.form.submit()">
                            <option value="">-- Select Course --</option>
                            <c:forEach var="c" items="${courses}">
                                <option value="${c.id}" ${c.id == selectedCourseId ? 'selected' : ''}>
                                    ${c.courseCode} - ${c.courseName}
                                </option>
                            </c:forEach>
                        </select>
                        <input type="date" name="date" class="input-field" style="width: auto;" value="${currentDate}" onchange="this.form.submit()">
                        <input type="text" name="search" class="input-field" style="width: auto;" placeholder="Search student..." value="${searchQuery}">
                        <button type="submit" class="btn btn-sm btn-primary">Search</button>
                    </div>
                </div>
            </form>

            <c:if test="${not empty selectedCourseId}">
                <form action="${pageContext.request.contextPath}/teacher/attendance" method="post">
                    <input type="hidden" name="courseId" value="${selectedCourseId}">
                    <input type="hidden" name="date" value="${currentDate}">
                    <input type="hidden" name="search" value="${searchQuery}">
                    <input type="hidden" name="page" value="${currentPage}">
                    
                    <div class="table-container">
                        <table>
                            <thead>
                                <tr>
                                    <th>Student ID</th>
                                    <th>Name</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="sMap" items="${students}">
                                    <tr>
                                        <td>${sMap.student.studentNumber}</td>
                                        <td>${sMap.user.firstName} ${sMap.user.lastName}</td>
                                        <td>
                                            <label style="margin-right: 10px;">
                                                <input type="radio" name="status_${sMap.enrollmentId}" value="Present" ${sMap.status == 'Present' ? 'checked' : ''} required> Present
                                            </label>
                                            <label style="margin-right: 10px;">
                                                <input type="radio" name="status_${sMap.enrollmentId}" value="Absent" ${sMap.status == 'Absent' ? 'checked' : ''}> Absent
                                            </label>
                                            <label>
                                                <input type="radio" name="status_${sMap.enrollmentId}" value="Late" ${sMap.status == 'Late' ? 'checked' : ''}> Late
                                            </label>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty students}">
                                    <tr><td colspan="3" style="text-align:center;">No students found.</td></tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                    
                    <c:if test="${not empty students}">
                        <div class="text-center mt-2">
                             <button type="submit" class="btn btn-primary">Save Attendance Record</button>
                        </div>
                    </c:if>
                </form>

                <!-- Pagination -->
                <c:if test="${totalPages > 1}">
                    <div style="margin-top: 1rem; display: flex; justify-content: center; gap: 0.5rem;">
                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <a href="${pageContext.request.contextPath}/teacher/attendance?courseId=${selectedCourseId}&date=${currentDate}&search=${searchQuery}&page=${i}" 
                               class="btn btn-sm ${i == currentPage ? 'btn-primary' : 'btn-danger'}" 
                               style="${i == currentPage ? 'opacity: 1;' : 'opacity: 0.7;'}">
                                ${i}
                            </a>
                        </c:forEach>
                    </div>
                </c:if>
            </c:if>
            
            <c:if test="${empty selectedCourseId}">
                <p style="text-align: center; color: #666;">Please select a course to take attendance.</p>
            </c:if>
        </div>
    </div>
</div>

</body>
</html>
