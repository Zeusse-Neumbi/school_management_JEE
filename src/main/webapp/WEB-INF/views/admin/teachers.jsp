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

            <!-- Search Bar -->
            <form action="${pageContext.request.contextPath}/admin/teachers" method="get" class="mb-2" style="display:flex; gap:10px;">
                <input type="text" name="q" class="form-control" placeholder="Search by name or specialization..." value="${searchQuery}" style="width: 300px;">
                <button type="submit" class="btn btn-sm btn-primary">Search</button>
                <c:if test="${not empty searchQuery}">
                    <a href="${pageContext.request.contextPath}/admin/teachers" class="btn btn-sm btn-secondary" style="line-height: 2;">Clear</a>
                </c:if>
            </form>
            
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
                                    <a href="${pageContext.request.contextPath}/admin/impersonate?userId=${t.userId}" class="btn btn-sm btn-primary">View Dashboard</a>
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

            <!-- Pagination -->
            <div class="pagination" style="margin-top: 1rem; display: flex; justify-content: flex-end; gap: 5px;">
                <c:if test="${currentPage > 1}">
                    <a href="?page=${currentPage - 1}&q=${searchQuery}" class="btn btn-sm btn-secondary">Previous</a>
                </c:if>
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <a href="?page=${i}&q=${searchQuery}" class="btn btn-sm ${currentPage == i ? 'btn-primary' : 'btn-secondary'}">${i}</a>
                </c:forEach>
                <c:if test="${currentPage < totalPages}">
                    <a href="?page=${currentPage + 1}&q=${searchQuery}" class="btn btn-sm btn-secondary">Next</a>
                </c:if>
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
            <input type="hidden" name="userId" id="userId">
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
            // Creation is not used from here anymore
            modalTitle.innerText = "Add New Teacher";
            formAction.value = "create";
            document.getElementById("teacherForm").reset();
            teacherIdInput.value = "";
        } else {
            modalTitle.innerText = "Edit Teacher";
            formAction.value = "update";
            teacherIdInput.value = id;
            userIdInput.value = userId;
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
