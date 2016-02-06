/**
 * Created by neikila on 03.02.16.
 */
define(["backbone"], function(Backbone) {
    return Backbone.Router.extend({
        routes: {
            "": "start", // Пустой hash-тэг
            "!/": "start", // Начальная страница
            "!/login": "login", // Показ группы
            "!/purchase": "purchases" // Показ группы
        },

        initializeWithAppState: function(appStateExternal) {
            this.appState = appStateExternal;
        },

        start: function () {
            this.appState.set({state: "start"});
        },

        login: function() {
            this.appState.set({state: "login"})
        },

        purchases: function() {
            this.appState.set({state: "purchases"})
        }
    });
});