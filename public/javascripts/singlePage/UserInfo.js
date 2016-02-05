/**
 * Created by neikila on 03.02.16.
 */
define(["backbone", "underscore"], function(Backbone, _) {
    return Backbone.View.extend({
        templates: { // Шаблоны на разное состояние
            "userInfo": _.template($('#userInfoInner').html())
        },

        initialize: function () { // Подписка на событие модели
            this.listenTo(this.model, 'change', this.render);
        },

        render: function () {
            $(this.el).html(this.templates["userInfo"](this.model.toJSON()));
            return this;
        },

        update: function (newEl) {
            this.el = newEl;
        }
    });
});