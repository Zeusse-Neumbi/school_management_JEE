<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Create User - School App</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

    <jsp:include page="/WEB-INF/common/header.jsp"/>

    <div class="main-container">
        <jsp:include page="/WEB-INF/common/sidebar.jsp"/>

        <div class="content">
            <!-- Sub Navigation Tabs -->
            <div class="sub-nav">
                <a href="${pageContext.request.contextPath}/users/create"><button style="background: #a8e063;">Nouvel utilisateur</button></a>
                <a href="${pageContext.request.contextPath}/users"><button>Voir la liste</button></a>
            </div>

            <h3>Création d'un utilisateur</h3>

            <div class="form-container">
                <form action="${pageContext.request.contextPath}/users/create" method="post">
                    <div style="display: flex; gap: 20px;">
                        <div style="flex: 1;">
                            <div class="form-group">
                                <label>Nom</label>
                                <input type="text" name="name" required>
                            </div>
                            <div class="form-group">
                                <label>Prénom</label>
                                <input type="text" name="username" required placeholder="(Used as login for demo)">
                            </div>
                            <div class="form-group">
                                <label>Grade</label>
                                <input type="text" value="Professeur" readonly>
                            </div>
                        </div>
                        <div style="flex: 1;">
                            <div class="form-group">
                                <label>Email</label>
                                <input type="text">
                            </div>
                            <div class="form-group">
                                <label>Mot de passe</label>
                                <input type="password" name="password" required>
                            </div>
                            <div class="form-group">
                                <label>Répéter MP</label>
                                <input type="password">
                            </div>
                        </div>
                    </div>
                    
                    <div style="text-align: right; margin-top: 10px;">
                         <button type="submit" class="btn-primary" style="width: auto; padding: 10px 40px; border-radius: 20px;">Create</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</body>
</html>
