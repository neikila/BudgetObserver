/**
 * Created by neikila on 06.02.16.
 */
define(["backbone", "error", "errorView", "utils"], function(Backbone, Error, ErrorView, Utils) {
    return Backbone.View.extend({
        templates: {
            "signup": _.template($("script.container__signup").html())
        },

        init: function(user) {
            this.user = user;
            return this;
        },

        events: {
            "click button": "signup"
        },

        signup: function() {
            var self = this;
            var errorEl = this.$("div.alert");
            errorEl.slideUp("slow");
            Utils.postJSON("/signup", {
                "login": $("#login").val(),
                "password": $("#password").val(),
                "password_repeat": $("#password_repeat").val(),
                "name": $("#name").val(),
                "surname": $("#surname").val(),
                "email": $("#email").val()
            }, function(json){
                if ('error' in json) {
                    errorEl.slideUp("fast", function() {
                        self.setErrorMessage(json.code);
                        errorEl.slideDown("slow");
                    });
                } else {
                    self.user.authWithJson(json);
                    self.model.set("state", "start");
                }
            });
            return false;
        },

        setErrorMessage: function(code) {
            if (code == 7) {
                if (this.prevErr != 7) {
                    this.signupError.set({
                        strong: "Oh snap!",
                        message: "Someone is already using this login."
                    });
                } else {
                    this.signupError.set({
                        strong: "No, no!",
                        message: "This one is busy too."
                    });
                }
            } else if (code == 8) {
                this.signupError.set({
                    strong: "Look out!",
                    message: "Passwords don't match."
                });
            } else {
                this.signupError.set({
                    strong: "Ups!",
                    message: "Something gone wrong."
                });
            }
            this.prevErr = code;
        },

        initialize: function () { // Подписка на событие модели
            this.signupError = new Error();
            this.prevErr = -1;
        },

        render: function() {
            $(this.el).html(this.templates["signup"]());
            new ErrorView({ model: this.signupError, el: this.$("form div.alert") }).render()
        }
    })
});