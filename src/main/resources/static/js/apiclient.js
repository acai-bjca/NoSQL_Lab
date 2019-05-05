var apiclient = (function () {

    return {
        getCinemaByName: function (name, callback) {
            $.get("cinemas/" + name, function (data) {
                callback(data);
            });
        },
        getAllCinemas: function (callback) {
            $.get("cinemas", function (data) {
                callback(data);
            });
        },
        buyTicket: function (row, col, cinema, funcion, asiento, callback) {
            $.ajax({
                type: "PUT",
                url: "/cinemas/" + cinema + "/funciones/seats/" + row + "/" + col,
                data: JSON.stringify(funcion),
                contentType: "application/json"
            }).done(function () {
                callback(row, col, asiento);
            }).fail(function (datosDelFallo) {
                alert("¡Compra Rechazada!. El asiento está ocupado.");
            });
        }
    };
    
})();