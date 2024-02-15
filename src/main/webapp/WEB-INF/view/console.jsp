<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head></head>

<body>
<h1>Users</h1>
<table border = 1>
    <th>id</th>
    <th>Username</th>
    <th>Password</th>
    <th>Role</th>

    <c:forEach var="user" items="${listUsers}" varStatus="status">
        <tr>
            <td>
                    ${user.id}
            </td>
            <td>
                    ${user.username}
            </td>
            <td>
                    ${user.password}
            </td>
            <td>
                    ${user.role_name}
            </td>
        </tr>
    </c:forEach>
</table>
</body>
</html>