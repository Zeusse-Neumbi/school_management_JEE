<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Student Management - School Management</title>
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
            <div class="page-title">Student Management</div>
            <div class="user-profile">
                <span>Admin <strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar" style="background: linear-gradient(135deg, #ef4444, #b91c1c);">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem;">
            <div class="flex-between mb-2">
                <h3>Enrolled Students</h3>
                <!-- Creation handled via User Management -->
            </div>
            
            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>Student ID</th>
                            <th>Name</th>
                            <th>Class/Year</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="s" items="${students}">
                             <tr>
                                <td>${s.studentNumber}</td>
                                <td>${userMap[s.userId].firstName} ${userMap[s.userId].lastName}</td>
                                <td>Year 1</td> <!-- TODO: Add Class/Year to Student model -->
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/profile?userId=${s.userId}" class="btn btn-sm btn-primary">View Profile</a>
                                    <button class="btn btn-sm btn-primary" onclick="openModal('update', ${s.id}, ${s.userId}, '${s.studentNumber}', '${s.email}', '${s.dateOfBirth}')">Edit</button>
                                    <button class="btn btn-sm btn-danger" onclick="deleteStudent(${s.id})">Delete</button>
                                </td>
                            </tr>
                        </c:forEach>
                         <c:if test="${empty students}">
                             <tr>
                                <td colspan="4" style="text-align:center;">No students found.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<!-- Student Modal -->
<div id="studentModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeModal()">&times;</span>
        <h2 id="modalTitle">Enroll Student</h2>
        <form id="studentForm" action="${pageContext.request.contextPath}/admin/students" method="post">
            <input type="hidden" name="action" id="formAction" value="create">
            <input type="hidden" name="id" id="studentId">
            
            <div class="form-group">
                <label>Linked User ID (Enter ID of existing User)</label>
                <input type="number" name="userId" id="userId" class="form-control" required>
                <small style="color: grey;">* Create a User first if not exists</small>
            </div>
            <div class="form-group">
                <label>Student Number</label>
                <input type="text" name="studentNumber" id="studentNumber" class="form-control" required>
            </div>
            <div class="form-group">
                <label>Email (School)</label>
                <input type="email" name="email" id="email" class="form-control" required>
            </div>
            <div class="form-group">
                <label>Date of Birth</label>
                <input type="date" name="dateOfBirth" id="dateOfBirth" class="form-control" required>
            </div>
            <button type="submit" class="btn btn-primary" style="width:100%">Save Student</button>
        </form>
    </div>
</div>

<!-- Delete Form -->
<form id="deleteForm" action="${pageContext.request.contextPath}/admin/students" method="post" style="display:none;">
    <input type="hidden" name="action" value="delete">
    <input type="hidden" name="id" id="deleteId">
</form>

<script>
    const modal = document.getElementById("studentModal");
    const modalTitle = document.getElementById("modalTitle");
    const formAction = document.getElementById("formAction");
    const studentIdInput = document.getElementById("studentId");
    const userIdInput = document.getElementById("userId");

    function openModal(mode, id, userId, studentNumber, email, dateOfBirth) {
        modal.style.display = "block";
        if (mode === 'create') {
            modalTitle.innerText = "Enroll New Student";
            formAction.value = "create";
            document.getElementById("studentForm").reset();
            studentIdInput.value = "";
            userIdInput.readOnly = false;
        } else {
            modalTitle.innerText = "Edit Student";
            formAction.value = "update";
            studentIdInput.value = id;
            userIdInput.value = userId;
            userIdInput.readOnly = true; // Prevent changing user link on edit for safety
            document.getElementById("studentNumber").value = studentNumber;
            document.getElementById("email").value = email;
            document.getElementById("dateOfBirth").value = dateOfBirth;
        }
    }

    function closeModal() {
        modal.style.display = "none";
    }

    function deleteStudent(id) {
        if(confirm("Are you sure you want to unenroll this student? (This does not delete the User account)")) {
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
