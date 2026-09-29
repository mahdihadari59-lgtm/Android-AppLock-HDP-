package com.example.hormozgansmart.data.repository

import com.example.hormozgansmart.data.model.CoastalWeather

object WeatherTrafficRepository {

    val coastalStations: List<CoastalWeather> = listOf(
        CoastalWeather(
            city = "بندرعباس",
            tempC = 31,
            humidityPercent = 74,
            windSpeedKm = 14,
            seaStatus = "آرام تا ملایم",
            ferryOpen = true,
            conditionText = "آفتابی همراه با غبار محلی و شرجی مطبوع ساحلی"
        ),
        CoastalWeather(
            city = "جزیره قشم",
            tempC = 30,
            humidityPercent = 78,
            windSpeedKm = 16,
            seaStatus = "آرام",
            ferryOpen = true,
            conditionText = "صاف تا کمی ابری، تردد لنج‌ها و شناورها روان"
        ),
        CoastalWeather(
            city = "جزیره هرمز",
            tempC = 29,
            humidityPercent = 72,
            windSpeedKm = 12,
            seaStatus = "آرام",
            ferryOpen = true,
            conditionText = "آفتابی، شرایط دید افقی و دریانوردی عالی"
        ),
        CoastalWeather(
            city = "جزیره کیش",
            tempC = 32,
            humidityPercent = 68,
            windSpeedKm = 18,
            seaStatus = "نیمه مواج",
            ferryOpen = true,
            conditionText = "صاف همراه با باد ملایم دریایی"
        ),
        CoastalWeather(
            city = "بندر لنگه",
            tempC = 30,
            humidityPercent = 70,
            windSpeedKm = 15,
            seaStatus = "آرام",
            ferryOpen = true,
            conditionText = "آفتابی ساحلی با نسیم دریا به خشکی"
        ),
        CoastalWeather(
            city = "میناب",
            tempC = 33,
            humidityPercent = 55,
            windSpeedKm = 10,
            seaStatus = "خشکی (بدون دریا)",
            ferryOpen = true,
            conditionText = "گرم و خشک با نسیم نخلستان"
        ),
        CoastalWeather(
            city = "بندر جاسک",
            tempC = 29,
            humidityPercent = 76,
            windSpeedKm = 22,
            seaStatus = "مواج دریای عمان",
            ferryOpen = false,
            conditionText = "دریای عمان مواج، احتیاط برای قایق‌های سبک"
        )
    )
}
