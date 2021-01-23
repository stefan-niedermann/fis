let clock;
let infoWeatherTemperature;
let infoWeatherIcon;
let operation;
let operationTopicKeyword;
let operationTopicTags;
let operationTopicNote;
let operationLocation;
let operationVehicleList;

let activeOperation = false;

const keywords = ["b", "thl", "abc", "rd", "son", "inf", "dekon"];

document.addEventListener('DOMContentLoaded', () => {
    clock = document.getElementById('info-clock');
    infoWeatherTemperature = document.getElementById('info-weather-temperature');
    infoWeatherIcon = document.getElementById('info-weather-icon');
    operation = document.getElementById('operation');
    operationTopicKeyword = document.getElementById('operation-topic-keyword');
    operationTopicTags = document.getElementById('operation-topic-tags');
    operationTopicNote = document.getElementById('operation-topic-note');
    operationLocation = document.getElementById('operation-location');
    operationVehicleList = document.getElementById('operation-vehicles-list');

    setInterval(updateClock, 5 * 1000);
    updateClock();
    Promise.all([
        fetch('/weather').then(resp => resp.json()),
        fetch('/parameter').then(resp => resp.json())
    ]).then(([weather, params]) => {
        console.info('⚙️ Parameter:', params);
        document.documentElement.setAttribute('lang', params.lang);

        handleIncomingWeather(weather);

        if (!params.operation.highlight) {
            document.documentElement.classList.add('no-highlight');
        }

        initStompClient('/socket')
            .then((stompClient) => stompClientSendMessage(stompClient, '/register'))
            .then((stompClient) => stompSubscribe(stompClient, `/notification/operation`, (data) => handleIncomingOperation(JSON.parse(data.body), params)))
            .then((stompClient) => stompSubscribe(stompClient, `/notification/weather`, (data) => handleIncomingWeather(JSON.parse(data.body))));
    });
});

// const handleIncomingWeather = (weather) => {
//     console.info('⛅️ New weather:', weather);
//     if (!activeOperation) {
//         if (weather.isDay) {
//             document.documentElement.classList.remove('dark-theme');
//         } else {
//             document.documentElement.classList.add('dark-theme');
//         }
//     }
//     infoWeatherTemperature.innerText = `${(Math.round(weather.temperature * 100) / 100).toFixed((weather.temperature > -10 && weather.temperature < 10) ? 1 : 0)}°`;
//     infoWeatherIcon.setAttribute('src', `./icons/${weather.icon}.svg`);
// }

const handleIncomingOperation = (operation, params) => {
    console.info('🚒️ New operation:', operation);
    const mainElement = document.querySelector('body>main');
    mainElement.classList.add('active-operation');
    setTimeout(() => {
        // TODO reset this when a new operation arrived in the meantime.
        console.debug('⏰ Timeout over… unset active operation.');
        mainElement.classList.remove('active-operation');
        resetOperationData();
    }, params.operation.duration);
    fillOperationData(operation, (params.operation.highlight || '').toLowerCase());
}

// const updateClock = () => {
//     const date = new Date();
//     const hours = date.getHours();
//     const minutes = date.getMinutes();
//     clock.textContent = `${hours < 10 ? `0${hours}` : hours}:${minutes < 10 ? `0${minutes}` : minutes} Uhr`;
// }

// const resetOperationData = () => {
//     operation.removeAttribute('class');
//     operationTopicKeyword.innerHTML = '';
//     operationTopicTags.innerHTML = '';
//     operationTopicNote.innerHTML = '';
//     operationLocation.innerHTML = '';
//     operationVehicleList.innerHTML = '';
// }

// const fillOperationData = (data, highlight) => {
//     resetOperationData();
//     operation.classList.add(getOperationTypeClass(data.keyword));
//     operationTopicKeyword.innerHTML = getOperationKeyword(data.keyword);
//     operationLocation.innerHTML = formatLocation(data.street, data.number, data.location, data.obj);
//     operationTopicNote.innerText = data.note;
//     if (Array.isArray(data.tags)) {
//         data.tags.forEach(tag => {
//             const li = document.createElement('li');
//             li.innerText = tag;
//             operationTopicTags.appendChild(li);
//         });
//     }
//
//     if (Array.isArray(data.vehicles)) {
//         data.vehicles.forEach(vehicle => {
//             const li = document.createElement('li');
//             if (highlight && vehicle.toLowerCase().indexOf(highlight) >= 0) {
//                 const strong = document.createElement('strong');
//                 strong.innerText = vehicle;
//                 li.appendChild(strong);
//             } else {
//                 li.innerText = vehicle;
//             }
//             operationVehicleList.appendChild(li);
//         });
//     }
// }
// const formatLocation = (street, number, location, obj) => {
//     const streetWithNumber = number ? `${street} ${number}` : street;
//     if (streetWithNumber && location && obj) {
//         return `${obj}, ${streetWithNumber} ${location}`;
//     } else if (streetWithNumber && location && !obj) {
//         return `${streetWithNumber} ${location}`;
//     } else if (streetWithNumber && !location && obj) {
//         return `${obj} ${streetWithNumber}`;
//     } else if (!streetWithNumber && location && obj) {
//         return `${obj}, ${location}`;
//     } else if (streetWithNumber && !location && !obj) {
//         return streetWithNumber;
//     } else if (!streetWithNumber && location && !obj) {
//         return location;
//     } else if (!streetWithNumber && !location && obj) {
//         return obj;
//     }
//     return '';
// }

// const getOperationTypeClass = (keyword) => keywords.find(k => keyword.toLowerCase().startsWith(k)) || '';
const getOperationKeyword = (keyword) => {
    const keywordLowerCase = keyword.toLowerCase();
    if (keywordLowerCase.startsWith('b') || keywordLowerCase.startsWith('thl')) {
        const splitted = keywordLowerCase.split(" ");
        if (splitted.length > 1) {
            if (splitted[1].length < 3) {
                return splitted[0] + splitted[1];
            } else {
                return splitted[0];
            }
        }
    } else {
        const splitted = keywordLowerCase.split(" ");
        if (splitted.length > 0) {
            if (splitted[0].length <= 3) {
                return splitted[0];
            } else {
                return splitted[0].substring(0, 3);
            }
        }
    }
    return '';
};

// const initStompClient = (path) => new Promise((resolve) => {
//     const stompClient = Stomp.over(new SockJS(path))
//     stompClient.connect({}, () => resolve(stompClient))
// });
//
// const stompSubscribe = (stompClient, endpoint, callback) => {
//     stompClient.subscribe(endpoint, callback)
//     return stompClient
// }
//
// const stompClientSendMessage = (stompClient, endpoint) => {
//     stompClient.send(endpoint, {})
//     return stompClient
// }