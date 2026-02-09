<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Student Grade - School App</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

    <jsp:include page="/WEB-INF/common/header.jsp"/>

    <div class="main-container">
        <jsp:include page="/WEB-INF/common/sidebar.jsp"/>

        <div class="content">
            <!-- Sub Navigation Tabs -->
            <div class="sub-nav">
                <a href="#"><button>Nouvel élève</button></a>
                <a href="#"><button>Voir la liste</button></a>
                <a href="#"><button style="background: #a8e063;">Moyenne</button></a>
            </div>

            <h3>Calcul de la moyenne d'un élève</h3>

            <div class="form-container" style="border-radius: 30px;">
                <form action="${pageContext.request.contextPath}/eleves/moyenne" method="post">
                    
                    <div class="form-group">
                        <label>Nom</label>
                        <select name="nom" style="background: #e6f2ff;">
                            <option value="">Choisir un nom</option>
                            <option value="Jean">Jean</option>
                            <option value="Marie">Marie</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>Note JEE</label>
                        <input type="number" step="0.5" name="note1" required>
                    </div>

                    <div class="form-group">
                        <label>Note IDM</label>
                        <input type="number" step="0.5" name="note2" required>
                    </div>

                    <div style="text-align: right;">
                        <button type="submit" class="btn-blue" style="width: auto; padding: 10px 30px; border-radius: 5px;">Calculer</button>
                    </div>

                </form>

                <c:if test="${not empty moyenne}">
                    <div style="margin-top: 20px; text-align: center; color: green; font-weight: bold;">
                        Moyenne: ${moyenne}
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</body>
</html>
