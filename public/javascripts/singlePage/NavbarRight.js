/**
 * Created by neikila on 05.02.16.
 */
define(["backbone", "utils"], function(Backbone, Utils) {
    return Backbone.View.extend({
        el: $(".nav.navbar-right"),

        templates: { // Шаблоны на разное состояние
            "auth": _.template($("script.navbar-right-html.auth").html()),
            "notAuth": _.template($("script.navbar-right-html.not-auth").html())
        },

        events: {
            "click li.navbar-right_logout": "logout" // Обработчик клика на кнопке "Проверить"
        },

        logout: function() {
            Utils.getJSON("/logout");
            this.model.deauth();
        },

        initialize: function () { // Подписка на событие модели
            this.listenTo(this.model, 'change:isAuth', this.render);
        },

        init: function(appState) {
            this.appState = appState;
            this.listenTo(this.appState, 'change:state', function() {
                if (this.appState.get("state") == "login")
                    $(document).xpathEvaluate("//a[contains(text(),'Login')]/..").addClass("active");
            })
        },

        render: function () {
            if (this.model.get("isAuth")) {
                $(this.el).html(this.templates["auth"](this.model.toJSON()));
            } else {
                $(this.el).html(this.templates["notAuth"](this.model.toJSON()));
            }
            return this;
        }
    });
});