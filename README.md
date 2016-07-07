<h2> SunShine App </h2>

<p> This app is made as a part of <a href="https://www.udacity.com/course/developing-android-apps--ud853"><b><em>Udacity</em></b></a> Android Development Course. <br><br><b>SunShine</b> app will be a reflection of all the android development I have learnt from the course.</p>

<p> This app makes use of OpenWeatherMap Web API to fetch the weather data for a given ZipCode which can be mentioned in the user settings. In turn this weather forecast data can be shared with multiple compatible apps on the Phone. </p>

<p> To use this application. You will need your own API KEY.<br> This can be obtained from <a href="http://openweathermap.org/appid" target="_blank">OpenWeatherMap</a>. After clicking the link, create an account(if you don't have one) and follow the necessary steps provided on the website to obtain the API Key.<br></p>

<p> Once you obtain the API Key, go to build.gradle file in the 'app' folder of this repository. And place your API Key in the section that looks as follows <br><br><em>buildTypes.each</em><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;it.buildConfigField&nbsp;'String',&nbsp;'OPEN_WEATHER_MAP_API_KEY',&nbsp;"\"1234567890123456789012\""<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br><br>In Place of "OPEN_WEATHER_MAP_API_KEY" insert your API Key and run the App.</p>
