<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="sidebar">
    <!-- Active class logic would ideally happen here based on URL -->
    <a href="${pageContext.request.contextPath}/users" class="${pageContext.request.servletPath.startsWith('/users') ? 'active' : ''}">Utilisateurs</a>
    <a href="${pageContext.request.contextPath}/eleves/moyenne" class="${pageContext.request.servletPath.startsWith('/eleves') ? 'active' : ''}">Eleves</a>
    <a href="${pageContext.request.contextPath}/profs" class="${pageContext.request.servletPath.startsWith('/profs') ? 'active' : ''}">Profs</a>
    <a href="${pageContext.request.contextPath}/classes" class="${pageContext.request.servletPath.startsWith('/classes') ? 'active' : ''}">Classes</a>
    <a href="${pageContext.request.contextPath}/logout" style="margin-top: auto; background: linear-gradient(to bottom, #ff9966, #ff5e62); color:white;">Logout</a>
</div>
