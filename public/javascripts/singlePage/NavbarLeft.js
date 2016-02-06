/**
 * Created by neikila on 06.02.16.
 */
define(["backbone"], function(Backbone) {
    return Backbone.View.extend({
        el: $("ul.navbar__groups"),

        templates: { // Шаблоны на разное состояние
            "auth": _.template($("script.navbar__groups.auth").html()),
            "notAuth": _.template($("script.navbar__groups.not-auth").html())
        },

        events: {
            "click li a": "test" // Обработчик клика на кнопке "Проверить"
        },

        test: function(data) {
            this.appState.set("groupToShow", data.target.textContent);
        },

        initialize: function () { // Подписка на событие модели
            this.listenTo(this.model, 'change:isAuth', this.render);
        },

        init: function(appState) {
            this.appState = appState;
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