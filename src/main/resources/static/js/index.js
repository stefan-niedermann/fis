let clock;
let operation;
let operationTopicKeyword;
let operationTopicTags;
let operationLocation;
let operationVehicleList;

let activeOperation = false;

let icon_mapping;
let keywords;

document.addEventListener('DOMContentLoaded', () => {
    clock = document.getElementById('info-clock');
    setInterval(updateClock, 5 * 1000);
    updateClock();
    Promise.all([
        fetch('/parameter').then(resp => resp.json()),
        fetch('/json/icon-mapping.json').then(resp => resp.json()),
        fetch('/json/operation-keywords.json').then(resp => resp.json())
    ])
        .then(([params, iconMapping, operationKeywords]) => {
            console.log('Parameter:', params);
            icon_mapping = iconMapping;
            keywords = operationKeywords;

            document.documentElement.setAttribute('lang', params.weather.lang);
            if (!params.operation.highlight) {
                document.documentElement.classList.add('no-highlight');
            }

            operation = document.getElementById('operation');
            operationTopicKeyword = document.getElementById('operation-topic-keyword');
            operationTopicTags = document.getElementById('operation-topic-tags');
            operationLocation = document.getElementById('operation-location');
            operationVehicleList = document.getElementById('operation-vehicles-list');

            const mainElement = document.querySelector('body>main');
            setInterval(() => requestNewWeatherInformation(params.weather.lang, params.weather.location, params.weather.units, params.weather.key), params.weather.pollInterval);
            requestNewWeatherInformation(params.weather.lang, params.weather.location, params.weather.units, params.weather.key);

            new Promise((resolve) => {
                let stompClient = Stomp.over(new SockJS('/socket'))
                stompClient.connect({}, () => resolve(stompClient))
            })
                .then((stompClient) => stompClientSendMessage(stompClient, '/register'))
                .then((stompClient) => stompSubscribe(stompClient, `/user/notification/operation`, (data) => {
                    console.log('received operation push notification: ', data.body);
                    // mainElement.classList.add('active-operation');
                    // setTimeout(() => {
                    //     console.debug('Timeout over… unset active operation.');
                    //     mainElement.classList.remove('active-operation');
                    //     resetOperationData();
                    // }, params.operation.duration);
                    // fillOperationData(JSON.parse(data.body).payload, (params.operation.highlight || '').toLowerCase());
                }))
                .then((stompClient) => stompSubscribe(stompClient, `/user/notification/weather`, (data) => {
                        console.log('received weather push notification: ', data.body);
                        // mainElement.classList.add('active-operation');
                        // setTimeout(() => {
                        //     console.debug('Timeout over… unset active operation.');
                        //     mainElement.classList.remove('active-operation');
                        //     resetOperationData();
                        // }, params.operation.duration);
                        // fillOperationData(JSON.parse(data.body).payload, (params.operation.highlight || '').toLowerCase());
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

const requestNewWeatherInformation = (lang, location, units, key) => {
    if (!activeOperation) {
        fetch(`https://api.openweathermap.org/data/2.5/weather?lang=${lang}&${isNaN(parseInt(location)) ? 'q' : 'id'}=${location}&units=${units}&appId=${key}`)
            .then(data => data.json())
            .then(data => {
                if (isDay(data.sys.sunrise, data.sys.sunset)) {
                    document.documentElement.classList.remove('dark-theme');
                } else {
                    document.documentElement.classList.add('dark-theme');
                }
                return data;
            })
            .then(data => {
                document.getElementById('info-weather-temperature').innerText = formatTemperature(data.main.temp);
                document.getElementById('info-weather-icon').setAttribute('src', mapOpenWeatherMapIconToImageUrl(data.weather[0].id, data.sys.sunrise, data.sys.sunset));
            });
    }
};
const formatTemperature = (temperature) => `${(Math.round(temperature * 100) / 100).toFixed((temperature > -10 && temperature < 10) ? 1 : 0)}°`;
const mapOpenWeatherMapIconToImageUrl = (openWeatherMapId, sunrise, sunset) => {
    return isDay(sunrise, sunset)
        ? `./icons/${icon_mapping[openWeatherMapId].day}.svg`
        : `./icons/${icon_mapping[openWeatherMapId].night}.svg`;
};
const isDay = (sunrise, sunset) => {
    const currentTime = new Date().getTime();
    return currentTime > sunrise * 1000 && currentTime < sunset * 1000;
};

const stompSubscribe = (stompClient, endpoint, callback) => {
    stompClient.subscribe(endpoint, callback)
    return stompClient
}

const stompClientSendMessage = (stompClient, endpoint) => {
    stompClient.send(endpoint, {})
    return stompClient
}