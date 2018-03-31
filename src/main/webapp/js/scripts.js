var socket = null;
var stompClient = null;
var queryInProgress = false;

function connect() {
    socket = new SockJS('/ws');

    stompClient = Stomp.over(socket);
    stompClient.connect({},
        function (frame) {

            queryInProgress = true;
            setConnected(true);

            //console.log('Connected: ' + frame);

            var request = $('#input-query').val();

            stompClient.subscribe('/weather/' + request, function (message) {
                showWeather(message);
            });

            send(request);
        },
        function (error) {
            console.log('Error: ' + error);
            if (queryInProgress) {
                disconnect();
            }
        }
    );
}

function send(request) {
    stompClient.send("/request", {}, JSON.stringify({ 'request': request }));
}

function showWeather(message) {

    stompClient.unsubscribe();
    //console.log('Message received: ' + message);

    queryInProgress = false;
    hideAllAlerts();

    // Смотрим, что-за сообщение. Выводим либо ошибку, либо результат

    switch (JSON.parse(message.body).code) {

        case 200:
            $('#city').html(JSON.parse(message.body).weather.city);
            $('#lat').html(JSON.parse(message.body).weather.lat);
            $('#lon').html(JSON.parse(message.body).weather.lon);
            $('#country').html(JSON.parse(message.body).weather.country);
            $('#temp').html(JSON.parse(message.body).weather.temp);
            $('#pressure').html(JSON.parse(message.body).weather.pressure);
            $('#humidity').html(JSON.parse(message.body).weather.humidity);
            $('#wind').html(JSON.parse(message.body).weather.wind);
            $('#clouds').html(JSON.parse(message.body).weather.clouds);

            $('#result').removeClass('hide');
            break;

        case 404:
            alertShow('info', 'Искомое не найдено');
            break;

        case 503:
            alertShow('danger', 'Удаленный сервер недоступен. Повторите попытку позже');
            break;

        case 520:
            alertShow('danger', 'Неопознанная ошибка. Повторите попытку позже');
            break;

        default:
            console.log('Error!!! Unusual response code of Message');
            break;
    }

    deBlockSearchBar();
    stompClient.disconnect();
    socket.close();

}

function setConnected(connected) {
    if (connected) {
        $('#input-query').prop('disabled', connected);
        $('#input-submit').prop('disabled', connected);
        $('#input-submit i').addClass('fa-spin');

        $('#result').addClass('hide');

        alertShow('info', 'Ожидайте ответа ...');
    }
}

function disconnect() {
    queryInProgress = false;
    deBlockSearchBar();
    alertShow('danger', 'Нет соединения с сервером. Повторите попытку позже');
}

function alertShow(className, text) {
    hideAllAlerts();
    $('#result-row .alert-' + className).html(text).removeClass('hide');
}

function deBlockSearchBar() {
    $('#input-query').prop('disabled', false);
    $('#input-submit').prop('disabled', false);

    $('#input-submit i').removeClass('fa-spin');
}

function hideAllAlerts() {
    $('#result-row .alert').addClass('hide');
}

$(function () {
    $('form').on('submit', function (e) {
        e.preventDefault();
        connect();
    });
    $('#input-submit').click(function() { connect(); });
});
