/**
 * Created by neikila on 05.02.16.
 */
define(["backbone"], function(Backbone) {
    return Backbone.View.extend({
        el: $(".container.todo"),

        templates: {
            "def": _.template($("script.todo").html())
        },

        initialize: function () { // Подписка на событие модели
            this.listenTo(this.model, 'change', this.render);
        },

        render: function() {
            if (this.model.get("state") == 'start')
                $(this.el).html(this.templates["def"]());
            else
                $(this.el).html("");
        }
    })
});