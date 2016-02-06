/**
 * Created by neikila on 06.02.16.
 */
define(["backbone", "error", "errorView", "utils"], function(Backbone, Error, ErrorView, Utils) {
    return Backbone.View.extend({
        templates: {
            "login": _.template($("script.container__login").html())
        },

        init: function(user) {
            this.user = user;
            return this;
        },

        events: {
            "click form.form-signin button": "login" // Обработчик клика на кнопке "Проверить"
        },

        login: function() {
            var self = this;
            var errorEl = $("div.alert");
            errorEl.slideUp("slow");
            Utils.postJSON("/loginJSON", {
                "login": $("#login").val(),
                "password": $("#password").val()
            }, function(json){
                console.log(json);
                if ('error' in json) {
                    if (json.code == 2) {
                        self.model.set("state", "start");
                    }
                    errorEl.slideUp("fast", function() {
                        errorEl.slideDown("slow");
                    });
                } else {
                    self.user.authWithJson(json);
                    self.model.set("state", "start");
                }
            });
            return false;
        },

        initialize: function () { // Подписка на событие модели
            this.loginError = new Error({strong: "Oh snap!", message: "Error in login or password. Try once more."});
        },

        render: function() {
            var state = this.model.get("state");
            $(this.el).html(this.templates[state]());

            new ErrorView({ model: this.loginError, el: $("form div.alert") }).render()
        }
    })
});