/**
 * Created by neikila on 05.02.16.
 */
define(["backbone"], function(Backbone) {
    return Backbone.View.extend({
        el: $(".nav.navbar-right"),

        templates: { // Шаблоны на разное состояние
            "auth": _.template($("script.navbar-right-html.auth").html()),
            "notAuth": _.template($("script.navbar-right-html.not-auth").html())
        },

        events: {
            //"click input:button": "check" // Обработчик клика на кнопке "Проверить"
        },

        initialize: function () { // Подписка на событие модели
            this.listenTo(this.model, 'change', this.render);
        },

        init: function (root) {
        },

        render: function () {
            console.log(this.model.get("isAuth"));
            if (this.model.get("isAuth")) {
                console.log("auth");
                $(this.el).html(this.templates["auth"](this.model.toJSON()));
            } else {
                console.log("not auth");
                $(this.el).html(this.templates["notAuth"](this.model.toJSON()));
            }
            return this;
        }
    });
});