$(function() {

    $('#login-form-link').click(function(e) {
        $("#login-form").delay(100).fadeIn(100);
        $("#register-form").fadeOut(100);
        $('#register-form-link').removeClass('active');
        $(this).addClass('active');
        e.preventDefault();
    });
    $('#register-form-link').click(function(e) {
        $("#register-form").delay(100).fadeIn(100);
        $("#login-form").fadeOut(100);
        $('#login-form-link').removeClass('active');
        $(this).addClass('active');
        e.preventDefault();
    });
});

function register(){
    var psw1 = $("#reg-password").val();
    var psw2 = $("#confirm-password").val();
    if(psw1 !== psw2){
        $("#feedback").append("<p id=\"error\">Passwords does not match to each other! Please try again!</p>")
        return;
    }

    var userRegistrationData = new Object();
    userRegistrationData.firstName = $("#firstname").val();
    userRegistrationData.lastName = $("#lastname").val();
    userRegistrationData.nickName = $("#nickname").val();
    userRegistrationData.email = $("#email").val();
    userRegistrationData.password = Sha256.hash(psw1);

    $.ajax({
        type: "POST",
        url: "https://localhost:8181/javaFiddle/fiddle/users",
        contentType: "application/json",
        'data': JSON.stringify(userRegistrationData),
        dataType: "text",
        success: function(data)
        {
            $("#feedback").append("<p id=\"success\">Registration succeeded!Redirecting to home page...</p>");
            authUser(userRegistrationData.nickName, psw1);
        },
        error: function(textStatus, errorThrown)
        {
            alert("error in saving to database: " + textStatus + " " + errorThrown);
        }
    });

    return;
}

function authUser(nick, psw) {
    $("#auth_nickname").val(nick);
    $("#auth_password").val(psw);
    $("#login-form").submit();
}
