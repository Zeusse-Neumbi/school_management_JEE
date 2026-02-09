<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Hello World Servlet</title>
</head>
<body>
    <h1>Servlet Result</h1>
    <p>Message from HelloServlet: <strong>${servletMessage}</strong></p>
    <br/>
    <a href="${pageContext.request.contextPath}/">Back to Home</a>
</body>
</html>
