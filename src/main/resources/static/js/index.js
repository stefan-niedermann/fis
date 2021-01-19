let operation;
let operationTopicKeyword;
let operationTopicTags;
let operationLocation;
let operationVehicleList;

let activeOperation = false;

let icon_mapping;
let keywords;

document.addEventListener('DOMContentLoaded', () => {
    Promise.all([
        fetch('/weather').then(resp => resp.json()),
        fetch('/operation/duration').then(resp => resp.json()),
        fetch('/js/icon-mapping.json').then(resp => resp.json()),
        fetch('/js/operation-keywords.json').then(resp => resp.json())
    ])
        .then(([weatherInformation, operationDuration, iconMapping, operationKeywords]) => {
            icon_mapping = iconMapping;
            keywords = operationKeywords;

            document.documentElement.setAttribute('lang', weatherInformation.lang);

            operation = document.getElementById('operation');
            operationTopicKeyword = document.getElementById('operation-topic-keyword');
            operationTopicTags = document.getElementById('operation-topic-tags');
            operationLocation = document.getElementById('operation-location');
            operationVehicleList = document.getElementById('operation-vehicles-list');

            const mainElement = document.querySelector('body>main');
            setInterval(() => requestNewWeatherInformation(weatherInformation.lang, weatherInformation.location, weatherInformation.units, weatherInformation.key), weatherInformation.pollInterval);
            requestNewWeatherInformation(weatherInformation.lang, weatherInformation.location, weatherInformation.units, weatherInformation.key);

            const username = 'dashboard-ui';
            new Promise((resolve) => {
                let stompClient = Stomp.over(new SockJS('/socket'))
                stompClient.connect({}, () => resolve(stompClient))
            })
                .then((stompClient) => stompClientSendMessage(stompClient, '/register', username))
                .then((stompClient) => stompSubscribe(stompClient, `/user/${username}/operation`, (data) => {
                    mainElement.classList.add('active-operation');
                    setTimeout(() => {
                        mainElement.classList.remove('active-operation');
                        resetOperationData();
                    }, operationDuration);
                    fillOperationData(JSON.parse(data.body).payload);
                }));
        });
});

const resetOperationData = () => {
    operation.removeAttribute('class');
    operationTopicKeyword.innerHTML = '';
    operationTopicTags.innerHTML = '';
    operationLocation.innerHTML = '';
    operationVehicleList.innerHTML = '';
}

const fillOperationData = (data) => {
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
            li.innerText = vehicle;
            operationVehicleList.appendChild(li);
        });
    }
}

const getOperationTypeClass = (keyword) => keywords.find(k => keyword.toLowerCase().startsWith(k)) || '';
const getOperationKeyword = (keyword) => {
    const keywordLowerCase = keyword.toLowerCase();
    if(keywordLowerCase.startsWith('b') || keywordLowerCase.startsWith('thl')) {
        const splitted = keywordLowerCase.split(" ");
        if(splitted.length > 1) {
            if(splitted[1].length < 3) {
                return splitted[0] + splitted[1];
            } else {
                return splitted[0];
            }
        }
    } else {
        const splitted = keywordLowerCase.split(" ");
        if(splitted.length > 0) {
            if(splitted[0].length <= 3) {
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
                document.getElementById('weather-temperature').innerText = formatTemperature(data.main.temp);
                document.getElementById('weather-icon').setAttribute('src', mapOpenWeatherMapIconToImageUrl(data.weather[0].id, data.sys.sunrise, data.sys.sunset));
            });
    }
};
const formatTemperature = (temperature) => `${(Math.round(temperature * 100) / 100).toFixed(1)}°`;
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

const stompClientSendMessage = (stompClient, endpoint, message) => {
    stompClient.send(endpoint, {}, message)
    return stompClient
}