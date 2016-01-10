/**
 * Created by neikila on 10.01.16.
 */
var auth = function() {
    if(sessionStorage.getItem("isAuth") == undefined) {
        console.log("Request to get userData");
        $.ajax({
            type: "GET",
            url: "/userData",
            data: {},
            success: function(json){
                console.log(json);
                if (json.isAuth) {
                    sessionStorage.setItem("isAuth", true);
                    sessionStorage.setItem("login", json.user.login);
                    sessionStorage.setItem("name", json.user.name);
                    updateNavbarRight();
                } else {
                    sessionStorage.setItem("isAuth", false);
                }
            }
        });
    }
};

var invalidate = function() {
    sessionStorage.removeItem("isAuth");
    sessionStorage.removeItem("login");
    sessionStorage.removeItem("name");
};

var updateNavbarRight = function() {
    var navbarRight = $("ul.navbar-right");
    var isAuth = sessionStorage.getItem("isAuth") == "true";
    if (isAuth) {
        sessionStorage.getItem("login");
        navbarRight.empty();
        navbarRight.append('<p class="navbar-text">Signed in as <a href="#" class="navbar-link">' + sessionStorage.getItem("name") +
            '</a></p><li><a href="/logout"><span class="glyphicon glyphicon-log-out"></span> Logout</a></li>');
        $("ul.navbar-right li a").click(invalidate());
    } else {
        navbarRight.empty();
        navbarRight.append('<li><a href="/login"><span class="glyphicon glyphicon-log-in"></span> Login</a></li>' +
            '<li><a href="/signup"><span class="glyphicon glyphicon-user"></span> Signup</a></li>');
        auth();
    }
};

updateNavbarRight();
