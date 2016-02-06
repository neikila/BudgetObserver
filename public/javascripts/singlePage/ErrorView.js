/**
 * Created by neikila on 06.02.16.
 */
define(["backbone"], function(Backbone) {
    return Backbone.View.extend({
        templates: {
            "error": _.template("<strong><%= strong %></strong> <%= message %>")
        },

        initialize: function () { // Подписка на событие модели
            this.listenTo(this.model, 'change', this.render);
        },

        render: function() {
            $(this.el).html(this.templates["error"](this.model.toJSON()));
        }
    })
});