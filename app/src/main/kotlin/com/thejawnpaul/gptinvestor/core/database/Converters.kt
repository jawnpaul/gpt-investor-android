package com.thejawnpaul.gptinvestor.core.database

import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.thejawnpaul.gptinvestor.features.company.data.local.model.PriceChange
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteResponse
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyNews
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.HistoricalData
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.NewsResolution
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.NewsThumbNail

class Converters {

    private val moshi =
        Moshi.Builder().addLast(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
            .build()

    private val priceChangeAdapter: JsonAdapter<PriceChange> =
        moshi.adapter(PriceChange::class.java)

    @TypeConverter
    fun stringToPriceChange(string: String): PriceChange? {
        return priceChangeAdapter.fromJson(string)
    }

    @TypeConverter
    fun priceChangeToString(priceChange: PriceChange): String {
        return priceChangeAdapter.toJson(priceChange)
    }

    // CompanyDetailRemoteResponse converters
    @TypeConverter
    fun fromCompanyDetail(value: CompanyDetailRemoteResponse?): String? {
        if (value == null) return null
        return moshi.adapter(CompanyDetailRemoteResponse::class.java).toJson(value)
    }

    @TypeConverter
    fun toCompanyDetail(value: String?): CompanyDetailRemoteResponse? {
        if (value == null) return null
        return moshi.adapter(CompanyDetailRemoteResponse::class.java).fromJson(value)
    }

    // List<CompanyNews> converters
    @TypeConverter
    fun fromNewsList(news: List<CompanyNews>?): String? {
        if (news == null) return null
        val type = Types.newParameterizedType(List::class.java, CompanyNews::class.java)
        return moshi.adapter<List<CompanyNews>>(type).toJson(news)
    }

    @TypeConverter
    fun toNewsList(value: String?): List<CompanyNews>? {
        if (value == null) return null
        val type = Types.newParameterizedType(List::class.java, CompanyNews::class.java)
        return moshi.adapter<List<CompanyNews>>(type).fromJson(value)
    }

    // List<HistoricalData> converters
    @TypeConverter
    fun fromHistoricalDataList(data: List<HistoricalData>?): String? {
        if (data == null) return null
        val type = Types.newParameterizedType(List::class.java, HistoricalData::class.java)
        return moshi.adapter<List<HistoricalData>>(type).toJson(data)
    }

    @TypeConverter
    fun toHistoricalDataList(value: String?): List<HistoricalData>? {
        if (value == null) return null
        val type = Types.newParameterizedType(List::class.java, HistoricalData::class.java)
        return moshi.adapter<List<HistoricalData>>(type).fromJson(value)
    }

    // NewsThumbNail converters
    @TypeConverter
    fun fromNewsThumbNail(thumbnail: NewsThumbNail?): String? {
        if (thumbnail == null) return null
        return moshi.adapter(NewsThumbNail::class.java).toJson(thumbnail)
    }

    @TypeConverter
    fun toNewsThumbNail(value: String?): NewsThumbNail? {
        if (value == null) return null
        return moshi.adapter(NewsThumbNail::class.java).fromJson(value)
    }

    // List<NewsResolution> converters
    @TypeConverter
    fun fromResolutionList(resolutions: List<NewsResolution>?): String? {
        if (resolutions == null) return null
        val type = Types.newParameterizedType(List::class.java, NewsResolution::class.java)
        return moshi.adapter<List<NewsResolution>>(type).toJson(resolutions)
    }

    @TypeConverter
    fun toResolutionList(value: String?): List<NewsResolution>? {
        if (value == null) return null
        val type = Types.newParameterizedType(List::class.java, NewsResolution::class.java)
        return moshi.adapter<List<NewsResolution>>(type).fromJson(value)
    }

    // List<String> converters
    @TypeConverter
    fun fromStringList(strings: List<String>?): String? {
        if (strings == null) return null
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        return moshi.adapter<List<String>>(type).toJson(strings)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        if (value == null) return null
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        return moshi.adapter<List<String>>(type).fromJson(value)
    }
}
