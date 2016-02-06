/**
 * Created by neikila on 05.02.16.
 */
define(["backbone", "error", "utils"], function(Backbone, Error, Utils) {
    return Backbone.View.extend({
        el: $(".container.content-bb"),

        templates: {
            "start": _.template($("script.container__todo").html()),
            "login": _.template($("script.container__login").html())
        },

        init: function(user) {
            this.user = user;
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
                        window.location.replace("/purchases");
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
            this.listenTo(this.model, 'change:state', this.render);
        },

        render: function() {
            var state = this.model.get("state");
            if (state == "login") {
                this.loginError = new Error({strong: "Oh snap!", message: "Error in login or password. Try once more."});
                $(this.el).html(this.templates[state]({"error": this.loginError.toJSON()}));
            } else {
                $(this.el).html(this.templates[state]());
                this.loginError = undefined;
            }
        }
    })
});