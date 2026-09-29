package com.example.hormozgansmart.engine

import android.util.Log
import com.example.hormozgansmart.BuildConfig
import com.example.hormozgansmart.data.repository.BandariDictionaryRepository
import com.example.hormozgansmart.data.repository.KnowledgeRepository
import com.example.hormozgansmart.data.repository.PoiRepository
import com.example.hormozgansmart.data.repository.WeatherTrafficRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class GeminiCopilotService {

    companion object {
        private const val TAG = "GeminiCopilot"
        private const val GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"
    }

    suspend fun getResponse(userMessage: String, useBandariDialect: Boolean = false): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY.trim()

        if (apiKey.isNotEmpty()) {
            try {
                val apiResponse = callGeminiApi(apiKey, userMessage, useBandariDialect)
                if (apiResponse.isNotBlank()) {
                    return@withContext apiResponse
                }
            } catch (e: Exception) {
                Log.w(TAG, "Gemini API failed, falling back to offline hybrid engine", e)
            }
        }

        // Offline / Fallback Intelligent Engine
        return@withContext generateLocalResponse(userMessage, useBandariDialect)
    }

    private fun callGeminiApi(apiKey: String, message: String, useBandari: Boolean): String {
        val systemInstruction = """
            شما «دستیار هوشمند هرمزگان» (Hormozgan Intelligent Copilot) هستید؛ یک دستیار متخصص در تاریخ، فرهنگ، گردشگری، گویش بندری، ترابری دریایی و اطلاعات شهری بندرعباس و استان هرمزگان.
            ${if (useBandari) "پاسخ را با لهجه گرم و صمیمی بندری همراه با کلمات اصیل هرمزگانی بیان کنید." else "پاسخ را به فارسی روان و محترمانه همراه با ذکر اصطلاحات محلی در صورت لزوم بیان کنید."}
            اطلاعات امدادی، اماکن دیدنی، شرایط اسکله‌ها و ترابری را دقیق و کامل ارائه دهید.
        """.trimIndent()

        val url = URL("$GEMINI_API_URL?key=$apiKey")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.doOutput = true
        conn.connectTimeout = 8000
        conn.readTimeout = 12000

        val payload = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", "$systemInstruction\n\nسوال کاربر: $message")
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.7)
                put("maxOutputTokens", 1024)
            })
        }

        OutputStreamWriter(conn.outputStream).use { it.write(payload.toString()) }

        if (conn.responseCode == 200) {
            val reader = BufferedReader(InputStreamReader(conn.inputStream))
            val response = reader.readText()
            reader.close()
            val json = JSONObject(response)
            val candidates = json.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val candidate = candidates.getJSONObject(0)
                val content = candidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    return parts.getJSONObject(0).optString("text", "")
                }
            }
        }
        return ""
    }

    private fun generateLocalResponse(query: String, useBandari: Boolean): String {
        val q = query.trim().lowercase()

        // 1. Check emergency keywords
        if (q.contains("پلیس") || q.contains("اورژانس") || q.contains("آتش") || q.contains("امداد") || q.contains("بیمارستان") || q.contains("ضروری")) {
            val em = KnowledgeRepository.items.filter { it.category == "emergency" }
            val sb = StringBuilder()
            if (useBandari) {
                sb.append("سَلام رَفیق! شُماره‌های اضطراری هُرمُزگان دِز خِدمَتِت:\n\n")
            } else {
                sb.append("شماره‌های فوریتی و امدادی استان هرمزگان و بندرعباس:\n\n")
            }
            em.forEach { item ->
                sb.append("• ${item.title}: ${item.content}\n\n")
            }
            sb.append("• شماره امداد و نجات دریایی (تنگه هرمز): ۱۵۵۰\n")
            sb.append("• شماره فوریت‌های پلیس: ۱۱۰ | اورژانس: ۱۱۵ | آتش‌نشانی: ۱۲۵")
            return sb.toString()
        }

        // 2. Check weather / maritime ferry conditions
        if (q.contains("هوا") || q.contains("شرجی") || q.contains("دریا") || q.contains("شناور") || q.contains("قشم") || q.contains("هرمز") || q.contains("اسکله")) {
            val bnd = WeatherTrafficRepository.coastalStations.find { it.city == "بندرعباس" }
            val qeshm = WeatherTrafficRepository.coastalStations.find { it.city == "جزیره قشم" }
            val hormuz = WeatherTrafficRepository.coastalStations.find { it.city == "جزیره هرمز" }

            if (useBandari) {
                return """
                    سَلام خَری! وضعیت دَریا و هَوای اَمرُز:
                    🌊 دریا: ${bnd?.seaStatus ?: "آرام"}
                    🌡️ دمای بندرعباس: ${bnd?.tempC ?: 31} درجه با شرجی ${bnd?.humidityPercent ?: 74}٪
                    🚢 تردد شناورها در اسکله حقانی: ${if (bnd?.ferryOpen == true) "روان و بدون مشکل (باز)" else "به دلیل امواج متوقف"}
                    جزیره قشم: ${qeshm?.tempC} درجه | جزیره هرمز: ${hormuz?.tempC} درجه
                    سفرت خاش و سلامت باشِه رفیق!
                """.trimIndent()
            } else {
                return """
                    وضعیت جوی و تردد دریایی خلیج فارس:
                    • بندرعباس: ${bnd?.tempC}°C با رطوبت (شرجی) ${bnd?.humidityPercent}%
                    • وضعیت دریا: ${bnd?.seaStatus} | سرعت باد: ${bnd?.windSpeedKm} کیلومتر بر ساعت
                    • اسکله مسافربری شهید حقانی: ${if (bnd?.ferryOpen == true) "تردد شناورهای تندرو به قشم و هرمز فعال و برقرار است." else "به دلیل شرایط جوی موقتاً متوقف است."}
                    • جزیره قشم: ${qeshm?.conditionText} (${qeshm?.tempC}°C)
                    • جزیره هرمز: ${hormuz?.conditionText} (${hormuz?.tempC}°C)
                """.trimIndent()
            }
        }

        // 3. Check Dialect translation / words
        if (q.contains("معنی") || q.contains("ترجمه") || q.contains("گویش") || q.contains("اصطلاح") || q.contains("کلمه") || q.contains("بندری")) {
            val translation = BandariDialectEngine.translate(query)
            if (translation.detectedWords.isNotEmpty()) {
                val sb = StringBuilder()
                sb.append("بررسی زبانی در گویش بندری:\n\n")
                sb.append("ترجمه/برابر: «${translation.translatedText}»\n")
                sb.append(translation.explanation)
                return sb.toString()
            }
        }

        // 4. Check Knowledge Repository search
        val foundKb = KnowledgeRepository.searchKnowledge(query)
        if (foundKb.isNotEmpty()) {
            val top = foundKb.first()
            return """
                اطلاعات ثبت‌شده در پایگاه دانش هرمزگان:
                📌 ${top.title}
                دسته‌بندی: ${top.categoryFa} (${top.city})
                
                ${top.content}
            """.trimIndent()
        }

        // 5. Check POI repository
        val foundPoi = PoiRepository.pois.find { it.name.contains(query, ignoreCase = true) || it.categoryFa.contains(query, ignoreCase = true) }
        if (foundPoi != null) {
            return """
                اطلاعات مکان:
                📍 ${foundPoi.name} (${foundPoi.categoryFa})
                نشانی: ${foundPoi.address}
                تلفن تماس: ${foundPoi.phone}
                امتیاز: ⭐ ${foundPoi.rating}
                
                توضیحات: ${foundPoi.description}
            """.trimIndent()
        }

        // Default intelligent fallback
        if (useBandari) {
            return """
                سَلام خَری! مه دستیار هوشمند هُرمُزگانُم.
                می‌تونی درباره اماکن تاریخی و گردشگری بندرعباس، اسکله حقانی و شناورهای قشم، کلمات و اصطلاحات گویش بندری، یا ترافیک و شماره‌های امدادی از مه سوال بپرسی تا راهنماییت بُکُنُم!
            """.trimIndent()
        } else {
            return """
                درود! من دستیار هوشمند هرمزگان (Hermezgan Intelligent) هستم.
                می‌توانید درباره جاذبه‌های گردشگری (معبد هندوها، حمام گله‌داری، ساحل سورو، گنو)، وضعیت شناورها در اسکله شهید حقانی، فرهنگ لغت و گویش بندری، بیمارستان‌ها و وضعیت ترافیک بندرعباس سوال بفرمایید.
            """.trimIndent()
        }
    }
}
