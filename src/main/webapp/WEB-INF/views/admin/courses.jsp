<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Course Management - School Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .sidebar { position: fixed; height: 100vh; }
        .main-content { margin-left: var(--sidebar-width); width: calc(100% - var(--sidebar-width)); padding: 2rem; }
    </style>
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/layout/admin_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">Course Management</div>
            <div class="user-profile">
                <span>Admin <strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar" style="background: linear-gradient(135deg, #ef4444, #b91c1c);">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem;">
            <div class="flex-between mb-2">
                <h3>Course Catalog</h3>
                <button class="btn btn-primary btn-sm" onclick="openModal('create')">+ Create Course</button>
            </div>
            
            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>Code</th>
                            <th>Name</th>
                            <th>Credits</th>
                            <th>Teacher</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="c" items="${courses}">
                            <tr>
                                <td>${c.courseCode}</td>
                                <td>${c.courseName}</td>
                                <td>${c.credits}</td>
                                <td>
                                    <c:set var="teacherObj" value="${teacherMap[c.teacherId]}" />
                                    <c:if test="${not empty teacherObj}">
                                        ${userMap[teacherObj.userId].firstName} ${userMap[teacherObj.userId].lastName}
                                    </c:if>
                                    <c:if test="${empty teacherObj}">
                                        Unknown (ID: ${c.teacherId})
                                    </c:if>
                                </td>
                                <td>
                                    <button class="btn btn-sm btn-primary" onclick="openModal('update', ${c.id}, '${c.courseName}', '${c.courseCode}', ${c.teacherId}, ${c.credits})">Edit</button>
                                    <button class="btn btn-sm btn-danger" onclick="deleteCourse(${c.id})">Delete</button>
                                </td>
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

<!-- Course Modal -->
<div id="courseModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeModal()">&times;</span>
        <h2 id="modalTitle">Create Course</h2>
        <form id="courseForm" action="${pageContext.request.contextPath}/admin/courses" method="post">
            <input type="hidden" name="action" id="formAction" value="create">
            <input type="hidden" name="id" id="courseId">
            
            <div class="form-group">
                <label>Course Name</label>
                <input type="text" name="courseName" id="courseName" class="form-control" required>
            </div>
            <div class="form-group">
                <label>Course Code</label>
                <input type="text" name="courseCode" id="courseCode" class="form-control" required>
            </div>
            <div class="form-group">
                <label>Credits</label>
                <input type="number" name="credits" id="credits" class="form-control" required>
            </div>
            <div class="form-group">
                <label>Assigned Teacher</label>
                <select name="teacherId" id="teacherId" class="form-control" required>
                    <c:if test="${empty teachers}">
                        <option value="" disabled selected>No teachers available - create one first</option>
                    </c:if>
                    <c:forEach var="t" items="${teachers}">
                        <c:set var="tUser" value="${userMap[t.userId]}" />
                        <option value="${t.id}">${tUser.firstName} ${tUser.lastName} (${t.specialization})</option>
                    </c:forEach>
                </select>
            </div>
            <button type="submit" class="btn btn-primary" style="width:100%">Save Course</button>
        </form>
    </div>
</div>

<!-- Delete Form -->
<form id="deleteForm" action="${pageContext.request.contextPath}/admin/courses" method="post" style="display:none;">
    <input type="hidden" name="action" value="delete">
    <input type="hidden" name="id" id="deleteId">
</form>

<script>
    const modal = document.getElementById("courseModal");
    const modalTitle = document.getElementById("modalTitle");
    const formAction = document.getElementById("formAction");
    const courseIdInput = document.getElementById("courseId");

    function openModal(mode, id, courseName, courseCode, teacherId, credits) {
        modal.style.display = "block";
        if (mode === 'create') {
            modalTitle.innerText = "Create New Course";
            formAction.value = "create";
            document.getElementById("courseForm").reset();
            courseIdInput.value = "";
        } else {
            modalTitle.innerText = "Edit Course";
            formAction.value = "update";
            courseIdInput.value = id;
            document.getElementById("courseName").value = courseName;
            document.getElementById("courseCode").value = courseCode;
            document.getElementById("teacherId").value = teacherId;
            document.getElementById("credits").value = credits;
        }
    }

    function closeModal() {
        modal.style.display = "none";
    }

    function deleteCourse(id) {
        if(confirm("Are you sure you want to delete this course?")) {
            document.getElementById("deleteId").value = id;
            document.getElementById("deleteForm").submit();
        }
    }

    window.onclick = function(event) {
        if (event.target == modal) {
            closeModal();
        }
    }
</script>

</body>
</html>
