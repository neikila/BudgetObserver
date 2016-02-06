/**
 * Created by neikila on 03.02.16.
 */
define(["backbone"], function(Backbone) {
    return Backbone.Router.extend({
        routes: {
            "": "start", // Пустой hash-тэг
            "!/": "start", // Начальная страница
            "!/login": "login",
            "!/signup": "signup",
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

        signup: function() {
            this.appState.set({state: "signup"})
        },

        purchases: function() {
            this.appState.set({state: "purchases"})
        }
    });
});