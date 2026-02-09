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

            <!-- Search Bar -->
            <form action="${pageContext.request.contextPath}/admin/students" method="get" class="mb-2" style="display:flex; gap:10px;">
                <input type="text" name="q" class="form-control" placeholder="Search by name or student number..." value="${searchQuery}" style="width: 300px;">
                <button type="submit" class="btn btn-sm btn-primary">Search</button>
                <c:if test="${not empty searchQuery}">
                    <a href="${pageContext.request.contextPath}/admin/students" class="btn btn-sm btn-secondary" style="line-height: 2;">Clear</a>
                </c:if>
            </form>
            
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
                                    <a href="${pageContext.request.contextPath}/admin/impersonate?userId=${s.userId}" class="btn btn-sm btn-primary">View Dashboard</a>
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

<!-- Student Modal -->
<div id="studentModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeModal()">&times;</span>
        <h2 id="modalTitle">Enroll Student</h2>
        <form id="studentForm" action="${pageContext.request.contextPath}/admin/students" method="post">
            <input type="hidden" name="action" id="formAction" value="create">
            <input type="hidden" name="id" id="studentId">
            <input type="hidden" name="userId" id="userId">
            
            <div class="form-group" id="userIdGroup" style="display:none;">
                <label>Linked User ID (Enter ID of existing User)</label>
                <input type="number" id="userIdDisplay" class="form-control" readonly>
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
            // Creation is not used from here anymore
            modalTitle.innerText = "Enroll New Student";
            formAction.value = "create";
            document.getElementById("studentForm").reset();
            studentIdInput.value = "";
        } else {
            modalTitle.innerText = "Edit Student";
            formAction.value = "update";
            studentIdInput.value = id;
            userIdInput.value = userId;
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
