<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>User Management - School Management</title>
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
            <div class="page-title">User Management</div>
            <div class="user-profile">
                <span>Admin <strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar" style="background: linear-gradient(135deg, #ef4444, #b91c1c);">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem;">
            <div class="flex-between mb-2">
                <h3>All System Users</h3>
                <button class="btn btn-primary btn-sm" onclick="openModal('create')">+ Add New User</button>
            </div>

            <!-- Search Bar -->
            <form action="${pageContext.request.contextPath}/admin/users" method="get" class="mb-2" style="display:flex; gap:10px;">
                <input type="text" name="q" class="form-control" placeholder="Search by name or email..." value="${searchQuery}" style="width: 300px;">
                <button type="submit" class="btn btn-sm btn-primary">Search</button>
                <c:if test="${not empty searchQuery}">
                    <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-sm btn-secondary" style="line-height: 2;">Clear</a>
                </c:if>
            </form>
            
            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Role</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="u" items="${users}">
                            <tr>
                                <td>${u.id}</td>
                                <td>${u.firstName} ${u.lastName}</td>
                                <td>${u.email}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${u.roleId == 1}"><span class="badge badge-danger">Admin</span></c:when>
                                        <c:when test="${u.roleId == 2}"><span class="badge badge-warning">Teacher</span></c:when>
                                        <c:when test="${u.roleId == 3}"><span class="badge badge-success">Student</span></c:when>
                                        <c:otherwise><span class="badge">Unknown</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:set var="studentData" value="${studentMap[u.id]}" />
                                    <c:set var="teacherData" value="${teacherMap[u.id]}" />
                                    <button class="btn btn-sm btn-primary" 
                                        onclick="openModal('update', ${u.id}, '${u.email}', '${u.firstName}', '${u.lastName}', ${u.roleId})"
                                        data-student-number="${studentData != null ? studentData.studentNumber : ''}"
                                        data-date-of-birth="${studentData != null ? studentData.dateOfBirth : ''}"
                                        data-employee-id="${teacherData != null ? teacherData.employeeId : ''}"
                                        data-specialization="${teacherData != null ? teacherData.specialization : ''}"
                                        id="editBtn-${u.id}">Edit</button>
                                    <button class="btn btn-sm btn-danger" onclick="deleteUser(${u.id})">Delete</button>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty users}">
                             <tr>
                                <td colspan="5" style="text-align:center;">No users found.</td>
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

<!-- User Modal -->
<div id="userModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeModal()">&times;</span>
        <h2 id="modalTitle">Add New User</h2>
        <form id="userForm" action="${pageContext.request.contextPath}/admin/users" method="post">
            <input type="hidden" name="action" id="formAction" value="create">
            <input type="hidden" name="id" id="userId">
            
            <div class="form-group">
                <label>First Name</label>
                <input type="text" name="firstName" id="firstName" class="form-control" required>
            </div>
            <div class="form-group">
                <label>Last Name</label>
                <input type="text" name="lastName" id="lastName" class="form-control" required>
            </div>
            <div class="form-group">
                <label>Email</label>
                <input type="email" name="email" id="email" class="form-control" required>
            </div>
            <div class="form-group">
                <label>Password</label>
                <input type="password" name="password" id="password" class="form-control" placeholder="Leave blank to keep current (update only)">
            </div>
            <div class="form-group">
                <label>Role</label>
                <select name="roleId" id="roleId" class="form-control" onchange="toggleRoleFields()">
                    <option value="1">Admin</option>
                    <option value="2">Teacher</option>
                    <option value="3">Student</option>
                </select>
            </div>

            <!-- Student Fields -->
            <div id="studentFields" style="display:none; border-top: 1px solid #ccc; padding-top: 10px; margin-top: 10px;">
                 <h4>Student Details</h4>
                 <div class="form-group">
                    <label>Student Number</label>
                    <input type="text" name="studentNumber" id="studentNumber" class="form-control">
                </div>
                <div class="form-group">
                    <label>Date of Birth (YYYY-MM-DD)</label>
                    <input type="date" name="dateOfBirth" id="dateOfBirth" class="form-control">
                </div>
            </div>

            <!-- Teacher Fields -->
            <div id="teacherFields" style="display:none; border-top: 1px solid #ccc; padding-top: 10px; margin-top: 10px;">
                <h4>Teacher Details</h4>
                <div class="form-group">
                    <label>Employee ID</label>
                    <input type="text" name="employeeId" id="employeeId" class="form-control">
                </div>
                <div class="form-group">
                    <label>Specialization</label>
                    <input type="text" name="specialization" id="specialization" class="form-control">
                </div>
            </div>

            <button type="submit" class="btn btn-primary" style="width:100%">Save User</button>
        </form>
    </div>
</div>

<!-- Delete Form -->
<form id="deleteForm" action="${pageContext.request.contextPath}/admin/users" method="post" style="display:none;">
    <input type="hidden" name="action" value="delete">
    <input type="hidden" name="id" id="deleteId">
</form>

<script>
    const modal = document.getElementById("userModal");
    const modalTitle = document.getElementById("modalTitle");
    const formAction = document.getElementById("formAction");
    const userIdInput = document.getElementById("userId");
    const passwordInput = document.getElementById("password");
    
    // Role fields
    const roleSelect = document.getElementById("roleId");
    const studentFields = document.getElementById("studentFields");
    const teacherFields = document.getElementById("teacherFields");

    function toggleRoleFields() {
        const role = roleSelect.value;
        // Reset
        studentFields.style.display = "none";
        teacherFields.style.display = "none";
        
        // Remove required attributes to avoid validations on hidden fields
        document.getElementById("studentNumber").required = false;
        document.getElementById("dateOfBirth").required = false;
        document.getElementById("employeeId").required = false;
        document.getElementById("specialization").required = false;

        if (role === "3") { // Student
            studentFields.style.display = "block";
            if (formAction.value === 'create') {
                document.getElementById("studentNumber").required = true;
                document.getElementById("dateOfBirth").required = true;
            }
        } else if (role === "2") { // Teacher
            teacherFields.style.display = "block";
            if (formAction.value === 'create') {
                document.getElementById("employeeId").required = true;
                document.getElementById("specialization").required = true;
            }
        }
    }

    function openModal(mode, id, email, firstName, lastName, roleId) {
        modal.style.display = "block";
        if (mode === 'create') {
            modalTitle.innerText = "Add New User";
            formAction.value = "create";
            document.getElementById("userForm").reset();
            userIdInput.value = "";
            passwordInput.required = true;
            passwordInput.placeholder = "Enter password";
            roleSelect.value = "3"; // Default to student
            // Clear student/teacher fields
            document.getElementById("studentNumber").value = "";
            document.getElementById("dateOfBirth").value = "";
            document.getElementById("employeeId").value = "";
            document.getElementById("specialization").value = "";
        } else {
            modalTitle.innerText = "Edit User";
            formAction.value = "update";
            userIdInput.value = id;
            document.getElementById("firstName").value = firstName;
            document.getElementById("lastName").value = lastName;
            document.getElementById("email").value = email;
            roleSelect.value = roleId;
            passwordInput.required = false;
            passwordInput.placeholder = "Leave blank to keep current";
            
            // Read student/teacher data from the edit button's data attributes
            var editBtn = document.getElementById("editBtn-" + id);
            if (editBtn) {
                var studentNumber = editBtn.getAttribute("data-student-number") || "";
                var dateOfBirth = editBtn.getAttribute("data-date-of-birth") || "";
                var employeeId = editBtn.getAttribute("data-employee-id") || "";
                var specialization = editBtn.getAttribute("data-specialization") || "";
                
                document.getElementById("studentNumber").value = studentNumber;
                document.getElementById("dateOfBirth").value = dateOfBirth;
                document.getElementById("employeeId").value = employeeId;
                document.getElementById("specialization").value = specialization;
            }
        }
        toggleRoleFields();
    }

    function closeModal() {
        modal.style.display = "none";
    }

    function deleteUser(id) {
        if(confirm("Are you sure you want to delete this user?")) {
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
