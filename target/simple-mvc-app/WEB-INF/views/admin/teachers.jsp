<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Teacher Management - School Management</title>
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
            <div class="page-title">Teacher Management</div>
            <div class="user-profile">
                <span>Admin <strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar" style="background: linear-gradient(135deg, #ef4444, #b91c1c);">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem;">
            <div class="flex-between mb-2">
                <h3>Faculty Members</h3>
                <!-- Creation handled via User Management -->
            </div>
            
            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>Employee ID</th>
                            <th>Name</th>
                            <th>Specialization</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="t" items="${teachers}">
                            <tr>
                                <td>${t.employeeId}</td>
                                <td>${userMap[t.userId].firstName} ${userMap[t.userId].lastName}</td>
                                <td>${t.specialization}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/profile?userId=${t.userId}" class="btn btn-sm btn-primary">View Profile</a>
                                    <button class="btn btn-sm btn-primary" onclick="openModal('update', ${t.id}, ${t.userId}, '${t.employeeId}', '${t.email}', '${t.specialization}')">Edit</button>
                                    <button class="btn btn-sm btn-danger" onclick="deleteTeacher(${t.id})">Delete</button>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty teachers}">
                             <tr>
                                <td colspan="4" style="text-align:center;">No teachers found.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<!-- Teacher Modal -->
<div id="teacherModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeModal()">&times;</span>
        <h2 id="modalTitle">Add Teacher</h2>
        <form id="teacherForm" action="${pageContext.request.contextPath}/admin/teachers" method="post">
            <input type="hidden" name="action" id="formAction" value="create">
            <input type="hidden" name="id" id="teacherId">
            
            <div class="form-group">
                <label>Linked User ID</label>
                <input type="number" name="userId" id="userId" class="form-control" required>
                <small style="color: grey;">* User must exist</small>
            </div>
            <div class="form-group">
                <label>Employee ID</label>
                <input type="text" name="employeeId" id="employeeId" class="form-control" required>
            </div>
            <div class="form-group">
                <label>Email (Work)</label>
                <input type="email" name="email" id="email" class="form-control" required>
            </div>
            <div class="form-group">
                <label>Specialization</label>
                <input type="text" name="specialization" id="specialization" class="form-control" required>
            </div>
            <button type="submit" class="btn btn-primary" style="width:100%">Save Teacher</button>
        </form>
    </div>
</div>

<!-- Delete Form -->
<form id="deleteForm" action="${pageContext.request.contextPath}/admin/teachers" method="post" style="display:none;">
    <input type="hidden" name="action" value="delete">
    <input type="hidden" name="id" id="deleteId">
</form>

<script>
    const modal = document.getElementById("teacherModal");
    const modalTitle = document.getElementById("modalTitle");
    const formAction = document.getElementById("formAction");
    const teacherIdInput = document.getElementById("teacherId");
    const userIdInput = document.getElementById("userId");

    function openModal(mode, id, userId, employeeId, email, specialization) {
        modal.style.display = "block";
        if (mode === 'create') {
            modalTitle.innerText = "Add New Teacher";
            formAction.value = "create";
            document.getElementById("teacherForm").reset();
            teacherIdInput.value = "";
            userIdInput.value = "";
            userIdInput.readOnly = false;
        } else {
            modalTitle.innerText = "Edit Teacher";
            formAction.value = "update";
            teacherIdInput.value = id;
            userIdInput.value = userId;
            userIdInput.readOnly = true;
            document.getElementById("employeeId").value = employeeId;
            document.getElementById("email").value = email;
            document.getElementById("specialization").value = specialization;
        }
    }

    function closeModal() {
        modal.style.display = "none";
    }

    function deleteTeacher(id) {
        if(confirm("Are you sure you want to remove this teacher record?")) {
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
