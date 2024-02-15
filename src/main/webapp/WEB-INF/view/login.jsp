<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head></head>
<body>
<h1>Login</h1>

<form:form name='login' id='login' action="/auth/login" modelAttribute="loginDTO" method='POST'>
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
                <input name="submit" id='submit' type="submit" value="login" />
            </td>
        </tr>
    </table>
</form:form>
<a href="/auth/register">
    Register
</a>
<%--<script>--%>
<%--    function redirectToFetchUsers() {--%>
<%--        fetch('/auth/login', {--%>
<%--            method: 'POST',--%>
<%--            body: new FormData(document.getElementById('login'))--%>
<%--        })--%>
<%--            .then(function(response) {--%>
<%--                if (response.ok) {--%>
<%--                    return response.text();--%>

<%--                } else {--%>
<%--                    console.log('Login failed');--%>
<%--                    throw new Error('Login failed');--%>
<%--                }--%>
<%--            })--%>
<%--            .then(function(token) {--%>
<%--                console.log(token);--%>
<%--                redirectToFetchUsers2(token);--%>
<%--            })--%>
<%--            .catch(function(error) {--%>
<%--                console.log('Error occurred:', error);--%>
<%--            });--%>
<%--    }--%>
<%--    function redirectToFetchUsers2(token) {--%>

<%--        fetch('/fetchusers', {--%>
<%--            headers: {--%>
<%--                'Authorization': 'Bearer ' + token--%>
<%--            }--%>
<%--        })--%>
<%--            .then(function(response) {--%>
<%--                if (response.ok) {--%>
<%--                    window.location.href = '/fetchusers';--%>
<%--                } else if (response.status === 401) {--%>
<%--                    console.log(token);--%>
<%--                } else {--%>
<%--                    console.log('Error occurred:', response.statusText);--%>
<%--                }--%>
<%--            })--%>
<%--            .catch(function(error) {--%>
<%--                console.log('Error occurred:', error);--%>
<%--            });--%>
<%--    }--%>
<%--     document.getElementById('login').addEventListener('submit', function(event) {--%>
<%--          event.preventDefault();--%>
<%--          redirectToFetchUsers();--%>
<%--      });--%>
</script>
</body>
</html>