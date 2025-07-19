package com.thejawnpaul.gptinvestor.core.database

import androidx.room.TypeConverter
import com.thejawnpaul.gptinvestor.core.utility.json
import com.thejawnpaul.gptinvestor.features.company.data.local.model.PriceChange
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteResponse
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyNews
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.HistoricalData
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.NewsResolution
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.NewsThumbNail

class Converters {

    @TypeConverter
    fun stringToPriceChange(string: String): PriceChange? {
        return json.decodeFromString<PriceChange>(string)
    }

    @TypeConverter
    fun priceChangeToString(priceChange: PriceChange): String {
        return json.encodeToString(priceChange)
    }

    // CompanyDetailRemoteResponse converters
    @TypeConverter
    fun fromCompanyDetail(value: CompanyDetailRemoteResponse?): String? {
        if (value == null) return null
        return json.encodeToString(value)
    }

    @TypeConverter
    fun toCompanyDetail(value: String?): CompanyDetailRemoteResponse? {
        if (value == null) return null
        return json.decodeFromString<CompanyDetailRemoteResponse>(value)
    }

    // List<CompanyNews> converters
    @TypeConverter
    fun fromNewsList(news: List<CompanyNews>?): String? {
        if (news == null) return null
        return json.encodeToString(news)
    }

    @TypeConverter
    fun toNewsList(value: String?): List<CompanyNews>? {
        if (value == null) return null
        return json.decodeFromString<List<CompanyNews>>(value)
    }

    // List<HistoricalData> converters
    @TypeConverter
    fun fromHistoricalDataList(data: List<HistoricalData>?): String? {
        if (data == null) return null
        return json.encodeToString(data)
    }

    @TypeConverter
    fun toHistoricalDataList(value: String?): List<HistoricalData>? {
        if (value == null) return null
        return json.decodeFromString<List<HistoricalData>>(value)
    }

    // NewsThumbNail converters
    @TypeConverter
    fun fromNewsThumbNail(thumbnail: NewsThumbNail?): String? {
        if (thumbnail == null) return null
        return json.encodeToString(thumbnail)
    }

    @TypeConverter
    fun toNewsThumbNail(value: String?): NewsThumbNail? {
        if (value == null) return null
        return json.decodeFromString<NewsThumbNail>(value)
    }

    // List<NewsResolution> converters
    @TypeConverter
    fun fromResolutionList(resolutions: List<NewsResolution>?): String? {
        if (resolutions == null) return null
        return json.encodeToString(resolutions)
    }

    @TypeConverter
    fun toResolutionList(value: String?): List<NewsResolution>? {
        if (value == null) return null
        return json.decodeFromString<List<NewsResolution>>(value)
    }

    // List<String> converters
    @TypeConverter
    fun fromStringList(strings: List<String>?): String? {
        if (strings == null) return null
        return json.encodeToString(strings)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        if (value == null) return null
        return json.decodeFromString<List<String>>(value)
    }
}