package com.example.hormozgansmart.data.repository

import com.example.hormozgansmart.data.model.PointOfInterest
import com.example.hormozgansmart.data.model.TrafficCamera
import com.example.hormozgansmart.data.model.TrafficHotspot

object PoiRepository {

    val pois: List<PointOfInterest> = listOf(
        // Hospitals
        PointOfInterest(
            id = "poi_hosp_1",
            name = "بیمارستان شهید محمدی بندرعباس",
            category = "hospital",
            categoryFa = "درمان و بیمارستان",
            latitude = 27.2000,
            longitude = 56.2900,
            address = "بندرعباس، بلوار جمهوری اسلامی، مجتمع بیمارستانی شهید محمدی",
            phone = "076-33347000",
            description = "بزرگ‌ترین بیمارستان آموزشی و درمانی تخصصی و فوق تخصصی استان هرمزگان و مرکز ترومای اصلی جنوب.",
            rating = 4.7,
            isFeatured = true
        ),
        PointOfInterest(
            id = "poi_hosp_2",
            name = "بیمارستان تخصصی خلیج فارس",
            category = "hospital",
            categoryFa = "درمان و بیمارستان",
            latitude = 27.1890,
            longitude = 56.3200,
            address = "بندرعباس، بلوار امام خمینی، انتهای آزادگان",
            phone = "076-33668000",
            description = "بیمارستان تخصصی تامین اجتماعی مجهز به بخش‌های جراحی قلب، آنژیوگرافی و فوریت‌های اورژانس.",
            rating = 4.6
        ),
        PointOfInterest(
            id = "poi_hosp_3",
            name = "بیمارستان کودکان بندرعباس",
            category = "hospital",
            categoryFa = "درمان و بیمارستان",
            latitude = 27.1980,
            longitude = 56.2820,
            address = "بندرعباس، بلوار طالقانی، روبروی پارک دولت",
            phone = "076-33334000",
            description = "مرکز تخصصی و فوق تخصصی اطفال و نوزادان با بخش‌های NICU و PICU.",
            rating = 4.5
        ),

        // Marine Piers & Transport
        PointOfInterest(
            id = "poi_pier_1",
            name = "اسکله مسافربری شهید حقانی",
            category = "pier",
            categoryFa = "اسکله‌ها و ترابری",
            latitude = 27.1800,
            longitude = 56.2750,
            address = "بندرعباس، بلوار ساحلی، میدان ولایت، پایانه شهید حقانی",
            phone = "076-31222",
            description = "مهم‌ترین پایانه مسافری دریایی کشور با شناورهای پیشرفته به جزایر قشم، هرمز و هنگام.",
            rating = 4.9,
            isFeatured = true
        ),
        PointOfInterest(
            id = "poi_pier_2",
            name = "مجتمع بندری شهید رجایی",
            category = "pier",
            categoryFa = "اسکله‌ها و ترابری",
            latitude = 27.1167,
            longitude = 56.2333,
            address = "۲۳ کیلومتری غرب بندرعباس، بزرگراه شهید رجایی",
            phone = "076-33514000",
            description = "بزرگ‌ترین بندر تجاری و کانتینری ایران با اتصال مستقیم به شبکه راه‌آهن سراسری.",
            rating = 4.8
        ),
        PointOfInterest(
            id = "poi_airport",
            name = "فرودگاه بین‌المللی شهدای پرواز ۶۵۵ بندرعباس",
            category = "transport",
            categoryFa = "اسکله‌ها و ترابری",
            latitude = 27.2183,
            longitude = 56.3778,
            address = "بندرعباس، بلوار ۱۵ خرداد، چهارراه فرودگاه",
            phone = "199",
            description = "فرودگاه بین‌المللی با پروازهای منظم به تمام نقاط کشور و پروازهای بین‌المللی حاشیه خلیج فارس.",
            rating = 4.6
        ),
        PointOfInterest(
            id = "poi_railway",
            name = "ایستگاه راه‌آهن بندرعباس",
            category = "transport",
            categoryFa = "اسکله‌ها و ترابری",
            latitude = 27.2100,
            longitude = 56.3200,
            address = "بندرعباس، شمال بلوار راه‌آهن",
            phone = "076-32115144",
            description = "مهم‌ترین ایستگاه قطار ترانزیتی جنوب کشور با قطارهای مسافری لوکس به تهران، مشهد و یزد.",
            rating = 4.4
        ),

        // Tourism & History
        PointOfInterest(
            id = "poi_tour_1",
            name = "معبد هندوها (بت‌گوران)",
            category = "tourism",
            categoryFa = "گردشگری و تاریخی",
            latitude = 27.1980,
            longitude = 56.2900,
            address = "بندرعباس، خیابان امام خمینی، روبروی بازار روز",
            phone = "076-32223000",
            description = "اثر تاریخی بی‌نظیر دوره قاجار با گنبد مخروطی شبیه معابد هندوستان در قلب شهر.",
            rating = 4.8,
            isFeatured = true
        ),
        PointOfInterest(
            id = "poi_tour_2",
            name = "حمام تاریخی و موزه گله‌داری",
            category = "tourism",
            categoryFa = "گردشگری و تاریخی",
            latitude = 27.1850,
            longitude = 56.2800,
            address = "بندرعباس، خیابان دلگشا، محله اوزی‌ها",
            phone = "076-32240000",
            description = "موزه مردم‌شناسی هرمزگان و حمام سنتی ساخته‌شده از سنگ‌های مرجانی دریایی.",
            rating = 4.7
        ),
        PointOfInterest(
            id = "poi_tour_3",
            name = "ساحل و نخلستان‌های باستانی سورو",
            category = "tourism",
            categoryFa = "گردشگری و تاریخی",
            latitude = 27.1700,
            longitude = 56.2500,
            address = "بندرعباس، انتهای غربی بلوار ساحلی، محله سورو",
            phone = "-",
            description = "ساحل شنی آرام و غروب‌های رؤیایی با کارگاه‌های لنج‌سازی سنتی چوبی.",
            rating = 4.9,
            isFeatured = true
        ),
        PointOfInterest(
            id = "poi_tour_4",
            name = "آبگرم و قله حفاظت‌شده گنو",
            category = "tourism",
            categoryFa = "گردشگری و تاریخی",
            latitude = 27.4000,
            longitude = 56.3000,
            address = "۳۵ کیلومتری شمال بندرعباس، جاده حاجی‌آباد",
            phone = "076-33338900",
            description = "استخرهای آبگرم گوگردی با خواص درمانی و منطقه کوهستانی ییلاقی خنک.",
            rating = 4.7
        ),

        // Shopping & Bazaars
        PointOfInterest(
            id = "poi_shop_1",
            name = "بازار سنتی و قدیم بندرعباس",
            category = "shopping",
            categoryFa = "مراکز خرید و بازار",
            latitude = 27.1950,
            longitude = 56.2850,
            address = "بندرعباس، بلوار طالقانی، محله بازار قدیم",
            phone = "-",
            description = "مرکز خرید ادویه‌جات اصل جنوب، صنایع دستی حصیری، خرما و پارچه‌های هندی و سنتی.",
            rating = 4.7,
            isFeatured = true
        ),
        PointOfInterest(
            id = "poi_shop_2",
            name = "سیتی سنتر بندرعباس",
            category = "shopping",
            categoryFa = "مراکز خرید و بازار",
            latitude = 27.1900,
            longitude = 56.2800,
            address = "بندرعباس، میدان ۱۷ شهریور، بلوار امام خمینی",
            phone = "076-32247000",
            description = "مرکز خرید مدرن چند طبقه شامل پوشاک، لوازم خانگی، فودکورت و شهربازی سرپوشیده.",
            rating = 4.5
        ),
        PointOfInterest(
            id = "poi_shop_3",
            name = "مرکز تجاری بندرعباس مال",
            category = "shopping",
            categoryFa = "مراکز خرید و بازار",
            latitude = 27.1850,
            longitude = 56.2750,
            address = "بندرعباس، خیابان رسالت جنوبی",
            phone = "076-33670000",
            description = "مجتمع تجاری، تفریحی و لوکس با پارکینگ اختصاصی و برندهای معتبر بین‌المللی.",
            rating = 4.6
        ),

        // Hotels
        PointOfInterest(
            id = "poi_hotel_1",
            name = "هتل بین‌المللی هما بندرعباس",
            category = "hotel",
            categoryFa = "هتل و اقامتگاه",
            latitude = 27.1900,
            longitude = 56.2850,
            address = "بندرعباس، بلوار پاسداران، خیابان هما",
            phone = "076-33512000",
            description = "هتل ۵ ستاره ساحلی با چشم‌انداز باز به خلیج فارس، استخر روباز و رستوران دریایی.",
            rating = 4.8
        ),
        PointOfInterest(
            id = "poi_hotel_2",
            name = "هتل بزرگ هرمز",
            category = "hotel",
            categoryFa = "هتل و اقامتگاه",
            latitude = 27.1950,
            longitude = 56.2900,
            address = "بندرعباس، میدان انقلاب، بلوار امام خمینی",
            phone = "076-33682000",
            description = "هتل ۵ ستاره با معماری مجلل و امکانات کامل اقامتی در مرکز تجاری شهر.",
            rating = 4.7
        ),

        // Parks & Beach
        PointOfInterest(
            id = "poi_park_1",
            name = "پارک ساحلی غدیر و پلاژ بانوان",
            category = "park",
            categoryFa = "پارک و ساحل",
            latitude = 27.1800,
            longitude = 56.2700,
            address = "بندرعباس، بلوار ساحلی غدیر",
            phone = "-",
            description = "پارک ساحلی مجهز به مسیر پیاده‌روی، دوچرخه‌سواری، آلاچیق و ورزش‌های آبی.",
            rating = 4.6
        ),
        PointOfInterest(
            id = "poi_park_2",
            name = "پارک ساحلی دولت",
            category = "park",
            categoryFa = "پارک و ساحل",
            latitude = 27.1820,
            longitude = 56.3100,
            address = "بندرعباس، انتهای شرقی بلوار ساحلی",
            phone = "-",
            description = "فضای سبز ساحلی گسترده، پیست کارتینگ و محل گردهمایی خانواده‌ها.",
            rating = 4.5
        )
    )

    val cameras: List<TrafficCamera> = listOf(
        TrafficCamera("cam_1", "دوربین چهارراه شهدا", "تقاطع بلوار امام و شهدا", "فعال - پایش روان", 60, false),
        TrafficCamera("cam_2", "دوربین بلوار ساحلی امام سجاد (ع)", "روبروی پارک غدیر", "فعال - کنترل سرعت", 70, false),
        TrafficCamera("cam_3", "دوربین میدان ولیعصر (عج)", "ورودی پل ولیعصر", "فعال - ترافیک نیمه سنگین", 50, true),
        TrafficCamera("cam_4", "دوربین ورودی مجتمع شهید رجایی", "کیلومتر ۱۲ بزرگراه اسکله", "فعال - تردد تریلر و کامیون", 80, true),
        TrafficCamera("cam_5", "دوربین تقاطع رسالت و مصطفی خمینی", "چهارراه رسالت", "فعال - پایش هوشمند", 60, false),
        TrafficCamera("cam_6", "دوربین کمربندی میناب - بندرعباس", "میدان دفاع مقدس", "فعال - سرعت سنج راداری", 80, false)
    )

    val hotspots: List<TrafficHotspot> = listOf(
        TrafficHotspot(
            id = "hs_1",
            name = "سه راهی جهانبار",
            riskLevel = "بالا",
            description = "تقاطع پرتردد شهری و محل اتصال ترافیک محلی با خودروهای عبوری به سمت اسکله‌ها.",
            safetyTips = "رعایت کامل حق تقدم، پرهیز از تغییر لاین ناگهانی و توجه به چراغ راهنما در ساعات عصر."
        ),
        TrafficHotspot(
            id = "hs_2",
            name = "محور بندرعباس - سیرجان (گردنه حاجی‌آباد)",
            riskLevel = "خیلی بالا",
            description = "شریان اصلی ترانزیت کالا به مرکز کشور با شیب تند و تردد بسیار زیاد تریلرها.",
            safetyTips = "بررسی سلامت ترمز خودرو قبل از حرکت، رعایت فاصله طولی و خودداری مطلق از سبقت در پیچ‌ها."
        ),
        TrafficHotspot(
            id = "hs_3",
            name = "ورودی بزرگراه شهید رجایی",
            riskLevel = "بالا",
            description = "ورود و خروج کانتینربرها و خودروهای سنگین باربری.",
            safetyTips = "رانندگی با سرعت مطمئنه، دوری از نقاط کور کامیون‌ها و استفاده مداوم از راهنما."
        ),
        TrafficHotspot(
            id = "hs_4",
            name = "بلوار ساحلی محدوده بازار روز در ساعات شب",
            riskLevel = "متوسط",
            description = "تردد عرضی بالای عابران پیاده و توقف‌های دوبله خودروها.",
            safetyTips = "حداکثر سرعت ۴۰ کیلومتر و توجه ویژه به گذرگاه‌های عابر پیاده."
        )
    )
}
