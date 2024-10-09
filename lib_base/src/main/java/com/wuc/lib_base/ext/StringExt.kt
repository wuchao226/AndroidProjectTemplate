package com.wuc.lib_base.ext

import android.graphics.Color
import android.text.format.Formatter
import androidx.core.util.PatternsCompat
import org.json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.UUID
import java.util.regex.Pattern

/**
 * @author: wuc
 * @date: 2024/10/9
 * @desc:
 */
/**
 * Regex of exact phone number. Update at 2021.05.13.
 * - China mobile: 134,135,136,137,138,139,147,148,150,151,152,157,158,159,172,178,182,183,184,187,188,195,198
 * - China unicom: 130,131,132,145,146,155,156,166,175,176,185,186,196
 * - China telecom: 133,149,153,173,174,177,180,181,189,191,193,199
 * - China nrta: 190,192,197
 * - China mobile virtual: 165,1703,1705,1706
 * - China unicom virtual: 167,1704,1707,1708,1709,171
 * - China telecom virtual: 162,1700,1701,1702
 */
const val REGEX_PHONE_EXACT: String =
    "^1(3\\d|4[5-9]|5[0-35-9]|6[2567]|7[0-8]|8\\d|9[0-35-9])\\d{8}$"

const val REGEX_ID_CARD_15: String =
    "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$"

const val REGEX_ID_CARD_18: String =
    "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9Xx])$"

/**
 * 获取随机 UUID 字符串
 */
inline val randomUUIDString: String
    get() = UUID.randomUUID().toString()

/**
 * 数字转文件大小字符串
 */
fun Long.toFileSizeString(): String =
    Formatter.formatFileSize(application, this)

/**
 * 数字转精度位数较少的文件大小字符串
 */
fun Long.toShortFileSizeString(): String =
    Formatter.formatShortFileSize(application, this)

fun String.parseColor(): Int =
    Color.parseColor(this)

/**
 * 限制字符长度
 */
fun String.limitLength(length: Int): String =
    if (this.length <= length) this else substring(0, length)

/**
 * 判断是否是手机号
 */
fun String.isPhone(): Boolean =
    REGEX_PHONE_EXACT.toRegex().matches(this)

/**
 * 判断是否是域名
 */
fun String.isDomainName(): Boolean =
    PatternsCompat.DOMAIN_NAME.matcher(this).matches()

/**
 * 判断是否是邮箱
 */
fun String.isEmail(): Boolean =
    PatternsCompat.EMAIL_ADDRESS.matcher(this).matches()

/**
 * 判断是否是 IP 地址
 */
fun String.isIP(): Boolean =
    PatternsCompat.IP_ADDRESS.matcher(this).matches()

/**
 *  Regular expression pattern to match most part of RFC 3987
 *  Internationalized URLs, aka IRIs.
 *  判断是否是网址
 */
fun String.isWebUrl(): Boolean =
    PatternsCompat.WEB_URL.matcher(this).matches()

/**
 * 判断是否是 15 位身份证号码
 */
fun String.isIDCard15(): Boolean =
    REGEX_ID_CARD_15.toRegex().matches(this)

/**
 * 判断是否是 18 位身份证号码
 */
fun String.isIDCard18(): Boolean =
    REGEX_ID_CARD_18.toRegex().matches(this)

/**
 * 判断是否是 Json 字符串
 */
fun String.isJson(): Boolean =
    try {
        JSONObject(this)
        true
    } catch (e: Exception) {
        false
    }

/**
 * 小数转为字符串，默认保留小数点后两位
 * Float/Double.toNumberString(...)
 */
fun Float.toNumberString(
    fractionDigits: Int = 2,
    minIntDigits: Int = 1,
    isGrouping: Boolean = false,
    isHalfUp: Boolean = true
): String =
    toDouble().toNumberString(fractionDigits, minIntDigits, isGrouping, isHalfUp)

fun Double.toNumberString(
    fractionDigits: Int = 2,
    minIntDigits: Int = 1,
    isGrouping: Boolean = false,
    isHalfUp: Boolean = true
): String =
    (NumberFormat.getInstance() as DecimalFormat).apply {
        isGroupingUsed = isGrouping
        roundingMode = if (isHalfUp) RoundingMode.HALF_UP else RoundingMode.DOWN
        minimumIntegerDigits = minIntDigits
        minimumFractionDigits = fractionDigits
        maximumFractionDigits = fractionDigits
    }.format(this)

/**
 * 字符串逗号分隔为集合
 * 截取，逗号转集合，集合转逗号
 */
fun String.toCommaList(): List<String> {
    if (this.contains(",")) {
        return this.split(",").map { it.trim() }
    }
    return listOf()
}

/**
 * 集合转为逗号分隔
 */
fun Collection<String>.toCommaStr(): String {
    return this.let {
        val builder = StringBuilder()
        it.forEach {
            builder.append("$it,")
        }
        var str = builder.toString().trim()
        if (str.endsWith(",")) {
            str = str.substring(0, str.length - 1)
        }
        str
    }
}
/**
 * 是否是中文字符
 */
fun String?.isChinese() = "^[\u4E00-\u9FA5]+$".toRegex().matches(this!!)

/**
 * 判断字符串是否是数字类型
 */
fun CharSequence.checkNumberPoint(): Boolean {
    return if (this.isEmpty()) {
        false
    } else {
        val pattern = Pattern.compile("-?[0-9]*.?[0-9]*")
        val matcher = pattern.matcher(this)
        return matcher.matches()
    }
}

/**
 * 格式化数据,如果没有值默认显示0
 */
fun String.formatMoney(pointLength: Int = 0): String {
    if (checkNumberPoint()) {
        val formatStr = when (pointLength) {
            0 -> "###,###,##0.0#"  //最多2位长度，最少1位长度
            1 -> "###,###,##0.0"   //必定1位长度
            2 -> "###,###,##0.00"  //必定2位长度
            else -> {
                "###,###,##0.##"   //最多2位长度，最少0位长度
            }
        }
        val df = DecimalFormat(formatStr)
        return df.format(this.toDouble())
    }
    return this
}

//数字转百分比 ，0.50->50%
inline val String.formatWithPercent: String
    get() = run {
        return runCatching {
            BigDecimal(this).multiply(BigDecimal("100")).stripTrailingZeros().toPlainString() + "%"
        }.getOrDefault(this)
    }

//数字格式化，100000->100,000
inline val String.formatWithComma: String
    get() = run {
        return runCatching {
            val df = DecimalFormat()
            df.applyPattern(",###.########")
            df.format(toDouble())
        }.getOrDefault(this)
    }