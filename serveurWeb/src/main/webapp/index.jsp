<%
    if (session.getAttribute("userId") != null) {
        response.sendRedirect(request.getContextPath() + "/home.jsp");
        return;
    }
    response.sendRedirect(request.getContextPath() + "/api/auth/login");
%>
