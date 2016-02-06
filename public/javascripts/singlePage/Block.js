/**
 * Created by neikila on 03.02.16.
 */
define(["jquery", "backbone", "underscore"], function($, Backbone, _) {
    return Backbone.View.extend({
        el: $("div.content-bb"),

        templates: { // Шаблоны на разное состояние
            "start": _.template($('#start').html()),
            "login": _.template($('#start').html()),
            "purchases": _.template($('#start').html()),
            "success": _.template($('#success').html()),
            "error": _.template($('#error').html())
        },

        events: {
            "click input:button": "check" // Обработчик клика на кнопке "Проверить"
        },

        initialize: function () { // Подписка на событие модели
            this.listenTo(this.model, 'change', this.render);
        },

        check: function () {
            //var username = $(this.el).find("input:text").val();
            //var find = this.MyFamily.checkUser(username); // Проверка имени пользователя
            //this.model.set({ // Сохранение имени пользователя и состояния
            //    "state": find ? "success" : "error",
            //    "username": username
            //});
        },

        render: function () {
            var state = this.model.get("state");
            $(this.el).html(this.templates[state](this.model.toJSON()));
            return this;
        }
    });
});