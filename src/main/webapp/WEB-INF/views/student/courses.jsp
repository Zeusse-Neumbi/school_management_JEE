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
            <div style="flex-grow: 1;"></div>
            <button class="btn btn-primary" onclick="openEnrollModal()">+ Enroll in Course</button>
            <div class="user-profile" style="margin-left: 20px;">
                <span><strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem;">
            <h3>Enrolled Courses</h3>
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
                        <c:forEach var="course" items="${enrolledCourses}">
                            <tr>
                                <td>${course.courseCode}</td>
                                <td>${course.courseName}</td>
                                <td>${teacherNames[course.id] != null ? teacherNames[course.id] : 'TBA'}</td>
                                <td>${course.credits}</td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/student/unenroll" method="post" style="display:inline;" onsubmit="return confirm('Are you sure you want to unenroll?');">
                                        <input type="hidden" name="courseId" value="${course.id}">
                                        <button type="submit" class="btn btn-sm btn-danger" style="background-color: #ef4444; color: white; border: none;">Unenroll</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty enrolledCourses}">
                             <tr>
                                <td colspan="5" style="text-align:center;">You are not currently enrolled in any courses.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<!-- Enroll Modal -->
<div id="enrollModal" style="display: none; position: fixed; z-index: 1000; left: 0; top: 0; width: 100%; height: 100%; overflow: auto; background-color: rgba(0,0,0,0.4);">
    <div style="background-color: #fefefe; margin: 10% auto; padding: 20px; border: 1px solid #888; width: 60%; border-radius: 10px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px;">
            <h2 style="margin: 0;">Available Courses</h2>
            <span onclick="closeEnrollModal()" style="color: #aaa; float: right; font-size: 28px; font-weight: bold; cursor: pointer;">&times;</span>
        </div>
        
        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th>Code</th>
                        <th>Course Name</th>
                        <th>Teacher</th>
                        <th>Credits</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="course" items="${availableCourses}">
                        <tr>
                            <td>${course.courseCode}</td>
                            <td>${course.courseName}</td>
                            <td>${teacherNames[course.id] != null ? teacherNames[course.id] : 'TBA'}</td>
                            <td>${course.credits}</td>
                            <td>
                                <form action="${pageContext.request.contextPath}/student/enroll" method="post" style="display:inline;">
                                    <input type="hidden" name="courseId" value="${course.id}">
                                    <button type="submit" class="btn btn-sm btn-primary">Enroll</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty availableCourses}">
                         <tr>
                            <td colspan="5" style="text-align:center;">No courses available for enrollment.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
        <div style="margin-top: 15px; text-align: right;">
            <button onclick="closeEnrollModal()" class="btn btn-sm" style="background-color: #ccc; color: black;">Close</button>
        </div>
    </div>
</div>

<script>
    function openEnrollModal() {
        document.getElementById('enrollModal').style.display = "block";
    }

    function closeEnrollModal() {
        document.getElementById('enrollModal').style.display = "none";
    }

    // Close modal if clicked outside
    window.onclick = function(event) {
        var modal = document.getElementById('enrollModal');
        if (event.target == modal) {
            modal.style.display = "none";
        }
    }
</script>

</body>
</html>
