let clock;
let infoWeatherTemperature;
let infoWeatherIcon;
let operation;
let operationTopicKeyword;
let operationTopicTags;
let operationLocation;
let operationVehicleList;

let activeOperation = false;

let keywords;

document.addEventListener('DOMContentLoaded', () => {
    clock = document.getElementById('info-clock');
    setInterval(updateClock, 5 * 1000);
    updateClock();
    Promise.all([
        fetch('/parameter').then(resp => resp.json()),
        fetch('/json/operation-keywords.json').then(resp => resp.json())
    ])
        .then(([params, operationKeywords]) => {
            console.info('⚙️ Parameter:', params);
            keywords = operationKeywords;

            // TODO
            // document.documentElement.setAttribute('lang', params.weather.lang);
            if (!params.operation.highlight) {
                document.documentElement.classList.add('no-highlight');
            }

            infoWeatherTemperature = document.getElementById('info-weather-temperature');
            infoWeatherIcon = document.getElementById('info-weather-icon');
            operation = document.getElementById('operation');
            operationTopicKeyword = document.getElementById('operation-topic-keyword');
            operationTopicTags = document.getElementById('operation-topic-tags');
            operationLocation = document.getElementById('operation-location');
            operationVehicleList = document.getElementById('operation-vehicles-list');

            const mainElement = document.querySelector('body>main');

            new Promise((resolve) => {
                let stompClient = Stomp.over(new SockJS('/socket'))
                stompClient.connect({}, () => resolve(stompClient))
            })
                .then((stompClient) => stompClientSendMessage(stompClient, '/register'))
                .then((stompClient) => stompSubscribe(stompClient, `/user/notification/operation`, (data) => {
                    const operation = JSON.parse(data.body);
                    console.info('🚒️ New operation:', operation);
                    mainElement.classList.add('active-operation');
                    setTimeout(() => {
                        console.debug('Timeout over… unset active operation.');
                        mainElement.classList.remove('active-operation');
                        resetOperationData();
                    }, params.operation.duration);
                    fillOperationData(operation, (params.operation.highlight || '').toLowerCase());
                }))
                .then((stompClient) => stompSubscribe(stompClient, `/user/notification/weather`, (data) => {
                        const weather = JSON.parse(data.body);
                        console.info('⛅️ New weather:', weather);
                        if (!activeOperation) {
                            if (weather.isDay) {
                                document.documentElement.classList.remove('dark-theme');
                            } else {
                                document.documentElement.classList.add('dark-theme');
                            }
                        }
                        infoWeatherTemperature.innerText = formatTemperature(weather.temperature);
                        infoWeatherIcon.setAttribute('src', `./icons/${weather.icon}.svg`);
                    })
                );
        });
});

const updateClock = () => {
    const date = new Date();
    const hours = date.getHours();
    const minutes = date.getMinutes();
    clock.textContent = `${hours < 10 ? `0${hours}` : hours}:${minutes < 10 ? `0${minutes}` : minutes} Uhr`;
}

const resetOperationData = () => {
    operation.removeAttribute('class');
    operationTopicKeyword.innerHTML = '';
    operationTopicTags.innerHTML = '';
    operationLocation.innerHTML = '';
    operationVehicleList.innerHTML = '';
}

const fillOperationData = (data, highlight) => {
    resetOperationData();
    operation.classList.add(getOperationTypeClass(data.keyword));
    operationTopicKeyword.innerHTML = getOperationKeyword(data.keyword);
    operationLocation.innerHTML = `${data.street} ${data.number} ${data.location}`;
    if (Array.isArray(data.tags)) {
        data.tags.forEach(tag => {
            const li = document.createElement('li');
            li.innerText = tag;
            operationTopicTags.appendChild(li);
        });
    }

    if (Array.isArray(data.vehicles)) {
        data.vehicles.forEach(vehicle => {
            const li = document.createElement('li');
            if (highlight && vehicle.toLowerCase().indexOf(highlight) >= 0) {
                const strong = document.createElement('strong');
                strong.innerText = vehicle;
                li.appendChild(strong);
            } else {
                li.innerText = vehicle;
            }
            operationVehicleList.appendChild(li);
        });
    }
}

const getOperationTypeClass = (keyword) => keywords.find(k => keyword.toLowerCase().startsWith(k)) || '';
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

const formatTemperature = (temperature) => `${(Math.round(temperature * 100) / 100).toFixed((temperature > -10 && temperature < 10) ? 1 : 0)}°`;

const stompSubscribe = (stompClient, endpoint, callback) => {
    stompClient.subscribe(endpoint, callback)
    return stompClient
}

const stompClientSendMessage = (stompClient, endpoint) => {
    stompClient.send(endpoint, {})
    return stompClient
}