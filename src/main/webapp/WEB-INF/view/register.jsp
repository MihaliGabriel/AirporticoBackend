<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head></head>
<body>
<h1>Register</h1>

<form:form name='register' id='register' action="/auth/register" modelAttribute="registerDTO" method='POST'>
    <table>
        <tr>
            <td>
                User:
            </td>
            <td>
                <input type='text' id='username' name='username' value=''>
            </td>
        </tr>
        <tr>
            <td>
                Password:
            </td>
            <td>
                <input type='password' id='password' name='password' />
            </td>
        </tr>
        <tr>
            <td>
                <input name="submit" id='submit' type="submit" value="register" />
            </td>
        </tr>
    </table>
</form:form>
</body>

</html>