/**
 * Created by neikila on 03.02.16.
 */
define(["backbone"], function(Backbone) {
    return Backbone.Router.extend({
        routes: {
            "": "start", // Пустой hash-тэг
            "!/": "start", // Начальная страница
            "!/success": "success", // Блок удачи
            "!/error": "error", // Блок ошибки
            "!/userInfo": "userInfo" // Блок ошибки
        },

        initializeWithAppState: function(appStateExternal) {
            this.appState = appStateExternal;
        },

        start: function () {
            this.appState.set({state: "start"});
        },

        success: function () {
            this.appState.set({state: "success"});
        },

        error: function () {
            this.appState.set({state: "error"});
        },

        userInfo: function () {
            this.appState.set({state: "userInfo"});
        }
    });
});