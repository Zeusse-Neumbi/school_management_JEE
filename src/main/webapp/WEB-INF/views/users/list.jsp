<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>User Management - School App</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

    <jsp:include page="/WEB-INF/common/header.jsp"/>

    <div class="main-container">
        <jsp:include page="/WEB-INF/common/sidebar.jsp"/>

        <div class="content">
            <!-- Sub Navigation Tabs -->
            <div class="sub-nav">
                <a href="${pageContext.request.contextPath}/users/create"><button>Nouvel utilisateur</button></a>
                <a href="${pageContext.request.contextPath}/users"><button style="background: #a8e063;">Voir la liste</button></a>
            </div>

            <h3>Liste des utilisateurs</h3>

            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>Nom</th>
                            <th>Prenom</th>
                            <th>Grade</th>
                            <th>Email</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="u" items="${users}">
                            <tr>
                                <td>${u.lastName}</td> 
                                <td>${u.firstName}</td>
                                <td>${u.roleId}</td> <!-- Temporarily showing Role ID, need Role Name logic (e.g. JOIN or Service) -->
                                <td>${u.email}</td>
                                <td>
                                    <a class="btn-action btn-modify" href="${pageContext.request.contextPath}/users/edit?email=${u.email}">Modifier</a>
                                    <a class="btn-action btn-delete" href="${pageContext.request.contextPath}/users/delete?email=${u.email}" onclick="return confirm('Delete?');">Supprimer</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</body>
</html>
